package com.shadesofmorton;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.runelite.api.MenuAction;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;

public final class ShadesOfMortonConstants
{
	/** Menu option words for adding fuel to a pyre: "Build" = add pyre logs, "Use" = add remains. */
	public static final Set<String> PYRE_ADD_OPTIONS = Set.of("Build", "Use");

	/** Pyre logs items (sacred-oiled logs) used to build a funeral pyre. */
	public static final Set<Integer> PYRE_LOG_ITEM_IDS = Set.of(
		ItemID.LOGS_PYRE,
		ItemID.OAK_LOGS_PYRE,
		ItemID.WILLOW_LOGS_PYRE,
		ItemID.MAPLE_LOGS_PYRE,
		ItemID.YEW_LOGS_PYRE,
		ItemID.MAGIC_LOGS_PYRE,
		ItemID.TEAK_LOGS_PYRE,
		ItemID.MAHOGANY_LOGS_PYRE,
		ItemID.ARCTIC_PINE_LOGS_PYRE,
		ItemID.REDWOOD_LOGS_PYRE,
		ItemID.CAMPHOR_LOGS_PYRE,
		ItemID.IRONWOOD_LOGS_PYRE,
		ItemID.ROSEWOOD_LOGS_PYRE
	);

	/** Shade remains items (Loar → Urium) placed on a pyre before burning. */
	public static final Set<Integer> SHADE_REMAINS_ITEM_IDS = Set.of(
		ItemID.SHADE_BONES1,
		ItemID.SHADE_BONES2,
		ItemID.SHADE_BONES3,
		ItemID.SHADE_BONES4,
		ItemID.SHADE_BONES5,
		ItemID.SHADE_BONES6
	);

	/** Items validly used on a pyre: pyre logs (on empty pyre) or shade remains (on logs pyre). */
	public static final Set<Integer> PYRE_FUEL_ITEM_IDS = Stream
		.concat(PYRE_LOG_ITEM_IDS.stream(), SHADE_REMAINS_ITEM_IDS.stream())
		.collect(Collectors.toUnmodifiableSet());

	/** Olive oil doses used on the flaming fire altar to make sacred oil. */
	public static final Set<Integer> OLIVE_OIL_ITEM_IDS = Set.of(
		ItemID.OLIVEOIL1,
		ItemID.OLIVEOIL2,
		ItemID.OLIVEOIL3,
		ItemID.OLIVEOIL4
	);

	/** The temple fire altar (used to sanctify oil) — flaming, unlit, and broken variants. */
	public static final Set<Integer> FIRE_ALTAR_OBJECT_IDS = Set.of(
		ObjectID.TEMPLEFIRE_ALTAR,
		ObjectID.TEMPLEFIRE_ALTAR_NOFIRE,
		ObjectID.TEMPLEFIRE_ALTAR_NOFIRE_BROKEN
	);

	/** Game message shown when using too-high remains on too-low pyre logs (a failed add). */
	public static final String INCOMPATIBLE_REMAINS_MESSAGE = "You need higher level pyre logs to burn these remains.";

	/**
	 * Menu actions that target a game object (so {@code getId()} / params are the object's).
	 * Includes widget-target-on-object, since "Use remains on pyre" is an item-on-object action.
	 */
	public static final Set<MenuAction> OBJECT_MENU_ACTIONS = Set.of(
		MenuAction.GAME_OBJECT_FIRST_OPTION,
		MenuAction.GAME_OBJECT_SECOND_OPTION,
		MenuAction.GAME_OBJECT_THIRD_OPTION,
		MenuAction.GAME_OBJECT_FOURTH_OPTION,
		MenuAction.GAME_OBJECT_FIFTH_OPTION,
		MenuAction.WIDGET_TARGET_ON_GAME_OBJECT
	);
	/**
	 * The shade catacombs, as an axis-aligned bounding box (SW corner + size, plane 0).
	 * Corners captured in-game: SW 3456,9664 · SE 3518,9665 · NE 3521,9727 · NW 3456,9728.
	 */
	public static final WorldArea CATACOMBS_AREA = new WorldArea(3456, 9664, 66, 65, 0);

	/**
	 * Tiles of the Mort'ton temple wall segments you repair (the square perimeter; the
	 * 3506,3314 entrance gap is not a temple tile). Used to identify a repairing interaction.
	 */
	public static final Set<WorldPoint> TEMPLE_REPAIR_TILES = Set.of(
		new WorldPoint(3504, 3314, 0),
		new WorldPoint(3505, 3314, 0),
		new WorldPoint(3507, 3314, 0),
		new WorldPoint(3508, 3314, 0),
		new WorldPoint(3508, 3315, 0),
		new WorldPoint(3508, 3316, 0),
		new WorldPoint(3508, 3317, 0),
		new WorldPoint(3508, 3318, 0),
		new WorldPoint(3507, 3318, 0),
		new WorldPoint(3506, 3318, 0),
		new WorldPoint(3505, 3318, 0),
		new WorldPoint(3504, 3318, 0),
		new WorldPoint(3504, 3317, 0),
		new WorldPoint(3504, 3316, 0),
		new WorldPoint(3504, 3315, 0)
	);

	public static final Set<Integer> PYRE_OBJECT_IDS = Set.of(
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
		// No ObjectID gameval constant exists for camphor/ironwood/rosewood — raw IDs.
		58407, // camphor
		58408, // ironwood
		58409, // rosewood

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
		ObjectID.TEMPLE_PYRE_BONES_REDWOOD,
		// raw IDs (no gameval constant)
		58410, // camphor + remains
		58411, // ironwood + remains
		58412  // rosewood + remains
	);

	/**
	 * Pyres that actually hold fuel (logs or shade remains) and therefore run the 30s
	 * despawn timer — every pyre ID except the empty {@link ObjectID#TEMPLE_PYRE} stand.
	 */
	public static final Set<Integer> FUELED_PYRE_OBJECT_IDS = PYRE_OBJECT_IDS.stream()
		.filter(id -> id != ObjectID.TEMPLE_PYRE)
		.collect(Collectors.toUnmodifiableSet());

	/**
	 * All shade key item IDs (obtained by burning shade remains), derived from
	 * {@link ShadeKey} so the key definitions stay the single source of truth.
	 */
	public static final Set<Integer> SHADE_KEY_ITEM_IDS = Arrays.stream(ShadeKey.values())
		.map(ShadeKey::getKeyItemId)
		.collect(Collectors.toUnmodifiableSet());

	private ShadesOfMortonConstants()
	{
	}
}
