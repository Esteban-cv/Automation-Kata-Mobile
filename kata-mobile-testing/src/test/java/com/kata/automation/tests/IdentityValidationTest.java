package com.kata.automation.tests;

import com.kata.automation.pages.IdentityValidationPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class IdentityValidationTest extends BaseTest {

    private IdentityValidationPage identityValidationPage;

    /**
     * Configuramos la página usando el driver que ya viene heredado de BaseTest.
     */
    @BeforeClass
    public void setUpPage() {
        // Al heredar de BaseTest, 'driver' ya está listo y conectado a Appium
        identityValidationPage = new IdentityValidationPage(driver);
    }

    /**
     * El test específico para validar los botones de Identidad
     */
    @Test
    public void testPestanaValidacionIdentidad() {
        System.out.println("🚀 Iniciando prueba de la pestaña Validación de Identidad...");
    
        try {
            System.out.println("Ejecutando método processIdentityValidation...");
            identityValidationPage.processIdentityValidation("Aprobar (En campo)", "38569302");
            
            System.out.println("✅ ¡El flujo de identidad terminó sin arrojar errores de XPath!");
            
        } catch (Exception e) {
            System.err.println("❌ Falló la prueba aislada:");
            e.printStackTrace();
            throw e; // Hacemos fallar TestNG
        }
    }

    // Nota: Si tu BaseTest ya tiene el cierre del driver en su propio @AfterClass, 
    // puedes omitir el tearDown aquí para evitar conflictos de doble cierre.
}