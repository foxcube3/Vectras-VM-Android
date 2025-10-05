# History Rewrite Follow-up (Working Checklist)

Estimation Output (latest run):

```text
No candidate blobs found (maybe already removed?)
```

## Pending Items

- [ ] Confirm with maintainers whether zero-detection implies prior garbage collection or path relocation
- [ ] Decide if rewrite still desired (SHAs will change)
- [ ] Provide production firmware URLs + SHA-256 + optional signature
- [ ] Update README firmware section with real values
- [ ] (Optional) Execute rewrite using provided scripts

## Notes

No large candidate blobs currently resolvable in HEAD; history may still contain them if unreachable by simple ls-tree (pack objects may require deeper scan on a fresh clone). A dry-run mirror clone can confirm before proceeding.
