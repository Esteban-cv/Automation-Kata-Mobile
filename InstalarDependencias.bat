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
echo =======================================================
echo     SELECCIONA EL DISPOSITIVO PARA LA AUTOMATIZACION
echo =======================================================
echo  [1] Celular fisico conectado por cable USB (recomendado)
echo  [2] Emulador de Android Studio
echo.
choice /c 12 /n /m "Elige una opcion (1 o 2): "
if errorlevel 2 goto :EMULADOR
goto :USB

:: -------------------------------------------------------
:: OPCION 1: CELULAR POR CABLE
:: -------------------------------------------------------
:USB
echo.
echo POR FAVOR:
echo 1. Habilita "Depuracion USB" en tu celular Android.
echo 2. Conecta el celular con un cable USB.
echo 3. Acepta en el celular el mensaje "Permitir depuracion USB".
echo.
pause
:BUSCAR_USB
set "DEVICE_SERIAL="
set "USB_UNAUTHORIZED="
for /f "skip=1 tokens=1,2" %%A in ('"%ADB_PATH%" devices 2^>nul') do call :TOMAR_USB %%A %%B
if defined DEVICE_SERIAL goto :GUARDAR_DISPOSITIVO
if defined USB_UNAUTHORIZED (
    echo [ADVERTENCIA] El celular esta conectado pero no autorizado. Acepta el mensaje en su pantalla.
) else (
    echo [ADVERTENCIA] No se detecto ningun celular conectado por cable.
)
choice /c SN /m "Deseas volver a buscar el celular"
if errorlevel 2 goto :FIN
goto :BUSCAR_USB

:TOMAR_USB
if defined DEVICE_SERIAL exit /b
echo %~1| findstr /b /c:"emulator-" >nul && exit /b
if /i "%~2"=="unauthorized" set "USB_UNAUTHORIZED=1"
if /i "%~2"=="device" set "DEVICE_SERIAL=%~1"
exit /b

:: -------------------------------------------------------
:: OPCION 2: EMULADOR DE ANDROID STUDIO
:: -------------------------------------------------------
:EMULADOR
set "EMULATOR_EXE=%ANDROID_HOME%\emulator\emulator.exe"
if not exist "%EMULATOR_EXE%" (
    echo.
    echo [ERROR] No se encontro el emulador en "%ANDROID_HOME%\emulator".
    echo Instala Android Studio, crea un dispositivo virtual en "Device Manager"
    echo y vuelve a ejecutar este instalador.
    goto :FIN
)

:: Reutiliza un emulador que ya este encendido.
set "DEVICE_SERIAL="
for /f "skip=1 tokens=1,2" %%A in ('"%ADB_PATH%" devices 2^>nul') do (
    echo %%A| findstr /b /c:"emulator-" >nul && if /i "%%B"=="device" if not defined DEVICE_SERIAL set "DEVICE_SERIAL=%%A"
)
if defined DEVICE_SERIAL (
    echo [OK] Ya hay un emulador encendido: %DEVICE_SERIAL%
    goto :GUARDAR_DISPOSITIVO
)

echo.
echo Dispositivos virtuales disponibles:
set "AVD_COUNT=0"
for /f "delims=" %%A in ('"%EMULATOR_EXE%" -list-avds 2^>nul') do call :AGREGAR_AVD "%%A"
if "%AVD_COUNT%"=="0" (
    echo [ERROR] No hay dispositivos virtuales creados. Crea uno en Android Studio ^> Device Manager.
    goto :FIN
)
set "AVD_SEL=1"
if not "%AVD_COUNT%"=="1" set /p "AVD_SEL=Escribe el numero del dispositivo virtual: "
set "AVD_NAME="
call set "AVD_NAME=%%AVD_%AVD_SEL%%%"
if not defined AVD_NAME (
    echo [ERROR] Opcion no valida: %AVD_SEL%
    goto :FIN
)

echo [PROCESO] Iniciando el emulador "%AVD_NAME%"...
start "Emulador Android" "%EMULATOR_EXE%" -avd "%AVD_NAME%" -port 5554
set "DEVICE_SERIAL=emulator-5554"
set "WAIT_COUNT=0"
:ESPERAR_EMULADOR
"%ADB_PATH%" -s %DEVICE_SERIAL% shell getprop sys.boot_completed 2>nul | findstr "1" >nul && goto :EMULADOR_LISTO
set /a WAIT_COUNT+=1
if %WAIT_COUNT% GEQ 90 (
    echo [ERROR] El emulador no termino de iniciar en 3 minutos. Revisa la ventana del emulador.
    goto :FIN
)
timeout /t 2 /nobreak >nul
goto :ESPERAR_EMULADOR
:EMULADOR_LISTO
echo [OK] Emulador listo: %DEVICE_SERIAL%
goto :GUARDAR_DISPOSITIVO

:AGREGAR_AVD
set /a AVD_COUNT+=1
set "AVD_%AVD_COUNT%=%~1"
echo  [%AVD_COUNT%] %~1
exit /b

:: -------------------------------------------------------
:: Guarda el serial detectado para que el Dashboard use ese dispositivo
:: -------------------------------------------------------
:GUARDAR_DISPOSITIVO
set "CONFIG_FILE=%~dp0config\robot.properties"
if not exist "%CONFIG_FILE%" (
    echo [ADVERTENCIA] No se encontro "%CONFIG_FILE%". Configura device.name=%DEVICE_SERIAL% manualmente.
    goto :FIN
)
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$lines=@(Get-Content -LiteralPath $env:CONFIG_FILE);" ^
  "if($lines -match '^device\.name='){$lines=$lines -replace '^device\.name=.*',('device.name='+$env:DEVICE_SERIAL)}else{$lines+=('device.name='+$env:DEVICE_SERIAL)};" ^
  "Set-Content -LiteralPath $env:CONFIG_FILE -Value $lines -Encoding Default"
echo [OK] Dispositivo configurado: %DEVICE_SERIAL%

:FIN
echo.
echo Cierra esta ventana negra y ya puedes abrir el Dashboard.
echo.
pause
endlocal