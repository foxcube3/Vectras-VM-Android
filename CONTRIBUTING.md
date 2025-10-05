# Contributing

Thanks for your interest in improving Vectras VM!

## Quick Local Build

1. Install the Android SDK (ensure command line tools & required platforms).  
2. Provide the SDK path via either:
   - `local.properties` file:

     ```properties
     sdk.dir=C:\\Android\\Sdk
     ```

   - OR environment variable `ANDROID_HOME` (and optionally `ANDROID_SDK_ROOT`).
3. (Optional) Skip firmware fetch while working on UI/logic:

   ```bash
   ./gradlew assembleDebug -PskipFirmware
   ```

   When ready to exercise firmware logic, remove `-PskipFirmware` and set the appropriate env vars or Gradle properties (`QEMU_VARS_URL`, `QEMU_VARS_SHA256`, etc.).

## Firmware Tasks Overview
See `README.md` section: Firmware / ROM Assets.

## Lint Baseline
A lint baseline (`lint-baseline.xml`) is used to prevent existing warnings from failing builds. Fix issues gradually; regenerate with:

```bash
./gradlew :app:lintDebug --continue
```

If you intentionally remove issues and want the baseline updated:

```bash
./gradlew updateLintBaseline
```

## Pull Requests
* Keep PRs focused (one logical change set).
* Ensure `./gradlew assembleDebug -PskipFirmware` succeeds locally.
* If adding new large binary assets, prefer external hosting + fetch tasks instead of committing them.

## Environment Verification Script
Use `scripts/check-env.ps1` (Windows PowerShell) or `scripts/check-env.sh` (Unix) to quickly validate that required tools and SDK are present before a build.

## Code Style
Follow existing formatting. Avoid large unrelated reformatting diffs.

## Security / Secrets
Never commit credentials or `.env` contents. Add new secret patterns to `.gitignore` as needed.

## Questions?
Open a discussion or join the Telegram/Discord communities linked in the README.
