package com.kata.automation.pages;

import java.time.LocalDate;
import java.time.Duration;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;

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

        // --- Periodicidad de la primera cuota (días después del desembolso) ---
        // Fragmentos en minúscula de los productos "Crédito pop prod Urban Gota Sem360/Quin360".
        private static final String WEEKLY_GOTA_PRODUCT = "gota sem";
        private static final String BIWEEKLY_GOTA_PRODUCT = "gota quin";
        private static final int WEEKLY_INSTALLMENT_DAYS = 7;
        private static final int BIWEEKLY_INSTALLMENT_DAYS = 15;
        private static final int MONTHLY_INSTALLMENT_DAYS = 30;

        // --- Garantía FNG ---
        // Texto exacto de la app, sin signo de interrogación al final.
        private static final String FNG_QUESTION = "¿Requiere garantía FNG";
        // Desplegable que la app muestra solo al responder "Si" a la pregunta FNG.
        private static final String FNG_TYPE_LABEL = "Garantía FNG";
        private static final By FNG_TYPE = By.xpath(
                        "//android.widget.TextView[@text=\"" + FNG_TYPE_LABEL
                                        + "\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

        // ========================================================================
        // CHECKBOXES DE SEGUROS VOLUNTARIOS
        // Cada seguro es una fila marcable (View checkable) con su texto y su CheckBox.
        // ========================================================================

        private static final String PROTECTED_FAMILY = "Tu familia protegida";
        private static final String PROTECTED_HOME_BUSSINESS = "Tu hogar y negocio protegidos";
        private static final String PROTECTED_MICRO_ENTREPRENEURS = "Microempresarios protegidos";
        private static final String PROTECTED_LIFE = "Tu vida protegida";
        private static final String FUNERAL_PLANNING = "Previsión Exequial";

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

                LocalDate calculatedDate = LocalDate.of(disbYear, disbMonth, disbDay)
                                .plusDays(daysToFirstInstallment(product));

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
                configureFngGuarantee(hasFNG);

                //answerQuestion("¿Requiere Fianza?", hasBail);

                answerQuestion("¿Tiene garantía real?", hasRealGuarantee);

                // 5. Simular
                scrollAndClickReliable("Simular crédito", SIMULATE_CREDIT_BTN, 0.15);
                takeEvidence(numberId, "simulacion_credito_respuesta");
                waitForSimulationResponse();

                click(ACCEPT_BUTTON);

        }

        /**
         * Días entre el desembolso y la primera cuota según la periodicidad del producto:
         * los productos Gota semanal pagan cada 7 días, los Gota quincenal cada 15 y el
         * resto cada 30. Se compara por fragmento para cubrir sus variantes (ej. "Urban").
         */
        private static int daysToFirstInstallment(String product) {
                String normalizedProduct = product == null ? "" : product.toLowerCase(Locale.ROOT);
                if (normalizedProduct.contains(WEEKLY_GOTA_PRODUCT)) {
                        return WEEKLY_INSTALLMENT_DAYS;
                }
                if (normalizedProduct.contains(BIWEEKLY_GOTA_PRODUCT)) {
                        return BIWEEKLY_INSTALLMENT_DAYS;
                }
                return MONTHLY_INSTALLMENT_DAYS;
        }

        /**
         * La columna "Garantía FNG" del Excel trae "No" o el tipo de garantía que se
         * debe seleccionar (por ejemplo RECONSTRUYE), tal como aparece en la app.
         */
        private void configureFngGuarantee(String fngGuarantee) {
                String guaranteeType = fngGuarantee == null ? "" : fngGuarantee.trim();
                if ("si".equalsIgnoreCase(guaranteeType)) {
                        throw new IllegalArgumentException("La columna 'Garantía FNG' debe indicar el tipo de "
                                        + "garantía tal como aparece en la app (por ejemplo RECONSTRUYE), no 'Si'.");
                }

                boolean requiresFng = !guaranteeType.isEmpty() && !"no".equalsIgnoreCase(guaranteeType);
                answerQuestion(FNG_QUESTION, requiresFng ? "Si" : "No");
                if (requiresFng) {
                        scrollAndClickReliable(FNG_TYPE_LABEL, FNG_TYPE, 0.15);
                        clickSafely(byVisibleText(guaranteeType));
                }
        }

        private void waitForSimulationResponse() {
                try {
                        new WebDriverWait(driver, Duration.ofSeconds(15)).until(currentDriver -> {
                                String source = currentDriver.getPageSource();
                                return !currentDriver.findElements(ACCEPT_BUTTON).isEmpty()
                                                || containsSimulationError(source);
                        });
                } catch (TimeoutException exception) {
                        throw new RuntimeException(
                                        "La simulación no respondió: no apareció confirmación ni mensaje de error.",
                                        exception);
                }

                String pageSource = driver.getPageSource();
                if (containsSimulationError(pageSource)) {
                        throw new RuntimeException(
                                        "La simulación devolvió un error técnico. Revise la evidencia del caso.");
                }
                waitForVisibility(ACCEPT_BUTTON);
        }

        private boolean containsSimulationError(String pageSource) {
                if (pageSource == null) {
                        return false;
                }
                String normalized = pageSource.toLowerCase();
                return normalized.contains("ocurrió un error")
                                || normalized.contains("ocurrio un error")
                                || normalized.contains("error durante")
                                || normalized.contains("tasa obtenida")
                                || normalized.contains("código de pizarra")
                                || normalized.contains("codigo de pizarra");
        }

        /**
         * Acepta el mensaje mostrado por la aplicación después de una simulación
         * rechazada. El cierre de la orden lo coordina el flujo principal.
         */
        public void acceptSimulationMessage() {
                waitForVisibility(ACCEPT_BUTTON);
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
                // 1. MARCAR LOS SEGUROS SELECCIONADOS EN EL EXCEL
                // ==========================================
                selectVoluntaryInsurance(PROTECTED_FAMILY, hasFamilyInsurance);
                selectVoluntaryInsurance(PROTECTED_HOME_BUSSINESS, hasHomeBussinessInsurance);
                selectVoluntaryInsurance(PROTECTED_MICRO_ENTREPRENEURS, hasMicroEntrepreneursInsurance);
                selectVoluntaryInsurance(PROTECTED_LIFE, hasLifeInsurance);
                selectVoluntaryInsurance(FUNERAL_PLANNING, hasFuneralPlanning);

                takeEvidence(numberId, "Seguros_Voluntarios");

                // ==========================================
                // 2. LLENAR LOS FORMULARIOS DINÁMICOS
                // ==========================================

                // Regla A: Familia, Vida y Previsión Exequial comparten un único formulario de
                // beneficiarios (un solo beneficiario), así que se llena una vez.
                // Microempresarios protegidos no habilita ningún formulario.
                if ("si".equalsIgnoreCase(hasFamilyInsurance) || "si".equalsIgnoreCase(hasLifeInsurance)
                                || "si".equalsIgnoreCase(hasFuneralPlanning)) {
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

        /**
         * Marca el seguro solo si el Excel lo pide y aún no está marcado: un segundo
         * toque lo desmarcaría. Confirma el estado porque de él dependen los campos
         * que la app habilita debajo (beneficiarios o inmueble).
         */
        private void selectVoluntaryInsurance(String insuranceLabel, String decision) {
                if (!"si".equalsIgnoreCase(decision)) {
                        return;
                }
                By insuranceRow = By.xpath("//android.view.View[@checkable=\"true\"][android.widget.TextView[@text=\""
                                + insuranceLabel + "\"]]");

                scrollToText(insuranceLabel, 0.15);
                if (!isChecked(insuranceRow)) {
                        clickSafely(insuranceRow);
                }
                try {
                        new WebDriverWait(driver, Duration.ofSeconds(5)).until(currentDriver -> isChecked(insuranceRow));
                } catch (TimeoutException exception) {
                        throw new IllegalStateException("No se pudo marcar el seguro '" + insuranceLabel + "'.",
                                        exception);
                }
        }

        private boolean isChecked(By locator) {
                return Boolean.parseBoolean(waitForStableElement(locator, 1).getAttribute("checked"));
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