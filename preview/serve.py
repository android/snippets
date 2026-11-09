#!/usr/bin/env python3
#
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
#
# Helper server script to serve the Compose Preview static site with proper MIME types
#
import argparse
import http.server
import mimetypes
import os
import subprocess
import sys

DEFAULT_PORT = 8090
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
ROOT_DIR = os.path.abspath(os.path.join(SCRIPT_DIR, ".."))
DIRECTORY = os.path.join(SCRIPT_DIR, "wasm/build/dist/site")

mimetypes.add_type("application/wasm", ".wasm")
mimetypes.add_type("text/javascript", ".mjs")
mimetypes.add_type("text/javascript", ".js")


class CustomHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=DIRECTORY, **kwargs)

    def log_message(self, format, *args):
        sys.stderr.write("%s - - [%s] %s\n" % (self.address_string(), self.log_date_time_string(), format % args))


def run_gradle(task):
    gradle_cmd = os.path.join(ROOT_DIR, "gradlew")
    subprocess.run([gradle_cmd, task], cwd=ROOT_DIR, check=True)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Build and serve the interactive WASM Compose preview site.")
    parser.add_argument("port", nargs="?", type=int, default=DEFAULT_PORT, help=f"Port to listen on (default: {DEFAULT_PORT})")
    parser.add_argument("--prod", action="store_true", help="Build the optimized production WASM bundle (:preview:wasm:packageStaticSite)")
    parser.add_argument("--no-build", action="store_true", help="Serve existing build output without recompiling")
    parser.add_argument("--generate-images", action="store_true", help="Run Roborazzi screenshot generation before starting the server")
    args = parser.parse_args()

    if args.generate_images:
        run_gradle(":preview:generator:recordRoborazziDebug")

    if not args.no_build:
        task = ":preview:wasm:packageStaticSite" if args.prod else ":preview:wasm:packageDevelopmentSite"
        run_gradle(task)

    server = http.server.ThreadingHTTPServer(("127.0.0.1", args.port), CustomHandler)
    print(f"Serving Compose Preview site at http://127.0.0.1:{args.port}/wasm.html from {DIRECTORY}", flush=True)
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        pass
