#!/bin/bash
# Copyright 2026 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# ==============================================================================
# Wear OS Custom List Scrolling Video Recorder
# ==============================================================================
# Records the deterministic scrolling animation of `BoardingPassListSample`
# (defined in `CustomList.kt`) for the "Custom composables in lists" section
# on developer.android.com:
#
#   Page URL:
#     https://developer.android.com/training/wearables/compose/lists?version=3#custom-composables-in-lists
#
#   Target DevSite video file:
#     /images/wear/compose-tlc-custom.mp4
#     (Google3 path: third_party/devsite/android/en/images/wear/compose-tlc-custom.mp4)
#
# How to use:
#   1. Start a Wear OS emulator or connect a Wear OS device via ADB.
#   2. Deploy and display `BoardingPassListSamplePreview` (from `CustomList.kt`)
#      on the Wear OS screen.
#   3. Run this script:
#        ./record_wear.sh [optional_output_filename.mp4]
#
# How it works:
#   - Starts `adb shell screenrecord` in the background.
#   - Broadcasts `com.example.wear.ACTION_SCROLL` to `ScrollBroadcastReceiver`
#     inside `BoardingPassListSample`, triggering a smooth 10.5-second scroll
#     down and back up.
#   - Stops recording after 12 seconds and pulls the raw MP4 from the device.
#   - If `ffmpeg` is installed, automatically encodes the video with H.264
#     CRF 26 (`-preset veryslow -pix_fmt yuv420p -movflags +faststart -an`)
#     so the output file (~190 KB) is immediately ready for DevSite upload.
# ==============================================================================

set -e

ACTION="com.example.wear.ACTION_SCROLL"
DEVICE_PATH="/sdcard/wear_record_temp.mp4"
RAW_FILE="raw_compose_tlc_custom.mp4"
OUTPUT_FILE="${1:-compose-tlc-custom.mp4}"

echo "========================================="
echo " Preparing to record: $OUTPUT_FILE"
echo "========================================="

# Clean up any leftover temp file on the device
adb shell rm -f "$DEVICE_PATH"

echo "-> Starting screenrecord on device..."
adb shell screenrecord "$DEVICE_PATH" &
RECORD_PID=$!

# Give screenrecord a moment to initialize before starting the scroll
sleep 1

echo "-> Sending broadcast: $ACTION"
adb shell am broadcast -a "$ACTION"

echo "-> Recording for 12 seconds..."
sleep 12

echo "-> Stopping screenrecord..."
# Send SIGINT (2) to screenrecord on the device so it finalizes the MP4 header cleanly
adb shell pkill -l INT screenrecord
wait $RECORD_PID 2>/dev/null || true

# Give the device a moment to finalize writing the MP4 container
sleep 2

echo "-> Pulling file from device..."
adb pull "$DEVICE_PATH" "$RAW_FILE"

echo "-> Cleaning up device storage..."
adb shell rm -f "$DEVICE_PATH"

if command -v ffmpeg &> /dev/null; then
    echo "-> Compressing with ffmpeg for DevSite (H.264 CRF 26)..."
    ffmpeg -y -i "$RAW_FILE" \
        -c:v libx264 -preset veryslow -crf 26 \
        -pix_fmt yuv420p -movflags +faststart -an \
        "$OUTPUT_FILE" < /dev/null
    rm -f "$RAW_FILE"
else
    echo "-> Note: ffmpeg not found; saving uncompressed recording."
    mv "$RAW_FILE" "$OUTPUT_FILE"
fi

echo "========================================="
echo " Done! Saved to $OUTPUT_FILE ($(du -h "$OUTPUT_FILE" | cut -f1))"
echo "========================================="
