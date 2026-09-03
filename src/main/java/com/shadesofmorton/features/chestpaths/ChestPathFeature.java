package com.shadesofmorton.features.chestpaths;

import com.shadesofmorton.ShadeKey;
import com.shadesofmorton.ShadeKeyMetal;
import com.shadesofmorton.ShadesOfMortonConfig;
import com.shadesofmorton.features.Feature;

import java.util.EnumSet;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;

/**
 * Shows a path from the catacombs entrance to the chest area for each key metal the player
 * is currently holding. Held metals are recomputed only when the inventory changes.
 */
public class ChestPathFeature implements Feature
{
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ShadesOfMortonConfig config;

	private final Set<ShadeKeyMetal> heldMetals = EnumSet.noneOf(ShadeKeyMetal.class);

	private ChestPathOverlay overlay;

	@Override
	public void startUp()
	{
		overlay = new ChestPathOverlay(client, this);
		overlayManager.add(overlay);
		// getItemContainer must run on the client thread; startUp() does not.
		clientThread.invokeLater(this::recomputeHeldMetals);
	}

	@Override
	public void shutDown()
	{
		if (overlay != null)
		{
			overlayManager.remove(overlay);
			overlay = null;
		}
		heldMetals.clear();
	}

	@Override
	public boolean isEnabled()
	{
		return config.chestPaths();
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() == InventoryID.INV)
		{
			recomputeHeldMetals();
		}
	}

	Set<ShadeKeyMetal> getHeldMetals()
	{
		return heldMetals;
	}

	private void recomputeHeldMetals()
	{
		heldMetals.clear();

		final ItemContainer inventory = client.getItemContainer(InventoryID.INV);
		if (inventory == null)
		{
			return;
		}

		for (Item item : inventory.getItems())
		{
			final ShadeKey key = ShadeKey.fromKeyItemId(item.getId());
			if (key != null)
			{
				heldMetals.add(key.getMetal());
			}
		}
	}
}
