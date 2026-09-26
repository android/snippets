#!/usr/bin/env bash
#
# Script to generate screenshots using Roborazzi
#

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/../../.." && pwd)"
OUTPUT_DIR="${ROOT_DIR}/preview/wasm/src/wasmJsMain/resources/screenshots"

mkdir -p "${OUTPUT_DIR}"

python3 "${SCRIPT_DIR}/../generate_preview_images.py"
echo "Screenshots generated in: ${OUTPUT_DIR}"
