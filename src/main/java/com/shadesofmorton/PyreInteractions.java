package com.shadesofmorton;

import net.runelite.api.Client;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.client.util.Text;

/**
 * Single source of truth for detecting an "add logs / add remains" interaction with a pyre,
 * so the interrupt and despawn-timer features agree on what counts.
 */
public final class PyreInteractions
{
	/**
	 * @return true if this click adds fuel to a pyre: the "Build" object option (adds pyre
	 * logs), or "Use" of a pyre-logs / shade-remains item on the pyre. Using an unrelated
	 * item on a pyre is ignored.
	 */
	public static boolean isAddInteraction(Client client, MenuOptionClicked event)
	{
		if (!ShadesOfMortonConstants.OBJECT_MENU_ACTIONS.contains(event.getMenuAction())
			|| !ShadesOfMortonConstants.PYRE_OBJECT_IDS.contains(event.getId()))
		{
			return false;
		}

		final String option = Text.removeTags(event.getMenuOption());
		if ("Build".equals(option))
		{
			// Object option — builds the pyre from pyre logs in the inventory.
			return true;
		}

		if ("Use".equals(option))
		{
			// Item-on-object — require the used item to be pyre logs or shade remains.
			final Widget selected = client.getSelectedWidget();
			return selected != null
				&& ShadesOfMortonConstants.PYRE_FUEL_ITEM_IDS.contains(selected.getItemId());
		}

		return false;
	}

	private PyreInteractions()
	{
	}
}
