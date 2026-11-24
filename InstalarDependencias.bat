@echo off
setlocal
color 0B
set "SCRIPT_DIR=%~dp0"
echo =======================================================
echo     INSTALADOR DE DEPENDENCIAS - KATA MOBILE
echo =======================================================
echo.

:: Lo que winget instala no queda en el PATH de esta ventana; si se instala algo,
:: hay que abrir una ventana nueva para continuar.
set "NEEDS_RESTART="

:: 1. VERIFICAR NODE.JS
node -v >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] Node.js ya esta instalado.
) else (
    echo [PROCESO] Instalando Node.js...
    winget install --id OpenJS.NodeJS.LTS --exact --source winget --accept-source-agreements --accept-package-agreements
    set "NEEDS_RESTART=1"
)

:: 2. VERIFICAR PLATFORM TOOLS (ADB)
adb version >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] Android Platform-Tools ^(ADB^) ya esta instalado.
) else (
    echo [PROCESO] Instalando Android Platform-Tools...
    winget install --id Google.PlatformTools --exact --source winget --accept-source-agreements --accept-package-agreements
    set "NEEDS_RESTART=1"
)

:: 3. VERIFICAR JAVA 17
java -version >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] Java ya esta instalado.
) else (
    echo [PROCESO] Instalando Java 17...
    winget install --id Microsoft.OpenJDK.17 --exact --source winget --accept-source-agreements --accept-package-agreements
    set "NEEDS_RESTART=1"
)

if defined NEEDS_RESTART (
    echo.
    echo [IMPORTANTE] Se instalaron programas nuevos. Cierra esta ventana y vuelve a
    echo ejecutar InstalarDependencias.bat para terminar la configuracion.
    echo.
    pause
    exit /b 0
)

:: 4. VERIFICAR APPIUM Y SU DRIVER (se revisan por separado: Appium puede estar sin el driver)
call appium -v >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] Appium ya esta instalado.
) else (
    echo [PROCESO] Instalando Appium...
    call npm install --location=global appium
)
call appium driver list --installed > "%TEMP%\kata_drivers.txt" 2>&1
findstr /i "uiautomator2" "%TEMP%\kata_drivers.txt" >nul
if %ERRORLEVEL% EQU 0 (
    echo [OK] Driver UiAutomator2 ya esta instalado.
) else (
    echo [PROCESO] Instalando driver UiAutomator2...
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
call :RESOLVER_ANDROID_HOME
if not defined ANDROID_HOME (
    echo [ADVERTENCIA] No se pudo ubicar la carpeta del SDK de Android a partir de "%ADB_PATH%".
    echo Appium necesita ANDROID_HOME. Reinstala Platform-Tools o instala Android Studio.
    pause
    exit /b 1
)
echo [OK] SDK de Android: %ANDROID_HOME%
call :RESOLVER_JAVA_HOME

:: Bloque de PowerShell para inyectar variables al sistema sin duplicar (Tu logica original)
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$user=[Environment]::GetEnvironmentVariable('Path','User');" ^
  "$entries=@('%PLATFORM_TOOLS%','%NPM_PREFIX%','%NPM_PREFIX%\node_modules\appium\build\lib');" ^
  "$parts=@($user -split ';' | Where-Object { $_ -and $_.Trim() });" ^
  "foreach($entry in $entries){if($parts -notcontains $entry){$parts += $entry}};" ^
  "[Environment]::SetEnvironmentVariable('Path',($parts -join ';'),'User');" ^
  "[Environment]::SetEnvironmentVariable('ANDROID_HOME','%ANDROID_HOME%','User');" ^
  "[Environment]::SetEnvironmentVariable('ANDROID_SDK_ROOT','%ANDROID_HOME%','User');" ^
  "if('%JAVA_HOME%'){[Environment]::SetEnvironmentVariable('JAVA_HOME','%JAVA_HOME%','User')};"

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
    echo [AVISO] Este PC no tiene el emulador de Android.
    choice /c SN /m "Deseas instalar Android Studio ahora (descarga grande, unos minutos)"
    if errorlevel 2 goto :FIN
    winget install --id Google.AndroidStudio --exact --source winget --accept-source-agreements --accept-package-agreements
    echo.
    echo [IMPORTANTE] Abre Android Studio UNA vez y termina el asistente inicial
    echo ^(descarga el SDK y el emulador^). Luego vuelve a ejecutar InstalarDependencias.bat
    echo y elige la opcion [2]: el dispositivo virtual se crea solo.
    goto :FIN
)

