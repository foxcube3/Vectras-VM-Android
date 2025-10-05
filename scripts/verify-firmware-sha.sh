#!/usr/bin/env bash
# Preflight firmware asset SHA256 verification script.
# Usage: ./scripts/verify-firmware-sha.sh <url> <expected-sha256>
set -euo pipefail
URL="${1:?First argument URL required}"
EXPECTED="${2:?Second argument expected SHA256 required}" 

echo "== Preflight Firmware SHA Verification =="
echo "URL: $URL"
tmp=$(mktemp)
trap 'rm -f "$tmp"' EXIT
curl -fsSL "$URL" -o "$tmp"
ACTUAL=$(sha256sum "$tmp" | awk '{print $1}')
if [[ "${ACTUAL,,}" != "${EXPECTED,,}" ]]; then
  echo "Expected: $EXPECTED" >&2
  echo "Actual:   $ACTUAL" >&2
  exit 1
fi
echo "Hash OK: $ACTUAL"