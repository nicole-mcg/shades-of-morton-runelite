package com.shadesofmorton.features;

import static com.shadesofmorton.testing.TestHelpers.clickObject;
import static com.shadesofmorton.testing.TestHelpers.passTicks;
import static com.shadesofmorton.testing.TestHelpers.setPlayerAnimation;
import static com.shadesofmorton.testing.TestHelpers.setSanctity;
import static com.shadesofmorton.testing.TestHelpers.useItemOnObject;
import static com.shadesofmorton.testing.TestHelpers.worldView;
import static net.runelite.api.MenuAction.GAME_OBJECT_FIRST_OPTION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.shadesofmorton.PluginHarness;
import java.util.stream.Stream;
import net.runelite.api.MenuAction;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.config.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Posts whole repair / sanctify / sanctity flows on the plugin's event bus and asserts on the
 * notifications emitted. Exercises the real plugin with every feature registered.
 */
class TempleNotificationsFeatureTest
{
	private static final int ACTION_ANIMATION = 832;
	private static final int IDLE = -1;
	private static final int REPAIR_IDLE_TICKS = 2;
	private static final int SANCTIFY_IDLE_TICKS = 3;

	/**
	 * Any object id that is not the altar, standing in for a temple wall segment.
	 */
	private static final int WALL_OBJECT_ID = 0;

	private PluginHarness harness;

	@BeforeEach
	void setUp()
	{
		harness = new PluginHarness();
		harness.start();
	}

	private void assertStopsWith(MenuOptionClicked start, int idleTicks, Notification expected, String message)
	{
		harness.post(start);
		setPlayerAnimation(harness.player, ACTION_ANIMATION);
		passTicks(harness.eventBus, 1);
		setPlayerAnimation(harness.player, IDLE);
		passTicks(harness.eventBus, idleTicks);

		verify(harness.notifier).notify(expected, message);
	}

	private void assertNoNotification(MenuOptionClicked start)
	{
		harness.post(start);
		setPlayerAnimation(harness.player, ACTION_ANIMATION);
		passTicks(harness.eventBus, 1);
		setPlayerAnimation(harness.player, IDLE);
		passTicks(harness.eventBus, SANCTIFY_IDLE_TICKS);

		verify(harness.notifier, never()).notify(any(Notification.class), anyString());
	}

	static Stream<WorldPoint> repairTiles()
	{
		return Stream.of(
			new WorldPoint(3504, 3314, 0), new WorldPoint(3505, 3314, 0), new WorldPoint(3507, 3314, 0),
			new WorldPoint(3508, 3314, 0), new WorldPoint(3508, 3315, 0), new WorldPoint(3508, 3316, 0),
			new WorldPoint(3508, 3317, 0), new WorldPoint(3508, 3318, 0), new WorldPoint(3507, 3318, 0),
			new WorldPoint(3506, 3318, 0), new WorldPoint(3505, 3318, 0), new WorldPoint(3504, 3318, 0),
			new WorldPoint(3504, 3317, 0), new WorldPoint(3504, 3316, 0), new WorldPoint(3504, 3315, 0));
	}

	@ParameterizedTest
	@MethodSource("repairTiles")
	void repairingOnTile_thenStopping_notifies(WorldPoint tile)
	{
		worldView(harness.client, 0, 0, 0);

		assertStopsWith(
			clickObject("Reinforce", GAME_OBJECT_FIRST_OPTION, WALL_OBJECT_ID, tile.getX(), tile.getY()),
			REPAIR_IDLE_TICKS, harness.stoppedRepairingNotification, "Stopped repairing the temple.");
	}

	@Test
	void sanctifyingByLeftClickOnLitAltar_thenStopping_notifies()
	{
		assertStopsWith(
			clickObject("Use", GAME_OBJECT_FIRST_OPTION, ObjectID.TEMPLEFIRE_ALTAR, 0, 0),
			SANCTIFY_IDLE_TICKS, harness.stoppedSanctifyingNotification, "Stopped sanctifying oil.");
	}

