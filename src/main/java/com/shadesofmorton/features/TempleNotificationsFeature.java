package com.shadesofmorton.features;

import com.shadesofmorton.ShadesOfMortonConfig;
import com.shadesofmorton.ShadesOfMortonConstants;
import java.util.Set;
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
import net.runelite.client.Notifier;
import net.runelite.client.eventbus.Subscribe;

/**
 * Fires notifications for temple activities: stopping a repair, stopping oil sanctifying, and
 * (TODO) reaching full sanctity. Both repair and sanctify use animation 832, so the activity
 * is identified by the tile of the last object the player interacted with, not by position.
 */
public class TempleNotificationsFeature implements Feature
{
	private static final int ACTION_ANIM = 832;
	private static final int IDLE_TICKS = 2;
	private static final int FULL_SANCTITY = 100;

	// Left-click / right-click options on a game object.
	private static final Set<MenuAction> GAME_OBJECT_ACTIONS = Set.of(
		MenuAction.GAME_OBJECT_FIRST_OPTION,
		MenuAction.GAME_OBJECT_SECOND_OPTION,
		MenuAction.GAME_OBJECT_THIRD_OPTION,
		MenuAction.GAME_OBJECT_FOURTH_OPTION,
		MenuAction.GAME_OBJECT_FIFTH_OPTION
	);

	@Inject
	private Client client;

	@Inject
	private Notifier notifier;

	@Inject
	private ShadesOfMortonConfig config;

	private WorldPoint lastObjectTile;
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
		if (GAME_OBJECT_ACTIONS.contains(event.getMenuAction()))
		{
			// param0/param1 are the scene X/Y of the clicked object.
			final WorldView worldView = client.getTopLevelWorldView();
			lastObjectTile = WorldPoint.fromScene(worldView, event.getParam0(), event.getParam1(), worldView.getPlane());
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		final Player local = client.getLocalPlayer();
		final boolean actionAnim = local != null && local.getAnimation() == ACTION_ANIM;

		final boolean repairingNow = actionAnim && lastObjectTile != null
			&& ShadesOfMortonConstants.TEMPLE_REPAIR_TILES.contains(lastObjectTile);
		final boolean sanctifyingNow = actionAnim
			&& ShadesOfMortonConstants.SANCTIFY_FIRE_TILE.equals(lastObjectTile);

		if (repairingNow)
		{
			repairing = true;
			repairingIdle = 0;
		}
		else if (repairing && ++repairingIdle >= IDLE_TICKS)
		{
			notifier.notify(config.stoppedRepairingNotification(), "Stopped repairing the temple.");
			repairing = false;
			repairingIdle = 0;
		}

		if (sanctifyingNow)
		{
			sanctifying = true;
			sanctifyingIdle = 0;
		}
		else if (sanctifying && ++sanctifyingIdle >= IDLE_TICKS)
		{
			notifier.notify(config.stoppedSanctifyingNotification(), "Stopped sanctifying oil.");
			sanctifying = false;
			sanctifyingIdle = 0;
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
		lastObjectTile = null;
		repairing = false;
		repairingIdle = 0;
		sanctifying = false;
		sanctifyingIdle = 0;
		lastSanctity = -1;
	}
}
