package com.kata.automation.pages;

import org.openqa.selenium.By;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

public class AnalysisPage extends BasePage {

        // ========================================================================
        // LOCALIZADORES: ANÁLISIS CUALITATIVO Y CUANTITATIVO
        // ========================================================================

        // ========================================================================
        // LOCALIZADORES: GEO REFERENCIAS E INGRESO REFERENCIAS
        // ========================================================================

        private static final By ANALYSIS_BUTTON = By.xpath(
                        "//android.widget.TextView[@text=\"Análisis cualitativo y cuantitativo\"]");

        private static final By ADD_RECORD_BUTTON = By.xpath("//android.widget.Button");
        private static final By CONFIRM_BUTTON = By.xpath("//android.widget.TextView[@text=\"GUARDAR\"]");
        private static final By GO_BACK = AppiumBy.accessibilityId("Ir atrás");

        // Botón para abrir el modal de Geo Referencias
        private static final By OPEN_GEO_REFERENCES = By.xpath(
                        "//android.widget.TextView[@text=\"Geo Referencias\"]/following::android.widget.Button");

        // Botón para abrir el modal de Ingreso Referencias
        private static final By OPEN_ENTRY_REFERENCES = By.xpath(
                        "//android.widget.TextView[@text=\"Ingreso Referencias\"]/following::android.widget.Button");

        // Botones de ingreso a las secciones (se pasan como parámetro a los métodos)
        public static final By GEO_REFERENCES_SECTION = By.xpath(
                        "//android.widget.TextView[@text=\"Geo Referencias\"]/following::android.widget.Button");
        public static final By ENTRY_REFERENCES_SECTION = By.xpath(
                        "//android.widget.TextView[@text=\"Ingreso Referencias\"]/following::android.widget.Button");

        // --- Campos Exclusivos de Geo Referencias ---
        private static final By GEO_FIRST_NAME = By.xpath(
                        "//android.widget.TextView[@text=\"Primer nombre\"]/following::android.widget.EditText");
        private static final By GEO_FIRST_LAST_NAME = By.xpath(
                        "//android.widget.TextView[@text=\"Primer apellido\"]/following::android.widget.EditText");

        // Dropdowns geográficos
        private static final By MODAL_DEPARTMENT = By.xpath(
                        "//android.widget.TextView[@text=\"Departamento\"]/following::android.view.View[@content-desc=\"Menú desplegable\"]");
        private static final By MODAL_CITY = By.xpath(
                        "//android.widget.TextView[@text=\"Ciudad\"]/following::android.view.View[@content-desc=\"Menú desplegable\"]");
        private static final By MODAL_NEIGHBORHOOD = By.xpath(
                        "//android.widget.TextView[@text=\"Barrio\"]/following::android.view.View[@content-desc=\"Menú desplegable\"]");

        // --- Campos Exclusivos de Ingreso Referencias ---
        private static final By REF_TYPE = By.xpath(
                        "//android.widget.TextView[@text=\"Tipo de referencia\"]/following::android.view.View[@content-desc=\"Menú desplegable\"]");
        private static final By REF_FULL_NAME = By.xpath(
                        "//android.widget.TextView[@text=\"Nombre completo\"]/following::android.widget.EditText");
        private static final By REF_RELATIONSHIP = By.xpath(
                        "//android.widget.TextView[@text=\"Relación con el solicitante\"]/following::android.view.View[@content-desc=\"Menú desplegable\"]");

        // Ojo: Se deja "Opinion" sin tilde porque así está en el código fuente de la
        // app.
        private static final By REF_ACTIVITY = By.xpath(
                        "//android.widget.TextView[@text=\"¿Qué actividad tiene o realiza el solicitante?\"]/following::android.widget.EditText");
        private static final By REF_ACTIVITY_TIME = By.xpath(
                        "//android.widget.TextView[@text=\"¿Cuánto tiempo lleva con la actividad (meses)?\"]/following::android.widget.EditText");

        // --- Campos Compartidos (Sirven para ambos modales) ---
        private static final By MODAL_ADDRESS = By.xpath(
                        "//android.widget.TextView[@text=\"Dirección\"]/following::android.widget.EditText");
        private static final By MODAL_CONTACT_NUMBER = By.xpath(
                        "//android.widget.TextView[@text=\"Número de contacto\"]/following::android.widget.EditText");
        private static final By MODAL_OBSERVATIONS = By.xpath(
                        "//android.widget.TextView[@text=\"Observaciones\"]/following::android.widget.EditText");

