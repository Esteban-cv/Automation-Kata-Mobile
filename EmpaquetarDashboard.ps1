$ErrorActionPreference = "Stop"

Write-Host "Cerrando procesos anteriores de KataMobile..." -ForegroundColor Yellow
Stop-Process -Name "KataMobile" -Force -ErrorAction SilentlyContinue
# Solo los Java que ejecutan el dashboard; otros programas Java del equipo no se tocan.
Get-CimInstance Win32_Process -Filter "Name = 'java.exe' OR Name = 'javaw.exe'" |
    Where-Object { $_.CommandLine -like "*AutomationDashboard*" -or $_.CommandLine -like "*KataMobileDashboard*" } |
    ForEach-Object { Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue }
Start-Sleep -Seconds 2

# =========================================================================
# 1. INYECCIÓN DEL PATH DE WiX
# =========================================================================
$wixBin = Join-Path ${env:ProgramFiles(x86)} "WiX Toolset v3.14\bin"
if (Test-Path $wixBin) {
    $env:PATH += ";$wixBin"
}
elseif (-not (Get-Command candle.exe -ErrorAction SilentlyContinue)) {
    throw "No se encontró WiX Toolset 3.14 (necesario para jpackage). Instálalo o agrega su carpeta bin al PATH."
}

$repositoryRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = Join-Path $repositoryRoot "kata-mobile-testing"
$distRoot = Join-Path $projectRoot "target\dashboard-dist"
$stagingRoot = Join-Path $projectRoot "target\dashboard-staging"

