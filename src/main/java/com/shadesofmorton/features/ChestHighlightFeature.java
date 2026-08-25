package com.shadesofmorton.features;

import com.shadesofmorton.ShadeKey;
import com.shadesofmorton.ShadesOfMortonConfig;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

/**
 * Outlines the catacombs chests that match the keys the player is holding (exact metal +
 * colour). Held keys are recomputed only when the inventory changes; chest objects are
 * tracked via spawn/despawn.
 */
public class ChestHighlightFeature implements Feature
{
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ModelOutlineRenderer modelOutlineRenderer;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ShadesOfMortonConfig config;

	private final Set<ShadeKey> heldKeys = EnumSet.noneOf(ShadeKey.class);
	private final Set<GameObject> chests = new HashSet<>();

	private ChestHighlightOverlay overlay;

	@Override
	public void startUp()
	{
		overlay = new ChestHighlightOverlay(client, this, modelOutlineRenderer);
		overlayManager.add(overlay);
		// getItemContainer must run on the client thread; startUp() does not.
		clientThread.invokeLater(this::recomputeHeldKeys);
	}

	@Override
	public void shutDown()
	{
		if (overlay != null)
		{
			overlayManager.remove(overlay);
			overlay = null;
		}
		heldKeys.clear();
		chests.clear();
	}

	@Override
	public boolean isEnabled()
	{
		return config.chestHighlight();
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() == InventoryID.INV)
		{
			recomputeHeldKeys();
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		final GameObject object = event.getGameObject();
		if (ShadeKey.fromChestObjectId(object.getId()) != null)
		{
			chests.add(object);
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		chests.remove(event.getGameObject());
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		// A scene reload clears objects without always firing GameObjectDespawned; drop the
		// stale references so nothing is outlined after leaving.
		if (event.getGameState() == GameState.LOADING)
		{
			chests.clear();
		}
	}

	Set<GameObject> getChests()
	{
		return chests;
	}

	Set<ShadeKey> getHeldKeys()
	{
		return heldKeys;
	}

	private void recomputeHeldKeys()
	{
		heldKeys.clear();

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
				heldKeys.add(key);
			}
		}
	}
}