        // --- Análisis cualitativo ---
        private static final By REPUTATION_OBSERVATIONS = By.xpath(
                        "//android.widget.TextView[@text=\"Observaciones de reputación\"]/following::android.widget.EditText");
        private static final By STABILITY = By.xpath(
                        "//android.widget.TextView[@text=\"Estabilidad\"]/following::android.widget.EditText");
        private static final By BUSINESS_ABILITY = By.xpath(
                        "//android.widget.TextView[@text=\"Habilidad empresarial\"]/following::android.widget.EditText");
        private static final By PAYMENT_BEHAVIOR = By.xpath(
                        "//android.widget.TextView[@text=\"Comportamiento de pagos\"]/following::android.widget.EditText");

        private static final By RESOURCE_ORIGIN_COUNTRY = By.xpath(
                        "//android.widget.TextView[@text=\"País de origen de los recursos\"]/following::android.view.View[@content-desc=\"Menú desplegable\"]");

        // --- Resumen de información financiera ---
        private static final By TOTAL_SALES = By.xpath(
                        "//android.widget.TextView[@text=\"Total Ventas\"]/following::android.widget.EditText");
        private static final By TOTAL_PURCHASES = By.xpath(
                        "//android.widget.TextView[@text=\"Total Compras\"]/following::android.widget.EditText");
        private static final By BUSINESS_EXPENSES = By.xpath(
                        "//android.widget.TextView[@text=\"Gastos del negocio\"]/following::android.widget.EditText");
        private static final By FINANCIAL_EXPENSES = By.xpath(
                        "//android.widget.TextView[@text=\"Gastos financieros\"]/following::android.widget.EditText");

        private static final By TOTAL_OTHER_INCOME = By.xpath(
                        "//android.widget.TextView[@text=\"Total otros ingresos\"]/following::android.widget.EditText");
        // Nota: El texto exacto de la app tiene un error tipográfico "fllujo" con doble
        // L. Se deja así para que funcione.
        private static final By OTHER_CASH_FLOW_INCOME = By.xpath(
                        "//android.widget.TextView[@text=\"Otros ingresos fllujo de caja (según políticas)\"]/following::android.widget.EditText");
        private static final By OTHER_CASH_DESCRIPTION = By.xpath(
                        "//android.widget.TextView[@text=\"Descripción de otros ingresos\"]/following::android.widget.EditText");

        // Nota: Falta espacio antes del paréntesis en la app.
        private static final By TOTAL_FAMILY_CONTRIBUTIONS = By.xpath(
                        "//android.widget.TextView[@text=\"Total aportes familiares(según políticas)\"]/following::android.widget.EditText");
        private static final By FAMILY_EXPENSES = By.xpath(
                        "//android.widget.TextView[@text=\"Gastos Familiares\"]/following::android.widget.EditText");

        private static final By TOTAL_BUSINESS_ASSETS = By.xpath(
                        "//android.widget.TextView[@text=\"Total bienes del negocio\"]/following::android.widget.EditText");
        private static final By BUSINESS_LIABILITIES = By.xpath(
                        "//android.widget.TextView[@text=\"Pasivos del negocio\"]/following::android.widget.EditText");

        private static final By W_ACCOUNTS = By.xpath(
                        "//android.widget.TextView[@text=\"Cuentas W\"]/following::android.widget.EditText");
        private static final By TOTAL_ASSETS = By.xpath(
                        "//android.widget.TextView[@text=\"Total de activos\"]/following::android.widget.EditText");
        private static final By TOTAL_LIABILITIES = By.xpath(
                        "//android.widget.TextView[@text=\"Pasivos totales\"]/following::android.widget.EditText");

        // Botón de validación final
        private static final By VALIDATE_FINANCIAL_INFO_BTN = By.xpath(
                        "//android.widget.TextView[@text=\"Validar información financiera\"]/following::android.view.View");

        // Atrapa el único campo de texto disponible en esa ventana de búsqueda
        private static final By SEARCH_INPUT = By.xpath("//android.widget.EditText");

        private static final By REF_HOW_LONG_KNOWN = By.xpath(
                        "//android.widget.TextView[@text=\"¿Hace cuanto tiempo lo conoce?\"]/following::android.view.View[@content-desc=\"Menú desplegable\"]");
        
