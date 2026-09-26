# Jetpack Compose Interactive WASM Previews & Roborazzi Screenshot Generator

Generates static Roborazzi screenshots (`screenshots/<region_tag>.png`) and a **Compose Multiplatform WebAssembly (WASM-JS)** interactive runner (`wasm.html#/snippet/<region_tag>`) for embedding directly into `developer.android.com` (DAC) via the `macros.compose_snippet` macro.

---

## Key Design Principles

1. **Single Canonical Identifier (`region_tag`)**:
   - Every snippet is keyed by its existing `// [START <region_tag>]` identifier (for example, `android_compose_components_filledbutton`).
   - Roborazzi screenshots are output as `screenshots/<region_tag>.png`.
   - The interactive WASM runner loads any snippet via `wasm.html#/snippet/<region_tag>` (or `?snippet=<region_tag>`).
2. **No Duplicate Static HTML Pages**:
   - Documentation pages, navigation, and code blocks live on `developer.android.com` (DAC).
   - This module only builds the **Roborazzi static PNGs** and the **embeddable WASM runner** (`wasm.html` + `.mjs` + `.wasm`).

---

## Directory Layout

```
snippets/
├── compose/snippets/                          # Android Jetpack Compose snippets module
│   └── src/main/java/.../snippets/components/ # Canonical snippet Kotlin files with [START]/[END] tags
│
├── preview/
│   ├── README.md                              # This documentation
│   ├── serve.py                               # Local development HTTP server (127.0.0.1)
│   │
│   ├── generator/                             # Roborazzi screenshot test & region_tag code extractor
│   │   ├── build.gradle.kts                   # Android library + Roborazzi/Robolectric configuration
│   │   ├── config.py                          # Source and output paths
│   │   ├── generate_preview_images.py         # Runs :preview:generator:recordRoborazziDebug
│   │   ├── snippet_extractor.py               # Scans [START <region_tag>] blocks -> SnippetCodeMap.kt
│   │   ├── scripts/run-preview-daemon.sh      # CI entrypoint for screenshot + code map generation
│   │   └── src/test/.../PreviewScreenshotTest.kt # Roborazzi screenshot tests keyed by region_tag
│   │
│   └── wasm/                                  # Compose Multiplatform WASM-JS interactive runner
│       ├── build.gradle.kts                   # Kotlin Multiplatform WASM build script
│       └── src/wasmJsMain/
│           ├── kotlin/.../preview/wasm/
│           │   ├── WasmPreviewApp.kt          # Root WASM Composable (routing & theme sync)
│           │   ├── main.kt                    # Canvas initialization entrypoint
│           │   ├── navigation/UrlNavigation.kt# URL parameters, hash routing, density scale
│           │   ├── registry/                  # SnippetRegistry & categorized Composable providers
│           │   └── ui/                        # Embedded canvas view & standalone code/theme inspector
│           └── resources/
│               ├── wasm.html                  # Embeddable Skiko WASM canvas runner
│               └── screenshots/               # Generated Roborazzi PNGs (<region_tag>.png)
```

---

## How to Add a New Interactive Snippet

1. **Write the Composable Snippet in `compose/snippets`**:
   Wrap your `@Composable` function in `// [START <region_tag>]` and `// [END <region_tag>]` in `compose/snippets/src/main/java/com/example/compose/snippets/components/<Component>.kt`:
   ```kotlin
   // [START android_compose_components_filledbutton]
   @Composable
   fun FilledButtonExample(onClick: () -> Unit) {
       Button(onClick = { onClick() }) {
           Text("Filled")
       }
   }
   // [END android_compose_components_filledbutton]
   ```
2. **Register the `region_tag` in WASM & Roborazzi**:
   - Add a `ComponentSnippet(id = "android_compose_components_filledbutton", ...)` entry in `preview/wasm/src/wasmJsMain/kotlin/com/example/compose/preview/wasm/registry/`.
   - Add a `@Test` in `preview/generator/src/test/java/com/example/compose/preview/generator/PreviewScreenshotTest.kt` calling `captureComponent("android_compose_components_filledbutton") { ... }`.
3. **Embed in DAC (`developer.android.com`)**:
   In your DAC Markdown page, pass `interactive=True` (or `preview=True` for static-only) to `macros.compose_snippet`:
   ```jinja
   {{ macros.compose_snippet("android_compose_components_filledbutton", "components", "Button", interactive=True) }}
   ```

---

## Building & Running Locally

```bash
# 1. Generate Roborazzi screenshots (<region_tag>.png) and extract SnippetCodeMap.kt
./preview/generator/scripts/run-preview-daemon.sh

# 2. Build and package the WASM bundle into preview/wasm/build/dist/site
./gradlew :preview:wasm:packageDevelopmentSite

# 3. Serve locally on http://127.0.0.1:8000/wasm.html
python3 preview/serve.py 8000
```

