package com.shadesofmorton.features;

import com.shadesofmorton.ShadeChestPaths;
import com.shadesofmorton.ShadeKeyMetal;
import com.shadesofmorton.ShadesOfMortonConstants;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

/**
 * Draws the polyline path(s) to the chest area for the metals the player is holding keys
 * for, but only while inside the catacombs.
 */
class ChestPathOverlay extends Overlay
{
	private final Client client;
	private final ChestPathFeature feature;

	ChestPathOverlay(Client client, ChestPathFeature feature)
	{
		this.client = client;
		this.feature = feature;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		final Player local = client.getLocalPlayer();
		if (local == null)
		{
			return null;
		}

		final WorldPoint playerLocation = local.getWorldLocation();
		if (playerLocation == null || !ShadesOfMortonConstants.CATACOMBS_AREA.contains(playerLocation))
		{
			return null;
		}

		graphics.setStroke(new BasicStroke(2f));
		for (ShadeKeyMetal metal : feature.getHeldMetals())
		{
			final List<List<WorldPoint>> branches = ShadeChestPaths.PATHS.get(metal);
			if (branches == null)
			{
				continue;
			}
			final Color colour = metal.getColour();
			for (List<WorldPoint> branch : branches)
			{
				if (branch.size() >= 2)
				{
					drawPath(graphics, branch, colour);
				}
			}
		}

		return null;
	}

	private void drawPath(Graphics2D graphics, List<WorldPoint> path, Color colour)
	{
		graphics.setColor(colour);
		for (int i = 0; i < path.size() - 1; i++)
		{
			final Point from = toCanvas(path.get(i));
			final Point to = toCanvas(path.get(i + 1));
			if (from == null || to == null)
			{
				continue;
			}
			graphics.drawLine(from.getX(), from.getY(), to.getX(), to.getY());
		}
	}

	private Point toCanvas(WorldPoint worldPoint)
	{
		final LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
		if (localPoint == null)
		{
			return null;
		}
		return Perspective.localToCanvas(client, localPoint, worldPoint.getPlane());
	}
}
