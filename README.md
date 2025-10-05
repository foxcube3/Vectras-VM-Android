<p align="center">
  <img src="resources/vectrasvm.png" style="width: 30%;" />
</p>

# Vectras VM
[![Ceasefire Now](https://badge.techforpalestine.org/default)](https://techforpalestine.org/learn-more)

![GitHub Repo stars](https://img.shields.io/github/stars/xoureldeen/Vectras-VM-Android)
![GitHub watchers](https://img.shields.io/github/watchers/xoureldeen/Vectras-VM-Android)
![GitHub forks](https://img.shields.io/github/forks/xoureldeen/Vectras-VM-Android)
[![Total downloads](https://img.shields.io/github/downloads/xoureldeen/Vectras-VM-Android/total)](https://github.com/xoureldeen/Vectras-VM-Android/releases)
[![Discord server](https://img.shields.io/discord/911060166810681345)][link-discord]
[![Telegram Channel][ico-telegram]][link-telegram]
[![Software License][ico-license]](LICENSE)

Welcome to Vectras VM! A virtual machine app for Android based on QEMU that lets you emulate various OSes including: [![Windows](https://custom-icon-badges.demolab.com/badge/Windows-0078D6?logo=windows11&logoColor=white)](https://www.microsoft.com/en-us/windows) [![Linux](https://img.shields.io/badge/Linux-FCC624?logo=linux&logoColor=black)](https://www.linux.org/) [![macOS](https://img.shields.io/badge/macOS-000000?logo=apple&logoColor=F0F0F0)](https://www.apple.com/macos) [![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)](https://www.android.com/).

If you need help, check out [our documentation](https://vectras.vercel.app/how.html). For quick answers, join the [Vectras Telegram group](http://t.me/vectras_vm_discussion).

[![Tutorial for beginners](https://img.youtube.com/vi/AlNbverd0xE/mqdefault.jpg)](https://www.youtube.com/watch?v=AlNbverd0xE)

## Device Compatibility

Works fine on devices manufactured in 2021 or later and devices equipped with Snapdragon 855 CPU or better. You can try running Vectras VM on unsupported devices, but we cannot guarantee stability or support. Here are the devices tested:

| Stable           | Unstable                                        |
| --------------- | ------------------------------------------- |
| Samsung      | Oppo      |
| Google Pixel      | Realme      |
| Xiaomi      | OnePlus      |
| Redmi      | Huawei      |
| Poco      | Honor      |
| ZTE      | vivo      |
| RedMagic      | IQOO      |

### Minimum System Requirements
- Android 6.0 and up.
- 3GB RAM (1GB of free RAM).
- A good processor.

### Recommended System Requirements
- Android 8.1 and up.
- 8GB RAM (3GB of free RAM).
- CPU and Android OS support 64-bit.
- Snapdragon 855 CPU or better.
- Integrated or removable cooling system (if running operating systems from 2010 to present).
> [!TIP]
> If the OS you are trying to emulate crashes, try using an older version.

# Installation

### Stable Releases

You can download Vectras VM from the [releases](https://github.com/xoureldeen/Vectras-VM-Android/releases) page or the [official website](https://vectras.vercel.app/download.html).

or


[![OpenAPK](https://img.shields.io/badge/Get%20it%20on-OpenAPK-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://www.openapk.net/vectras-vm/com.vectras.vm/)

### Beta Releases

We publish a **new beta release after every commit** — so you can always test the latest features and improvements!

[![Download Beta](https://img.shields.io/badge/Download-Beta-blue?style=for-the-badge&logo=github)](https://github.com/AnBui2004/Vectras-VM-Emu-Android/releases)

# Donate
Help support the project by contributing!

[![Buy Me A Coffee][ico-buymeacoffee]][link-buymeacoffee]
[![Buy Me a Coffee at ko-fi.com][ico-ko-fi]][link-ko-fi]
[![Support me on Patreon](https://img.shields.io/endpoint.svg?url=https%3A%2F%2Fshieldsio-patreon.vercel.app%2Fapi%3Fusername%3Dendel%26type%3Dpatrons&style=flat)](https://patreon.com/VectrasTeam)

# Thanks to
- [3DFX QEMU PATCH](https://github.com/kjliew/qemu-3dfx)
- [Alpine Linux](https://www.alpinelinux.org/)
- [Glide](https://github.com/bumptech/glide)
- [Gson](https://github.com/google/gson)
- [OkHttp](https://github.com/square/okhttp)
- [PROOT](https://proot-me.github.io/)
- [QEMU](https://github.com/qemu/qemu)
- [Termux](https://github.com/termux)

[ico-telegram]: https://img.shields.io/badge/Telegram-2CA5E0?logo=telegram&logoColor=white
[ico-discord]: https://img.shields.io/badge/Discord-%235865F2.svg?&logo=discord&logoColor=white
[ico-version]: https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white
[ico-license]: https://img.shields.io/badge/License-GPL_v2-blue.svg
[ico-buymeacoffee]: https://img.shields.io/badge/Buy%20Me%20a%20Coffee-ffdd00?&logo=buy-me-a-coffee&logoColor=black
[ico-ko-fi]: https://img.shields.io/badge/Ko--fi-FF5E5B?logo=ko-fi&logoColor=white

[link-discord]: https://discord.gg/t8TACrKSk7
[link-telegram]: https://t.me/vectras_os
[link-repo]: https://github.com/xoureldeen/Vectras-VM-Android/
[link-releases]: https://github.com/xoureldeen/Vectras-VM-Android/releases/
[link-buymeacoffee]: https://www.buymeacoffee.com/vectrasvm
[link-ko-fi]: https://ko-fi.com/vectrasvm

## Firmware / ROM Assets (QEMU_VARS.img)

Large firmware images are not committed directly to this fork to avoid Git LFS push restrictions on forks and to keep clone size small.

The build includes Gradle tasks to automatically fetch and verify `QEMU_VARS.img` into `app/src/main/assets/roms/` when missing:

Tasks:

- `:app:fetchFirmware` – Download (with retries, ETag caching, progress) if absent or forced.
- `:app:verifyFirmware` – Verify SHA-256 integrity (if an expected hash is provided).
- `:app:printFirmwareSha256` – Output current firmware hash (fetches first if needed).
- `:app:ciFirmwareCheck` – Aggregate (fetch + verify + print); useful in CI pipelines.
- `:app:fetchAllFirmware` – Parallel download of all defined firmware assets.
- `:app:ciAllFirmwareCheck` – Multi-asset aggregate verification.
- `:app:verifyFirmwareSignature` – GPG signature verification (if signature URL provided).
- `:app:ciAllFirmwareSecureCheck` – Multi-asset + hash + signature verification.

### Variants

You may host multiple firmware variants (e.g., debug, secure). Select via:

- Gradle property: `-PfirmwareVariant=debug`
- Env var: `QEMU_VARS_VARIANT=debug`

Default variant: `default`.

Files are stored as `QEMU_VARS-<variant>.img`.

### URL Configuration Precedence

1. Gradle property: `-PfirmwareUrl=https://.../QEMU_VARS-<variant>.img`
2. Env var: `QEMU_VARS_URL`
3. Fallback placeholder (will 404 until replaced)

### SHA-256 Integrity Verification

Provide the expected hash to enforce verification:

1. Gradle property: `-PfirmwareSha256=<hex>`
2. Env var: `QEMU_VARS_SHA256`

If omitted, verification logs a skip message. To skip only verification (even if hash supplied): add `-PskipFirmwareVerify`.

### Retry & Caching Logic

Retries: default 3 (configure `-PfirmwareAttempts=5` or env `QEMU_VARS_ATTEMPTS=5`).
Backoff: exponential (1s,2s,4s,8s ... capped at 30s). Progress logged ~every 5s.
Caching: ETag stored in a `.meta` file; future fetches can leverage conditional requests (future optimization path).

### Environment Examples

Linux/macOS:

```bash
export QEMU_VARS_URL="https://your.hosted.location/QEMU_VARS-default.img"
export QEMU_VARS_SHA256="<expected_sha256>"
./gradlew assembleDebug
```

PowerShell:

```powershell
$env:QEMU_VARS_URL = "https://your.hosted.location/QEMU_VARS-default.img"
$env:QEMU_VARS_SHA256 = "<expected_sha256>"
./gradlew.bat assembleDebug
```

Using Gradle properties instead of env vars:

```bash
./gradlew assembleDebug \
  -PfirmwareUrl=https://your.hosted.location/QEMU_VARS-default.img \
  -PfirmwareSha256=<expected_sha256> \
  -PfirmwareVariant=default
```

### Skipping Fetch

If CI or local workflows stage the file another way:

```bash
./gradlew assembleDebug -PskipFirmware
```

### Manual Invocation

### Firmware Configuration Matrix

| Purpose | Gradle Property | Environment Variable | Example |
|---------|-----------------|----------------------|---------|
| Primary firmware URL | `firmwareUrl` | `QEMU_VARS_URL` | `-PfirmwareUrl=https://downloads.example.com/firmware/QEMU_VARS-default.img` |
| Primary firmware SHA-256 | `firmwareSha256` | `QEMU_VARS_SHA256` | `-PfirmwareSha256=3d4c2...e9f0a` |
| Primary firmware signature URL (detached .sig) | `firmwareSigUrl` | `QEMU_VARS_SIG_URL` | `-PfirmwareSigUrl=https://downloads.example.com/firmware/QEMU_VARS-default.img.sig` |
| Variant selection | `firmwareVariant` | `QEMU_VARS_VARIANT` | `-PfirmwareVariant=debug` |
| Retry attempts | `firmwareAttempts` | `QEMU_VARS_ATTEMPTS` | `-PfirmwareAttempts=5` |
| Skip fetch (use pre-provided file) | `skipFirmware` (flag) | — | `-PskipFirmware` |
| Skip verification | `skipFirmwareVerify` (flag) | — | `-PskipFirmwareVerify` |
| Force re-download | `forceFirmware` (flag) | — | `-PforceFirmware` |
| Allow sample placeholder SHA (suppress warning) | `allowSampleFirmwareSha` (flag) | — | `-PallowSampleFirmwareSha` |
| Multi-asset URL override | `asset.<BASE>.url` | `<BASE>_URL` | `-Passet.RELEASEX64_OVMF_VARS.url=https://.../RELEASEX64_OVMF_VARS-default.fd` |
| Multi-asset SHA override | `asset.<BASE>.sha256` | `<BASE>_SHA256` | `-Passet.RELEASEX64_OVMF_VARS.sha256=<sha>` |

All hashes expected as lowercase hex. Flags are presence-based (value ignored).

Just fetch (default variant):

```bash
./gradlew :app:fetchFirmware
```

Fetch + verify specific variant:

```bash
./gradlew :app:verifyFirmware -PfirmwareVariant=debug -PfirmwareSha256=<expected_sha256>
```

Print hash:

```bash
./gradlew :app:printFirmwareSha256 -PfirmwareVariant=secure
```

CI helper:

```bash
./gradlew :app:ciFirmwareCheck -PfirmwareSha256=<expected_sha256>
```

### Verification Failure Handling

On mismatch the file is deleted and the build fails, preventing stale/incomplete firmware from shipping.

### Hosting Recommendations

- Upstream repository with Git LFS enabled.
- Separate "assets" repository (LFS) referenced by releases.
- Trusted CDN or object storage (S3, Cloudflare R2, etc.).

### Security Considerations

- Always pin a SHA-256 for production distributions.
- Optionally supply a detached GPG signature URL (`-PfirmwareSigUrl` or `QEMU_VARS_SIG_URL`) and run `:app:verifyFirmwareSignature`.
- Prefer HTTPS and stable, versioned URLs.
- Rotate / invalidate compromised assets by changing URL + hash.

#### Signature Verification (Optional)

1. Generate a detached signature for the exact firmware binary you intend to distribute:

  ```bash
  gpg --armor --detach-sign --output QEMU_VARS-default.img.sig QEMU_VARS-default.img
  ```

1. Host both `QEMU_VARS-default.img` and `QEMU_VARS-default.img.sig` (same directory recommended).

1. Provide the signature location via either:

- Environment: `QEMU_VARS_SIG_URL=https://downloads.example.com/firmware/QEMU_VARS-default.img.sig`
- Property: `-PfirmwareSigUrl=https://downloads.example.com/firmware/QEMU_VARS-default.img.sig`

1. Run (locally / CI):

  ```bash
  ./gradlew :app:verifyFirmwareSignature \
    -PfirmwareUrl=https://downloads.example.com/firmware/QEMU_VARS-default.img \
    -PfirmwareSha256=<real_sha256> \
    -PfirmwareSigUrl=https://downloads.example.com/firmware/QEMU_VARS-default.img.sig
  ```

1. Ensure the correct public key is imported in the CI environment (e.g., `gpg --import public-key.asc`).

If signature URL is absent the signature step is skipped gracefully.

### CI

[![History Size Estimation](https://github.com/foxcube3/Vectras-VM-Android/actions/workflows/history-size-estimation.yml/badge.svg)](../../actions/workflows/history-size-estimation.yml)

GitHub Actions workflow (`firmware-ci.yml`) runs variant matrix (default, debug) and a secure job with optional signature verification. Provide secrets:

- `QEMU_VARS_URL`, `QEMU_VARS_SHA256`, `QEMU_VARS_SIG_URL` (optional)
- `RELEASEX64_OVMF_VARS_URL`, `RELEASEX64_OVMF_VARS_SHA256`

If you maintain the upstream repository and want to host the file via Git LFS, add it there; forks will then reference the pointer without needing this fetch (you may later remove this mechanism or leave it as a fallback).

### Optional Repository History Rewrite Toolkit

This repository includes tooling (introduced in PR #1) to optionally purge legacy large binaries from Git history without committing new large objects:

- `scripts/history-rewrite-plan.md` – Procedure (git-filter-repo) with safety steps.
- `scripts/paths-to-remove.txt` – Canonical removal list.
- `scripts/run-history-rewrite.ps1` / `scripts/run-history-rewrite.sh` – Automation helpers (create safety branch/tag, run filter, manual force-push step).
- `scripts/estimate-post-rewrite-size.ps1` – Heuristic size impact estimator (may show zero if objects already pruned locally).

The rewrite is deferred until maintainers explicitly approve a force-push window. See PR template: `.github/PULL_REQUEST_TEMPLATE/history-rewrite-followup.md` for a structured follow-up.

### Preflight Hash Validation

Before updating a firmware SHA in secrets or Gradle properties, run a local preflight check to ensure the expected hash actually matches the remote asset:

PowerShell:

```powershell
pwsh ./scripts/verify-firmware-sha.ps1 -Url $env:QEMU_VARS_URL -ExpectedSha256 $env:QEMU_VARS_SHA256
```

Bash:

```bash
./scripts/verify-firmware-sha.sh "$QEMU_VARS_URL" "$QEMU_VARS_SHA256"
```

If this fails, do NOT commit the new hash—investigate the discrepancy (possible mirror sync delay, truncation, or incorrect provided value).
