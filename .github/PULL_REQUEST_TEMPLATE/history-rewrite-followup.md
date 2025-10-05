---
name: "History Rewrite Follow-up"
about: "Execute large binary purge and finalize external firmware system"
---

# History Rewrite Follow-up

## Objective

Perform approved git history rewrite to remove legacy large binaries and finalize production firmware hosting references.

## Tasks

- [ ] Confirm all active PRs rebased / ready for rewrite window
- [ ] Run size estimation script (`scripts/estimate-post-rewrite-size.ps1`) and attach output
- [ ] Dry-run communication to collaborators (email/issue/discussion)
- [ ] Execute rewrite (PowerShell or bash script)
- [ ] Force push master + active feature branches
- [ ] Invalidate old caches (Actions workflows, any build caches referencing old SHAs)
- [ ] Update README with final firmware production URLs + real hashes
- [ ] Replace placeholder signature URL if applicable
- [ ] Re-run CI (firmware + secure job) and confirm green
- [ ] Re-scan for large blobs (supply updated report)
- [ ] Close this follow-up PR with summary comment & link to original PR #1

## Commands Reference

PowerShell rewrite:

```powershell
pwsh ./scripts/run-history-rewrite.ps1 -Execute
```

Bash rewrite:

```bash
bash ./scripts/run-history-rewrite.sh
```

Force push (after validation):

```bash
git push --force origin master
```

## Output Attachments

Paste outputs from estimation, verification, and final scan here.

## Risk Mitigation

- Safety branch & tag already created prior to rewrite.
- Rollback: force push `pre-history-cleanup` tag if needed.

## Approvals

- [ ] Maintainer approval to proceed
- [ ] Security review (optional)
- [ ] Dev team notified
