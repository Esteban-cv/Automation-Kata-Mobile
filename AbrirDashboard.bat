@echo off
setlocal

cd /d "%~dp0kata-mobile-testing"
rem El compilador de VS Code escribe en la misma carpeta target y puede dejar clases
rem rotas ("Unresolved compilation problem"); se recompila limpio con Maven.
if exist target\test-classes rmdir /s /q target\test-classes
call mvn -q test-compile
if errorlevel 1 (
    echo.
    echo No se pudo compilar el dashboard. Revisa el mensaje anterior.
    pause
    exit /b 1
)

call mvn -q -Dexec.classpathScope=test -Dexec.mainClass=com.kata.automation.gui.AutomationDashboard org.codehaus.mojo:exec-maven-plugin:3.5.0:java
if errorlevel 1 (
    echo.
    echo El dashboard termino con un error.
    pause
)

endlocal