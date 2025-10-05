Param(
  [int]$Top = 40
)

Write-Host "== History Impact Report Generator ==" -ForegroundColor Cyan
if (-not (Get-Command git -ErrorAction SilentlyContinue)) { Write-Error 'git not found'; exit 1 }

# Ensure full history
git fetch --all --prune --tags | Out-Null

$packIdx = Get-ChildItem .git/objects/pack/*.idx | Select-Object -First 1
if (-not $packIdx) { Write-Error 'No pack index found'; exit 1 }

Write-Host "Analyzing pack: $($packIdx.Name)" -ForegroundColor Cyan

$verify = git verify-pack -v $packIdx.FullName

# Extract lines with at least 3 columns where third is size (blob lines start with SHA)
$parsed = $verify | ForEach-Object {
  $parts = ($_ -split '\s+') | Where-Object { $_ -ne '' }
  if ($parts.Length -ge 3 -and $parts[0] -match '^[0-9a-f]{7,40}$' -and $parts[2] -match '^[0-9]+$') {
    [pscustomobject]@{ Sha=$parts[0]; Type=$parts[1]; Size=[int]$parts[2] }
  }
} | Where-Object { $_.Type -eq 'blob' } | Sort-Object Size -Descending | Select-Object -First $Top

git rev-list --objects --all > rev-list-all.txt
$map = @{}
Get-Content rev-list-all.txt | ForEach-Object {
  $p = $_ -split ' '
  if ($p.Length -ge 2) { $map[$p[0]] = ($p[1..($p.Length-1)] -join ' ') }
}

$rows = $parsed | ForEach-Object {
  $path = $map[$_.Sha]
  if (-not $path) { $path = '<unmapped>' }
  [pscustomobject]@{ Sha=$_.Sha; Size=$_.Size; Path=$path }
}

$packStats = git count-objects -v | Out-String

$reportPath = 'HISTORY_IMPACT_REPORT.md'
"# History Impact Report\n" | Out-File $reportPath -Encoding UTF8
"Generated: $(Get-Date -Format o)" | Out-File $reportPath -Append
"\nPack statistics (git count-objects -v):\n" | Out-File $reportPath -Append
$packStats | Out-File $reportPath -Append
"\nTop $Top largest blob objects (size bytes):\n" | Out-File $reportPath -Append

$rows | ForEach-Object {
  "- $($_.Size) $($_.Sha) $($_.Path)" | Out-File $reportPath -Append
}

Write-Host "Report written to $reportPath" -ForegroundColor Green