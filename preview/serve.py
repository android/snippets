#!/usr/bin/env python3
#
# Helper server script to serve the Compose Preview static site with proper MIME types & CORS headers
#
import http.server
import mimetypes
import os
import sys

PORT = 8000
DIRECTORY = os.path.abspath(os.path.join(os.path.dirname(__file__), "wasm/build/dist/site"))

mimetypes.add_type("application/wasm", ".wasm")
mimetypes.add_type("text/javascript", ".mjs")
mimetypes.add_type("text/javascript", ".js")

class CustomHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=DIRECTORY, **kwargs)

    def end_headers(self):
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Cross-Origin-Opener-Policy", "same-origin")
        self.send_header("Cross-Origin-Embedder-Policy", "credentialless")
        super().end_headers()

    def log_message(self, format, *args):
        # Keep logs concise
        sys.stderr.write("%s - - [%s] %s\n" % (self.address_string(), self.log_date_time_string(), format % args))

if __name__ == "__main__":
    if len(sys.argv) > 1:
        PORT = int(sys.argv[1])
    server = http.server.ThreadingHTTPServer(("0.0.0.0", PORT), CustomHandler)
    print(f"Serving Compose Preview site at http://localhost:{PORT} from {DIRECTORY}", flush=True)
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        pass
