Param(
    [switch]$VerboseOutput
)

Write-Host "[check-env] Starting environment verification..." -ForegroundColor Cyan

$errors = @()

# SDK detection
$localProps = Join-Path $PSScriptRoot '..' 'local.properties'
$SdkDir = $null
if (Test-Path $localProps) {
    $line = (Select-String -Path $localProps -Pattern '^sdk.dir=' -ErrorAction SilentlyContinue | Select-Object -First 1).Line
    if ($line) { $SdkDir = $line.Split('=')[1] }
}
if (-not $SdkDir -and $env:ANDROID_HOME) { $SdkDir = $env:ANDROID_HOME }
if (-not $SdkDir -and $env:ANDROID_SDK_ROOT) { $SdkDir = $env:ANDROID_SDK_ROOT }

if (-not $SdkDir) {
    $errors += 'Android SDK path not found (no local.properties sdk.dir and no ANDROID_HOME / ANDROID_SDK_ROOT).'
} elseif (-not (Test-Path $SdkDir)) {
    $errors += "Android SDK directory does not exist: $SdkDir"
} else {
    Write-Host "[check-env] SDK directory: $SdkDir" -ForegroundColor Green
}

# Java detection
$javaVersion = (& java -version 2>&1 | Select-Object -First 1)
if ($LASTEXITCODE -ne 0) {
    $errors += 'Java (JDK) not found on PATH.'
} else {
    Write-Host "[check-env] $javaVersion" -ForegroundColor Green
}

# Gradle wrapper
$gradlew = Join-Path $PSScriptRoot '..' 'gradlew.bat'
if (-not (Test-Path $gradlew)) { $errors += 'gradlew.bat missing at repository root.' } else { Write-Host '[check-env] gradlew.bat present.' -ForegroundColor Green }

# Optional: gpg if signature verification used
if (Get-Command gpg -ErrorAction SilentlyContinue) {
    Write-Host '[check-env] gpg found.' -ForegroundColor Green
} else {
    Write-Host '[check-env] gpg not found (signature verification tasks will be skipped).' -ForegroundColor Yellow
}

if ($errors.Count -gt 0) {
    Write-Host "[check-env] FAIL" -ForegroundColor Red
    $errors | ForEach-Object { Write-Host " - $_" -ForegroundColor Red }
    exit 1
} else {
    Write-Host '[check-env] All required environment checks passed.' -ForegroundColor Green
}
