package com.shadesofmorton.features;

import static com.shadesofmorton.testing.TestHelpers.clickObject;
import static com.shadesofmorton.testing.TestHelpers.setPlayerAnimation;
import static com.shadesofmorton.testing.TestHelpers.useItemOnObject;
import static net.runelite.api.MenuAction.GAME_OBJECT_FIRST_OPTION;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.shadesofmorton.PluginHarness;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Posts spam-click sequences on the plugin's event bus and asserts the redundant click is
 * consumed. Exercises the real plugin: every feature is registered and receives the events.
 */
class PreventActionInterruptFeatureTest
{
	private static final int ACTION_ANIMATION = 832;
	private static final int PYRE_OBJECT_ID = ObjectID.TEMPLE_PYRE_LOGS;
	private static final int NON_PYRE_OBJECT_ID = 0;
	private static final int PYRE_TILE_X = 5;
	private static final int PYRE_TILE_Y = 6;

	private PluginHarness harness;

	@BeforeEach
	void setUp()
	{
		harness = new PluginHarness();
		harness.start();
	}

	private void assertSpamIsBlocked(MenuOptionClicked first, MenuOptionClicked second)
	{
		harness.post(first);
		assertFalse(first.isConsumed());

		setPlayerAnimation(harness.player, ACTION_ANIMATION);
		harness.post(second);
		assertTrue(second.isConsumed());
	}

	private void assertNeitherClickBlocked(MenuOptionClicked first, MenuOptionClicked second)
	{
		harness.post(first);
		setPlayerAnimation(harness.player, ACTION_ANIMATION);
		harness.post(second);

		assertFalse(first.isConsumed());
		assertFalse(second.isConsumed());
	}

	@ParameterizedTest
	@ValueSource(strings = { "Build", "Use", "Light" })
	void directObjectOptionSpam_isBlocked(String option)
	{
		assertSpamIsBlocked(
			clickObject(option, GAME_OBJECT_FIRST_OPTION, PYRE_OBJECT_ID, PYRE_TILE_X, PYRE_TILE_Y),
			clickObject(option, GAME_OBJECT_FIRST_OPTION, PYRE_OBJECT_ID, PYRE_TILE_X, PYRE_TILE_Y));
	}

	@ParameterizedTest
	@ValueSource(ints = { ItemID.LOGS_PYRE, ItemID.SHADE_BONES1 })
	void useFuelItemOnPyreSpam_isBlocked(int fuelItemId)
	{
		assertSpamIsBlocked(
			useItemOnObject(harness.client, PYRE_OBJECT_ID, fuelItemId),
			useItemOnObject(harness.client, PYRE_OBJECT_ID, fuelItemId));
	}

	@Test
	void useNonFuelItemOnPyre_isNotBlocked()
	{
		assertNeitherClickBlocked(
			useItemOnObject(harness.client, PYRE_OBJECT_ID, ItemID.HAMMER),
			useItemOnObject(harness.client, PYRE_OBJECT_ID, ItemID.HAMMER));
	}

	@Test
	void buildOnNonPyreObject_isNotBlocked()
	{
		assertNeitherClickBlocked(
			clickObject("Build", GAME_OBJECT_FIRST_OPTION, NON_PYRE_OBJECT_ID, PYRE_TILE_X, PYRE_TILE_Y),
			clickObject("Build", GAME_OBJECT_FIRST_OPTION, NON_PYRE_OBJECT_ID, PYRE_TILE_X, PYRE_TILE_Y));
	}

	@Test
	void nextStepAfterObjectAdvances_isNotBlocked()
	{
		// Add logs to an empty pyre.
		final MenuOptionClicked build =
			clickObject("Build", GAME_OBJECT_FIRST_OPTION, ObjectID.TEMPLE_PYRE, PYRE_TILE_X, PYRE_TILE_Y);
		harness.post(build);
		assertFalse(build.isConsumed());

		// Still animating, but the pyre has advanced to the logs stage — adding remains is allowed.
		setPlayerAnimation(harness.player, ACTION_ANIMATION);
		final MenuOptionClicked addRemains =
			clickObject("Use", GAME_OBJECT_FIRST_OPTION, ObjectID.TEMPLE_PYRE_LOGS, PYRE_TILE_X, PYRE_TILE_Y);
		harness.post(addRemains);
		assertFalse(addRemains.isConsumed());
	}
}
