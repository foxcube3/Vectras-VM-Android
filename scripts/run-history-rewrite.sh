#!/usr/bin/env bash
set -euo pipefail

PATHS_FILE="scripts/paths-to-remove.txt"
BACKUP_BRANCH="backup-before-history-clean"
ROLLBACK_TAG="pre-history-cleanup"

if [[ ! -f "$PATHS_FILE" ]]; then
  echo "Paths file not found: $PATHS_FILE" >&2
  exit 1
fi

echo "== Git Large Binary History Rewrite Helper (bash) =="

echo "Creating safety branch/tag (if absent)..."
if ! git rev-parse --verify "$BACKUP_BRANCH" >/dev/null 2>&1; then
  git branch "$BACKUP_BRANCH"
fi
if ! git rev-parse --verify "refs/tags/$ROLLBACK_TAG" >/dev/null 2>&1; then
  git tag "$ROLLBACK_TAG"
fi

if ! command -v git-filter-repo >/dev/null 2>&1; then
  echo "git-filter-repo not found. Install with: python -m pip install git-filter-repo" >&2
  exit 1
fi

echo "Running git-filter-repo..."
git filter-repo --paths-from-file "$PATHS_FILE" --invert-paths

echo "Verifying removal..."
while IFS= read -r p; do
  if git ls-files | grep -Fx "$p" >/dev/null; then
    echo "WARNING: path still present in current tree: $p"
  fi
done < "$PATHS_FILE"

echo "Next steps: force push cleaned history"
echo "  git push --force origin master"
echo "  git push --force origin $(git branch --show-current)"