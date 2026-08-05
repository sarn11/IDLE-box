package com.idlealert;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

@PluginDescriptor(
	name = "Idle Alert",
	description = "Muestra una alerta visual cuando llevas idle X segundos, teniendo en cuenta animacion/XP/interaccion, no solo input",
	tags = {"idle", "afk", "alert", "notification", "overlay"}
)
public class IdleAlertPlugin extends Plugin
{
	// RuneLite corre a 0.6 segundos por game tick
	private static final double SECONDS_PER_TICK = 0.6;

	@Inject
	private Client client;

	@Inject
	private IdleAlertConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private IdleAlertOverlay overlay;

	@Inject
	private InfoBoxManager infoBoxManager;

	private boolean idle = false;
	private IdleInfoBox infoBox;

	// Ultimo game tick en el que hubo CUALQUIER senal de actividad
	private int lastActivityTick;

	private WorldPoint lastLocation;
	private final Map<Skill, Integer> lastKnownXp = new EnumMap<>(Skill.class);
	private boolean xpGainedThisTick;

	@Provides
	IdleAlertConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(IdleAlertConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		lastActivityTick = client.getTickCount();
		lastLocation = null;
		lastKnownXp.clear();
		xpGainedThisTick = false;
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		removeInfoBox();
		idle = false;
	}

	@Subscribe
	public void onStatChanged(StatChanged event)
	{
		Integer previous = lastKnownXp.put(event.getSkill(), event.getXp());
		if (previous != null && event.getXp() > previous)
		{
			xpGainedThisTick = true;
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		int currentTick = client.getTickCount();
		boolean activeThisTick = hadPhysicalInputThisTick();

		if (config.considerGameActivity())
		{
			Player local = client.getLocalPlayer();

			if (local != null)
			{
				boolean animating = local.getAnimation() != -1;
				boolean interacting = local.getInteracting() != null;

				WorldPoint currentLocation = local.getWorldLocation();
				boolean moved = lastLocation != null && !lastLocation.equals(currentLocation);
				lastLocation = currentLocation;

				activeThisTick = activeThisTick || animating || interacting || moved || xpGainedThisTick;
			}
		}

		xpGainedThisTick = false;

		if (activeThisTick)
		{
			lastActivityTick = currentTick;
		}

		int thresholdTicks = (int) Math.round(config.idleSeconds() / SECONDS_PER_TICK);
		boolean nowIdle = (currentTick - lastActivityTick) >= thresholdTicks;

		if (nowIdle == idle)
		{
			return;
		}

		idle = nowIdle;

		if (idle && config.showInfoBox())
		{
			addInfoBox();
		}
		else
		{
			removeInfoBox();
		}
	}

	private boolean hadPhysicalInputThisTick()
	{
		// Los contadores de idle de teclado/raton se ponen a 0 justo en
		// el tick en el que hay input.
		return client.getKeyboardIdleTicks() == 0 || client.getMouseIdleTicks() == 0;
	}

	public boolean isIdle()
	{
		return idle;
	}

	private void addInfoBox()
	{
		if (infoBox != null)
		{
			return;
		}

		infoBox = new IdleInfoBox(createIcon(), this);
		infoBoxManager.addInfoBox(infoBox);
	}

	private void removeInfoBox()
	{
		if (infoBox != null)
		{
			infoBoxManager.removeInfoBox(infoBox);
			infoBox = null;
		}
	}

	// Icono generado por codigo, sin depender de recursos externos
	private BufferedImage createIcon()
	{
		BufferedImage img = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setColor(Color.RED);
		g.fillOval(2, 2, 20, 20);
		g.setColor(Color.WHITE);
		g.drawOval(2, 2, 20, 20);
		g.dispose();
		return img;
	}
}
