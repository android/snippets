#!/usr/bin/env python3
"""
Generates high-fidelity preview screenshots for Jetpack Compose Material 3 snippets
using Roborazzi / Robolectric Native Graphics.
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


def generate_with_roborazzi():
    """Generates preview screenshots using Roborazzi / Robolectric Native Graphics."""
    print("Generating preview screenshots using Roborazzi / Robolectric Native Graphics...")
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
    print("Compose Preview Screenshot Generator (Roborazzi)")
    print("=" * 60)

    if generate_with_roborazzi():
        print("Successfully generated screenshots via Roborazzi.")
        sync_screenshots_to_dist()
    else:
        print("Error: Failed to generate screenshots with Roborazzi.", file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()