        private static final By CLIENT_OPINION = By.xpath(
                        "//android.widget.TextView[@text=\"Opinion del solicitante\"]/following::android.view.View[@content-desc=\"Menú desplegable\"]");

        private static final By ACCEPT_BUTTON = By.xpath("//android.widget.TextView[@text=\"ACEPTAR\"]");
        
        public AnalysisPage(AndroidDriver driver) {
                super(driver);
        }

        /**
         * Llena exactamente 2 registros de Geo Referencias (Obligatorio).
         */
        public void fillGeoReferences(By openSectionLocator) {
                if (openSectionLocator != null) {
                        click(openSectionLocator);
                }
                sleep(500);

                for (int i = 1; i <= 2; i++) {
                        System.out.println("Agregando Geo Referencia #" + i);
                        clickSafely(ADD_RECORD_BUTTON);

                        type(GEO_FIRST_NAME, "Carlos");
                        type(GEO_FIRST_LAST_NAME, "Perez");

                        scrollToText("Dirección", 0.15);
                        type(MODAL_ADDRESS, "Calle Falsa 123");

                        scrollAndClickReliable("Departamento", MODAL_DEPARTMENT, 0.10);
                        click(byVisibleText("ANTIOQUIA"));

                        findTextWithSearch(MODAL_CITY, SEARCH_INPUT, "MEDELLÍN");

                        findTextWithSearch(MODAL_NEIGHBORHOOD, SEARCH_INPUT, "VILLA GUADALUPE");

                        scrollToText("Observaciones", 0.15);
                        type(MODAL_OBSERVATIONS, "Geo referencia confirmada y verificada.");

                        sleep(200);
                        clickSafely(CONFIRM_BUTTON);
                        clickSafely(CONFIRM_BUTTON);
                        sleep(500); // Pausa para que cierre el modal

                }

                System.out.println("--- Geo Referencias completadas ---");
                clickSafely(GO_BACK);
        }

        /**
         * Llena 3 registros: 2 Comerciales/Personales y 1 Familiar (Obligatorio).
         */
        public void fillReferences(By openSectionLocator) {
                if (openSectionLocator != null) {
                        click(openSectionLocator);
                }
                sleep(1000);

                for (int i = 1; i <= 3; i++) {
                        System.out.println("Agregando Ingreso Referencia #" + i);
                        clickSafely(ADD_RECORD_BUTTON);

                        if (i <= 2) {
                                clickComposeDropdown(REF_TYPE);
                                click(byVisibleText("Referencia Comercial y/o Personal"));
                                sleep(500); // Pausa necesaria para que Compose actualice la UI

                                // 1. CAMPOS COMUNES SUPERIORES
                                type(REF_FULL_NAME, "Referencia Solicitante " + i);
                                type(MODAL_ADDRESS, "Carrera 45 # 12-34");

                                scrollToText("Número de contacto", 0.15);
                                type(MODAL_CONTACT_NUMBER, "315987654" + i);

                                scrollAndClickReliable("Relación con el solicitante", REF_RELATIONSHIP, 0.15);
                                clickSafely(byVisibleText("Amigo"));

                                scrollAndClickReliable("¿Hace cuanto tiempo lo conoce?", REF_HOW_LONG_KNOWN, 0.15);
                                clickSafely(byVisibleText("De 37 a 59 meses"));

                                scrollAndClickReliable("Opinion del solicitante", CLIENT_OPINION, 0.15);
                                clickSafely(byVisibleText("Buen Amigo"));

                                answerQuestion("¿Es propietario del negocio?", "No");

                                scrollToText("Observaciones", 0.15);

                                type(REF_ACTIVITY, "Comerciante");
                                type(REF_ACTIVITY_TIME, "60");
                                type(MODAL_OBSERVATIONS, "Ninguna");

                                clickSafely(CONFIRM_BUTTON);
                                sleep(200);
                                click(CONFIRM_BUTTON);
                        } else{
                                clickComposeDropdown(REF_TYPE);
                                click(byVisibleText("Referencia Familiar"));
                                sleep(500); // Pausa necesaria para que Compose actualice la UI

                                // 1. CAMPOS COMUNES SUPERIORES
                                type(REF_FULL_NAME, "Referencia Solicitante " + i);
                                type(MODAL_ADDRESS, "Carrera 45 # 12-34");

                                scrollToText("Número de contacto", 0.15);
                                type(MODAL_CONTACT_NUMBER, "315987654" + i);

                                scrollAndClickReliable("Relación con el solicitante", REF_RELATIONSHIP, 0.15);
                                click(byVisibleText("Padre"));

                                scrollAndClickReliable("Opinion del solicitante", CLIENT_OPINION, 0.15);
                                clickSafely(byVisibleText("Buen Amigo"));

                                scrollToText("Observaciones", 0.15);

                                type(REF_ACTIVITY, "Comerciante");
                                type(REF_ACTIVITY_TIME, "60");
                                type(MODAL_OBSERVATIONS, "Ninguna");

                                clickSafely(CONFIRM_BUTTON);
                                sleep(200);
                                click(CONFIRM_BUTTON);
                        }
                }

                System.out.println("--- Ingreso Referencias completado ---");
                clickSafely(GO_BACK);
        }

