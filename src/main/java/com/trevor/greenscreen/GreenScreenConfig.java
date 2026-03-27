package com.trevor.greenscreen;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

import java.awt.Color;

@ConfigGroup("greenscreen")
public interface GreenScreenConfig extends Config
{
	@ConfigItem(
		keyName = "mode",
		name = "Greenscreen mode",
		description = "Full game: green only on game world. Minimap only: green only on minimap. Both: green on game world and minimap.",
		position = 0
	)
	default GreenscreenMode mode()
	{
		return GreenscreenMode.FULL_GAME;
	}

	@ConfigItem(
		keyName = "color",
		name = "Color",
		description = "The color of the greenscreen",
		position = 1
	)
	default Color greenscreenColor()
	{
		return new Color(41, 244, 24);
	}

	@ConfigItem(
			keyName = "toggleKey",
			name= "Toggle Key",
			description = "Key to press to toggle greenscreen",
			position = 2
	)
	default Keybind hotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "defaultState",
			name = "Should Default On",
			description = "What state should the greenscreen default to",
			position = 3
	)
	default boolean defaultState()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hideXpOrb",
		name = "Hide XP orb (Resizable Modern)",
		description = "Hide the XP orb when using Resizable Modern layout",
		position = 4
	)
	default boolean hideXpOrb()
	{
		return false;
	}

	@ConfigItem(
		keyName = "hideCompass",
		name = "Hide compass (Resizable Modern)",
		description = "Hide the compass when using Resizable Modern layout",
		position = 5
	)
	default boolean hideCompass()
	{
		return false;
	}

	@ConfigItem(
		keyName = "hideAllOrbs",
		name = "Hide all orbs (Resizable Modern)",
		description = "Hide health, prayer, run, spec, XP, and related minimap orbs in Resizable Modern layout",
		position = 6
	)
	default boolean hideAllOrbs()
	{
		return false;
	}

	@ConfigItem(
		keyName = "moveCompassTopLeft",
		name = "Move compass to top-left (Resizable Modern)",
		description = "Move the compass widget to the top-left of the game canvas in Resizable Modern layout",
		position = 7
	)
	default boolean moveCompassTopLeft()
	{
		return false;
	}

	@ConfigItem(
		keyName = "compassOffsetX",
		name = "Compass X offset",
		description = "X offset from the top-left when moving the compass",
		position = 8
	)
	default int compassOffsetX()
	{
		return 8;
	}

	@ConfigItem(
		keyName = "compassOffsetY",
		name = "Compass Y offset",
		description = "Y offset from the top-left when moving the compass",
		position = 9
	)
	default int compassOffsetY()
	{
		return 8;
	}

	@ConfigItem(
		keyName = "moveSpecToXpSlot",
		name = "Move spec orb to XP slot (Resizable Modern)",
		description = "Move the combat spec orb to the XP orb position in Resizable Modern layout",
		position = 10
	)
	default boolean moveSpecToXpSlot()
	{
		return false;
	}
}
