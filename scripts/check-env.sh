#!/usr/bin/env bash
set -euo pipefail

echo "[check-env] Starting environment verification..."
errors=()

# SDK detection
sdk_dir=""
if [[ -f local.properties ]]; then
  line=$(grep -E '^sdk.dir=' local.properties || true)
  if [[ -n "$line" ]]; then
    sdk_dir=${line#sdk.dir=}
  fi
fi
if [[ -z "$sdk_dir" && -n "${ANDROID_HOME:-}" ]]; then sdk_dir=$ANDROID_HOME; fi
if [[ -z "$sdk_dir" && -n "${ANDROID_SDK_ROOT:-}" ]]; then sdk_dir=$ANDROID_SDK_ROOT; fi

if [[ -z "$sdk_dir" ]]; then
  errors+=("Android SDK path not found (no local.properties sdk.dir and no ANDROID_HOME / ANDROID_SDK_ROOT).")
elif [[ ! -d "$sdk_dir" ]]; then
  errors+=("Android SDK directory does not exist: $sdk_dir")
else
  echo "[check-env] SDK directory: $sdk_dir"
fi

# Java detection
if command -v java >/dev/null 2>&1; then
  echo "[check-env] $(java -version 2>&1 | head -n1)"
else
  errors+=("Java (JDK) not found on PATH.")
fi

# Gradle wrapper
if [[ -f gradlew || -f gradlew.bat ]]; then
  echo "[check-env] Gradle wrapper present."
else
  errors+=("Gradle wrapper missing (gradlew).");
fi

# Optional: gpg
if command -v gpg >/dev/null 2>&1; then
  echo "[check-env] gpg found."
else
  echo "[check-env] gpg not found (signature verification tasks will be skipped)."
fi

if (( ${#errors[@]} > 0 )); then
  echo "[check-env] FAIL" >&2
  for e in "${errors[@]}"; do echo " - $e" >&2; done
  exit 1
else
  echo "[check-env] All required environment checks passed."
fi
