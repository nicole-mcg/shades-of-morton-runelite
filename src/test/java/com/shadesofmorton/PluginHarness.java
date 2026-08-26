package com.shadesofmorton;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.Notification;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

/**
 * Builds the real {@link ShadesOfMortonPlugin} with mocked RuneLite APIs and a real
 * {@link EventBus}, so tests drive features the way the client does: register everything and
 * post events on the bus, rather than calling handlers directly.
 */
public final class PluginHarness
{
	public final EventBus eventBus = new EventBus();
	public final Client client = mock(Client.class);
	public final Notifier notifier = mock(Notifier.class);
	public final ShadesOfMortonConfig config = mock(ShadesOfMortonConfig.class);
	public final OverlayManager overlayManager = mock(OverlayManager.class);
	public final ClientThread clientThread = mock(ClientThread.class);
	public final ModelOutlineRenderer modelOutlineRenderer = mock(ModelOutlineRenderer.class);
	public final Player player = mock(Player.class);

	public final Notification fullSanctityNotification = new Notification();
	public final Notification stoppedRepairingNotification = new Notification();
	public final Notification stoppedSanctifyingNotification = new Notification();

	private final ShadesOfMortonPlugin plugin;

	public PluginHarness()
	{
		when(client.getLocalPlayer()).thenReturn(player);

		when(config.preventActionInterrupt()).thenReturn(true);
		when(config.pyreDespawnTimer()).thenReturn(true);
		when(config.chestPaths()).thenReturn(true);
		when(config.chestHighlight()).thenReturn(true);
		when(config.fullSanctityNotification()).thenReturn(fullSanctityNotification);
		when(config.stoppedRepairingNotification()).thenReturn(stoppedRepairingNotification);
		when(config.stoppedSanctifyingNotification()).thenReturn(stoppedSanctifyingNotification);

		final Injector injector = Guice.createInjector(new AbstractModule()
		{
			@Override
			protected void configure()
			{
				bind(EventBus.class).toInstance(eventBus);
				bind(Client.class).toInstance(client);
				bind(Notifier.class).toInstance(notifier);
				bind(ShadesOfMortonConfig.class).toInstance(config);
				bind(OverlayManager.class).toInstance(overlayManager);
				bind(ClientThread.class).toInstance(clientThread);
				bind(ModelOutlineRenderer.class).toInstance(modelOutlineRenderer);
			}
		});

		plugin = injector.getInstance(ShadesOfMortonPlugin.class);
	}

	/** Starts the plugin, registering every enabled feature on the event bus. */
	public void start()
	{
		plugin.startUp();
	}

	/** Posts an event on the bus, dispatching it to every registered feature. */
	public void post(Object event)
	{
		eventBus.post(event);
	}
}
