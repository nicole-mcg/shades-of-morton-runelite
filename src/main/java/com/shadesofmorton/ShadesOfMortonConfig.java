package com.shadesofmorton;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("shades-of-morton")
public interface ShadesOfMortonConfig extends Config
{
	@ConfigItem(
		keyName = "blockSpamClicks",
		name = "Block spam clicks",
		description = "Block redundant clicks on the burial site while the action is already in progress, so spam-clicking doesn't restart it."
	)
	default boolean blockSpamClicks()
	{
		return true;
	}

	@ConfigItem(
		keyName = "debugLogging",
		name = "Debug logging",
		description = "Log every menu click and animation change (used to discover the burial-site object IDs). Leave off during normal play."
	)
	default boolean debugLogging()
	{
		return false;
	}
}
