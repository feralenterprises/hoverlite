param([Parameter(ValueFromRemainingArguments=$true)][string[]]$GradleArgs = @('test'))
$ErrorActionPreference = 'Stop'
$root = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$version = '8.10'
$filename = "gradle-$version-bin.zip"
$url = "https://services.gradle.org/distributions/$filename"
$expectedSha256 = '5b9c5eb3f9fc2c94abaea57d90bd78747ca117ddbbf96c859d3741181a12bf2a'
$cache = Join-Path $env:USERPROFILE '.gradle\hoverlite-bootstrap'
$archive = Join-Path $cache $filename
$gradleExe = Join-Path $cache "gradle-$version\bin\gradle.bat"

try {
    Push-Location $root
    if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
        throw 'Java JDK 17+ was not found in PATH. Install JDK 17 and restart the terminal.'
    }
    if (-not (Test-Path $gradleExe)) {
        New-Item -ItemType Directory -Force -Path $cache | Out-Null
        if (-not (Test-Path $archive)) {
            Write-Host "[INFO] Downloading official Gradle $version for initial setup..."
            [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
            Invoke-WebRequest -Uri $url -OutFile $archive -UseBasicParsing
        }
        $actual = (Get-FileHash -Algorithm SHA256 $archive).Hash.ToLowerInvariant()
        if ($actual -ne $expectedSha256) {
            Remove-Item $archive -Force -ErrorAction SilentlyContinue
            throw "Gradle download checksum mismatch. Expected $expectedSha256; received $actual."
        }
        Write-Host '[PASS] Gradle distribution SHA-256 verified.'
        Expand-Archive -Path $archive -DestinationPath $cache -Force
    }
    Write-Host "[INFO] Running: gradle $($GradleArgs -join ' ')"
    & $gradleExe @GradleArgs --console=plain
    if ($LASTEXITCODE -ne 0) {
        throw "Gradle exited with code $LASTEXITCODE."
    }
    Write-Host '[PASS] Gradle task complete.'
}
catch {
    Write-Host "[FAIL] $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}
finally {
    Pop-Location
}
