#!/usr/bin/env bash
set -euo pipefail
echo "Scanning for accidental Git LFS pointer .so files in jniLibs..."
if grep -R "version https://git-lfs" terminal-emulator/src/main/jniLibs 2>/dev/null; then
  echo "ERROR: Found LFS pointer content in jniLibs .so files." >&2
  exit 1
fi
echo "OK: No LFS pointer .so files present." 
