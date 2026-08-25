package com.shadesofmorton;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Notification;

@ConfigGroup(ShadesOfMortonConfig.CONFIG_GROUP)
public interface ShadesOfMortonConfig extends Config
{
	String CONFIG_GROUP = "shades-of-morton";

	@ConfigSection(
		name = "Notifications",
		description = "Notifications for temple and pyre activity.",
		position = 0
	)
	String NOTIFICATIONS_SECTION = "notifications";

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
		description = "Draw paths to the chests for keys you have in your inventory."
	)
	default boolean chestPaths()
	{
		return true;
	}

	@ConfigItem(
		keyName = "chestHighlight",
		name = "Highlight chests",
		description = "Highlight chests that you have the keys to open."
	)
	default boolean chestHighlight()
	{
		return true;
	}

	@ConfigItem(
		keyName = "fullSanctityNotification",
		name = "Notify on full sanctity",
		description = "Notify when the temple reaches full sanctity.",
		section = NOTIFICATIONS_SECTION
	)
	default Notification fullSanctityNotification()
	{
		return new Notification().withEnabled(true);
	}

	@ConfigItem(
		keyName = "stoppedRepairingNotification",
		name = "Notify on stop repairing",
		description = "Notify when you stop repairing the temple.",
		section = NOTIFICATIONS_SECTION
	)
	default Notification stoppedRepairingNotification()
	{
		return new Notification().withEnabled(true);
	}

	@ConfigItem(
		keyName = "stoppedSanctifyingNotification",
		name = "Notify on stop sanctifying",
		description = "Notify when you stop sanctifying oil at the fire.",
		section = NOTIFICATIONS_SECTION
	)
	default Notification stoppedSanctifyingNotification()
	{
		return new Notification().withEnabled(true);
	}
}
