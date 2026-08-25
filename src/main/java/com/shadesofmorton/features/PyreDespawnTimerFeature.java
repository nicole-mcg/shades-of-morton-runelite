package com.shadesofmorton.features;

import com.shadesofmorton.ShadesOfMortonConfig;
import com.shadesofmorton.ShadesOfMortonConstants;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.GameObject;
import net.runelite.api.MenuAction;
import net.runelite.api.Point;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;

/**
 * Draws a pie-style countdown over the local player's fueled pyre. A pyre holding logs or
 * shade remains despawns 30 seconds after it is built; adding more fuel resets the timer.
 */
public class PyreDespawnTimerFeature implements Feature
{
	static final Duration DESPAWN_TIME = Duration.ofSeconds(30);

	// Left-click / right-click options on a game object.
	private static final Set<MenuAction> GAME_OBJECT_ACTIONS = Set.of(
		MenuAction.GAME_OBJECT_FIRST_OPTION,
		MenuAction.GAME_OBJECT_SECOND_OPTION,
		MenuAction.GAME_OBJECT_THIRD_OPTION,
		MenuAction.GAME_OBJECT_FOURTH_OPTION,
		MenuAction.GAME_OBJECT_FIFTH_OPTION
	);

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ShadesOfMortonConfig config;

	private final PyreDespawnTimerOverlay overlay = new PyreDespawnTimerOverlay(this);

	// Scene tile of the pyre the local player last interacted with ("mine").
	private int myPyreSceneX = -1;
	private int myPyreSceneY = -1;

	private GameObject trackedPyre;
	private Instant startTime;

	@Override
	public void startUp()
	{
		resetState();
		overlayManager.add(overlay);
	}

	@Override
	public void shutDown()
	{
		overlayManager.remove(overlay);
		resetState();
	}

	@Override
	public boolean isEnabled()
	{
		return config.pyreDespawnTimer();
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (GAME_OBJECT_ACTIONS.contains(event.getMenuAction())
			&& ShadesOfMortonConstants.PYRE_OBJECT_IDS.contains(event.getId()))
		{
			// param0/param1 are the scene X/Y of the clicked object.
			myPyreSceneX = event.getParam0();
			myPyreSceneY = event.getParam1();
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		final GameObject object = event.getGameObject();
		if (!ShadesOfMortonConstants.FUELED_PYRE_OBJECT_IDS.contains(object.getId()))
		{
			return;
		}

		final Point tile = object.getSceneMinLocation();
		if (tile.getX() == myPyreSceneX && tile.getY() == myPyreSceneY)
		{
			// Fuel added on the player's pyre — (re)start the countdown from full.
			trackedPyre = object;
			startTime = Instant.now();
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		if (event.getGameObject() == trackedPyre)
		{
			trackedPyre = null;
			startTime = null;
		}
	}

	GameObject getTrackedPyre()
	{
		return trackedPyre;
	}

	/**
	 * Fraction of the 30s window still remaining, clamped to [0, 1]. 0 when nothing tracked.
	 */
	double getRemainingFraction()
	{
		if (trackedPyre == null || startTime == null)
		{
			return 0d;
		}

		final double elapsed = Duration.between(startTime, Instant.now()).toMillis();
		final double fraction = 1d - elapsed / DESPAWN_TIME.toMillis();
		return Math.max(0d, Math.min(1d, fraction));
	}

	private void resetState()
	{
		trackedPyre = null;
		startTime = null;
		myPyreSceneX = -1;
		myPyreSceneY = -1;
	}
}
