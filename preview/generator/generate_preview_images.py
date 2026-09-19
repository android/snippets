#!/usr/bin/env python3
"""
Generates high-fidelity preview screenshots for Jetpack Compose Material 3 snippets.
Uses LayoutLib (via Roborazzi / Robolectric Native Graphics) or compose-preview-daemon / render-cli.
Renders with the Android Green (#3DDC84 / #006D3B) and Android Blue (#4285F4 / #00639B) theme palette.
"""

import os
import shutil
import subprocess
import sys

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
ROOT_DIR = os.path.abspath(os.path.join(SCRIPT_DIR, "../.."))
OUTPUT_DIR = os.path.join(ROOT_DIR, "preview", "wasm", "src", "wasmJsMain", "resources", "screenshots")
DIST_SCREENSHOTS_DIR = os.path.join(ROOT_DIR, "preview", "wasm", "build", "dist", "site", "screenshots")

os.makedirs(OUTPUT_DIR, exist_ok=True)
if os.path.exists(os.path.dirname(DIST_SCREENSHOTS_DIR)):
    os.makedirs(DIST_SCREENSHOTS_DIR, exist_ok=True)

def generate_with_daemon():
    """Attempts to use yschimke/compose-preview-daemon or render-cli if available."""
    if shutil.which("compose-preview-daemon"):
        print("Using compose-preview-daemon...")
        res = subprocess.run(["compose-preview-daemon", "--output-dir", OUTPUT_DIR], cwd=ROOT_DIR)
        return res.returncode == 0
    elif shutil.which("render-cli"):
        print("Using render-cli...")
        res = subprocess.run(["render-cli", "--output-dir", OUTPUT_DIR], cwd=ROOT_DIR)
        return res.returncode == 0
    return False

def generate_with_layoutlib():
    """Generates preview screenshots using LayoutLib via Roborazzi / Robolectric Native Graphics."""
    print("Generating preview screenshots using LayoutLib (Roborazzi / Robolectric Native Graphics)...")
    gradle_cmd = os.path.join(ROOT_DIR, "gradlew")
    res = subprocess.run(
        [gradle_cmd, ":preview:generator:recordRoborazziDebug"],
        cwd=ROOT_DIR
    )
    return res.returncode == 0

def sync_screenshots_to_dist():
    """Copies generated screenshots to build/dist/site/screenshots if present."""
    if os.path.exists(DIST_SCREENSHOTS_DIR):
        for f in os.listdir(OUTPUT_DIR):
            if f.endswith(".png"):
                shutil.copy2(os.path.join(OUTPUT_DIR, f), os.path.join(DIST_SCREENSHOTS_DIR, f))
        print(f"Synced screenshots to {DIST_SCREENSHOTS_DIR}")

def main():
    print("=" * 60)
    print("Compose Preview Screenshot Generator (LayoutLib / Compose)")
    print("=" * 60)

    # 1. Check for compose-preview-daemon / render-cli (Yuri's daemon)
    if generate_with_daemon():
        print("Successfully generated screenshots via compose-preview-daemon.")
        sync_screenshots_to_dist()
        return

    # 2. Use LayoutLib (Roborazzi / Robolectric Native Graphics)
    success = generate_with_layoutlib()
    if success:
        print("Successfully generated screenshots via LayoutLib.")
        sync_screenshots_to_dist()
    else:
        print("Error: Failed to generate screenshots with LayoutLib.", file=sys.stderr)
        sys.exit(1)

if __name__ == "__main__":
    main()
