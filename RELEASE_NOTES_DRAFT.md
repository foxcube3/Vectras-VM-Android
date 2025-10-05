# Release Notes Draft (Infrastructure & Firmware System Overhaul)

## Accessibility & Lint

- Added comprehensive contentDescription attributes for decorative / meaningful images.
- Introduced lint baseline to stabilize builds while improving incrementally.

## Secret & Hygiene

- Removed committed `.env` and expanded `.gitignore` for environment files.
- Cleansed repository of local build artifacts in tracked history (current state only).

## Large File Governance

- Added `.gitattributes` with LFS patterns for firmware and native libs (no large binaries committed in this fork).
- Implemented dynamic firmware fetch system (no binary storage in repo).

## Firmware Fetch & Verification

- Multi-asset parallel download support (QEMU_VARS + RELEASEX64_OVMF_VARS variants).
- SHA-256 integrity verification and printable hash task.
- ETag caching for conditional re-validation.
- Variant selection via property/env.
- Retries with exponential backoff & progress logging.
- Optional GPG signature download & verification.
- New configuration matrix and examples in README.
- Sample SHA guard & assertion task (`assertNoSampleFirmwareSha`).
- Composite quality gate task (`firmwareQualityGate`).

## Build & Dependency Management

- Migrated dependencies to Gradle Version Catalog.

## CI Enhancements

- Firmware matrix workflow (`firmware-ci.yml`) with secure job.
- Added history size estimation workflow (scheduled + manual) producing artifact reports.
- Quality gate integration (assert sample SHA not shipped).

## History Rewrite Toolkit (Optional)

- Scripts for estimation, impact report, and safe git-filter-repo execution (not executed automatically).
- PR template & checklist for controlled force-push planning.

## Documentation

- README overhaul: firmware section, config matrix, signature instructions, examples, toolkit description, workflow badge.
- HISTORY_IMPACT_REPORT.md added for transparency of large object footprint.

## Follow-Up Items (If Needed)

- Provide production firmware URLs, hashes, signatures.
- Decide on executing history rewrite vs. deferring.
- Upstream hosting integration; potential removal of fallback mechanism later.

---
Generated draft – refine wording & add tangible version numbers before tagging a release.
