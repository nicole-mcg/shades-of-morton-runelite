package com.shadesofmorton;

import com.google.inject.Provides;
import com.shadesofmorton.features.ChestHighlightFeature;
import com.shadesofmorton.features.Feature;
import com.shadesofmorton.features.chestpaths.ChestPathFeature;
import com.shadesofmorton.features.despawntimer.PyreDespawnTimerFeature;
import com.shadesofmorton.features.interrupt.PreventActionInterruptFeature;
import com.shadesofmorton.features.notifications.TempleNotificationsFeature;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Shades of Mort'ton"
)
public class ShadesOfMortonPlugin extends Plugin
{
	@Inject
	private EventBus eventBus;

	@Inject
	private PreventActionInterruptFeature preventActionInterruptFeature;

	@Inject
	private PyreDespawnTimerFeature pyreDespawnTimerFeature;

	@Inject
	private ChestPathFeature chestPathFeature;

	@Inject
	private ChestHighlightFeature chestHighlightFeature;

	@Inject
	private TempleNotificationsFeature templeNotificationsFeature;

	private List<Feature> allFeatures;
	private final Set<Feature> activeFeatures = new HashSet<>();

	@Override
	protected void startUp()
	{
		allFeatures = List.of(preventActionInterruptFeature, pyreDespawnTimerFeature, chestPathFeature, chestHighlightFeature, templeNotificationsFeature);
		syncFeatures();
		log.debug("Shades of Mort'ton started");
	}

	@Override
	protected void shutDown()
	{
		for (Feature feature : activeFeatures)
		{
			eventBus.unregister(feature);
			feature.shutDown();
		}
		activeFeatures.clear();
		log.debug("Shades of Mort'ton stopped");
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (ShadesOfMortonConfig.CONFIG_GROUP.equals(event.getGroup()))
		{
			syncFeatures();
		}
	}

	/**
	 * Register + start each enabled feature and unregister + stop each disabled one, so the
	 * set of active features matches the config toggles.
	 */
	private void syncFeatures()
	{
		for (Feature feature : allFeatures)
		{
			final boolean enabled = feature.isEnabled();
			final boolean active = activeFeatures.contains(feature);

			if (enabled && !active)
			{
				eventBus.register(feature);
				feature.startUp();
				activeFeatures.add(feature);
			}
			else if (!enabled && active)
			{
				eventBus.unregister(feature);
				feature.shutDown();
				activeFeatures.remove(feature);
			}
		}
	}

	@Provides
	ShadesOfMortonConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ShadesOfMortonConfig.class);
	}
}
