# Compose Snippets Web Preview & WASM Runner

A static preview catalog and interactive WebAssembly (WASM) runner for Jetpack Compose snippets in this repository.

Built to run and preview Material Design 3 composables directly in any modern browser, deployed to GitHub Pages without modifying the Android Compose dependencies or downgrading Compose versions in `:compose:snippets`.

---

## Features

- **Dedicated Component Documentation Pages (`components/<id>.html`)**:
  - Separate dedicated page for each of the 25 Material 3 composables matching **developer.android.com** branding.
  - Comprehensive documentation:
    - **Overview & Description**: Role and visual style of the composable.
    - **When to Use**: Specific scenarios, comparisons with alternatives, and Material Design guidelines.
    - **How to Use**: Practical instructions, state management, and accessibility considerations.
    - **Key API Parameters**: Detailed parameters table (types, default values, callbacks).
    - **Real Code Snippets**: Extracted directly from `compose/snippets/src/main/java/com/example/compose/snippets/components/<File>.kt` (using the official `// [START ...]` tags).
    - **Direct GitHub Repository Mapping**: Explicit banner stating: *"Part of the android/snippets repository on GitHub"* linking directly to the source file in `github.com/android/snippets`.
  - **Embedded Live WASM Runner**:
    - Embedded directly into the page flow (no modal dialogs!).
    - Fully interactive Compose canvas with reload, fullscreen, and light/dark theme synchronization.
  - **Sequential Navigation**: Next / Previous buttons to navigate across all components.

- **Static Catalog Website (`index.html`)**:
  - Live search and filter by category or component name.
  - Light and Dark theme toggle adhering to the **Android Green & Blue** palette.
  - Direct links to dedicated component pages.
  - Developer.android.com branding with Android robot icon and repository link.

- **Interactive WASM Runner (`wasm.html`)**:
  - Built with **Compose Multiplatform 1.12.0 for WebAssembly (WASM-JS)**.
  - Runs full Compose UI rendering natively on HTML5 Canvas via Skiko WebAssembly.
  - Supports dynamic theme toggling and single-snippet isolation.

- **Android Green & Blue Theming**:
  - Replaces default purple colors with official Android branding colors:
    - **Android Green**: Primary `#3DDC84`, Container `#00522A` / `#D4F7DF`, OnPrimary `#00391C`
    - **Android Blue**: Secondary `#4285F4`, Container `#004975` / `#D1E4FF`, OnSecondary `#003253`
  - Consistent across both Android app `:compose:snippets` and Compose WASM `:preview:wasm`.

- **Direct Extraction from `:compose:snippets`**:
  - Snippets are dynamically extracted directly from `compose/snippets/src/main/java/com/example/compose/snippets/components/*.kt` using the repository's `// [START ...]` and `// [END ...]` documentation tags. No synthetic or generated code snippets are used.

- **Screenshot Generation on Merge**:
  - Integration script (`preview/generator/scripts/run-preview-daemon.sh`) supporting [compose-preview-daemon](https://github.com/yschimke/compose-preview-daemon) / `render-cli` with an automated fallback generator and component page generator.
  - Automated CI deployment via GitHub Actions (`.github/workflows/deploy-preview-pages.yml`).

---

## Component Catalog (25 Snippets Across 6 Categories)

| Category | Snippets |
| :--- | :--- |
| **Buttons & Actions** | Button Examples (Filled, Elevated, Tonal, Outlined, Text), Floating Action Button (FAB, Small, Large, Extended), Segmented Buttons (Single & Multi-Select) |
| **Containment & Sheets** | Card Examples (Filled, Elevated, Outlined), Dialog Examples (Alert Dialog, Basic Dialog), Bottom Sheet (Modal Bottom Sheet), Dividers & Scaffold |
| **Navigation** | App Bars (Small, Center-Aligned, Medium, Large), Navigation Bar & Rail, Navigation Drawer (Modal Drawer) |
| **Selection & Inputs** | Checkbox Examples (Standard & Tri-State), Switch Examples, Radio Button, Slider Examples (Continuous, Steps, Range), Chip Examples (Assist, Filter, Input, Suggestion), Date Picker, Time Picker |
| **Feedback & Communication** | Badge Examples, Progress Indicators (Linear, Circular, Indeterminate), Tooltip Examples (Plain & Rich) |
| **Lists & Menus** | Search Bar (Docked & Expanded), Menu Examples (Dropdown & Exposed), Carousel (Horizontal Multi-Browse), Swipe to Dismiss (SwipeToDismissBox) |

---

## Architecture

```
snippets/
├── compose/snippets/                  # Unaltered Android Compose module (Android Compose BOM)
│   └── src/main/java/.../ui/theme/    # Updated with Android Green & Blue palette
├── preview/
│   ├── wasm/                          # Compose Multiplatform WASM-JS module
│   │   ├── src/wasmJsMain/kotlin/     # WASM Compose App & 25 Component Snippet implementations
│   │   │   └── .../preview/wasm/
│   │   │       ├── model/             # Category and Snippet data structures
│   │   │       ├── registry/          # SnippetRegistry mapping IDs to Composables & Code
│   │   │       ├── snippets/          # Categorized Material 3 Composable Snippets
│   │   │       ├── theme/             # Android Green & Blue Theme for Compose WASM
│   │   │       └── WasmPreviewApp.kt  # Root WASM Application with routing & controls
│   │   └── src/wasmJsMain/resources/  # Static web assets
│   │       ├── index.html             # Catalog UI (Search, Filter, Modal, Theme Toggle)
│   │       ├── wasm.html              # Dedicated WASM Canvas Runner
│   │       └── screenshots/           # 25 Preview PNGs
│   ├── generator/                     # Preview screenshot generator & daemon integration
│   │   ├── generate_preview_images.py # Standalone screenshot generator
│   │   └── scripts/run-preview-daemon.sh # compose-preview-daemon / CI entrypoint
│   └── README.md
└── .github/workflows/
    └── deploy-preview-pages.yml       # GitHub Actions workflow for building & deploying to GitHub Pages
```

---

## Local Development & Testing

### 1. Build and Package Static Website
Run the packaging task to compile the Kotlin WASM binary and assemble all static assets:

```bash
./gradlew :preview:wasm:packageStaticSite
```

This generates the complete static website bundle at:
```
preview/wasm/build/dist/site/
├── index.html
├── wasm.html
├── skiko.wasm
├── skiko.mjs
├── snippets-preview-wasm.wasm
├── snippets-preview-wasm.mjs
├── snippets-preview-wasm.import-object.mjs
├── custom-formatters.js
└── screenshots/
    └── *.png (25 component preview images)
```

### 2. Preview Locally
Serve the static bundle with any local HTTP server:

```bash
cd preview/wasm/build/dist/site
python3 -m http.server 8000
```

Then open [http://localhost:8000](http://localhost:8000) in your browser.

### 3. Generate Screenshots
To re-generate or update preview screenshots:

```bash
./preview/generator/scripts/run-preview-daemon.sh
```

---

## GitHub Actions & GitHub Pages Deployment

The repository includes an automated workflow at [deploy-preview-pages.yml](file:///.github/workflows/deploy-preview-pages.yml):
1. Runs automatically on `push` to `main` (and verifies builds on PRs).
2. Sets up Java 25 and Python.
3. Generates preview screenshots via `./preview/generator/scripts/run-preview-daemon.sh`.
4. Compiles the WASM app and packages the static site via `./gradlew :preview:wasm:packageStaticSite`.
5. Deploys the static bundle in `preview/wasm/build/dist/site` directly to GitHub Pages.
