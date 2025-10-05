#!/usr/bin/env bash
set -euo pipefail
TOP="${1:-40}"
echo "== History Size Estimation (top ${TOP} blobs) =="
git fetch --all --prune --tags >/dev/null 2>&1 || true
idx=$(ls .git/objects/pack/*.idx 2>/dev/null | head -n1)
if [[ -z "${idx}" ]]; then
  echo "No pack index found" >&2
  exit 1
fi
git rev-list --objects --all > rev-list-all.txt
git verify-pack -v "$idx" | awk 'length($1)>=7 && $2 ~ /blob/ {print $1,$3}' | sort -k2 -n | tail -n "$TOP" > largest-blobs.txt
awk '{print $1}' largest-blobs.txt > largest-shas.txt
sort rev-list-all.txt > rev-list-sorted.txt
sort largest-shas.txt > largest-shas-sorted.txt
join largest-shas-sorted.txt rev-list-sorted.txt > largest-mapped.txt || true
echo "Pack stats:" > history-estimate.txt
git count-objects -v >> history-estimate.txt
echo "\nTop ${TOP} largest blobs (bytes sha path):" >> history-estimate.txt
join largest-shas-sorted.txt rev-list-sorted.txt | while read -r line; do
  sha=$(echo "$line" | awk '{print $1}')
  size=$(grep "^${sha} " largest-blobs.txt | awk '{print $2}')
  path=$(echo "$line" | cut -d' ' -f2-)
  echo "${size} ${sha} ${path}" >> history-estimate.txt
done
echo "Done. See history-estimate.txt"