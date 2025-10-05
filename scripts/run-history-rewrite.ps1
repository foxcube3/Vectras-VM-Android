Param(
    [switch]$Execute,
    [string]$PathsFile = "scripts/paths-to-remove.txt"
)

Write-Host "== Git Large Binary History Rewrite Helper ==" -ForegroundColor Cyan

if (-not (Test-Path $PathsFile)) {
    Write-Error "Paths file not found: $PathsFile"; exit 1
}

if (-not $Execute) {
    Write-Host "Dry run mode (no rewrite). Showing target paths:" -ForegroundColor Yellow
    Get-Content $PathsFile | ForEach-Object { Write-Host "  - $_" }
    Write-Host "\nTo perform the rewrite run again with -Execute" -ForegroundColor Yellow
    exit 0
}

# Safety tag/branch
$backupBranch = "backup-before-history-clean"
$rollbackTag = "pre-history-cleanup"

# Create safety references if missing (suppressing errors)
& git rev-parse --verify $backupBranch 2>$null 1>$null
if ($LASTEXITCODE -ne 0) { git branch $backupBranch }

& git rev-parse --verify "refs/tags/$rollbackTag" 2>$null 1>$null
if ($LASTEXITCODE -ne 0) { git tag $rollbackTag }

# Ensure git-filter-repo availability
$filterRepo = (Get-Command git-filter-repo -ErrorAction SilentlyContinue)
if (-not $filterRepo) {
    Write-Host "git-filter-repo not found. Install via: python -m pip install git-filter-repo" -ForegroundColor Red
    exit 1
}

Write-Host "Running git-filter-repo..." -ForegroundColor Cyan

git filter-repo --paths-from-file $PathsFile --invert-paths || exit 1

Write-Host "Verifying removal..." -ForegroundColor Cyan
$remaining = Select-String -Path (Get-ChildItem -Recurse -File | Select-Object -ExpandProperty FullName) -Pattern (Get-Content $PathsFile) -ErrorAction SilentlyContinue
if ($remaining) {
    Write-Host "Some patterns may still exist; manual review needed." -ForegroundColor Yellow
} else {
    Write-Host "All target paths removed from current working tree history view." -ForegroundColor Green
}

Write-Host "Next steps (manual):" -ForegroundColor Cyan
Write-Host "  git push --force origin master" -ForegroundColor Yellow
Write-Host "  git push --force origin accessibility/content-descriptions" -ForegroundColor Yellow
Write-Host "Then notify collaborators to prune and reset." -ForegroundColor Cyan
