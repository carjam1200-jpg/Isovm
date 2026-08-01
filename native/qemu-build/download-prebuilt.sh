#!/usr/bin/env bash
# download-prebuilt.sh - placeholder to fetch prebuilt QEMU binaries if available
# This script does not point to any particular prebuilt; adjust URL to your artifact storage.

set -euo pipefail

OUTPUT_DIR=${1:-$(pwd)/output}
mkdir -p "$OUTPUT_DIR"

# Example: fetch a prebuilt tarball from a release server
# PREBUILT_URL="https://example.com/qemu-prebuilt-arm64.tar.xz"
# curl -L "$PREBUILT_URL" -o "$OUTPUT_DIR/qemu-prebuilt.tar.xz"
# tar -xf "$OUTPUT_DIR/qemu-prebuilt.tar.xz" -C "$OUTPUT_DIR"

echo "No prebuilt URL configured. Edit this script to point to your prebuilt QEMU binaries or CI artifacts. Output dir: $OUTPUT_DIR"