	@ParameterizedTest
	@ValueSource(ints = { ItemID.OLIVEOIL1, ItemID.OLIVEOIL2, ItemID.OLIVEOIL3, ItemID.OLIVEOIL4 })
	void sanctifyingWithOilDoseOnLitAltar_thenStopping_notifies(int oilId)
	{
		assertStopsWith(
			useItemOnObject(harness.client, ObjectID.TEMPLEFIRE_ALTAR, oilId),
			SANCTIFY_IDLE_TICKS, harness.stoppedSanctifyingNotification, "Stopped sanctifying oil.");
	}

	@ParameterizedTest
	@ValueSource(ints = { ObjectID.TEMPLEFIRE_ALTAR_NOFIRE, ObjectID.TEMPLEFIRE_ALTAR_NOFIRE_BROKEN })
	void sanctifyingByLeftClickOnUnlitAltar_doesNotNotify(int altarId)
	{
		assertNoNotification(clickObject("Use", GAME_OBJECT_FIRST_OPTION, altarId, 0, 0));
	}

	@ParameterizedTest
	@ValueSource(ints = { ObjectID.TEMPLEFIRE_ALTAR_NOFIRE, ObjectID.TEMPLEFIRE_ALTAR_NOFIRE_BROKEN })
	void usingOilOnUnlitAltar_doesNotNotify(int altarId)
	{
		assertNoNotification(useItemOnObject(harness.client, altarId, ItemID.OLIVEOIL1));
	}

	@Test
	void sanctityReachingFull_notifies()
	{
		setSanctity(harness.eventBus, 100);

		verify(harness.notifier).notify(harness.fullSanctityNotification, "Temple at full sanctity.");
	}

	@Test
	void sanctityClimbingPastFull_notifiesOnlyOnce()
	{
		setSanctity(harness.eventBus, 100);
		setSanctity(harness.eventBus, 101);

		verify(harness.notifier, times(1)).notify(harness.fullSanctityNotification, "Temple at full sanctity.");
	}

	@Test
	void sanctityBelowFull_neverNotifies()
	{
		setSanctity(harness.eventBus, 99);

		verifyNoInteractions(harness.notifier);
	}

	@Test
	void sanctifying_withinIdleThreshold_doesNotNotifyYet()
	{
		harness.post(clickObject("Use", GAME_OBJECT_FIRST_OPTION, ObjectID.TEMPLEFIRE_ALTAR, 0, 0));

		setPlayerAnimation(harness.player, ACTION_ANIMATION);
		passTicks(harness.eventBus, 1);
		setPlayerAnimation(harness.player, IDLE);
		passTicks(harness.eventBus, 2);

		verify(harness.notifier, never()).notify(any(Notification.class), anyString());
	}

	@Test
	void clickingAwayMidAction_doesNotNotify()
	{
		harness.post(clickObject("Use", GAME_OBJECT_FIRST_OPTION, ObjectID.TEMPLEFIRE_ALTAR, 0, 0));
		setPlayerAnimation(harness.player, ACTION_ANIMATION);
		passTicks(harness.eventBus, 1);

		harness.post(clickObject("Walk here", MenuAction.WALK, 0, 0, 0));
		setPlayerAnimation(harness.player, IDLE);
		passTicks(harness.eventBus, 3);

		verifyNoInteractions(harness.notifier);
	}

	@Test
	void walkingToAction_doesNotNotifyBeforeItStarts_thenNotifiesAtEnd()
	{
		worldView(harness.client, 0, 0, 0);
		final WorldPoint tile = repairTiles().iterator().next();
		harness.post(clickObject("Reinforce", GAME_OBJECT_FIRST_OPTION, WALL_OBJECT_ID, tile.getX(), tile.getY()));

		setPlayerAnimation(harness.player, IDLE);
		passTicks(harness.eventBus, 3);

		verify(harness.notifier, never()).notify(any(Notification.class), anyString());

		setPlayerAnimation(harness.player, ACTION_ANIMATION);
		passTicks(harness.eventBus, 1);
		setPlayerAnimation(harness.player, IDLE);
		passTicks(harness.eventBus, 2);

		verify(harness.notifier).notify(harness.stoppedRepairingNotification, "Stopped repairing the temple.");
	}

	@Test
	void usingNonOilItemOnLitAltar_isNotTreatedAsSanctifying()
	{
		assertNoNotification(useItemOnObject(harness.client, ObjectID.TEMPLEFIRE_ALTAR, ItemID.HAMMER));
	}
}
