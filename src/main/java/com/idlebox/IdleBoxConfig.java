package com.idlebox;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.Color;

@ConfigGroup("idlebox")
public interface IdleBoxConfig extends Config
{
	@ConfigItem(
		keyName = "idleSeconds",
		name = "Segundos idle",
		description = "Segundos sin animación, interacción, movimiento ni XP antes de mostrar el indicador",
		position = 1
	)
	default int idleSeconds()
	{
		return 10;
	}

	@ConfigItem(
		keyName = "showOverlaySquare",
		name = "Mostrar cuadrado",
		description = "Muestra un cuadrado de color en pantalla mientras estás idle",
		position = 2
	)
	default boolean showOverlaySquare()
	{
		return true;
	}

	@ConfigItem(
		keyName = "squareColor",
		name = "Color del cuadrado",
		description = "Color del cuadrado que se muestra al estar idle",
		position = 3
	)
	default Color squareColor()
	{
		return Color.RED;
	}

	@ConfigItem(
		keyName = "squareSize",
		name = "Tamaño del cuadrado",
		description = "Lado del cuadrado en píxeles",
		position = 4
	)
	default int squareSize()
	{
		return 24;
	}

	@ConfigItem(
		keyName = "showInfoBox",
		name = "Mostrar InfoBox",
		description = "Muestra un InfoBox (icono + texto) mientras estás idle",
		position = 5
	)
	default boolean showInfoBox()
	{
		return true;
	}
}