        /**
         * Llena exclusivamente los campos obligatorios (rojos) de la pestaña de
         * Análisis Cualitativo y Cuantitativo.
         */
        public void fillQualitativeAndQuantitativeAnalysis() {
                System.out.println("--- Iniciando formulario: Análisis Cualitativo y Cuantitativo ---");
                click(ANALYSIS_BUTTON);

                fillGeoReferences(OPEN_GEO_REFERENCES);
                fillReferences(OPEN_ENTRY_REFERENCES);

                // 1. Textos Cualitativos[cite: 8, 9]
                type(REPUTATION_OBSERVATIONS, "Cliente con buena reputación en el sector comercial.");

                type(STABILITY, "Negocio estable con ubicación fija.");

                type(BUSINESS_ABILITY, "Adecuada administración y conocimiento del negocio.");

                scrollToText("Comportamiento de pagos", 0.15);
                type(PAYMENT_BEHAVIOR, "Buen hábito de pago, al día con proveedores.");

                sleep(300);

                // 2. Selección de Opciones[cite: 9]
                scrollToText("¿Cliente es exclusivo?", 0.15);
                answerQuestion("¿Cliente es exclusivo?", "Si");

                scrollAndClickReliable("País de origen de los recursos", RESOURCE_ORIGIN_COUNTRY, 0.15);
                click(byVisibleText("Colombia"));

                // 3. Información Financiera Básica[cite: 9, 10]
                scrollToText("Total Ventas", 0.15);
                type(TOTAL_SALES, "40000000"); // 40M (Coherente para microcrédito)
                type(TOTAL_PURCHASES, "15000000"); // 1.2M

                scrollToText("Gastos del negocio", 0.15);
                type(BUSINESS_EXPENSES, "6000000");
                type(FINANCIAL_EXPENSES, "1500000");

                // 4. Ingresos adicionales (En 0 por defecto)[cite: 10]
                scrollToText("Total otros ingresos", 0.15);
                type(TOTAL_OTHER_INCOME, "2000000");
                //type(OTHER_CASH_FLOW_INCOME, "0");

                scrollToText("Total aportes familiares(según políticas)", 0.15);

                //scrollToText("Descripción de otros ingresos", 0.15);
                type(OTHER_CASH_DESCRIPTION, "Ahorros");

                type(OTHER_CASH_FLOW_INCOME, "1000000");

                // 5. Entorno Familiar[cite: 10, 11]
                //scrollToText("Total aportes familiares(según políticas)", 0.15);
                type(TOTAL_FAMILY_CONTRIBUTIONS, "2000000");

                scrollToText("Gastos Familiares", 0.15);
                type(FAMILY_EXPENSES, "800000"); // Gastos de manutención básicos

                // 6. Activos y Pasivos[cite: 11, 12]
                scrollToText("Total bienes del negocio", 0.15);
                type(TOTAL_BUSINESS_ASSETS, "5000000");
                type(BUSINESS_LIABILITIES, "2000000");

                scrollToText("Cuentas W", 0.15);
                type(W_ACCOUNTS, "1000000");

                scrollToText("Total de activos", 0.15);
                type(TOTAL_ASSETS, "10000000");
                type(TOTAL_LIABILITIES, "1000000");

                // 7. Cierre del formulario[cite: 12]
                scrollAndClickReliable("Validar información financiera", VALIDATE_FINANCIAL_INFO_BTN, 0.15);
                click(ACCEPT_BUTTON);

                System.out.println("--- Análisis Cualitativo y Cuantitativo Finalizado ---");
        }

}