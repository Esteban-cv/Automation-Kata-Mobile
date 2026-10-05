@echo off
setlocal
color 0B
echo =======================================================
echo     INSTALADOR DE DEPENDENCIAS - KATA MOBILE
echo =======================================================
echo.

:: 1. VERIFICAR NODE.JS
node -v >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] Node.js ya esta instalado.
) else (
    echo [PROCESO] Instalando Node.js...
    winget install --id OpenJS.NodeJS.LTS --exact --source winget --accept-source-agreements --accept-package-agreements
)

:: 2. VERIFICAR PLATFORM TOOLS (ADB)
adb version >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] Android Platform-Tools (ADB) ya esta instalado.
) else (
    echo [PROCESO] Instalando Android Platform-Tools...
    winget install --id Google.PlatformTools --exact --source winget --accept-source-agreements --accept-package-agreements
)

:: 3. VERIFICAR JAVA 17
java -version >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] Java ya esta instalado.
) else (
    echo [PROCESO] Instalando Java 17...
    winget install --id Microsoft.OpenJDK.17 --exact --source winget --accept-source-agreements --accept-package-agreements
)

:: 4. VERIFICAR APPIUM Y DRIVER
call appium -v >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] Appium ya esta instalado.
) else (
    echo [PROCESO] Instalando Appium y UiAutomator2...
    call npm install --location=global appium
    call appium driver install uiautomator2
)

echo.
echo =======================================================
echo     CONFIGURANDO VARIABLES DE ENTORNO AUTOMATICAMENTE
echo =======================================================
for /f "delims=" %%I in ('where adb 2^>nul') do if not defined ADB_PATH set "ADB_PATH=%%I"
for /f "delims=" %%I in ('npm config get prefix 2^>nul') do if not defined NPM_PREFIX set "NPM_PREFIX=%%I"
for /f "delims=" %%I in ('where java 2^>nul') do if not defined JAVA_PATH set "JAVA_PATH=%%I"

if not defined ADB_PATH (
    echo [ADVERTENCIA] No se encontro ADB. Cierra la ventana y vuelve a ejecutar.
    pause
    exit /b 1
)

for %%I in ("%ADB_PATH%") do set "PLATFORM_TOOLS=%%~dpI"
for %%I in ("%JAVA_PATH%") do set "JAVA_BIN=%%~dpI"
for %%I in ("%JAVA_BIN:~0,-1%") do set "JAVA_HOME=%%~dpI"
set "ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk"

:: Bloque de PowerShell para inyectar variables al sistema sin duplicar (Tu logica original)
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$user=[Environment]::GetEnvironmentVariable('Path','User');" ^
  "$entries=@('%PLATFORM_TOOLS%','%NPM_PREFIX%','%NPM_PREFIX%\node_modules\appium\build\lib');" ^
  "$parts=@($user -split ';' | Where-Object { $_ -and $_.Trim() });" ^
  "foreach($entry in $entries){if($parts -notcontains $entry){$parts += $entry}};" ^
  "[Environment]::SetEnvironmentVariable('Path',($parts -join ';'),'User');" ^
  "[Environment]::SetEnvironmentVariable('ANDROID_HOME','%ANDROID_HOME%','User');" ^
  "[Environment]::SetEnvironmentVariable('ANDROID_SDK_ROOT','%ANDROID_HOME%','User');" ^
  "[Environment]::SetEnvironmentVariable('JAVA_HOME','%JAVA_HOME%','User');"

echo [OK] Variables configuradas con exito.
echo.
echo POR FAVOR:
echo 1. Habilita "Depuracion USB" en tu celular Android.
echo 2. Conecta el celular con un cable USB.
echo 3. Cierra esta ventana negra y ya puedes abrir el Dashboard.
echo.
pause
endlocal