:: Reutiliza un emulador que ya este encendido.
set "DEVICE_SERIAL="
for /f "skip=1 tokens=1,2" %%A in ('"%ADB_PATH%" devices 2^>nul') do (
    echo %%A| findstr /b /c:"emulator-" >nul && if /i "%%B"=="device" if not defined DEVICE_SERIAL set "DEVICE_SERIAL=%%A"
)
if defined DEVICE_SERIAL (
    echo [OK] Ya hay un emulador encendido: %DEVICE_SERIAL%
    call :INSTALAR_APP
    goto :GUARDAR_DISPOSITIVO
)

echo.
echo Dispositivos virtuales disponibles:
set "AVD_COUNT=0"
for /f "delims=" %%A in ('"%EMULATOR_EXE%" -list-avds 2^>nul') do call :AGREGAR_AVD "%%A"
if "%AVD_COUNT%"=="0" call :CREAR_AVD
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
start "Emulador Android" "%EMULATOR_EXE%" -avd "%AVD_NAME%" -port 5554 -gpu auto
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
call :INSTALAR_APP
goto :GUARDAR_DISPOSITIVO

:AGREGAR_AVD
set /a AVD_COUNT+=1
set "AVD_%AVD_COUNT%=%~1"
echo  [%AVD_COUNT%] %~1
exit /b

:: -------------------------------------------------------
:: Crea el dispositivo virtual "Kata_Emulator" (Pixel 7, Android 14 con Play Store, 4 GB
:: de RAM). Con la imagen sin Play Store la app de Kata no deja iniciar sesion, y con poca
:: RAM el emulador va lento y la app muestra "Error de conexion". Descarga ~1.5 GB.
:: -------------------------------------------------------
:CREAR_AVD
set "SDKMANAGER=%ANDROID_HOME%\cmdline-tools\latest\bin\sdkmanager.bat"
set "AVDMANAGER=%ANDROID_HOME%\cmdline-tools\latest\bin\avdmanager.bat"
set "AVD_IMAGE=system-images;android-34;google_apis_playstore;x86_64"
if not exist "%SDKMANAGER%" (
    echo [AVISO] Falta "Android SDK Command-line Tools". Instalalo en Android Studio:
    echo   Settings ^> Languages and Frameworks ^> Android SDK ^> SDK Tools ^> Command-line Tools.
    exit /b
)
choice /c SN /m "No hay dispositivos virtuales. Deseas crear Kata_Emulator automaticamente"
if errorlevel 2 exit /b
echo [PROCESO] Aceptando licencias del SDK...
(for /l %%N in (1,1,30) do @echo y) | call "%SDKMANAGER%" --licenses >nul 2>&1
echo [PROCESO] Descargando la imagen del sistema, puede tardar varios minutos...
call "%SDKMANAGER%" "emulator" "platform-tools" "platforms;android-34" "%AVD_IMAGE%"
echo [PROCESO] Creando el dispositivo virtual Kata_Emulator...
echo no| call "%AVDMANAGER%" create avd -n Kata_Emulator -k "%AVD_IMAGE%" -d pixel_7 --force
set "AVD_CONFIG=%USERPROFILE%\.androidvd\Kata_Emulator.avd\config.ini"
if exist "%AVD_CONFIG%" powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$c=Get-Content -LiteralPath $env:AVD_CONFIG;" ^
  "$c=@($c | Where-Object { $_ -notmatch '^(hw.ramSize|vm.heapSize|hw.cpu.ncore)\s*=' });" ^
  "$c+='hw.ramSize = 4096'; $c+='vm.heapSize = 512'; $c+='hw.cpu.ncore = 4';" ^
  "Set-Content -LiteralPath $env:AVD_CONFIG -Value $c"
for /f "delims=" %%A in ('"%EMULATOR_EXE%" -list-avds 2^>nul') do call :AGREGAR_AVD "%%A"
exit /b

