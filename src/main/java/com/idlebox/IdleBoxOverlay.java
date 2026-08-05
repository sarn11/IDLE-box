package com.idlebox;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;

public class IdleBoxOverlay extends Overlay
{
	private final IdleBoxPlugin plugin;
	private final IdleBoxConfig config;

	@Inject
	private IdleBoxOverlay(IdleBoxPlugin plugin, IdleBoxConfig config)
	{
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.TOP_CENTER);
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
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
