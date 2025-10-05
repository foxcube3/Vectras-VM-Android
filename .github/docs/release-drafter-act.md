# Local Dry Run of Release Drafter with `act`

This document shows how to simulate the Release Drafter workflow locally using [`act`](https://github.com/nektos/act).

## 1. Install `act`

On Windows (PowerShell):

```powershell
choco install act-cli -y
```

Or see the act README for alternatives.

## 2. Provide a GitHub Token (optional)

For a pure dry run you can use a dummy token. To exercise more API calls create a classic PAT with `repo` scope and export it:

```powershell
$Env:GITHUB_TOKEN="ghp_yourtoken"
```

If unset, `act` injects a placeholder token; Release Drafter will run in reduced mode.

## 3. Sample Event Payload

A sample merged PR event is provided at:

```text
.github/events/pull_request_closed_merged.json
```

It includes a `release` label which will trigger publish mode.

## 4. Run the Workflow (Draft Only)

To force draft mode regardless of labels:

```bash
act pull_request -e .github/events/pull_request_closed_merged.json -W .github/workflows/release-drafter.yml --container-architecture linux/amd64 --env INPUT_PUBLISH=false
```

## 5. Run the Workflow (Publish)

Allow auto-detect (label based) publish:

```bash
act pull_request -e .github/events/pull_request_closed_merged.json -W .github/workflows/release-drafter.yml --container-architecture linux/amd64
```

## 6. Manual Dispatch Simulation

To simulate `workflow_dispatch` with publish=true:

```bash
act workflow_dispatch -W .github/workflows/release-drafter.yml --container-architecture linux/amd64 --input publish=true
```

## 7. Inspect Logs

`act` prints step output. Look for the `Run Release Drafter` step and the final summary indicating whether a draft or published release occurred.

## 8. Troubleshooting

- If `jq` not found: the GitHub hosted runner image used by `act` variant should include it; if not, modify the workflow step to install: `sudo apt-get update && sudo apt-get install -y jq`.
- If version doesn't increment: ensure at least one merged PR bears a version-resolver label (e.g. `feat`, `fix`, `breaking`). In local simulation, labels come from the provided event JSON.
- If publish unexpectedly false: verify the label names (`release` or `publish`) or pass `--input publish=true` for manual dispatch.

## 9. Clean Up

No artifacts are generated locally by this workflow. Just remove any temporary tokens from your environment.

---

Maintained alongside the Release Drafter workflow; update if the workflow logic changes.
