package com.shadesofmorton;

import java.awt.Color;

/**
 * The five shade key metals. Each key ({@link ShadeKey}) has one metal; behavior can branch
 * on it. The colour is the shared display colour used by the path and highlight overlays.
 */
public enum ShadeKeyMetal
{
	BRONZE("Bronze", new Color(205, 127, 50)),
	STEEL("Steel", new Color(170, 175, 185)),
	BLACK("Black", new Color(215, 215, 215)),
	SILVER("Silver", new Color(230, 230, 235)),
	GOLD("Gold", new Color(255, 215, 0));

	private final String displayName;
	private final Color colour;

	ShadeKeyMetal(String displayName, Color colour)
	{
		this.displayName = displayName;
		this.colour = colour;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public Color getColour()
	{
		return colour;
	}
}
