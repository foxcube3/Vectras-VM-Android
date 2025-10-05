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

## Deprecated API Scan & Baseline

We enforce a zero baseline for a small set of deprecated Android API patterns (see README section "Deprecated API Modernization & Scan"). The task runs automatically during CI and locally when you invoke `check`.

Run manually:

```bash
./gradlew deprecatedApiScan -PskipFirmware
```

Fail build if any active findings (should normally be zero):

```bash
./gradlew deprecatedApiScan -PfailOnDeprecated
```

Update (accept) current findings into `deprecation-baseline.txt` (only do this with strong justification; include reasoning in your PR description):

```bash
./gradlew deprecatedApiScan -PupdateDeprecationBaseline
```

Emit machine-readable JSON report:

```bash
./gradlew deprecatedApiScan -PjsonReport
```

Include Kotlin sources (future-proof flag; only needed once Kotlin is added):

```bash
./gradlew deprecatedApiScan -PscanKotlin
```

Guidelines:

- Keep the baseline empty. Treat new matches as regressions to fix, not to baseline.
- If you must baseline (e.g., temporarily landing a large refactor in stages), explain clearly in the PR why and add a follow-up issue to remove it.
- Avoid adding broad patterns; keep regexes narrowly focused on actual deprecated constructs (constructors or signatures) to minimize noise.

Extending the scan:

1. Add a new regex in the `patterns` map in root `build.gradle`.
2. Run `./gradlew deprecatedApiScan -PjsonReport` and review the findings.
3. If legitimate usages exist that cannot be removed immediately, either fix them or (rarely) baseline them with justification.
4. Update README/this section if the scope of enforced deprecations changes.

If the task grows significantly consider migrating logic into a small `buildSrc` plugin module with unit tests (see TODO in root build file).

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
