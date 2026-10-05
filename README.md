# Automation-Kata-Mobile
Automation-Kata-Mobile

## Dashboard de ejecuciones

Desde `kata-mobile-testing` se puede abrir la interfaz gráfica para seleccionar un
archivo `.xlsx`, indicar cuántas corridas realizar y revisar los resultados:

```powershell
mvn test-compile
mvn -Dexec.classpathScope=test -Dexec.mainClass=com.kata.automation.gui.AutomationDashboard org.codehaus.mojo:exec-maven-plugin:3.5.0:java
```

Antes de ejecutar solicitudes debe estar disponible un dispositivo Android
configurado en `config/robot.properties`. Al pulsar **Iniciar ejecución**, el
dashboard verifica si Appium ya está activo y, si no lo está, inicia
automáticamente `appium --relaxed-security`. Solo se detiene el proceso de
Appium que haya iniciado el dashboard.

También se puede abrir con doble clic en `AbrirDashboard.bat`, ubicado en la
raíz del repositorio. Para crear un acceso directo en el escritorio:

1. Haz clic derecho sobre `AbrirDashboard.bat`.
2. Selecciona `Mostrar más opciones` > `Enviar a` > `Escritorio (crear acceso directo)`.
3. Desde el escritorio, puedes cambiarle el nombre a `Dashboard Kata Mobile`.

El lanzador `.bat` sirve para desarrollo. El instalador generado con `jpackage`
incluye un runtime de Java, el dashboard y sus dependencias Java; las
dependencias externas de Android se preparan una sola vez con
`InstalarDependencias.bat`.

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

### Preparar el PC de un compañero

Después de instalar el `.exe`, abre la carpeta instalada y ejecuta
`InstalarDependencias.bat`. El script instala Node.js LTS, Android SDK
Platform-Tools (ADB), Appium y el driver `uiautomator2`. Si Node.js acaba de
instalarse, cierra la consola y vuelve a ejecutar el script en una ventana
nueva para que el PATH se actualice. El script configura automáticamente las
variables de usuario `ANDROID_HOME`, `ANDROID_SDK_ROOT`, `JAVA_HOME` y las
entradas de PATH para ADB, npm y Appium; no es necesario editarlas a mano.
Finalmente, conecta un dispositivo Android con **USB debugging** activo y
acepta la autorización RSA.

El servidor se inicia de forma transparente al presionar **Iniciar ejecución**;
no es necesario abrir otra consola con Appium. Si no inicia, revisa
`Logs\appium.log` dentro de la carpeta de trabajo de la aplicación.

La aplicación incluye la carpeta `datos_solicitudes` con el archivo
`datos_solicitudes.xlsx`, que sirve como plantilla para cargar las solicitudes
en el dashboard. Las evidencias y el historial de ejecuciones se guardan en
`evidencias`; los diagnósticos de cada ejecución se guardan en `Logs`.
