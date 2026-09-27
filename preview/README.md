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

[`SnippetRegistry.kt`](wasm/src/wasmJsMain/kotlin/com/example/compose/preview/wasm/registry/SnippetRegistry.kt) maps each DAC snippet region tag (`// [START android_compose_components_...]`) to its `@Composable` preview lambda. Both the WASM interactive runner (`WasmPreviewApp.kt`) and the Roborazzi screenshot test (`PreviewScreenshotTest.kt`) read directly from `SnippetRegistry.snippets` and wrap previews in `MaterialExpressiveTheme`.

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
./gradlew :preview:generator:testDebugUnitTest
# or
python3 preview/generator/generate_preview_images.py
```

Screenshots are rendered at `384x216 dp` (`xhdpi` -> `768x432 px`, 16:9 aspect ratio) using the monochrome Material 3 Expressive light theme and saved to `preview/wasm/src/wasmJsMain/resources/screenshots/<snippet_id>.png`.

### 3. Build Production Static Site Bundle

```bash
./gradlew :preview:wasm:packageStaticSite
```

Outputs the self-contained WASM bundle (`wasm.html`, `snippets-wasm-preview.js`, `*.wasm`, `skiko.mjs`, `skiko.wasm`, `js-joda-core.mjs`) to `preview/wasm/build/dist/site/`.
