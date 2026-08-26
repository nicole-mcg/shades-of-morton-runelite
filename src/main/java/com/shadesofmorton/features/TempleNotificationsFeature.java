package com.shadesofmorton.features;

import com.shadesofmorton.ShadesOfMortonConfig;
import com.shadesofmorton.ShadesOfMortonConstants;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Player;
import net.runelite.api.WorldView;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.config.Notification;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.Text;

public class TempleNotificationsFeature implements Feature
{
	private static final int REPAIR_OR_SANCTIFY_ACTION_ANIMATION_ID = 832;

	private static final int FULL_SANCTITY = 100;

	private enum Activity
	{
		NONE(0, null),
		REPAIRING(2, "Stopped repairing the temple."),
		SANCTIFYING(3, "Stopped sanctifying oil.");

		/**
		 * Idle ticks beyond the inter-item animation gap before the action counts as stopped.
		 */
		final int numIdleTicksToNotify;
		final String idleNotificationMessage;

		Activity(int numIdleTicksToNotify, String idleNotificationMessage)
		{
			this.numIdleTicksToNotify = numIdleTicksToNotify;
			this.idleNotificationMessage = idleNotificationMessage;
		}
	}

	private static final Function<ShadesOfMortonConfig, Notification> NO_NOTIFICATION = config -> null;

	/**
	 * The config notification each activity fires when it stops. NONE is a no-op.
	 */
	private static final Map<Activity, Function<ShadesOfMortonConfig, Notification>> STOPPED_NOTIFICATIONS =
		new EnumMap<>(Map.of(
			Activity.NONE, NO_NOTIFICATION,
			Activity.REPAIRING, ShadesOfMortonConfig::stoppedRepairingNotification,
			Activity.SANCTIFYING, ShadesOfMortonConfig::stoppedSanctifyingNotification
		));

	static
	{
		for (Activity activity : Activity.values())
		{
			if (!STOPPED_NOTIFICATIONS.containsKey(activity))
			{
				// Can only happen if a developer adds an Activity without a handler above.
				throw new IllegalStateException("No stopped-notification handler for " + activity);
			}
		}
	}

	@Inject
	private Client client;

	@Inject
	private Notifier notifier;

	@Inject
	private ShadesOfMortonConfig config;

	/**
	 * The activity that the user is starting, but the player has not started the animation.
	 * This prevents us from notifying before the character actually started the action.
	 * For example when the player has clicked but the character is walking to the object.
	 */
	private Activity pendingActivity = Activity.NONE;

	/**
	 * The action that is in-progress, meaning the character is performing the animation.
	 */
	private Activity currentActivity = Activity.NONE;

	/**
	 * Num idle ticks since the current action last animated.
	 */
	private int numTicksSinceLastAction;

	private int lastSanctity = -1;

	@Override
	public void startUp()
	{
		resetState();
	}

	@Override
	public void shutDown()
	{
		resetState();
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		// Closing a right-click menu is not a redirect — ignore it.
		if (event.getMenuAction() == MenuAction.CANCEL)
		{
			return;
		}

		final Activity clicked = getActivityForEvent(event);
		if (clicked == pendingActivity)
		{
			return;
		}

		// A switch or interrupt drops the in-progress action silently; only a natural end notifies.
		pendingActivity = clicked;
		currentActivity = Activity.NONE;
		numTicksSinceLastAction = 0;
	}


	private Activity getActivityForEvent(MenuOptionClicked event)
	{
		if (!ShadesOfMortonConstants.OBJECT_MENU_ACTIONS.contains(event.getMenuAction()))
		{
			return Activity.NONE;
		}

		if (event.getId() == ShadesOfMortonConstants.SANCTIFY_ALTAR_OBJECT_ID && isSanctifyEvent(event))
		{
			return Activity.SANCTIFYING;
		}

		if ("Reinforce".equals(Text.removeTags(event.getMenuOption())) && isRepairTileClickEvent(event))
		{
			return Activity.REPAIRING;
		}

		return Activity.NONE;
	}

	private boolean isSanctifyEvent(MenuOptionClicked event)
	{
		// Use olive oil on the altar
		if (event.getMenuAction() == MenuAction.WIDGET_TARGET_ON_GAME_OBJECT)
		{
			final Widget selected = client.getSelectedWidget();
			return selected != null
				&& ShadesOfMortonConstants.OLIVE_OIL_ITEM_IDS.contains(selected.getItemId());
		}

		// Left-click the altar (the text is just 'Use')
		return "Use".equals(Text.removeTags(event.getMenuOption()));
	}

	private boolean isRepairTileClickEvent(MenuOptionClicked event)
	{
		// param0/param1 are the scene X/Y of the clicked wall object.
		final WorldView worldView = client.getTopLevelWorldView();
		final WorldPoint tile = WorldPoint.fromScene(worldView, event.getParam0(), event.getParam1(), worldView.getPlane());
		return ShadesOfMortonConstants.TEMPLE_REPAIR_TILES.contains(tile);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		final Player local = client.getLocalPlayer();
		final boolean animating = local != null
			&& local.getAnimation() == REPAIR_OR_SANCTIFY_ACTION_ANIMATION_ID;

		if (animating)
		{
			if (pendingActivity != Activity.NONE)
			{
				currentActivity = pendingActivity;
				numTicksSinceLastAction = 0;
			}
			return;
		}

		// currentActivity stays NONE while walking to a clicked action, so that gap can't time out.
		if (currentActivity == Activity.NONE)
		{
			return;
		}

		numTicksSinceLastAction++;
		if (numTicksSinceLastAction >= currentActivity.numIdleTicksToNotify)
		{
			onActivityStopped();
		}
	}

	private void onActivityStopped()
	{
		final Notification notification = STOPPED_NOTIFICATIONS.get(currentActivity).apply(config);
		if (notification != null)
		{
			notifier.notify(notification, currentActivity.idleNotificationMessage);
		}

		pendingActivity = Activity.NONE;
		currentActivity = Activity.NONE;
		numTicksSinceLastAction = 0;
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (event.getVarpId() != VarPlayerID.TEMPLE_SANCTITY_P)
		{
			return;
		}

		final int sanctity = event.getValue();

		// Sanctity can go above 100. Only notify when we go from under 100 to 100 or above.
		if (sanctity >= FULL_SANCTITY && lastSanctity < FULL_SANCTITY)
		{
			notifier.notify(config.fullSanctityNotification(), "Temple at full sanctity.");
		}

		lastSanctity = sanctity;
	}

	private void resetState()
	{
		pendingActivity = Activity.NONE;
		currentActivity = Activity.NONE;
		numTicksSinceLastAction = 0;
		lastSanctity = -1;
	}
}
