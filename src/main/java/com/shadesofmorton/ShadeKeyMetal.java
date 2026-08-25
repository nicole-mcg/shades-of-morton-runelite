package com.shadesofmorton;

/**
 * The five shade key metals. Each key ({@link ShadeKey}) has one metal; behavior can branch
 * on it.
 */
public enum ShadeKeyMetal
{
	BRONZE("Bronze"),
	STEEL("Steel"),
	BLACK("Black"),
	SILVER("Silver"),
	GOLD("Gold");

	private final String displayName;

	ShadeKeyMetal(String displayName)
	{
		this.displayName = displayName;
	}

	public String getDisplayName()
	{
		return displayName;
	}
}
