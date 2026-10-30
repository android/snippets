# Compose Interactive Previews (`preview/`)

This directory contains the tooling that powers interactive Jetpack Compose Material 3 component previews and static 16:9 preview screenshots for `developer.android.com` (DAC).

## Architecture

```
preview/
├── serve.py                 # Local dev server for WASM
├── generator/               # Roborazzi + Robolectric 16:9 screenshot generator
│   ├── build.gradle.kts
│   ├── generate_preview_images.py
│   └── src/test/java/com/example/compose/preview/generator/
│       └── PreviewScreenshotTest.kt
└── wasm/                    # Compose Multiplatform (wasmJs) interactive runner
    ├── build.gradle.kts
    └── src/wasmJsMain/
        ├── kotlin/com/example/compose/preview/wasm/
        │   ├── main.kt
        │   ├── WasmPreviewApp.kt
        │   ├── navigation/UrlNavigation.kt
        │   ├── registry/SnippetRegistry.kt
        │   └── theme/Theme.kt
        └── resources/
            ├── wasm.html
            └── js-joda-core.mjs
```

### Single Source of Truth (`SnippetRegistry.kt`)

[`SnippetRegistry.kt`](wasm/src/wasmJsMain/kotlin/com/example/compose/preview/wasm/registry/SnippetRegistry.kt) maps each DAC snippet region tag (for example, `android_compose_components_filledbutton`) to its `@Composable` preview lambda. Both the WASM interactive runner (`WasmPreviewApp.kt`) and the Roborazzi screenshot test (`PreviewScreenshotTest.kt`) read directly from `SnippetRegistry.snippets` and wrap previews in `MaterialExpressiveTheme`.

## Common Workflows

### 1. Run the Interactive WASM Preview Locally

```bash
python3 preview/serve.py
```

Options:
- `--prod`: Build the optimized production WASM bundle (`:preview:wasm:packageStaticSite`) instead of the fast development bundle.
- `--no-build`: Serve existing build output in `preview/wasm/build/dist/site` without recompiling.
- `--generate-images`: Run Roborazzi screenshot generation before starting the server.

Once running, open `http://localhost:8090/wasm.html?id=android_compose_components_filledbutton&theme=light&preset=monochrome`.

### 2. Generate 16:9 Static Preview Screenshots

```bash
./gradlew :preview:generator:recordRoborazziDebug
# or
python3 preview/generator/generate_preview_images.py
```

Screenshots are rendered at `384x216 dp` (`xhdpi` -> `768x432 px`, 16:9 aspect ratio) using the monochrome Material 3 Expressive light theme and saved to `preview/wasm/src/wasmJsMain/resources/screenshots/<snippet_id>.png`.

### 3. Build Production Static Site Bundle

```bash
./gradlew :preview:wasm:packageStaticSite
```

Outputs the self-contained WASM bundle (`wasm.html`, `snippets-preview-wasm.mjs`, `*.wasm`, `skiko.mjs`, `skiko.wasm`, `fonts/`, `js-joda-core.mjs`) to `preview/wasm/build/dist/site/`.

## Writing snippets in `compose/snippets/.../components`

The WASM runner compiles **every** Kotlin file in `compose/snippets/src/main/java/com/example/compose/snippets/components/` (the `pullSnippets` task in [`wasm/build.gradle.kts`](wasm/build.gradle.kts)), not just the ones registered in `SnippetRegistry`. A new or edited file in that folder must therefore compile for Kotlin/Wasm, or the preview build and deploy fail:

- Use multiplatform Compose and Kotlin APIs. Android-only APIs (`android.*`, `java.text.SimpleDateFormat`, `java.util.Calendar`, `LocalContext`-based resources, and so on) don't compile for Wasm.
- `R.drawable.*`, `R.string.*`, `painterResource()` and `stringResource()` are supported: `generateSnippetResources` copies the referenced resources into Compose Multiplatform resources and generates a matching `R`.
- If a file can't be made multiplatform, add it to the `exclude(...)` list in `pullSnippets`. Its snippets then can't have interactive previews.

To add an interactive preview, register the snippet's region tag in `SnippetRegistry.snippets`, calling the composable inside that region (or a thin wrapper that only supplies its parameters). Add a `SnippetPreviewFrames` entry if it doesn't fit the default 384x216 dp frame, then regenerate the screenshots.
