#!/usr/bin/env bash
#
# Script to generate screenshots on merge using compose-preview-daemon or Roborazzi
# Reference: https://github.com/yschimke/compose-preview-daemon
#

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/../../.." && pwd)"
OUTPUT_DIR="${ROOT_DIR}/preview/wasm/src/wasmJsMain/resources/screenshots"

mkdir -p "${OUTPUT_DIR}"

echo "=========================================================="
echo "Compose Preview Generator"
echo "=========================================================="

# Check if compose-preview-daemon is available locally or in PATH
if command -v compose-preview-daemon &> /dev/null; then
    echo "Found compose-preview-daemon. Starting warm daemon..."
    # Start daemon in background if not already running
    if ! pgrep -f "compose-preview-daemon" > /dev/null; then
        compose-preview-daemon --port 8080 &
        DAEMON_PID=$!
        trap "kill ${DAEMON_PID} 2>/dev/null || true" EXIT
        sleep 2
    fi

    echo "Requesting screenshots from daemon..."
    # Connect via JSON-RPC or CLI client to render previews
    python3 "${SCRIPT_DIR}/../generate_preview_images.py"
elif command -v render-cli &> /dev/null; then
    echo "Found render-cli (ee.schimke.composeai). Rendering previews..."
    python3 "${SCRIPT_DIR}/../generate_preview_images.py"
else
    echo "compose-preview-daemon not detected in PATH."
    echo "Falling back to local screenshot generator..."
    python3 "${SCRIPT_DIR}/../generate_preview_images.py"
fi

echo "Screenshots generated in: ${OUTPUT_DIR}"

echo "Generating dedicated component documentation pages using snippets from compose/snippets..."
python3 "${SCRIPT_DIR}/../generate_component_pages.py"