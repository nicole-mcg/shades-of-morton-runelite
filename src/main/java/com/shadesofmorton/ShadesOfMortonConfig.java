package com.shadesofmorton;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Notification;

@ConfigGroup(ShadesOfMortonConfig.CONFIG_GROUP)
public interface ShadesOfMortonConfig extends Config
{
	String CONFIG_GROUP = "shades-of-morton";

	@ConfigItem(
		keyName = "preventActionInterrupt",
		name = "Prevent action interrupt",
		description = "Prevent redundant clicks on the pyre site from interrupting the action while it is already in progress, so spam-clicking doesn't restart it."
	)
	default boolean preventActionInterrupt()
	{
		return true;
	}

	@ConfigItem(
		keyName = "pyreDespawnTimer",
		name = "Pyre despawn timer",
		description = "Show a pie-style countdown over your fueled pyre before it despawns."
	)
	default boolean pyreDespawnTimer()
	{
		return true;
	}

	@ConfigItem(
		keyName = "chestPaths",
		name = "Chest paths",
		description = "Draw paths in the catacombs to the chests matching the key metals you are holding."
	)
	default boolean chestPaths()
	{
		return true;
	}

	@ConfigItem(
		keyName = "chestHighlight",
		name = "Chest highlight",
		description = "Outline the catacombs chests that match the keys you are holding."
	)
	default boolean chestHighlight()
	{
		return true;
	}

	@ConfigItem(
		keyName = "fullSanctityNotification",
		name = "Full sanctity",
		description = "Notify when the temple reaches full sanctity."
	)
	default Notification fullSanctityNotification()
	{
		return new Notification();
	}

	@ConfigItem(
		keyName = "stoppedRepairingNotification",
		name = "Stopped repairing",
		description = "Notify when you stop repairing the temple."
	)
	default Notification stoppedRepairingNotification()
	{
		return new Notification();
	}

	@ConfigItem(
		keyName = "stoppedSanctifyingNotification",
		name = "Stopped sanctifying oil",
		description = "Notify when you stop sanctifying oil at the fire."
	)
	default Notification stoppedSanctifyingNotification()
	{
		return new Notification();
	}
}
