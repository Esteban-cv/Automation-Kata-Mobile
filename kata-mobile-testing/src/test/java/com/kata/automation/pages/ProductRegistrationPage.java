package com.kata.automation.pages;

import java.time.LocalDate;

import org.openqa.selenium.By;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

public class ProductRegistrationPage extends BasePage {

        // ========================================================================
        // LOCALIZADORES: REGISTRO DEL PRODUCTO
        // ========================================================================

        private static final By PRODUCT_REGISTRATION_BUTTON = By.xpath(
                        "//android.widget.TextView[@text=\"Registro del producto\"]");

        // --- Selección del producto ---
        private static final By PRODUCT_TYPE = By.xpath(
                        "//android.widget.TextView[@text=\"Tipo de producto\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
        private static final By PRODUCT = By.xpath(
                        "//android.widget.TextView[@text=\"Producto\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

        private static final By SUGGESTED_AMOUNT = By.xpath(
                        "//android.widget.TextView[@text=\"Monto sugerido\"]/following::android.widget.EditText[1]");
        private static final By SUGGESTED_TERM = By.xpath(
                        "//android.widget.TextView[@text=\"Plazo sugerido\"]/following::android.widget.EditText[1]");

        private static final By PAYMENT_FREQUENCY = By.xpath(
                        "//android.widget.TextView[@text=\"Frecuencia de pago sugerida\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
        private static final By ECONOMIC_DESTINATION = By.xpath(
                        "//android.widget.TextView[@text=\"Destino económico del crédito\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

        // --- Fechas ---
        // Nota: En Compose, al lado del texto suele estar el contenedor del EditText o
        // la Vista cliqueable.
        private static final By DISBURSEMENT_DATE = By.xpath(
                        "//android.widget.TextView[@text=\"Fecha de desembolso\"]/following::android.view.View[1]");
        private static final By FIRST_INSTALLMENT_DATE = By.xpath(
                        "//android.widget.TextView[@text=\"Fecha de primera cuota\"]/following::android.view.View[1]");

        // --- Botones de acción final ---
        private static final By VALIDATE_GUARANTEES_BTN = By.xpath(
                        "//android.widget.TextView[@text=\"Validar garantías y seguros\"]/following::android.view.View[1]");

        // Asumiendo la estructura estándar de botones de la app para el último paso
        private static final By SIMULATE_CREDIT_BTN = By.xpath(
                        "//android.widget.TextView[@text=\"Simular crédito\"]/following::android.view.View[.//android.widget.Button][1]");

        private static final By ACCEPT_BUTTON = By.xpath("//android.widget.TextView[@text=\"ACEPTAR\"]");

        // CHECKBOXES DE SEGUROS VOLUNTARIOS

        private static final By PROTECTED_FAMILY = By.xpath(
                        "//android.widget.TextView[@text=\"Tu familia protegida\"]");

        private static final By PROTECTED_HOME_BUSSINESS = By.xpath(
                        "//android.widget.TextView[@text=\"Tu hogar y negocio protegidos\"]");

        private static final By PROTECTED_MICRO_ENTREPRENEURS = By.xpath(
                        "//android.widget.TextView[@text=\"Microempesarios protegidos\"]");

        private static final By PROTECTED_LIFE = By.xpath(
                        "//android.widget.TextView[@text=\"Tu vida protegida\"]");

        private static final By FUNERAL_PLANNING = By.xpath(
                        "//android.widget.TextView[@text=\"Previsión Exequial\"]");

        // BOTONCES DE LOS FORMULARIOS DE SEGUROS
        private static final By BENEFICIARIES_BTN = By.xpath(
                        "//android.widget.TextView[@text=\"Beneficiarios seguros voluntarios\"]/following::android.widget.Button");
        private static final By PROPERTY_TO_INSURE_BTN = By.xpath(
                        "//android.widget.TextView[@text=\"Inmueble a asegurar\"]/following::android.widget.Button");

        // LOCALIZADORES DE AÑADIR BENEFICIARIOS
        private static final By ADD = By.className("android.widget.Button");
        private static final By BENE_TYPE_ID = By.xpath(
                        "//android.widget.TextView[@text=\"Tipo de identificación del beneficiario\"]/following::android.view.View[@content-desc=\"Menú desplegable\"]");
        private static final By BENE_NUMBER_ID = By.xpath(
                        "//android.widget.TextView[@text=\"Número de identificación del beneficiario\"]/following::android.widget.EditText");
        private static final By BENE_COUNTRY = By.xpath(
                        "//android.widget.TextView[@text=\"País de origen de la identificación del beneficiario\"]/following::android.view.View[@content-desc=\"Menú desplegable\"]");
        private static final By BENE_FIRST_NAME = By.xpath(
                        "//android.widget.TextView[@text=\"Primer apellido beneficiario\"]/following::android.widget.EditText");
        private static final By BENE_NAME = By.xpath(
                        "//android.widget.TextView[@text=\"Primer nombre beneficiario\"]/following::android.widget.EditText");
        private static final By BENE_RELATIONSHIP = By.xpath(
                        "//android.widget.TextView[@text=\"Parentesco del beneficiario\"]/following::android.view.View[@content-desc=\"Menú desplegable\"]");
        private static final By BENE_PORCENTAGE = By.xpath(
                        "//android.widget.TextView[@text=\"Porcentaje del beneficiario\"]/following::android.widget.EditText");
        private static final By BENE_VALIDATE_BTN = By.xpath(
                        "//android.widget.TextView[@text=\"Validar el beneficiario\"]/following::android.widget.Button");

        // ========================================================================
        // LOCALIZADORES: MODAL INMUEBLE A ASEGURAR
        // ========================================================================

        private static final By PROP_ADDRESS = By.xpath(
                        "//android.widget.TextView[@text=\"Dirección del inmueble\"]/following::android.widget.EditText[1]");

        private static final By PROP_DEPARTMENT = By.xpath(
                        "//android.widget.TextView[@text=\"Departamento del inmueble\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
        private static final By PROP_CITY = By.xpath(
                        "//android.widget.TextView[@text=\"Ciudad del inmueble\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
        private static final By PROP_NEIGHBORHOOD = By.xpath(
                        "//android.widget.TextView[@text=\"Barrio del inmueble\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

        private static final By PROP_USAGE = By.xpath(
                        "//android.widget.TextView[@text=\"Uso\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
        private static final By PROP_TYPE = By.xpath(
                        "//android.widget.TextView[@text=\"Tipo de inmueble\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

        private static final By PROP_FLOOR_NUMBER = By.xpath(
                        "//android.widget.TextView[@text=\"Número de piso del Inmueble\"]/following::android.widget.EditText[1]");
        private static final By PROP_BASEMENT_NUMBER = By.xpath(
                        "//android.widget.TextView[@text=\"Número de sótano del Inmueble\"]/following::android.widget.EditText[1]");

        // Atrapa el único campo de texto disponible en esa ventana de búsqueda
        private static final By SEARCH_INPUT = By.xpath("//android.widget.EditText");
        // BOTON DE IR ATRAS
        private static final By GO_BACK = AppiumBy.accessibilityId("Ir atrás");
        // BOTON GUARDAR
        private static final By SAVE_BUTTON = By.xpath("//android.widget.TextView[@text=\"GUARDAR\"]");

        // --- Checkboxes de Seguros (Para usar después) ---
        // Ojo al error de la app: "Microempesarios" (sin R)
        // private static final By INS_FAMILY =
        // By.xpath("//android.widget.TextView[@text=\"Tu familia
        // protegida\"]/following-sibling::android.widget.CheckBox");
        // private static final By INS_MICRO =
        // By.xpath("//android.widget.TextView[@text=\"Microempesarios
        // protegidos\"]/following-sibling::android.widget.CheckBox");

        public ProductRegistrationPage(AndroidDriver driver) {
                super(driver);
        }

        /**
         * Llena el flujo principal de Registro del Producto usando parámetros externos
         * (Ej: Desde Excel).
         */
        public void fillProductRegistration(String numberId, String productType, String product, String amount,
                        String term,
                        String paymentFreq, String destination, String hasFNG,
                        String hasBail, String hasRealGuarantee,
                        String comprehensiveInsurance, String coDebtors,
                        String hasFamilyInsurance, String hasHomeBussinessInsurance,
                        String hasMicroEntrepreneursInsurance, String hasLifeInsurance,
                        String hasFuneralPlanning,
                        int disbDay, int disbMonth, int disbYear,
                        String hasSavings) {


                LocalDate calculatedDate = LocalDate.of(disbYear, disbMonth, disbDay).plusDays(30);

                waitForVisibility(PRODUCT_REGISTRATION_BUTTON);
                click(PRODUCT_REGISTRATION_BUTTON);

                // 1. Selección del producto y montos
                clickComposeDropdown(PRODUCT_TYPE);
                clickSafely(byVisibleText(productType));

                clickComposeDropdown(PRODUCT);
                clickSafely(byVisibleText(product));

                type(SUGGESTED_AMOUNT, amount);

                // No necesita scroll porque está pegado al monto
                type(SUGGESTED_TERM, term);

                takeEvidence(numberId, "07_Selección_producto");

                scrollAndClickReliable("Frecuencia de pago sugerida", PAYMENT_FREQUENCY,
                                0.15);
                clickSafely(byVisibleText(paymentFreq));

                scrollAndClickReliable("Destino económico del crédito", ECONOMIC_DESTINATION,
                                0.15);
                clickSafely(byVisibleText(destination));

                // AQUI VA EL SALTO: Los seguros voluntarios se ignoran en este método
                // principal.

                configureVoluntaryInsurances(hasFamilyInsurance, hasHomeBussinessInsurance,
                                hasMicroEntrepreneursInsurance, hasLifeInsurance, hasFuneralPlanning, numberId);

                // 2. Ahorros y Fechas
                scrollToText("¿Tiene ahorro?", 0.20);
                answerQuestion("¿Tiene ahorro?", hasSavings);

                scrollAndClickReliable("Fecha de desembolso", DISBURSEMENT_DATE, 0.15);
                selectDateInCalendar(disbDay, disbMonth, disbYear);

                scrollAndClickReliable("Fecha de primera cuota", FIRST_INSTALLMENT_DATE, 0.15);
                selectDateInCalendar(calculatedDate.getDayOfMonth(), calculatedDate.getMonthValue(),
                                calculatedDate.getYear());

                // 3. Validación de Garantías
                scrollAndClickReliable("Validar garantías y seguros", VALIDATE_GUARANTEES_BTN, 0.15);

                waitForVisibility(ACCEPT_BUTTON);
                takeEvidence(numberId, "Mensaje_validacion_seguros");
                click(ACCEPT_BUTTON);

                sleep(3000); // Pausa vital para que el sistema procese y marque los radio buttons solos.

                // 4. Llenar obligatoriamente FNG y Garantía Real (Como lo pediste)
                // OJO: Texto exacto de la app, sin signo de interrogación al final
                answerQuestion("¿Requiere garantía FNG", hasFNG);

                answerQuestion("¿Requiere Fianza?", hasBail);

                answerQuestion("¿Tiene garantía real?", hasRealGuarantee);

                // 5. Simular
                scrollAndClickReliable("Simular crédito", SIMULATE_CREDIT_BTN, 0.15);
                waitForVisibility(ACCEPT_BUTTON);
                takeEvidence(numberId, "Simulación_crédito");

                click(ACCEPT_BUTTON);

        }

        /**
         * Método aislado para configurar los seguros voluntarios.
         * Se deja vacío por ahora para no afectar el flujo principal, tal como se
         * solicitó.
         * Cuando vayas a automatizar los modales de familiares protegidos, lo
         * construimos aquí.
         */
        public void configureVoluntaryInsurances(String hasFamilyInsurance, String hasHomeBussinessInsurance,
                        String hasMicroEntrepreneursInsurance, String hasLifeInsurance,
                        String hasFuneralPlanning, String numberId) {


                // ==========================================
                // 1. MARCAR LOS CHECKBOXES HACIENDO CLIC EN EL TEXTO
                // ==========================================
                if ("si".equalsIgnoreCase(hasFamilyInsurance)) {
                        scrollAndClickReliable("Tu familia protegida", PROTECTED_FAMILY, 0.15);
                        sleep(700);
                }
                if ("si".equalsIgnoreCase(hasHomeBussinessInsurance)) {
                        scrollAndClickReliable("Tu hogar y negocio protegidos", PROTECTED_HOME_BUSSINESS, 0.15);
                        sleep(700);
                }
                if ("si".equalsIgnoreCase(hasMicroEntrepreneursInsurance)) {
                        scrollAndClickReliable("Microempesarios protegidos", PROTECTED_MICRO_ENTREPRENEURS, 0.15);
                        sleep(700);
                }
                takeEvidence(numberId, "Seguros_Voluntarios");
                if ("si".equalsIgnoreCase(hasLifeInsurance)) {
                        scrollAndClickReliable("Tu vida protegida", PROTECTED_LIFE, 0.15);
                        sleep(700);
                }
                if ("si".equalsIgnoreCase(hasFuneralPlanning)) {
                        // Usa también el scroll confiable aquí, por si el último seguro queda oculto
                        scrollAndClickReliable("Previsión Exequial", FUNERAL_PLANNING, 0.15);
                        sleep(700);
                }

                takeEvidence(numberId, "Seguros_Voluntarios");

                // ==========================================
                // 2. LLENAR LOS FORMULARIOS DINÁMICOS
                // ==========================================

                // Regla A: Si se seleccionó Familia O Vida, se llena Beneficiarios
                if ("si".equalsIgnoreCase(hasFamilyInsurance) || "si".equalsIgnoreCase(hasLifeInsurance)) {
                        scrollAndClickReliable("Beneficiarios seguros voluntarios", BENEFICIARIES_BTN, 0.15);
                        // método que llena el modal de beneficiarios
                        fillBeneficiariesModal(numberId);
                }

                // Regla B: Si se seleccionó Hogar y Negocio, se llena Inmueble
                if ("si".equalsIgnoreCase(hasHomeBussinessInsurance)) {
                        scrollToText("Inmueble a asegurar", 0.15);
                        clickSafely(PROPERTY_TO_INSURE_BTN);
                        // método que llena el modal de inmuebles
                        fillPropertyModal(numberId);
                }
        }

        public void fillBeneficiariesModal(String numberId) {
                click(ADD);

                waitForVisibility(BENE_TYPE_ID);
                clickComposeDropdown(BENE_TYPE_ID);
                click(byVisibleText("Cedula ciudadanía"));

                type(BENE_NUMBER_ID, "1116070669");

                clickComposeDropdown(BENE_COUNTRY);
                click(byVisibleText("COLOMBIA"));

                type(BENE_FIRST_NAME, "Colorado");

                scrollToText("Primer nombre beneficiario", 0.15);
                type(BENE_NAME, "Esteban");

                scrollAndClickReliable("Parentesco del beneficiario", BENE_RELATIONSHIP, 0.15);
                click(byVisibleText("Hermano (a)"));

                scrollToText("Mensajes de respuesta", 0.15);

                type(BENE_PORCENTAGE, "100");

                click(BENE_VALIDATE_BTN);
                waitForVisibility(ACCEPT_BUTTON);
                click(ACCEPT_BUTTON);

                click(SAVE_BUTTON);
                sleep(200);
                click(SAVE_BUTTON);

                click(GO_BACK);

                takeEvidence(numberId, "Beneficiario_registrado");
        }

        /**
         * Llena el modal de Inmueble a asegurar (Se activa con Seguro de
         * Hogar/Negocio).
         */
        public void fillPropertyModal(String numberId) {
                clickSafely(ADD); // Asumo que usas la misma variable ADD_RECORD_BUTTON que en Análisis

                // 1. Dirección e Info Geográfica
                waitForVisibility(PROP_ADDRESS);
                type(PROP_ADDRESS, "Calle Falsa 123");

                // Usamos tu método de búsqueda para la ciudad y el barrio
                findTextInDropDown(PROP_DEPARTMENT, "VALLE DEL CAUCA");
                findTextWithSearch(PROP_CITY, SEARCH_INPUT, "CALI");
                findTextWithSearch(PROP_NEIGHBORHOOD, SEARCH_INPUT, "CIUDAD CAPRI");

                // 2. Uso y Tipo
                scrollAndClickReliable("Uso", PROP_USAGE, 0.15);
                clickSafely(byVisibleText("Comercial")); // Opciones típicas: Comercial, Residencial, Mixto

                scrollAndClickReliable("Tipo de inmueble", PROP_TYPE, 0.15);
                clickSafely(byVisibleText("Urbano")); // Opciones típicas: Local, Casa, Apartamento

                // 3. Pisos y Sótanos
                scrollToText("Número de piso del Inmueble", 0.15);
                type(PROP_FLOOR_NUMBER, "1");

                scrollToText("Número de sótano del Inmueble", 0.15);
                type(PROP_BASEMENT_NUMBER, "0");

                // 4. Guardar y Salir (Usando tu misma estructura de guardado doble)
                clickSafely(SAVE_BUTTON);
                sleep(200);
                clickSafely(SAVE_BUTTON);

                clickSafely(GO_BACK);

                takeEvidence(numberId, "Inmueble_asegurado");
        }

        // PENDIENTE REALIZAR EL REGISTRO DEL CODEUDOR PARA LAS ORDENES
}