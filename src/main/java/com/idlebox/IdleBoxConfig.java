package com.idlebox;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.Color;

@ConfigGroup(IdleBoxConfig.GROUP)
public interface IdleBoxConfig extends Config
{
	String GROUP = "idlebox";

	@ConfigItem(
		keyName = "idleSeconds",
		name = "Idle seconds",
		description = "Seconds without animation, interaction, movement or XP before showing the indicator",
		position = 1
	)
	default int idleSeconds()
	{
		return 10;
	}

	@ConfigItem(
		keyName = "ignoreAnimation",
		name = "Ignore animation",
		description = "Animations no longer count as activity, so you can be marked idle while animating",
		position = 2
	)
	default boolean ignoreAnimation()
	{
		return false;
	}

	@ConfigItem(
		keyName = "ignoreInteraction",
		name = "Ignore interaction",
		description = "Interacting with an NPC or player no longer counts as activity, so you can be marked idle while interacting",
		position = 3
	)
	default boolean ignoreInteraction()
	{
		return false;
	}

	@ConfigItem(
		keyName = "ignoreXpGain",
		name = "Ignore XP gain",
		description = "Gaining XP no longer counts as activity, so you can be marked idle while gaining XP",
		position = 4
	)
	default boolean ignoreXpGain()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showOverlaySquare",
		name = "Show square",
		description = "Shows a colored square on screen while idle",
		position = 5
	)
	default boolean showOverlaySquare()
	{
		return true;
	}

	@ConfigItem(
		keyName = "squareColor",
		name = "Square color",
		description = "Color of the square shown while idle",
		position = 6
	)
	default Color squareColor()
	{
		return Color.RED;
	}

	@ConfigItem(
		keyName = "squareSize",
		name = "Square size",
		description = "Side of the square in pixels",
		position = 7
	)
	default int squareSize()
	{
		return 24;
	}

	@ConfigItem(
		keyName = "showInfoBox",
		name = "Show InfoBox",
		description = "Shows an InfoBox (icon + text) while idle",
		position = 8
	)
	default boolean showInfoBox()
	{
		return true;
	}
}
