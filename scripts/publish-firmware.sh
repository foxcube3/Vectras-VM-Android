#!/usr/bin/env bash
# Helper script to publish firmware (QEMU_VARS.img) as a GitHub Release asset.
# Usage:
#   ./scripts/publish-firmware.sh <tag> <path-to-QEMU_VARS.img>
# Requires: gh CLI authenticated with repo write rights.
set -euo pipefail

if [ $# -ne 2 ]; then
  echo "Usage: $0 <tag> <path-to-QEMU_VARS.img>" >&2
  exit 1
fi
TAG="$1"
FILE="$2"

if [ ! -f "$FILE" ]; then
  echo "File not found: $FILE" >&2
  exit 1
fi

SHA256=$(sha256sum "$FILE" | awk '{print $1}')
SIZE=$(stat -c %s "$FILE")

# Create release if not exists
if ! gh release view "$TAG" >/dev/null 2>&1; then
  gh release create "$TAG" -t "Firmware $TAG" -n "Automated firmware publish for tag $TAG\n\nSHA256: $SHA256\nSize: $SIZE bytes" --generate-notes
fi

echo "Uploading asset..."
ASSET_NAME=$(basename "$FILE")
# Delete existing asset with same name if present
if gh release view "$TAG" --json assets --jq ".assets[].name" | grep -q "^${ASSET_NAME}$"; then
  echo "Asset ${ASSET_NAME} exists; deleting prior version..."
  gh release delete-asset "$TAG" "$ASSET_NAME" -y
fi

gh release upload "$TAG" "$FILE" --clobber

echo "Done. SHA256: $SHA256"