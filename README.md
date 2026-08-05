# Idle Alert (plugin de RuneLite)

Muestra una alerta visual cuando llevas más de X segundos sin tocar el
teclado ni el ratón: un cuadrado de color en pantalla y/o un InfoBox
(icono + texto "IDLE") en la barra lateral. Ambos son configurables desde
las opciones del plugin (segundos, color, tamaño, y si quieres uno, el
otro o los dos).

## Cómo funciona

El idle real no depende solo del teclado/ratón: si estás pescando,
picando roca, cortando madera o en combate, puedes pasar minutos sin
tocar nada y seguir "activo". Por eso el plugin combina varias señales
y solo marca `idle = true` cuando **ninguna** de ellas ha ocurrido
durante el umbral configurado:

| Señal | Cómo se detecta | Cubre |
|---|---|---|
| Input físico | `client.getKeyboardIdleTicks()` / `getMouseIdleTicks()` == 0 en el tick actual | Cualquier tecla/click reciente |
| Animación | `player.getAnimation() != -1` | Pesca, tala, minería, combate cuerpo a cuerpo, cocinar, etc. |
| Interacción | `player.getInteracting() != null` | Combate (incluidos los huecos entre golpes), comerciar, seguir a alguien |
| XP ganada | Se compara la XP de cada skill tick a tick (`StatChanged`) | Red de seguridad para huecos de animación (p.ej. esperando a que pique un pez) |
| Movimiento | Cambia el `WorldPoint` del jugador respecto al tick anterior | Viajes largos iniciados con un solo click |

Cada game tick (0.6s) se comprueba si hubo alguna de estas señales; si
sí, se guarda ese tick como `lastActivityTick`. En cuanto pasan
`segundos configurados / 0.6` ticks sin ninguna señal, se activa la
alerta. En cuanto vuelve a haber cualquier señal, se desactiva.

Puedes desactivar las señales de "actividad del personaje" desde la
opción **Contar actividad del personaje** y dejarlo solo con
input físico (el comportamiento original, útil si quieres detectar
"AFK del jugador" literal, no "AFK del personaje").

## Estructura

```
Idle-box/
├── build.gradle                    # dependencias y configuración de compilación
├── runelite-plugin.properties      # metadatos del plugin (nombre, autor, tags...)
├── gradlew / gradlew.bat           # wrapper de Gradle (no requiere instalación previa)
├── gradle/wrapper/                 # binarios del wrapper
├── .editorconfig                   # estilo de código
├── .gitignore
├── LICENSE
└── src/main/java/com/idlealert/
    ├── IdleAlertPlugin.java        # lógica principal (detecta idle)
    ├── IdleAlertConfig.java        # opciones configurables
    ├── IdleAlertOverlay.java       # dibuja el cuadrado
    └── IdleInfoBox.java            # el InfoBox lateral
```

## Cómo probarlo (entorno de desarrollo)

Este proyecto usa el mismo mecanismo que usa RuneLite para todos sus
plugins externos: el `client` de RuneLite como dependencia de test, que
te permite lanzar el cliente completo con el plugin ya cargado, sin
necesidad de clonar ninguna plantilla aparte.

1. Requisitos: JDK 11 (RuneLite se compila con `sourceCompatibility 1.11`).

2. Compila y descarga dependencias:
   ```bash
   ./gradlew build
   ```

3. Lanza el cliente de RuneLite en modo desarrollador, con el plugin
   cargado automáticamente porque está en el classpath del proyecto.
   La forma más cómoda es desde un IDE (IntelliJ IDEA, que es lo que usa
   la comunidad de RuneLite):

   - Importa el proyecto como proyecto Gradle.
   - Crea una configuración de ejecución para la clase
     `net.runelite.client.RuneLite` (viene en la dependencia
     `net.runelite:client`, añadida como `testImplementation` en
     `build.gradle`).
   - Añade el argumento de programa `--developer-mode`.
   - Ejecuta. Se abrirá tu cliente local de RuneLite con "Idle Alert"
     disponible y activable desde el panel lateral de plugins.

   También puedes hacerlo desde línea de comandos con Gradle, por
   ejemplo añadiendo una tarea `run` que use `net.runelite.client.RuneLite`
   como `mainClass` con el `testRuntimeClasspath`, o ejecutando
   directamente con `java -cp ...` una vez resuelto el classpath de test.

4. Ajusta los segundos, colores y el resto de opciones desde el icono
   de engranaje del plugin en el panel lateral.

Si tu idea es solo usarlo tú, en el modo desarrollador ya te vale sin
publicarlo. Si quieres que llegue al Plugin Hub oficial
(`runelite/plugin-hub`), hay un proceso de PR y revisión aparte.

## Personalización rápida

- Cambiar posición del cuadrado: en `IdleAlertOverlay`, la línea
  `setPosition(OverlayPosition.TOP_CENTER)` — puedes usar
  `TOP_LEFT`, `BOTTOM_RIGHT`, etc.
- Que parpadee en vez de ser fijo: en `render()` puedes alternar el
  color según `System.currentTimeMillis() % 1000 < 500`.
- Detectar solo inactividad de ratón (no teclado): usa
  `client.getMouseIdleTicks()` directamente en vez de las dos señales.
