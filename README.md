# Idle Box (RuneLite plugin)

Shows an on-screen visual indicator when your **character** has been idle
for more than X seconds: a colored square and/or an InfoBox (icon + "IDLE"
text) in the sidebar. Both are configurable from the plugin's settings
(seconds, color, size, and whether you want one, the other, or both). It's
not an alert/notification system, just an on-screen status indicator.

## How it works

It deliberately **ignores keyboard and mouse**: only in-game character
activity counts. You can have the mouse sitting still over the window and
the plugin will keep it from being marked idle as long as the character is
still doing something. `idle` is set to `true` when **none** of these
signals have occurred during the configured threshold:

| Signal | How it's detected | Covers |
|---|---|---|
| Animation | `player.getAnimation() != -1` | Fishing, woodcutting, mining, melee combat, cooking, etc. |
| Interaction | `player.getInteracting() != null` | Combat (including the gaps between hits), trading, following someone |
| XP gained | Each skill's XP is compared tick to tick (`StatChanged`) | Safety net for animation gaps (e.g. waiting for a fish to bite) |
| Movement | The player's `WorldPoint` changes from the previous tick | Long journeys started with a single click |

Every game tick (0.6s) it checks whether any of these signals occurred; if
so, that tick is stored as `lastActivityTick`. Once `configured seconds /
0.6` ticks pass without any signal, the indicator appears. As soon as any
signal happens again, it disappears.

This project follows the official structure of
[`runelite/example-plugin`](https://github.com/runelite/example-plugin), the
template recommended by RuneLite's
[Plugin Hub](https://github.com/runelite/plugin-hub).

## Structure

```
Idle-box/
├── build.gradle                    # dependencies, compilation and the `run` task to test it
├── settings.gradle                 # Gradle project name
├── runelite-plugin.properties      # plugin metadata (name, author, tags...)
├── icon.png                        # icon for the Plugin Hub listing (48x48)
├── run-dev.sh                      # shortcut to launch `./gradlew run` with the right JDK
├── gradlew / gradlew.bat           # Gradle wrapper (no prior installation required)
├── gradle/wrapper/                 # wrapper binaries
├── .editorconfig                   # code style
├── .gitignore
├── LICENSE                         # BSD 2-Clause, the one recommended by the Plugin Hub
├── src/main/java/com/idlebox/
│   ├── IdleBoxPlugin.java          # main logic (detects idle)
│   ├── IdleBoxConfig.java          # configurable options
│   ├── IdleBoxOverlay.java         # draws the square
│   └── IdleBoxInfoBox.java         # the sidebar InfoBox
└── src/test/java/com/idlebox/
    └── IdleBoxPluginTest.java      # entry point for `./gradlew run`
```

## How to test it (development environment)

This project uses the same mechanism as the official external plugin
template: RuneLite's `client` as a test dependency, plus an
`IdleBoxPluginTest` class that registers the plugin with
`ExternalPluginManager.loadBuiltin(...)` before starting `RuneLite.main(...)`.
This way the full client starts up with "Idle Box" already loaded, without
relying on package naming conventions or leaving loose jars in config
folders.

1. Requirements: JDK 11 (or newer; `build.gradle` compiles with
   `options.release.set(11)`).

2. Launch the client in developer mode:
   ```bash
   ./run-dev.sh
   ```
   or directly:
   ```bash
   ./gradlew run
   ```
   The `run` task in `build.gradle` runs `IdleBoxPluginTest` (test
   classpath, which includes RuneLite's `client` and this plugin's classes)
   with `-ea` (required: RuneLite aborts with `AssertionError` in
   `--developer-mode` if assertions aren't enabled) and the arguments
   `--developer-mode --debug`.

3. To log into the development client you need a Jagex account; follow the
   official
   [Using Jagex Accounts](https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts)
   guide.

4. Once open, enable "Idle Box" from the plugin sidebar and adjust seconds,
   colors and the rest of the options from its gear icon.

## Publishing to the Plugin Hub

If you just want to use it yourself, developer mode is all you need. If you
want it to land on the official Plugin Hub
([`runelite/plugin-hub`](https://github.com/runelite/plugin-hub)), the
short version is:

1. Push this repository to GitHub (public).
2. Fork [`runelite/plugin-hub`](https://github.com/runelite/plugin-hub) and
   add a file under `plugins/` with:
   ```
   repository=https://github.com/<your-username>/<your-repo>.git
   commit=<commit hash>
   ```
3. Open a PR against `runelite/plugin-hub` with that file. CI checks that
   the build passes and the RuneLite team reviews the plugin before merging.

Full instructions in the Plugin Hub's README.

## Quick customization

- Change the square's position: in `IdleBoxOverlay`, the line
  `setPosition(OverlayPosition.TOP_CENTER)` — you can use `TOP_LEFT`,
  `BOTTOM_RIGHT`, etc.
- Make it blink instead of staying fixed: in `render()` you can alternate
  the color based on `System.currentTimeMillis() % 1000 < 500`.
- Change the idle text or color: `IdleBoxInfoBox.getText()` /
  `getTextColor()`.
