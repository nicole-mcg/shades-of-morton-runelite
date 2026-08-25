package com.shadesofmorton;

import java.util.Set;
import net.runelite.api.gameval.ObjectID;

public final class ShadesOfMortonConstants
{
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
