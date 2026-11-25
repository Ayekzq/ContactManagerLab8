@echo off
setlocal
set "GRADLE_VERSION=8.9"
set "GRADLE_HOME=%USERPROFILE%\.gradle\manual\gradle-%GRADLE_VERSION%"
set "GRADLE_EXE=%GRADLE_HOME%\bin\gradle.bat"
if not exist "%GRADLE_EXE%" (
  echo Downloading Gradle %GRADLE_VERSION%...
  set "ZIP=%TEMP%\gradle-%GRADLE_VERSION%-bin.zip"
  powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile '%TEMP%\gradle-%GRADLE_VERSION%-bin.zip'; New-Item -ItemType Directory -Force -Path '%USERPROFILE%\.gradle\manual' | Out-Null; Expand-Archive -LiteralPath '%TEMP%\gradle-%GRADLE_VERSION%-bin.zip' -DestinationPath '%USERPROFILE%\.gradle\manual' -Force"
  if errorlevel 1 exit /b 1
)
call "%GRADLE_EXE%" %*
exit /b %ERRORLEVEL%
