package com.idlealert;

import net.runelite.client.ui.overlay.infobox.InfoBox;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class IdleInfoBox extends InfoBox
{
	IdleInfoBox(BufferedImage image, IdleAlertPlugin plugin)
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
		return "Llevas idle sin actividad";
	}
}
