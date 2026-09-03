package com.shadesofmorton.helpers;

import java.util.Set;

import com.shadesofmorton.ShadesOfMortonConstants;

import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.client.util.Text;

/**
 * Single source of truth for detecting pyre interactions, so the interrupt and despawn-timer
 * features agree on what counts.
 */
public final class PyreInteractionHelpers
{
	/**
	 * @return true if this click adds fuel to a pyre: a "Build"/"Use" object option (adds pyre
	 * logs / remains from the inventory), or using a pyre-logs / shade-remains item on the pyre.
	 * Using an unrelated item on a pyre is ignored.
	 */
	public static boolean isAddInteraction(Client client, MenuOptionClicked event)
	{
		return matches(client, event, ShadesOfMortonConstants.PYRE_ADD_OPTIONS);
	}

	/**
	 * @return true if this click starts an interruptible pyre action: adding fuel (as
	 * {@link #isAddInteraction}) or lighting a completed pyre.
	 */
	public static boolean isPyreActionInteraction(Client client, MenuOptionClicked event)
	{
		return matches(client, event, ShadesOfMortonConstants.PYRE_ACTION_OPTIONS);
	}

	private static boolean matches(Client client, MenuOptionClicked event, Set<String> directOptions)
	{
		if (!ShadesOfMortonConstants.OBJECT_MENU_ACTIONS.contains(event.getMenuAction())
			|| !ShadesOfMortonConstants.PYRE_OBJECT_IDS.contains(event.getId()))
		{
			return false;
		}

		// Item-on-object — require the used item to be pyre logs or shade remains.
		if (event.getMenuAction() == MenuAction.WIDGET_TARGET_ON_GAME_OBJECT)
		{
			final Widget selected = client.getSelectedWidget();
			return selected != null
				&& ShadesOfMortonConstants.PYRE_FUEL_ITEM_IDS.contains(selected.getItemId());
		}

		return directOptions.contains(Text.removeTags(event.getMenuOption()));
	}

	private PyreInteractionHelpers()
	{
	}
}
