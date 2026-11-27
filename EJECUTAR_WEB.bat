@echo off
cd /d "%~dp0"
call gradlew.bat :composeApp:jsBrowserDevelopmentRun
pause
