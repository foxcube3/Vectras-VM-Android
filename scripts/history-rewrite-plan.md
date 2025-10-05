# Git History Rewrite Plan (Large Binary Purge)

This document describes the precise, reproducible steps to remove previously committed large binary artifacts from the repository history.

## Target Paths (to purge from ALL history)

See `scripts/paths-to-remove.txt` generated alongside this file. These include historical firmware images, JNI .so blobs, and APK artifacts discovered during the earlier scan.

## Preconditions

- Ensure all contributors have no unmerged work based on current `master`.
- Communicate the intent and expected force-push window.
- Create an out-of-band backup clone (safety) or rely on GitHub network forks retaining objects temporarily.

## Steps

1. Backup current state (optional but recommended):

   ```bash
   git branch backup-before-history-clean
   git tag pre-history-cleanup
   ```

2. Install git-filter-repo if not present:

   ```bash
   python -m pip install git-filter-repo
   ```

3. Run filter (dry run suggestion: clone a fresh mirror first):

   ```bash
   git filter-repo --paths-from-file scripts/paths-to-remove.txt --invert-paths
   ```

4. Verify:
   - `git log --oneline | head`
   - `git verify-pack -v .git/objects/pack/*.idx | sort -k3 -n | tail -n 15`
   - Ensure removed paths no longer appear: `grep -F -f scripts/paths-to-remove.txt -R . || echo "Paths gone"`

5. Force push (once satisfied):

   ```bash
   git push --force origin master
   git push --force origin accessibility/content-descriptions
   ```

6. Instruct collaborators to prune / rebase:

   ```bash
   git fetch --all --prune
   git reset --hard origin/master
   git reflog expire --expire=now --all
   git gc --prune=now --aggressive
   ```

## Post-Cleanup Validation

- Re-run large object scan script to confirm sizes.
- Ensure CI workflows still succeed.
- Optionally add a badge or note to README indicating dynamic firmware handling (already documented).

## Rollback Plan

If critical issues arise, force push `pre-history-cleanup` tag back to master while investigating.

## Notes

- This operation rewrites commit SHAs; open PRs need rebasing.
- LFS patterns remain for future large artifacts; dynamic fetch reduces need to ever commit these again.
