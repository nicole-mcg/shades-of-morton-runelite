package com.shadesofmorton.features;

import com.shadesofmorton.ShadesOfMortonConstants;
import com.shadesofmorton.ShadesOfMortonConfig;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Player;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.eventbus.Subscribe;

/**
 * Blocks redundant clicks on the burial site (funeral pyre) while the action is already in
 * progress, so spam-clicking doesn't keep restarting it.
 */
@Slf4j
public class PreventActionInterruptFeature implements Feature
{
	// Left-click / right-click options on a game object.
	private static final Set<MenuAction> GAME_OBJECT_ACTIONS = Set.of(
		MenuAction.GAME_OBJECT_FIRST_OPTION,
		MenuAction.GAME_OBJECT_SECOND_OPTION,
		MenuAction.GAME_OBJECT_THIRD_OPTION,
		MenuAction.GAME_OBJECT_FOURTH_OPTION,
		MenuAction.GAME_OBJECT_FIFTH_OPTION
	);

	// Ticks to keep blocking after a valid click, covering the gap before the animation starts.
	private static final int COOLDOWN_TICKS = 2;

	@Inject
	private Client client;

	@Inject
	private ShadesOfMortonConfig config;

	private int blockUntilTick = -1;
	private int activeParam0 = -1;
	private int activeParam1 = -1;

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
		log.debug("menu click option='{}' target='{}' action={} id={} p0={} p1={}",
			event.getMenuOption(), event.getMenuTarget(), event.getMenuAction(),
			event.getId(), event.getParam0(), event.getParam1());

		if (!config.preventActionInterrupt())
		{
			return;
		}

		if (!GAME_OBJECT_ACTIONS.contains(event.getMenuAction())
			|| !ShadesOfMortonConstants.PYRE_OBJECT_IDS.contains(event.getId()))
		{
			return;
		}

		final Player local = client.getLocalPlayer();
		final boolean isAnimating = local != null && local.getAnimation() != -1;
		final boolean cooling = client.getTickCount() < blockUntilTick;
		final boolean samePyre = event.getParam0() == activeParam0 && event.getParam1() == activeParam1;

		if ((isAnimating || cooling) && samePyre)
		{
			// Redundant spam click on the pyre we're already working — block it so the
			// in-progress action isn't restarted.
			event.consume();
			return;
		}

		// First / allowed click: let it through and arm the cooldown against the spam that follows.
		blockUntilTick = client.getTickCount() + COOLDOWN_TICKS;
		activeParam0 = event.getParam0();
		activeParam1 = event.getParam1();
	}

	private void resetState()
	{
		blockUntilTick = -1;
		activeParam0 = -1;
		activeParam1 = -1;
	}
}
