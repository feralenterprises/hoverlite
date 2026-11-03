@echo off
setlocal
cd /d "%~dp0"
echo ===================================================
echo HOVERLITE v0.5 - PUBLIC RELEASE CANDIDATE - OWNER QA
echo ===================================================
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0tools\bootstrap-gradle.ps1" test run
if errorlevel 1 (
  echo [FAIL] Build, tests, or launch failed. Copy the first error above.
  pause
  exit /b 1
)
echo [PASS] Gradle tests and RuneLite session completed.
pause