:: -------------------------------------------------------
:: Instala (o actualiza conservando datos) la app incluida en el instalador.
:: Justo despues del arranque el gestor de paquetes puede no estar listo: un reintento.
:: -------------------------------------------------------
:INSTALAR_APP
set "APP_APK=%SCRIPT_DIR%apk\KataMobile.apk"
if not exist "%APP_APK%" (
    echo [ADVERTENCIA] No se encontro la app en "%APP_APK%".
    echo Instala la app manualmente en el emulador antes de abrir el Dashboard.
    exit /b
)
echo [PROCESO] Instalando la app Kata Mobile en el emulador, puede tardar un minuto...
"%ADB_PATH%" -s %DEVICE_SERIAL% install -r "%APP_APK%" && goto :APP_INSTALADA
timeout /t 10 /nobreak >nul
"%ADB_PATH%" -s %DEVICE_SERIAL% install -r "%APP_APK%" && goto :APP_INSTALADA
echo [ERROR] No se pudo instalar la app. Revisa el mensaje anterior.
exit /b
:APP_INSTALADA
echo [OK] App instalada en el emulador.
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
call :VERIFICAR

:FIN
echo.
echo Cierra esta ventana negra y ya puedes abrir el Dashboard.
echo.
pause
endlocal
exit /b

:: -------------------------------------------------------
:: Resumen: confirma que cada pieza responde antes de abrir el Dashboard.
:: -------------------------------------------------------
:VERIFICAR
echo.
echo =======================================================
echo     VERIFICACION FINAL
echo =======================================================
call node -v >nul 2>&1 && (echo [OK] Node.js) || (echo [FALTA] Node.js)
call appium -v >nul 2>&1 && (echo [OK] Appium) || (echo [FALTA] Appium: vuelve a ejecutar este instalador en una ventana nueva)
call appium driver list --installed > "%TEMP%\kata_drivers.txt" 2>&1
findstr /i "uiautomator2" "%TEMP%\kata_drivers.txt" >nul && (echo [OK] Driver UiAutomator2) || (echo [FALTA] Driver UiAutomator2)
"%ADB_PATH%" -s %DEVICE_SERIAL% get-state 2>nul | findstr /i "device" >nul && (echo [OK] Dispositivo %DEVICE_SERIAL% conectado) || (echo [FALTA] Dispositivo %DEVICE_SERIAL% no responde)
"%ADB_PATH%" -s %DEVICE_SERIAL% shell pm list packages com.kata.mobile 2>nul | findstr /i "com.kata.mobile" >nul && (echo [OK] App Kata Mobile instalada en el dispositivo) || (echo [FALTA] App Kata Mobile en el dispositivo)
echo.
echo Recuerda: abre Kata Mobile en el dispositivo e inicia sesion antes de ejecutar.
exit /b

:: -------------------------------------------------------
:: ANDROID_HOME debe ser la carpeta que contiene platform-tools\adb.exe.
:: Orden: valor actual valido, SDK de Android Studio, carpeta real del adb instalado.
:: -------------------------------------------------------
:RESOLVER_ANDROID_HOME
if defined ANDROID_HOME if exist "%ANDROID_HOME%\platform-tools\adb.exe" exit /b
set "ANDROID_HOME="
if exist "%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" (
    set "ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk"
    exit /b
)
:: winget puede dejar en el PATH un acceso directo; se resuelve a la ubicacion real.
set "ADB_REAL="
for /f "delims=" %%I in ('powershell -NoProfile -Command "[string](Get-Item -LiteralPath $env:ADB_PATH).Target"') do set "ADB_REAL=%%I"
if not defined ADB_REAL set "ADB_REAL=%ADB_PATH%"
for %%I in ("%ADB_REAL%") do set "ADB_DIR=%%~dpI"
for %%D in ("%ADB_DIR:~0,-1%") do if /i "%%~nxD"=="platform-tools" set "ANDROID_HOME=%%~dpD"
if defined ANDROID_HOME set "ANDROID_HOME=%ANDROID_HOME:~0,-1%"
exit /b

:: -------------------------------------------------------
:: JAVA_HOME debe ser la carpeta que contiene bin\java.exe; se respeta si ya es valido.
:: -------------------------------------------------------
:RESOLVER_JAVA_HOME
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" exit /b
set "JAVA_HOME="
for %%I in ("%JAVA_PATH%") do set "JAVA_BIN=%%~dpI"
for %%D in ("%JAVA_BIN:~0,-1%") do if /i "%%~nxD"=="bin" set "JAVA_HOME=%%~dpD"
if defined JAVA_HOME set "JAVA_HOME=%JAVA_HOME:~0,-1%"
exit /b