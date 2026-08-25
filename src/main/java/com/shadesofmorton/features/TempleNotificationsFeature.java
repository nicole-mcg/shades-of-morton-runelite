package com.shadesofmorton.features;

import com.shadesofmorton.ShadesOfMortonConfig;
import com.shadesofmorton.ShadesOfMortonConstants;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.WorldView;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.client.Notifier;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.Text;

/**
 * Fires notifications for temple activities: stopping a repair, stopping oil sanctifying, and
 * reaching full sanctity. Both repair and sanctify use animation 832, so the activity is
 * identified deterministically by the menu option clicked ("Reinforce" a temple wall vs "Use"
 * olive oil on the flaming fire altar), then the animation drives start/stop.
 */
public class TempleNotificationsFeature implements Feature
{
	private static final int ACTION_ANIM = 832;
	// Both repair and sanctify auto-repeat with a 1-tick animation gap between each item; end
	// the sequence only after the animation has been absent for one tick longer than that gap.
	private static final int REPAIR_IDLE_TICKS = 2;
	private static final int SANCTIFY_IDLE_TICKS = 3;
	private static final int FULL_SANCTITY = 100;

	private enum Activity
	{
		NONE,
		REPAIRING,
		SANCTIFYING
	}

	@Inject
	private Client client;

	@Inject
	private Notifier notifier;

	@Inject
	private ShadesOfMortonConfig config;

	private Activity currentActivity = Activity.NONE;
	private boolean repairing;
	private int repairingIdle;
	private boolean sanctifying;
	private int sanctifyingIdle;
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
		if (!ShadesOfMortonConstants.OBJECT_MENU_ACTIONS.contains(event.getMenuAction()))
		{
			return;
		}

		// Any object interaction targeting the altar is a sanctify: the direct left-click
		// (GAME_OBJECT_* option) and the use-oil-on-altar click (WIDGET_TARGET_ON_GAME_OBJECT,
		// whose getId() is the altar) both land here.
		if (ShadesOfMortonConstants.FIRE_ALTAR_OBJECT_IDS.contains(event.getId()))
		{
			currentActivity = Activity.SANCTIFYING;
			return;
		}

		final String option = Text.removeTags(event.getMenuOption());
		if ("Reinforce".equals(option) && repairTileClicked(event))
		{
			currentActivity = Activity.REPAIRING;
		}
	}

	private boolean repairTileClicked(MenuOptionClicked event)
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
		final boolean actionAnim = local != null && local.getAnimation() == ACTION_ANIM;

		final boolean repairingNow = actionAnim && currentActivity == Activity.REPAIRING;
		final boolean sanctifyingNow = actionAnim && currentActivity == Activity.SANCTIFYING;

		if (repairingNow)
		{
			repairing = true;
			repairingIdle = 0;
		}
		else if (repairing && ++repairingIdle >= REPAIR_IDLE_TICKS)
		{
			notifier.notify(config.stoppedRepairingNotification(), "Stopped repairing the temple.");
			repairing = false;
			repairingIdle = 0;
			currentActivity = Activity.NONE;
		}

		if (sanctifyingNow)
		{
			sanctifying = true;
			sanctifyingIdle = 0;
		}
		else if (sanctifying && ++sanctifyingIdle >= SANCTIFY_IDLE_TICKS)
		{
			notifier.notify(config.stoppedSanctifyingNotification(), "Stopped sanctifying oil.");
			sanctifying = false;
			sanctifyingIdle = 0;
			currentActivity = Activity.NONE;
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (event.getVarpId() != VarPlayerID.TEMPLE_SANCTITY_P)
		{
			return;
		}

		final int sanctity = event.getValue();
		// Sanctity can climb past 100 (e.g. 101); notify only on the crossing into full, not
		// on every change at or above it.
		if (sanctity >= FULL_SANCTITY && lastSanctity < FULL_SANCTITY)
		{
			notifier.notify(config.fullSanctityNotification(), "Temple at full sanctity.");
		}
		lastSanctity = sanctity;
	}

	private void resetState()
	{
		currentActivity = Activity.NONE;
		repairing = false;
		repairingIdle = 0;
		sanctifying = false;
		sanctifyingIdle = 0;
		lastSanctity = -1;
	}
}
