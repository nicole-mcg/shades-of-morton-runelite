package com.shadesofmorton.features.despawntimer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import net.runelite.api.GameObject;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

/**
 * Renders the pyre despawn countdown as a pie over the tracked pyre. Turns red in the
 * final few seconds.
 */
class PyreDespawnTimerOverlay extends Overlay
{
	private static final int DIAMETER = 25;
	private static final double WARNING_THRESHOLD = 5d / 30d; // last ~5 seconds

	private final PyreDespawnTimerFeature feature;

	PyreDespawnTimerOverlay(PyreDespawnTimerFeature feature)
	{
		this.feature = feature;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		final GameObject pyre = feature.getTrackedPyre();
		if (pyre == null)
		{
			return null;
		}

		final double remaining = feature.getRemainingFraction();
		if (remaining <= 0d)
		{
			return null;
		}

		final Point location = pyre.getCanvasLocation();
		if (location == null)
		{
			return null;
		}

		final Color fill = remaining <= WARNING_THRESHOLD ? Color.RED : Color.GREEN;

		final ProgressPieComponent pie = new ProgressPieComponent();
		pie.setPosition(location);
		pie.setDiameter(DIAMETER);
		pie.setProgress(remaining);
		pie.setFill(fill);
		pie.setBorderColor(fill);
		pie.render(graphics);

		return null;
	}
}
