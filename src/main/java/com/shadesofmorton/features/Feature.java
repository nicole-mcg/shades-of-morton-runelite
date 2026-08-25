package com.shadesofmorton.features;

/**
 * A self-contained unit of plugin behavior. Each feature owns its own {@code @Subscribe}
 * handlers and state; the main plugin registers/unregisters it on the event bus and drives
 * this lifecycle.
 */
public interface Feature
{
	void startUp();

	void shutDown();

	/**
	 * Whether this feature is currently enabled (from its own injected config). The plugin
	 * registers/unregisters the feature to match. Defaults to always on.
	 */
	default boolean isEnabled()
	{
		return true;
	}
}
