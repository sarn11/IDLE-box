package com.idlebox;

import net.runelite.client.ui.overlay.infobox.InfoBox;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class IdleBoxInfoBox extends InfoBox
{
	IdleBoxInfoBox(BufferedImage image, IdleBoxPlugin plugin)
	{
		super(image, plugin);
	}

	@Override
	public String getText()
	{
		return "IDLE";
	}

	@Override
	public Color getTextColor()
	{
		return Color.RED;
	}

	@Override
	public String getTooltip()
	{
		return "You've been idle without activity";
	}
}
