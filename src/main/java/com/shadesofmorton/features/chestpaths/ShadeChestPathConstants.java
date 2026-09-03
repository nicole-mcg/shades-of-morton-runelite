package com.shadesofmorton.features.chestpaths;

import java.util.List;
import java.util.Map;

import com.shadesofmorton.ShadeKeyMetal;

import net.runelite.api.coords.WorldPoint;

/**
 * Waypoint paths from the catacombs entrance to each metal's chest area. Each metal maps to
 * a list of <b>branches</b>; a branch is a polyline (corner waypoints, forward order
 * entrance → chest, plane 0) drawn as connected line segments. A single-destination route is
 * just one branch; a forked route has several (they repeat the shared prefix).
 */
public final class ShadeChestPathConstants
{
	/** Shared entrance tile — the start of every path. */
	private static final WorldPoint ENTRANCE = new WorldPoint(3493, 9726, 0);

	public static final Map<ShadeKeyMetal, List<List<WorldPoint>>> PATHS = Map.of(
		ShadeKeyMetal.BLACK, List.of(
			List.of(
				ENTRANCE,
				new WorldPoint(3493, 9716, 0),
				new WorldPoint(3493, 9708, 0),
				new WorldPoint(3488, 9705, 0),
				new WorldPoint(3488, 9699, 0),
				new WorldPoint(3485, 9696, 0),
				new WorldPoint(3479, 9696, 0),
				new WorldPoint(3479, 9692, 0)
			)
		),
		ShadeKeyMetal.BRONZE, List.of(
			// forks at (3493,9723) along y=9723
			List.of(ENTRANCE, new WorldPoint(3493, 9723, 0), new WorldPoint(3504, 9723, 0)), // east
			List.of(ENTRANCE, new WorldPoint(3493, 9723, 0), new WorldPoint(3482, 9723, 0))  // west
		),
		ShadeKeyMetal.SILVER, List.of(
			List.of(
				ENTRANCE,
				new WorldPoint(3493, 9715, 0),
				new WorldPoint(3493, 9708, 0),
				new WorldPoint(3467, 9708, 0)
			)
		),
		ShadeKeyMetal.STEEL, List.of(
			// common corridor to (3493,9708), then a west spur and an east spur along
			// y=9708, each with three chest stubs poking north to y=9710.
			// west spur
			List.of(ENTRANCE, new WorldPoint(3493, 9708, 0), new WorldPoint(3487, 9708, 0), new WorldPoint(3487, 9710, 0)),
			List.of(ENTRANCE, new WorldPoint(3493, 9708, 0), new WorldPoint(3479, 9708, 0), new WorldPoint(3479, 9710, 0)),
			List.of(ENTRANCE, new WorldPoint(3493, 9708, 0), new WorldPoint(3471, 9708, 0), new WorldPoint(3471, 9710, 0)),
			// east spur
			List.of(ENTRANCE, new WorldPoint(3493, 9708, 0), new WorldPoint(3499, 9708, 0), new WorldPoint(3499, 9710, 0)),
			List.of(ENTRANCE, new WorldPoint(3493, 9708, 0), new WorldPoint(3507, 9708, 0), new WorldPoint(3507, 9710, 0)),
			List.of(ENTRANCE, new WorldPoint(3493, 9708, 0), new WorldPoint(3515, 9708, 0), new WorldPoint(3515, 9710, 0))
		),
		ShadeKeyMetal.GOLD, List.of(
			List.of(
				ENTRANCE,
				new WorldPoint(3493, 9725, 0),
				new WorldPoint(3493, 9708, 0),
				new WorldPoint(3499, 9705, 0),
				new WorldPoint(3499, 9698, 0),
				new WorldPoint(3501, 9696, 0),
				new WorldPoint(3507, 9696, 0),
				new WorldPoint(3507, 9692, 0)
			)
		)
	);

	private ShadeChestPathConstants()
	{
	}
}
