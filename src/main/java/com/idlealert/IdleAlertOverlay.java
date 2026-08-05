package com.idlealert;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;

public class IdleAlertOverlay extends Overlay
{
	private final IdleAlertPlugin plugin;
	private final IdleAlertConfig config;

	@Inject
	private IdleAlertOverlay(IdleAlertPlugin plugin, IdleAlertConfig config)
	{
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.TOP_CENTER);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showOverlaySquare() || !plugin.isIdle())
		{
			return null;
		}

		int size = config.squareSize();
		graphics.setColor(config.squareColor());
		graphics.fillRect(0, 0, size, size);

		return new Dimension(size, size);
	}
}
