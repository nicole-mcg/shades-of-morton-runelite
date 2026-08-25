package com.shadesofmorton;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("shades-of-morton")
public interface ShadesOfMortonConfig extends Config
{
	@ConfigItem(
		keyName = "preventActionInterrupt",
		name = "Prevent action interrupt",
		description = "Prevent redundant clicks on the pyre site from interrupting the action while it is already in progress, so spam-clicking doesn't restart it."
	)
	default boolean preventActionInterrupt()
	{
		return true;
	}
}
