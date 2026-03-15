package com.trevor.greenscreen;

public enum GreenscreenMode
{
	FULL_GAME("Full game only"),
	MINIMAP_ONLY("Minimap only"),
	BOTH("Full game and minimap");

	private final String name;

	GreenscreenMode(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
