Write-Host '== Estimating repository pack size after prospective rewrite ==' -ForegroundColor Cyan

if (-not (Test-Path 'scripts/paths-to-remove.txt')) { Write-Error 'Missing scripts/paths-to-remove.txt'; exit 1 }

# Gather size of objects associated with listed paths (rough heuristic via git rev-list + ls-tree)
Write-Host 'Collecting blob SHAs for target paths...'
$paths = Get-Content scripts/paths-to-remove.txt
$blobSizes = @()
foreach ($p in $paths) {
  $matchedPaths = git log --pretty=format: --name-only --follow -- $p 2>$null | Where-Object { $_ -ne '' } | Sort-Object -Unique
  if ($matchedPaths) {
    foreach ($m in $matchedPaths) {
      $sha = git ls-tree HEAD $m 2>$null | ForEach-Object { ($_ -split '\s+')[2] } | Select-Object -First 1
      if ($sha) {
        $size = git cat-file -s $sha 2>$null
        if ($size) { $blobSizes += [int]$size }
      }
    }
  }
}

if (-not $blobSizes) { Write-Host 'No candidate blobs found (maybe already removed?)' -ForegroundColor Yellow; exit 0 }

$totalRemove = ($blobSizes | Measure-Object -Sum).Sum
Write-Host ("Approx total removable size (uncompressed): {0:N0} bytes" -f $totalRemove) -ForegroundColor Green

# Current pack size heuristic
$packFiles = Get-ChildItem .git/objects/pack/*.pack -ErrorAction SilentlyContinue
if ($packFiles) {
  $current = ($packFiles | Measure-Object -Property Length -Sum).Sum
  Write-Host ("Current total pack size: {0:N0} bytes" -f $current) -ForegroundColor Cyan
  $pct = if ($current -gt 0) { [math]::Round(($totalRemove / $current) * 100, 2) } else { 0 }
  Write-Host ("Potential size reduction (very rough): {0}%" -f $pct) -ForegroundColor Magenta
} else {
  Write-Host 'No pack files found; repository may be very small or unpacked.' -ForegroundColor Yellow
}