Push-Location $projectRoot
try {
    Write-Host "Compilando clases principales y de prueba..." -ForegroundColor Cyan
    mvn -DskipTests test-compile
    if ($LASTEXITCODE -ne 0) { throw "Maven no pudo compilar las clases del dashboard." }

    Remove-Item $distRoot -Recurse -Force -ErrorAction SilentlyContinue
    Remove-Item $stagingRoot -Recurse -Force -ErrorAction SilentlyContinue
    New-Item -ItemType Directory -Path $distRoot -Force | Out-Null
    New-Item -ItemType Directory -Path $stagingRoot -Force | Out-Null
    Write-Host "Carpetas de empaquetado creadas." -ForegroundColor DarkCyan

    Write-Host "Reuniendo dependencias..." -ForegroundColor Cyan
    mvn dependency:copy-dependencies -DincludeScope=test -DoutputDirectory="$distRoot\lib"
    if ($LASTEXITCODE -ne 0) { throw "Maven no pudo copiar las dependencias del dashboard." }
    
    $testClassesPath = Join-Path (Get-Location) "target\test-classes"
    if (-not (Test-Path (Join-Path $testClassesPath "com"))) {
        throw "No se encontraron las clases compiladas en $testClassesPath."
    }
    
    Copy-Item -Path (Join-Path $testClassesPath "*") -Destination $stagingRoot -Recurse -Force
    if (-not (Test-Path (Join-Path $stagingRoot "com"))) {
        throw "No se pudieron copiar las clases compiladas al staging del paquete."
    }

    Write-Host "Extrayendo librerías (esto puede tardar unos segundos)..." -ForegroundColor Cyan
    Get-ChildItem "$distRoot\lib\*.jar" | ForEach-Object {
        Push-Location $stagingRoot
        try {
            jar xf $_.FullName
        }
        finally {
            Pop-Location
        }
    }

    Remove-Item "$stagingRoot\META-INF\*.SF" -Force -ErrorAction SilentlyContinue
    Remove-Item "$stagingRoot\META-INF\*.RSA" -Force -ErrorAction SilentlyContinue
    Remove-Item "$stagingRoot\META-INF\*.DSA" -Force -ErrorAction SilentlyContinue

    Write-Host "Creando el archivo JAR base..." -ForegroundColor Cyan
    $jarPath = Join-Path $distRoot "KataMobileDashboard.jar"
    jar --create --file $jarPath --main-class com.kata.automation.gui.AutomationDashboard -C $stagingRoot .
    if ($LASTEXITCODE -ne 0) { throw "No se pudo crear el JAR ejecutable del dashboard." }
    
    Copy-Item (Join-Path $repositoryRoot "config") $distRoot -Recurse -Force
    # cmd falla al saltar a etiquetas si el .bat tiene finales de línea LF; se garantiza CRLF.
    $installerSource = Join-Path $repositoryRoot "InstalarDependencias.bat"
    $installerText = [System.IO.File]::ReadAllText($installerSource) -replace "`r?`n", "`r`n"
    [System.IO.File]::WriteAllText((Join-Path $distRoot "InstalarDependencias.bat"), $installerText,
        [System.Text.Encoding]::Default)

    # APK de la app para instalarla en el emulador (opción [2] de InstalarDependencias.bat).
    # Se toma el más reciente de la carpeta apk\ y se copia con un nombre fijo.
    $apk = Get-ChildItem (Join-Path $repositoryRoot "apk") -Filter "*.apk" -ErrorAction SilentlyContinue |
        Sort-Object LastWriteTime -Descending | Select-Object -First 1
    if ($apk) {
        $apkDirectory = Join-Path $distRoot "apk"
        New-Item -ItemType Directory -Path $apkDirectory -Force | Out-Null
        Copy-Item $apk.FullName (Join-Path $apkDirectory "KataMobile.apk") -Force
        Write-Host "APK incluido: $($apk.Name)" -ForegroundColor DarkCyan
    }
    else {
        Write-Host "⚠️ No hay APK en la carpeta apk\: el instalador no podrá instalar la app en un emulador." -ForegroundColor Yellow
    }

    # Incluye únicamente el Excel configurado (casos + hoja Parametros) para no confundir al usuario.
    $templateName = "datos_solicitudes_configurado.xlsx"
    $templateSource = Join-Path $projectRoot "src\test\resources\$templateName"
    $templateDirectory = Join-Path $distRoot "datos_solicitudes"
    if (-not (Test-Path $templateSource)) {
        throw "No se encontró la plantilla de solicitudes en $templateSource."
    }
    New-Item -ItemType Directory -Path $templateDirectory -Force | Out-Null
    Copy-Item $templateSource (Join-Path $templateDirectory $templateName) -Force
    Write-Host "Plantilla de solicitudes incluida en: $templateDirectory" -ForegroundColor DarkCyan

    # =========================================================================
    # 2. GENERACIÓN DEL INSTALADOR EXE (Auto-Incremento de Versión)
    # =========================================================================
    
    # Lee la última versión usada y le suma 1 (Ej: 1.0.0 -> 1.0.1)
    $versionFile = Join-Path $projectRoot "version.txt"
    if (-not (Test-Path $versionFile)) {
        Set-Content -Path $versionFile -Value "1.0.0"
    }
    $lastVersion = Get-Content $versionFile
    $versionParts = $lastVersion.Split('.')
    $newBuild = [int]$versionParts[2] + 1
    $dynamicVersion = "$($versionParts[0]).$($versionParts[1]).$newBuild"

    $packageType = "exe"
    # Ahora cada build se guarda en su propia carpeta, evadiendo bloqueos de Windows
    $outputPath = "C:\KataMobile_Releases\Version_$dynamicVersion"
    
    if (Test-Path $outputPath) {
        Write-Host "Limpiando el instalador anterior..." -ForegroundColor DarkCyan
        
        $retryCount = 0
        $isDeleted = $false
        
        while ($retryCount -lt 4 -and -not $isDeleted) {
            Remove-Item $outputPath -Recurse -Force -ErrorAction SilentlyContinue
            if (-not (Test-Path $outputPath)) {
                $isDeleted = $true
            }
            else {
                $retryCount++
                Write-Host "⚠️ Carpeta bloqueada. Reintentando en 2 segundos... ($retryCount/4)" -ForegroundColor Yellow
                Start-Sleep -Seconds 2
            }
        }

        if (-not $isDeleted) {
            throw "❌ Un proceso zombi sigue bloqueando la carpeta en C:\KataMobile_Releases. Ciérralo manualmente."
        }
    }
    New-Item -ItemType Directory -Path $outputPath -Force | Out-Null

    Write-Host "Iniciando jpackage para compilar el instalador (Versión $dynamicVersion)..." -ForegroundColor Cyan
    
    $jpackageArgs = @(
        "--type", $packageType,
        "--name", "KataMobile",
        "--input", $distRoot,
        "--main-jar", "KataMobileDashboard.jar",
        "--main-class", "com.kata.automation.gui.AutomationDashboard",
        "--dest", $outputPath,
        "--app-version", $dynamicVersion, 
        "--vendor", "GreenSQA",
        "--java-options", "-Dfile.encoding=UTF-8",
        "--win-shortcut",
        "--win-menu",
        "--win-dir-chooser",
        "--win-per-user-install",
        "--install-dir", "KataMobile",
        "--win-console",
        "--win-upgrade-uuid", "f9a8b7c6-d5e4-3f2a-1b0c-9d8e7f6a5b4c",
        "--icon", "icono.ico"
    )

    jpackage @jpackageArgs

    if ($LASTEXITCODE -ne 0) {
        throw "jpackage no pudo generar el paquete. Revisa los logs de arriba."
    }

    # La versión solo se consume cuando el instalador se generó correctamente.
    Set-Content -Path $versionFile -Value $dynamicVersion

    Write-Host "✅ ¡Instalador EXE generado exitosamente!" -ForegroundColor Green
    Write-Host "Encuentra tu instalador en: $outputPath" -ForegroundColor Yellow
}
finally {
    Pop-Location
}