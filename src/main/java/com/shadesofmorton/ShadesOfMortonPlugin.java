package com.shadesofmorton;

import com.google.inject.Provides;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Player;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Shades of Mort'ton"
)
public class ShadesOfMortonPlugin extends Plugin
{
	private static final Set<Integer> PYRE_OBJECT_IDS = Set.of(
		ObjectID.TEMPLE_PYRE,

		// logs, no remains
		ObjectID.TEMPLE_PYRE_LOGS, // Normal logs
		ObjectID.TEMPLE_PYRE_OAK,
		ObjectID.TEMPLE_PYRE_WILLOW,
		ObjectID.TEMPLE_PYRE_MAPLE,
		ObjectID.TEMPLE_PYRE_YEW,
		ObjectID.TEMPLE_PYRE_MAGIC,
		ObjectID.TEMPLE_PYRE_TEAK,
		ObjectID.TEMPLE_PYRE_MAHOGANY,
		ObjectID.TEMPLE_PYRE_ARCTIC_PINE,
		ObjectID.TEMPLE_PYRE_REDWOOD,

		// logs + shade remains
		ObjectID.TEMPLE_PYRE_BONES_LOGS,
		ObjectID.TEMPLE_PYRE_BONES_OAK,
		ObjectID.TEMPLE_PYRE_BONES_WILLOW,
		ObjectID.TEMPLE_PYRE_BONES_MAPLE,
		ObjectID.TEMPLE_PYRE_BONES_YEW,
		ObjectID.TEMPLE_PYRE_BONES_MAGIC,
		ObjectID.TEMPLE_PYRE_BONES_TEAK,
		ObjectID.TEMPLE_PYRE_BONES_MAHOGANY,
		ObjectID.TEMPLE_PYRE_BONES_ARCTIC_PINE,
		ObjectID.TEMPLE_PYRE_BONES_REDWOOD

			// rosewood 58409
			// rosewood + remains 58412
			// camph 58407
			// camph + remains 58410
			// ironwood 58408
			// ironwood 58411

		// No ObjectID gameval constant exists for Camphor, Ironwood, or Rosewood pyre logs
		// (not present in runelite-api ObjectID). Add raw IDs here if captured in-game via debug logging.
	);

	// Left-click / right-click options on a game object.
	private static final Set<MenuAction> GAME_OBJECT_ACTIONS = Set.of(
		MenuAction.GAME_OBJECT_FIRST_OPTION,
		MenuAction.GAME_OBJECT_SECOND_OPTION,
		MenuAction.GAME_OBJECT_THIRD_OPTION,
		MenuAction.GAME_OBJECT_FOURTH_OPTION,
		MenuAction.GAME_OBJECT_FIFTH_OPTION
	);

	// Ticks to keep blocking after a valid click, covering the gap before the animation starts.
	private static final int NUM_COOLDOWN_TICKS = 2;

	@Inject
	private Client client;

	@Inject
	private ShadesOfMortonConfig config;

	private int blockUntilTick = -1;
	private int activeParam0 = -1;
	private int activeParam1 = -1;

	@Override
	protected void startUp()
	{
		resetState();
		log.debug("Shades of Mort'ton started");
	}

	@Override
	protected void shutDown()
	{
		resetState();
		log.debug("Shades of Mort'ton stopped");
	}

	@Subscribe(priority = 100)
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (config.debugLogging())
		{
			log.debug("menu click option='{}' target='{}' action={} id={} p0={} p1={}",
				event.getMenuOption(), event.getMenuTarget(), event.getMenuAction(),
				event.getId(), event.getParam0(), event.getParam1());
		}

		if (!config.blockSpamClicks())
		{
			return;
		}

		if (!GAME_OBJECT_ACTIONS.contains(event.getMenuAction())
			|| !PYRE_OBJECT_IDS.contains(event.getId()))
		{
			return;
		}

		final Player local = client.getLocalPlayer();
		final boolean isAnimating = local != null && local.getAnimation() != -1;
		final boolean isCooldown = client.getTickCount() < blockUntilTick;
		final boolean samePyre = event.getParam0() == activeParam0 && event.getParam1() == activeParam1;


		if ((isAnimating || isCooldown) && samePyre)
		{
			// Redundant spam click on the pyre we're already working — block it so the
			// in-progress action isn't restarted.
			event.consume();
			return;
		}

		// First / allowed click: let it through and arm the cooldown against the spam that follows.
		blockUntilTick = client.getTickCount() + NUM_COOLDOWN_TICKS;
		activeParam0 = event.getParam0();
		activeParam1 = event.getParam1();
	}

	private void resetState()
	{
		blockUntilTick = -1;
		activeParam0 = -1;
		activeParam1 = -1;
	}

	@Provides
	ShadesOfMortonConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ShadesOfMortonConfig.class);
	}
}
