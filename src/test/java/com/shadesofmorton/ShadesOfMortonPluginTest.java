package com.shadesofmorton;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ShadesOfMortonPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ShadesOfMortonPlugin.class);
		RuneLite.main(args);
	}
}
