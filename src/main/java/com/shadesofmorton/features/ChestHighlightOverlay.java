package com.shadesofmorton.features;

import com.shadesofmorton.ShadeKey;
import com.shadesofmorton.ShadesOfMortonConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

/**
 * Glowing outline over each tracked chest whose key the player is holding, only while inside
 * the catacombs.
 */
class ChestHighlightOverlay extends Overlay
{
	private static final int OUTLINE_WIDTH = 2;
	private static final int OUTLINE_FEATHER = 4;

	private final Client client;
	private final ChestHighlightFeature feature;
	private final ModelOutlineRenderer modelOutlineRenderer;

	ChestHighlightOverlay(Client client, ChestHighlightFeature feature, ModelOutlineRenderer modelOutlineRenderer)
	{
		this.client = client;
		this.feature = feature;
		this.modelOutlineRenderer = modelOutlineRenderer;
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

		for (GameObject chest : feature.getChests())
		{
			final ShadeKey key = ShadeKey.fromChestObjectId(chest.getId());
			if (key != null && feature.getHeldKeys().contains(key))
			{
				modelOutlineRenderer.drawOutline(chest, OUTLINE_WIDTH, new Color(0, 255, 0), OUTLINE_FEATHER);
			}
		}

		return null;
	}
}
