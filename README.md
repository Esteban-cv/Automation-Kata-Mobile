# Automation-Kata-Mobile
Automation-Kata-Mobile

## Dashboard de ejecuciones

Desde `kata-mobile-testing` se puede abrir la interfaz gráfica para seleccionar un
archivo `.xlsx`, indicar cuántas corridas realizar y revisar los resultados:

```powershell
mvn test-compile
mvn -Dexec.classpathScope=test -Dexec.mainClass=com.kata.automation.gui.AutomationDashboard org.codehaus.mojo:exec-maven-plugin:3.5.0:java
```

Antes de ejecutar solicitudes deben estar disponibles el servidor Appium y el
dispositivo Android configurado en `DriverManager`. La interfaz reutiliza el
flujo de `FlujoCompletoTest`; no crea una segunda implementación del proceso.

También se puede abrir con doble clic en `AbrirDashboard.bat`, ubicado en la
raíz del repositorio. Para crear un acceso directo en el escritorio:

1. Haz clic derecho sobre `AbrirDashboard.bat`.
2. Selecciona `Mostrar más opciones` > `Enviar a` > `Escritorio (crear acceso directo)`.
3. Desde el escritorio, puedes cambiarle el nombre a `Dashboard Kata Mobile`.

Por ahora el lanzador es `.bat`, porque el dashboard depende de Maven, TestNG,
Appium y las clases de prueba existentes. Más adelante se puede empaquetar como
`.exe` con `jpackage`, incluyendo un runtime de Java y todas esas dependencias.

## Empaquetar para compartir

La configuración del dispositivo y Appium está en `config/robot.properties`. No
incluye contraseñas. Para generar una aplicación portable:

```powershell
Set-ExecutionPolicy -Scope Process Bypass
.\EmpaquetarDashboard.ps1
```

El resultado queda en `kata-mobile-testing/target/dashboard-package`. Para crear
un instalador `.exe` se necesita instalar WiX Toolset y ejecutar:

```powershell
.\EmpaquetarDashboard.ps1 -Installer
```

Cada computador que ejecute el robot todavía necesita Appium, ADB y un
dispositivo Android accesible. El instalador no mata procesos ni borra la
carpeta temporal del sistema.

La aplicación incluye la carpeta `datos_solicitudes` con el archivo
`datos_solicitudes.xlsx`, que sirve como plantilla para cargar las solicitudes
en el dashboard. Las evidencias y el historial de ejecuciones se guardan en
`evidencias`; los diagnósticos de cada ejecución se guardan en `Logs`.
