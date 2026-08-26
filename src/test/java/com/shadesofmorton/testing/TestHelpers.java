package com.shadesofmorton.testing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Player;
import net.runelite.api.WorldView;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.EventBus;

/**
 * Builders for the real RuneLite events and API mocks that tests post on the plugin's event bus.
 * Events must be real instances (the EventBus dispatches by exact class), so only the backing
 * {@link MenuEntry} / API objects are mocked — never the event itself.
 */
public final class TestHelpers
{
	/** A menu click on a game object: option text, menu action, object id and scene x/y. */
	public static MenuOptionClicked clickObject(String option, MenuAction action, int id, int sceneX, int sceneY)
	{
		final MenuEntry entry = mock(MenuEntry.class);
		when(entry.getOption()).thenReturn(option);
		when(entry.getType()).thenReturn(action);
		when(entry.getIdentifier()).thenReturn(id);
		when(entry.getParam0()).thenReturn(sceneX);
		when(entry.getParam1()).thenReturn(sceneY);
		return new MenuOptionClicked(entry);
	}

	/** Using a selected inventory item on a game object (a "Use item -> object" interaction). */
	public static MenuOptionClicked useItemOnObject(Client client, int objectId, int selectedItemId)
	{
		final Widget selected = mock(Widget.class);
		when(selected.getItemId()).thenReturn(selectedItemId);
		when(client.getSelectedWidget()).thenReturn(selected);

		return clickObject("Use", MenuAction.WIDGET_TARGET_ON_GAME_OBJECT, objectId, 0, 0);
	}

	/** Sets the local player's animation for subsequent ticks (-1 = idle). Persists until changed. */
	public static void setPlayerAnimation(Player player, int animation)
	{
		when(player.getAnimation()).thenReturn(animation);
	}

	/** A VarbitChanged for a given var id and value (fires for VarPlayers too). */
	public static VarbitChanged varbitChanged(int varpId, int value)
	{
		final VarbitChanged event = new VarbitChanged();
		event.setVarpId(varpId);
		event.setValue(value);
		return event;
	}

	/** Stubs the client's top-level world view with the given base coords / plane, and returns it. */
	public static WorldView worldView(Client client, int baseX, int baseY, int plane)
	{
		final WorldView worldView = mock(WorldView.class);
		when(worldView.getBaseX()).thenReturn(baseX);
		when(worldView.getBaseY()).thenReturn(baseY);
		when(worldView.getPlane()).thenReturn(plane);
		when(client.getTopLevelWorldView()).thenReturn(worldView);
		return worldView;
	}

	/** Posts {@code count} game ticks on the bus. */
	public static void passTicks(EventBus eventBus, int count)
	{
		for (int i = 0; i < count; i++)
		{
			eventBus.post(new GameTick());
		}
	}

	/** Posts a temple-sanctity VarPlayer change on the bus. */
	public static void setSanctity(EventBus eventBus, int value)
	{
		eventBus.post(varbitChanged(VarPlayerID.TEMPLE_SANCTITY_P, value));
	}

	private TestHelpers()
	{
	}
}
