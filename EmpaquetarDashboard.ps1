$ErrorActionPreference = "Stop"

# =========================================================================
# 1. INYECCIÓN DEL PATH: Agregamos WiX a las variables de entorno para esta sesión
# Esto soluciona tu problema de variables sin tener que tocar Windows[cite: 32].
# =========================================================================
$env:PATH += ";C:\Archivos de programa (x86)\WiX Toolset v3.14\bin"

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

    # =========================================================================
    # 2. GENERACIÓN DEL INSTALADOR EXE (Forzado siempre a "exe")
    # =========================================================================
    $packageType = "exe"
    $outputPath = Join-Path $projectRoot "target\dashboard-package"
    Remove-Item $outputPath -Recurse -Force -ErrorAction SilentlyContinue
    New-Item -ItemType Directory -Path $outputPath | Out-Null

    Write-Host "Iniciando jpackage para compilar el instalador $packageType..." -ForegroundColor Cyan
    
    $jpackageArgs = @(
        "--type", $packageType,
        "--name", "KataMobile",
        "--input", $distRoot,
        "--main-jar", "KataMobileDashboard.jar",
        "--main-class", "com.kata.automation.gui.AutomationDashboard",
        "--dest", $outputPath,
        "--app-version", "1.0.0",
        "--vendor", "GreenSQA",
        "--java-options", "-Dfile.encoding=UTF-8",
        "--win-shortcut",
        "--win-menu",
        "--win-dir-chooser" 
    )

    jpackage @jpackageArgs

    if ($LASTEXITCODE -ne 0) {
        throw "jpackage no pudo generar el paquete. Revisa los logs de arriba."
    }

    Write-Host "✅ ¡Instalador EXE generado exitosamente!" -ForegroundColor Green
    Write-Host "Encuentra tu .exe en: $outputPath" -ForegroundColor Yellow
}
finally {
    Pop-Location
}