package com.shadesofmorton;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ObjectID;

public final class ShadesOfMortonConstants
{
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

	/** Tile of the fire/still used to sanctify oil (interior of the temple). */
	public static final WorldPoint SANCTIFY_FIRE_TILE = new WorldPoint(3506, 3316, 0);

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
