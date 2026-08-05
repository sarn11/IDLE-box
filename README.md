# Idle Box (plugin de RuneLite)

Muestra en pantalla un indicador visual cuando el **personaje** lleva más
de X segundos sin actividad en el juego: un cuadrado de color y/o un
InfoBox (icono + texto "IDLE") en la barra lateral. Ambos son
configurables desde las opciones del plugin (segundos, color, tamaño, y
si quieres uno, el otro o los dos). No es un sistema de alertas/
notificaciones, es solo un indicador de estado en pantalla.

## Cómo funciona

Deliberadamente **no mira teclado ni ratón**: solo actividad del
personaje dentro del juego. Puedes tener el ratón quieto encima de la
ventana y el plugin seguirá sin marcar idle mientras el personaje siga
haciendo algo. Se marca `idle = true` cuando **ninguna** de estas
señales ha ocurrido durante el umbral configurado:

| Señal | Cómo se detecta | Cubre |
|---|---|---|
| Animación | `player.getAnimation() != -1` | Pesca, tala, minería, combate cuerpo a cuerpo, cocinar, etc. |
| Interacción | `player.getInteracting() != null` | Combate (incluidos los huecos entre golpes), comerciar, seguir a alguien |
| XP ganada | Se compara la XP de cada skill tick a tick (`StatChanged`) | Red de seguridad para huecos de animación (p.ej. esperando a que pique un pez) |
| Movimiento | Cambia el `WorldPoint` del jugador respecto al tick anterior | Viajes largos iniciados con un solo click |

Cada game tick (0.6s) se comprueba si hubo alguna de estas señales; si
sí, se guarda ese tick como `lastActivityTick`. En cuanto pasan
`segundos configurados / 0.6` ticks sin ninguna señal, aparece el
indicador. En cuanto vuelve a haber cualquier señal, desaparece.

Este proyecto sigue la estructura oficial de
[`runelite/example-plugin`](https://github.com/runelite/example-plugin), la
plantilla recomendada por el
[Plugin Hub](https://github.com/runelite/plugin-hub) de RuneLite.

## Estructura

```
Idle-box/
├── build.gradle                    # dependencias, compilación y tarea `run` para probarlo
├── settings.gradle                 # nombre del proyecto Gradle
├── runelite-plugin.properties      # metadatos del plugin (nombre, autor, tags...)
├── icon.png                        # icono para el listado del Plugin Hub (48x48)
├── run-dev.sh                      # atajo para lanzar `./gradlew run` con el JDK correcto
├── gradlew / gradlew.bat           # wrapper de Gradle (no requiere instalación previa)
├── gradle/wrapper/                 # binarios del wrapper
├── AGENTS.md / CLAUDE.md           # guía para agentes de IA que trabajen en este repo
├── .editorconfig                   # estilo de código
├── .gitignore
├── LICENSE                         # BSD 2-Clause, la que recomienda el Plugin Hub
├── src/main/java/com/idlebox/
│   ├── IdleBoxPlugin.java          # lógica principal (detecta idle)
│   ├── IdleBoxConfig.java          # opciones configurables
│   ├── IdleBoxOverlay.java         # dibuja el cuadrado
│   └── IdleBoxInfoBox.java         # el InfoBox lateral
└── src/test/java/com/idlebox/
    └── IdleBoxPluginTest.java      # punto de entrada para `./gradlew run`
```

## Cómo probarlo (entorno de desarrollo)

Este proyecto usa el mismo mecanismo que la plantilla oficial de plugins
externos: el `client` de RuneLite como dependencia de test, más una clase
`IdleBoxPluginTest` que registra el plugin con
`ExternalPluginManager.loadBuiltin(...)` antes de arrancar
`RuneLite.main(...)`. Así el cliente completo arranca con "Idle Box" ya
cargado, sin depender de convenciones de paquete ni de dejar jars sueltos
en carpetas de configuración.

1. Requisitos: JDK 11 (o superior; `build.gradle` compila con
   `options.release.set(11)`).

2. Lanza el cliente en modo desarrollador:
   ```bash
   ./run-dev.sh
   ```
   o directamente:
   ```bash
   ./gradlew run
   ```
   La tarea `run` de `build.gradle` ejecuta `IdleBoxPluginTest` (classpath
   de test, que incluye el `client` de RuneLite y las clases de este
   plugin) con `-ea` (obligatorio: RuneLite aborta con `AssertionError` en
   `--developer-mode` si las aserciones no están activas) y los argumentos
   `--developer-mode --debug`.

3. Para iniciar sesión en el cliente de desarrollo necesitas una cuenta
   Jagex; sigue la guía oficial
   [Using Jagex Accounts](https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts).

4. Una vez abierto, activa "Idle Box" desde el panel lateral de plugins y
   ajusta segundos, colores y el resto de opciones desde su icono de
   engranaje.

## Publicarlo en el Plugin Hub

Si tu idea es solo usarlo tú, con el modo desarrollador ya te vale. Si
quieres que llegue al Plugin Hub oficial
([`runelite/plugin-hub`](https://github.com/runelite/plugin-hub)), el
proceso resumido es:

1. Sube este repositorio a GitHub (público).
2. Haz fork de [`runelite/plugin-hub`](https://github.com/runelite/plugin-hub)
   y añade un fichero en `plugins/` con:
   ```
   repository=https://github.com/<tu-usuario>/<tu-repo>.git
   commit=<hash del commit>
   ```
3. Abre un PR contra `runelite/plugin-hub` con ese fichero. Un CI comprueba
   que el build pase y el equipo de RuneLite revisa el plugin antes de
   fusionarlo.

Instrucciones completas en el README del Plugin Hub.

## Personalización rápida

- Cambiar posición del cuadrado: en `IdleBoxOverlay`, la línea
  `setPosition(OverlayPosition.TOP_CENTER)` — puedes usar
  `TOP_LEFT`, `BOTTOM_RIGHT`, etc.
- Que parpadee en vez de ser fijo: en `render()` puedes alternar el
  color según `System.currentTimeMillis() % 1000 < 500`.
- Detectar solo inactividad de ratón (no teclado): usa
  `client.getMouseIdleTicks()` directamente en vez de las dos señales.
