# NAZMUSA Launcher

A custom Android home-screen launcher: 6 built-in themes, 6 fonts, 3 icon shapes,
adjustable grid density, a swipe-up app drawer with live search, and a customizable
dock — more visual personalization than iOS's fixed grid allows.

## Features

- **Home screen**: live clock/date, swipeable multi-page app grid, bottom dock
- **App drawer**: swipe up anywhere on the home screen to open a searchable, full
  alphabetical list of every installed app
- **Dock**: long-press any app (on the home grid or in the drawer) to pin it to the
  bottom dock, up to 5 apps
- **Themes**: Light, Dark, Sunset, Ocean, Neon, Pastel — long-press the home screen
  background to open Settings and switch instantly
- **Fonts**: Sans, Serif, Monospace, Light, Condensed, Casual — applied across the
  clock, app labels, and menus
- **Icon shapes**: Circle, Rounded Square, Square — reshapes every app icon live
- **Grid density**: choose 3, 4, or 5 columns per home page
- Registered as a real HOME launcher (`category.HOME`), so Android will offer it
  in the "Select Home App" chooser

## Opening the project

1. Open **Android Studio** (Hedgehog or newer recommended).
2. `File > Open`, select the `NAZMUSALauncher` folder.
3. Let Gradle sync — Android Studio will auto-generate the Gradle wrapper if it's
   missing since the wrapper jar isn't bundled here (binary file). If prompted,
   click "Create Gradle Wrapper".
4. Run on a device or emulator (**API 26+**). When you first launch it, Android
   will ask whether to use NAZMUSA as your home app.

## Project structure

```
app/src/main/java/com/nazmusa/launcher/
  MainActivity.kt        - home screen, gestures, dock, page setup
  AppDrawerFragment.kt   - searchable all-apps list
  SettingsDialog.kt       - theme/font/shape/columns picker (long-press home to open)
  AppRepository.kt        - queries installed apps via PackageManager
  AppAdapter.kt            - shared RecyclerView adapter for grid/dock/drawer
  HomePagerAdapter.kt      - paginates apps across swipeable home screens
  ThemeManager.kt / FontManager.kt / IconShapeUtils.kt / PrefsManager.kt
app/src/main/res/
  values/themes.xml       - the 6 color themes
  layout/                 - home, drawer, dock, item, and settings layouts
```

## Extending it further

- Add real widgets: embed `AppWidgetHostView` for a clock/weather widget on the
  home page.
- Add wallpaper picking: use `WallpaperManager` + `ACTION_SET_WALLPAPER`.
- Add icon packs: parse third-party icon-pack APKs' `appfilter.xml`.
- Add more themes: just add an entry to `colors.xml`, `themes.xml`, and
  `ThemeManager.themes`.
- Add downloadable Google Fonts: swap `FontManager` to use
  `androidx.core.provider.FontRequest` (requires network + a signed release key).

Because I can't compile or test this from within this environment, do a full
Gradle sync and a test run on a device/emulator before relying on it as your
daily launcher.
