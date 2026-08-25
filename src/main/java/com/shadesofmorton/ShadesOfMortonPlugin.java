package com.shadesofmorton;

import com.google.inject.Provides;
import com.shadesofmorton.features.ChestPathFeature;
import com.shadesofmorton.features.Feature;
import com.shadesofmorton.features.PreventActionInterruptFeature;
import com.shadesofmorton.features.PyreDespawnTimerFeature;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
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

	private List<Feature> features;

	@Override
	protected void startUp()
	{
		features = List.of(preventActionInterruptFeature, pyreDespawnTimerFeature);
		for (Feature feature : features)
		{
			eventBus.register(feature);
			feature.startUp();
		}
		log.debug("Shades of Mort'ton started");
	}

	@Override
	protected void shutDown()
	{
		for (Feature feature : features)
		{
			eventBus.unregister(feature);
			feature.shutDown();
		}
		log.debug("Shades of Mort'ton stopped");
	}

	@Provides
	ShadesOfMortonConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ShadesOfMortonConfig.class);
	}
}
