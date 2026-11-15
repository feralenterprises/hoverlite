@echo off
setlocal EnableExtensions
title HoverLite - RuneLite Windows Playtest
cd /d "%~dp0"
echo =========================================================
echo   HOVERLITE ^| ONE-CLICK RUNELITE PLAYTEST
echo =========================================================
echo.
set "CLIENT=%~dp0PORTABLE-CLIENT\hoverlite-runelite-dev-all.jar"
if not exist "%CLIENT%" goto missing
where java >nul 2>&1
if errorlevel 1 goto nojava
echo [INFO] Java runtime:
java -version
echo.
echo [INFO] Launching separate RuneLite developer client with HoverLite...
echo [INFO] Your normal installed RuneLite client is not replaced.
echo [INFO] No Gradle install, project rebuild, or source checkout required.
echo.
java -ea -jar "%CLIENT%" --developer-mode --debug
set "EXIT_CODE=%ERRORLEVEL%"
if not "%EXIT_CODE%"=="0" goto failed
echo.
echo [INFO] Playtest client closed.
pause
exit /b 0
:missing
echo [ERROR] Missing portable RuneLite client JAR.
echo [ACTION] Extract the whole ZIP before double-clicking START-HOVERLITE.bat.
goto stop
:nojava
echo [ERROR] Java 17 or newer not found on PATH.
echo [ACTION] Install Java 17: https://adoptium.net/temurin/releases/?version=17
goto stop
:failed
echo.
echo [ERROR] RuneLite developer client exited with code %EXIT_CODE%.
echo [ACTION] Copy the first exception/error from this console window.
:stop
pause
exit /b 1
