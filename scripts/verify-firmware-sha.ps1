Param(
  [Parameter(Mandatory=$true)][string]$Url,
  [Parameter(Mandatory=$true)][string]$ExpectedSha256
)

Write-Host "== Preflight Firmware SHA Verification ==" -ForegroundColor Cyan
Write-Host "URL: $Url" -ForegroundColor Gray

try {
  $temp = New-TemporaryFile
  $wc = New-Object System.Net.WebClient
  $wc.DownloadFile($Url, $temp.FullName)
  $hash = (Get-FileHash -Path $temp.FullName -Algorithm SHA256).Hash.ToLower()
  if ($hash -ne $ExpectedSha256.ToLower()) {
    Write-Host "Expected: $ExpectedSha256" -ForegroundColor Yellow
    Write-Host "Actual:   $hash" -ForegroundColor Red
    throw "Hash mismatch"
  }
  Write-Host "Hash OK: $hash" -ForegroundColor Green
  exit 0
} catch {
  Write-Error $_
  exit 1
} finally {
  if ($temp -and (Test-Path $temp.FullName)) { Remove-Item $temp.FullName -Force }
}