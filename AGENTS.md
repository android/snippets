# Agent instructions for android/snippets

Code in this repository is shown to developers on developer.android.com, and
readers copy it into their apps. Follow these rules when you add or change
snippets.

## Icons

- **Never use the Material Icons libraries.** Don't write `Icons.Default.*`,
  `Icons.Filled.*`, `Icons.Outlined.*`, `Icons.Rounded.*`, `Icons.Sharp.*`,
  `Icons.TwoTone.*` or `Icons.AutoMirrored.*`. Don't add
  `androidx.compose.material:material-icons-core`,
  `androidx.compose.material:material-icons-extended`, or
  `org.jetbrains.compose.material:material-icons-*` dependencies. These
  libraries are deprecated.
- **Use `AppIcons` instead.** In `compose/snippets`, use the icons in
  [`AppIcons.kt`](compose/snippets/src/main/java/com/example/compose/snippets/components/AppIcons.kt).
  In other modules, define the icon as an `ImageVector` (or a vector drawable)
  inside that module.
- **Add missing icons; don't substitute.** If `AppIcons` doesn't have the icon
  you need, add it with the official
  [Material Symbols](https://fonts.google.com/icons) path data. Use the same
  `960x960` viewport and style as the existing entries. Don't swap in a
  different-looking icon just because it already exists.
- **Migrate when you touch existing code.** Some older snippets still use
  `Icons.Default`. When you change one of those snippets, switch its icons to
  `AppIcons` in the same change.
