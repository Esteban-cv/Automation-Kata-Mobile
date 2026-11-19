package com.kata.automation.pages;

import org.openqa.selenium.By;

import com.kata.automation.utils.TestScenario;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

public class RadicationPage extends BasePage {

        public RadicationPage(AndroidDriver driver) {
                super(driver); // Envía el driver a BasePage
        }

        // LOCALIZADORES DE LA PAGINA DE RADICACION

        private static final By RADICATE_BUTTON = By.xpath(
                        "//android.widget.TextView[@text=\"Radicación\"]");

        // ✅ Corregidos de following-sibling a following + [1]
        private static final By DEPARTMENT_DROPDOWN = By.xpath(
                        "//android.widget.TextView[@text=\"Departamento de nacimiento del solicitante\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
        private static final By CITY_DROPDOWN = By.xpath(
                        "//android.widget.TextView[@text=\"Ciudad de nacimiento del solicitante\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
        private static final By EXPEDITION_DATE = By.xpath(
                        "//android.widget.TextView[@text=\"Fecha de expedición de identificación del solicitante\"]/following::android.widget.EditText[1]");

        private static final By EXPEDITION_DEPARTMENT_CLIENT_DOCUMENT = By.xpath(
                        "//android.widget.TextView[@text=\"Departamento de expedición de identificación del solicitante\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

        // ✅ Faltaba el [1]
        private static final By LAST_YEAR_COMPLETED = By.xpath(
                        "//android.widget.TextView[@text=\"Último año cursado del solicitante\"]/following::android.widget.EditText[1]");

        private static final By AFFILIATION_CLIENT = By.xpath(
                        "//android.widget.TextView[@text=\"Afiliación salud del solicitante\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
        private static final By NETWOK_ACCESS_CLIENT = By.xpath(
                        "//android.widget.TextView[@text=\"Acceso a internet del solicitante\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

        // --- LOCALIZADORES COMPOSICIÓN FAMILIAR ---
        private static final By FAMILY_COMPOSITION = By.xpath(
                        "//android.widget.TextView[@text=\"Composición familiar\"]/following::android.widget.Button[1]");

        // ⚠️ Nota: Sigue siendo un XPath genérico, ten cuidado si la app tiene más
        // botones.
        private static final By ADD_RECORD_BUTTON = By.xpath("//android.widget.Button");

        private static final By FAMILY_MEMBER_TYPE = By.xpath(
                        "//android.widget.TextView[@text=\"Tipo de familiar\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

        // ✅ Faltaba el [1]
        private static final By FAMILY_MEMBER_AGE = By.xpath(
                        "//android.widget.TextView[@text=\"Edad del familiar\"]/following::android.widget.EditText[1]");

        private static final By FAMILY_MEMBER_GENDER = By.xpath(
                        "//android.widget.TextView[@text=\"Género del familiar\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
        private static final By FAMILY_MEMBER_EDUCATIONAL_LEVEL = By.xpath(
                        "//android.widget.TextView[@text=\"Nivel de educación del familiar\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

        // ✅ Faltaba el [1]
        private static final By FAMILY_MEMBER_LAST_YEAR = By.xpath(
                        "//android.widget.TextView[@text=\"Último año cursado del familiar (Año/Semestre)\"]/following::android.widget.EditText[1]");

        // ⚠️ Nota: Sigue siendo un XPath genérico.
        private static final By SAVE_FAMILY_BUTTON = By.xpath("//android.widget.Button");
        private static final By CONFIRM_BUTTON = By.xpath("//android.widget.TextView[@text=\"GUARDAR\"]");
        private static final By GO_BACK = AppiumBy.accessibilityId("Ir atrás");

        // ✅ Faltaban los [1]
        private static final By EMAIL = By.xpath(
                        "//android.widget.TextView[@text=\"Correo electrónico del solicitante\"]/following::android.widget.EditText[1]");
        private static final By CONFIRM_EMAIL = By.xpath(
                        "//android.widget.TextView[@text=\"Confirmar correo electrónico del solicitante\"]/following::android.widget.EditText[1]");

        private static final By RESIDENCE_ZONE_CLIENT = By.xpath(
                        "//android.widget.TextView[@text=\"Zona de residencia del solicitante\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

        // ✅ Faltaba el [1]
        private static final By RESIDENCE_OCUPATION_DATE = By.xpath(
                        "//android.widget.TextView[@text=\"Fecha de inicio de ocupación de la residencia\"]/following::android.widget.EditText[1]");

        // DEPARTAMENTO, CIUDAD Y BARRIO DEL ANALISTA
        // ✅ Faltaban los [1]
        private static final By BUSSINESS_DEPARTMENT_CLIENT = By.xpath(
                        "//android.widget.TextView[@text=\"Departamento del negocio del solicitante\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
        private static final By BUSSINESS_CITY_CLIENT = By.xpath(
                        "//android.widget.TextView[@text=\"Ciudad del negocio del solicitante\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
        private static final By BUSSINESS_NEIGHBORHOOD_CLIENT = By.xpath(
                        "//android.widget.TextView[@text=\"Barrio del negocio del solicitante\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

        private static final By BUSSINESS_COORDINATES = By.xpath(
                        "//android.widget.TextView[@text=\"Coordenadas del negocio\"]/following::android.widget.Button[1]");

        // ✅ Faltaba el [1]
        private static final By OBSERVATION_COORDINATES = By.xpath(
                        "//android.widget.TextView[@text=\"Observaciones de posicionamiento\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

        // ✅ Faltaba el [1]
        private static final By CLIENT_ANALYST_IMAGE = By.xpath(
                        "//android.widget.TextView[@text=\"Selfie del analista y el negocio\"]/following::android.widget.Button[1]");

        // ⚠️ Nota: Sigue siendo el XPath absoluto del botón de la cámara.
        private static final By CAPTURE_BUTTON = By.xpath(
                        "//androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View/android.view.View/android.view.View[5]");

        // ✅ Faltaba el [1]
        private static final By BUSSINESS_TYPE = By.xpath(
                        "//android.widget.TextView[@text=\"Tipo de local del negocio\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

        // CELULAR DEL NEGOCIO
        // ✅ Faltaban los [1]
        private static final By BUSSINESS_NUMBER_PHONE = By.xpath(
                        "//android.widget.TextView[@text=\"Celular del negocio\"]/following::android.widget.EditText[1]");
        private static final By CONFIRM_BUSSINESS_NUMBER_PHONE = By.xpath(
                        "//android.widget.TextView[@text=\"Confirmar celular del negocio\"]/following::android.widget.EditText[1]");

        // GENERAR VITA360
        private static final By GENERATE_VIEW360_BUTTON = By.xpath(
                        "//android.widget.TextView[@text=\"Vista 360 del cliente\"]/following::android.view.View[.//android.widget.Button][1]");
        // Busca cualquier componente en el modal que tenga el texto exacto de
        // confirmación
        // Anclamos el XPath al título del modal para garantizar que le dé al ACEPTAR
        // correcto
        private static final By ACCEPT_CONSULT_VIEW360_BUTTON = By
                        .xpath("//android.widget.TextView[@text=\"ACEPTAR\"]");
        private static final By ACCEPT_CONSULT_CLIENT_BUTTON = By.xpath(
                        "//*[@text=\"Actualización exitosa\"]/following::*[@text=\"ACEPTAR\"][1]");

        // ACTIVIDADES ECONOMICAS
        private static final By MAIN_SECTOR_ACTIVITY = By.xpath(
                        "//android.widget.TextView[@text=\"Sector actividad económica principal\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

        // ✅ Faltaban los [1]
        private static final By MAIN_ECONOMICAL_ACTIVITY = By.xpath(
                        "//android.widget.TextView[@text=\"Actividad económica principal\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
        private static final By MAIN_ECONOMICAL_ACTIVITY_ANTIQUE = By.xpath(
                        "//android.widget.TextView[@text=\"Antigüedad del cliente con la actividad económica principal (meses)\"]/following::android.widget.EditText[1]");
        private static final By GROSS_SALES = By.xpath(
                        "//android.widget.TextView[@text=\"Ventas brutas anuales actividad principal\"]/following::android.widget.EditText[1]");

        // DECISIÓN
        // ✅ Faltaba el [1]
        private static final By DECISION = By.xpath(
                        "//android.widget.TextView[@text=\"Decisión de la radicación\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
        private static final By PROCCESS_DECISION = By.xpath(
                        "//android.widget.TextView[@text=\"Procesar la decisión\"]/following::android.view.View[.//android.widget.Button][1]");

        // Atrapa el único campo de texto disponible en esa ventana de búsqueda
        private static final By SEARCH_INPUT = By.xpath("//android.widget.EditText");

        // IMPLEMENTACION DE REINTENTO SI FALLA EL BOTON DE PROCESAR DECISION EN
        // RADICACION
        private static final By CONSULTATION_RESULT = By.xpath("//*[@text=\"Actualización exitosa\"]");
        // Se simplifica el timeout para no colapsar el motor de búsqueda
        private static final By TIMEOUT_ERROR_MESSAGE = By.xpath(
                        "//*[contains(@text, 'tiempo excedido') or contains(@text, 'tiempo de espera') or contains(@text, 'timeout')]");

        //BOTON DE VISTA 360
        private static final By CONSULTATION_VIEW360_RESULT = By.xpath("//*[@text=\"Consulta Exitosa\"]");
        // Se simplifica el timeout para no colapsar el motor de búsqueda
    private static final By TIMEOUT_ERROR_VIEW360_MESSAGE = By.xpath(
            "//*[contains(@text, 'tiempo excedido') or contains(@text, 'tiempo de espera') or contains(@text, 'timeout')]");

        public void fillFamilyComposition(By openSectionLocator) {
                if (openSectionLocator != null) {
                        scrollAndClickReliable("Composición familiar", openSectionLocator, 0.15);
                }

                sleep(1000);

                // 2. Ejecuta el llenado exactamente 2 veces
                for (int i = 1; i <= 2; i++) {
                        waitForVisibility(ADD_RECORD_BUTTON);
                        clickSafely(ADD_RECORD_BUTTON);

                        clickComposeDropdown(FAMILY_MEMBER_TYPE);
                        click(byVisibleText("Hermano (a)"));

                        type(FAMILY_MEMBER_AGE, "25");

                        clickComposeDropdown(FAMILY_MEMBER_GENDER);
                        click(byVisibleText("Masculino"));

                        clickComposeDropdown(FAMILY_MEMBER_EDUCATIONAL_LEVEL);
                        click(byVisibleText("Profesional"));

                        type(FAMILY_MEMBER_LAST_YEAR, "10");

                        sleep(200);

                        waitForVisibility(SAVE_FAMILY_BUTTON);
                        clickSafely(SAVE_FAMILY_BUTTON);

                        waitForVisibility(CONFIRM_BUTTON);
                        clickSafely(CONFIRM_BUTTON);
                        sleep(200);

                }
                waitForVisibility(GO_BACK);
                clickSafely(GO_BACK);
        }

        public void completeRadicationExistingClient(String numberId, String lastYearCompleted,
                        String affiliation, String networkAccess, String email, String bussinessDepartmentAnalist,
                        String bussinessCityAnalist,
                        String bussinessNeighborhoodAnalist, String bussinesNumberPhone) {
                clickSafely(RADICATE_BUTTON);
                takeEvidence(numberId, "radicacion");

                scrollToText("El solicitante tiene otra nacionalidad", 0.30);

                answerQuestion("¿El solicitante tiene otra nacionalidad?", "No");

                scrollToText("Último año cursado del solicitante", 0.15);
                type(LAST_YEAR_COMPLETED, lastYearCompleted);

                scrollAndClickReliable("Afiliación salud del solicitante",
                                AFFILIATION_CLIENT, 0.15);
                selectDropdownOption(affiliation);

                scrollAndClickReliable("Acceso a internet del solicitante",
                                NETWOK_ACCESS_CLIENT, 0.15);
                selectDropdownOption(networkAccess);

                sleep(100);

                scrollToText("¿El solicitante tiene propiedad Raiz?", 0.15);

                answerQuestion("¿El solicitante tiene propiedad Raiz?", "No");

                fillFamilyComposition(FAMILY_COMPOSITION);

                scrollToText("Confirmar correo electrónico del solicitante", 0.15);

                type(EMAIL, email);
                type(CONFIRM_EMAIL, email);

                takeEvidence(numberId, "correo_electronico_registrado");

                try {
                        driver.hideKeyboard();
                } catch (Exception e) {
                        /* Ignorado */ }

                scrollAndClickReliable("Zona de residencia del solicitante", RESIDENCE_ZONE_CLIENT, 0.15);
                click(byVisibleText("Urbano"));

                scrollToText("Fecha de inicio de ocupación de la residencia", 0.15);

                click(RESIDENCE_OCUPATION_DATE);
                selectDateInCalendar(22, 9, 2020);

                // ciudad del analista para que la orden pueda ser validad
                scrollToText("Ciudad del negocio del solicitante", 0.15);

                // PROBAR SI ENCUENTRA EL DEPARTAMENTO EN LA LISTA DESPLAGABLE

                findTextInDropDown(BUSSINESS_DEPARTMENT_CLIENT, bussinessDepartmentAnalist);
                findTextInDropDown(BUSSINESS_CITY_CLIENT, bussinessCityAnalist);
                findTextWithSearch(BUSSINESS_NEIGHBORHOOD_CLIENT, SEARCH_INPUT, bussinessNeighborhoodAnalist);

                // Hace scroll hasta que el botón sea visible
                scrollToText("Coordenadas del negocio", 0.15);
                sleep(500); // Pausa breve para estabilizar la pantalla

                // Ejecuta el toque físico real
                try {
                        forceClick(BUSSINESS_COORDINATES);
                } catch (Exception e) {
                        // ignora si ya tiene la ubicacion
                }

                scrollAndClickReliable("Observaciones de posicionamiento", OBSERVATION_COORDINATES, 0.10);
                click(byVisibleText("No registro coordenadas por falla del sistema"));

                scrollAndClickReliable("Selfie del analista y el negocio", CLIENT_ANALYST_IMAGE, 0.10);
                click(CAPTURE_BUTTON);
                click(GO_BACK);

                scrollAndClickReliable("Tipo de local del negocio", BUSSINESS_TYPE, 0.10);
                click(byVisibleText("Familiar"));

                scrollToText("Confirmar celular del negocio", 0.15);

                type(BUSSINESS_NUMBER_PHONE, bussinesNumberPhone);
                type(CONFIRM_BUSSINESS_NUMBER_PHONE, bussinesNumberPhone);

                scrollToText("Vista 360 del cliente", 0.15);
                clickWithRetry(GENERATE_VIEW360_BUTTON, CONSULTATION_VIEW360_RESULT, TIMEOUT_ERROR_VIEW360_MESSAGE);
                click(ACCEPT_CONSULT_VIEW360_BUTTON);

                scrollAndClickReliable("Sector actividad económica principal", MAIN_SECTOR_ACTIVITY, 0.15);
                click(byVisibleText("SERVICIOS"));

                scrollAndClickReliable("Actividad económica principal", MAIN_ECONOMICAL_ACTIVITY, 0.15);
                scrollToTextAndClick("COMERCIALIZACIÓN DE ENERGÍA ELÉCTRICA");

                scrollToText("Antigüedad del cliente con la actividad económica principal (meses)", 0.15);
                type(MAIN_ECONOMICAL_ACTIVITY_ANTIQUE, "60");

                scrollToText("Ventas brutas anuales actividad principal", 0.10);
                type(GROSS_SALES, "15000000");

                scrollToText("¿Permanecí mas de 183 días en el último año, o en 183 días durante los tres últimos años, dentro de territorio de los Estados Unidos?",
                                0.25);

                answerQuestion("¿Manifiesto que por mi actividad económica declarada en la sección Información General del presente formulario, me encuentro obligado(a) ante la DIAN a inscribirme en el Registro Único Tributario (RUT).?",
                                "No");
                answerQuestion("¿Permanecí mas de 183 días en el último año, o en 183 días durante los tres últimos años, dentro de territorio de los Estados Unidos?",
                                "No");
                answerQuestion("¿Posee tarjeta verde o \"Green Card\" de EEUU? (Tarjeta de residencia).", "No");
                answerQuestion("¿Recibe cualquier pago de intereses, dividendos, rentas, salarios, honorarios, primas, anualidades, compensaciones, remuneraciones, emolumentos y otras ganancias fijas u ocasionales e ingresos (Ingresos FDAP*) cuya fuente se encuentre dentro de los Estados Unidos?",
                                "No");
                answerQuestion("¿El solicitante manifiesta ser reincorporado?", "No");
                answerQuestion("¿Recibo cualquier ingreso bruto procedente de la venta u otra disposición de cualquier propiedad que puede producir intereses o dividendos cuya fuente se encuentre dentro de los Estados Unidos?",
                                "No");

                scrollAndClickReliable("Decisión de la radicación", DECISION, 0.10);
                click(byVisibleText("Continuar a análisis"));

                scrollToText("Faltan por completar campos esperados para poder continuar con esta consulta", 0.10);

                processRadicationDecision(numberId);
        }

        // ========================================================================
        // RADICACIÓN COMPLETA / INTELIGENTE
        // Lo que la app ya trae diligenciado (cliente renovado) no se toca; lo vacío se
        // llena con el Excel (cliente nuevo: todo). Va en el orden del formulario porque
        // scrollToText solo baja.
        // ========================================================================

        /**
         * Llena la radicación de cualquier tipo de cliente. Los valores salen de las columnas
         * del Excel (por nombre); si una columna está vacía se usa el valor por defecto.
         */
        public void completeRadicationNewClient(String numberId, TestScenario data) {
                clickSafely(RADICATE_BUTTON);
                takeEvidence(numberId, "radicacion");

                // --- Información del cliente ---
                selectOption("Departamento de nacimiento del solicitante",
                                value(data, "VALLE DEL CAUCA", "Departamento de nacimiento"));
                selectOption("Ciudad de nacimiento del solicitante",
                                value(data, "CALI", "Ciudad de nacimiento"));

                selectDate("Fecha de expedición de identificación del solicitante",
                                number(data, 1, "Día de expedición"),
                                number(data, 1, "Mes de expedición"),
                                number(data, 2023, "Año de expedición"));
                selectOption("Departamento de expedición de identificación del solicitante",
                                value(data, "VALLE DEL CAUCA", "Departamento de expedición"));
                selectOption("Ciudad de expedición de identificación del solicitante",
                                value(data, "CALI", "Ciudad de expedición"));

                selectOption("Nacionalidad del solicitante", value(data, "COLOMBIA", "Nacionalidad"));
                answerIfEmpty("¿El solicitante tiene otra nacionalidad?", "No");

                selectOption("Género del solicitante", value(data, "Masculino", "Género"));
                // No es obligatorio, pero se diligencia siempre.
                selectOption("Grupo étnico del solicitante", value(data, ETHNIC_GROUP_DEFAULT, "Grupo étnico"));
                selectOption("Estado civil del solicitante", value(data, "Soltero(a)", "Estado civil"));
                selectOption("Nivel de educación del solicitante",
                                value(data, "Profesional", "Nivel de educación"));
                typeField("Último año cursado del solicitante", value(data, "10", "Último año cursado"));
                selectOption("Profesión del solicitante", value(data, "Administrador", "Profesión"));
                selectOption("Ocupación del solicitante", value(data, "MICROEMPRESARIO", "Ocupación"));
                selectOption("Afiliación salud del solicitante", value(data, "SISBEN", "Afiliación"));
                selectOption("Acceso a internet del solicitante", value(data, "Hogar", "Acceso a internet"));
                answerIfEmpty("¿El solicitante tiene propiedad Raiz?", "No");

                // --- Información familiar: solo si la app pide al menos un familiar ---
                scrollToText("Composición familiar", 0.15);
                if (isPresentWithin(FAMILY_REQUIRED_WARNING, java.time.Duration.ofSeconds(2))) {
                        fillFamilyComposition(FAMILY_COMPOSITION);
                } else {
                        System.out.println("Composición familiar ya tiene registros.");
                }

                // --- Contacto y residencia ---
                // Correo y su confirmación deben coincidir SIEMPRE. Si la app ya trae el correo
                // (cliente renovado), la confirmación se llena con ese mismo valor, no con el Excel.
                scrollToText("Confirmar correo electrónico del solicitante", 0.15);
                fillWithConfirmation("Correo electrónico del solicitante", EMAIL,
                                "Confirmar correo electrónico del solicitante", CONFIRM_EMAIL,
                                value(data, "", "Correo electrónico"));
                takeEvidence(numberId, "correo_electronico_registrado");
                hideKeyboardQuietly();

                typeField("Celular del solicitante", value(data, "", "Celular"));

                selectOption("Zona de residencia del solicitante", value(data, "Urbano", "Zona de residencia"));
                selectOption("País de residencia del solicitante", value(data, "COLOMBIA", "País de residencia"));
                selectOption("Departamento de residencia del solicitante",
                                value(data, "VALLE DEL CAUCA", "Departamento de residencia"));
                selectOption("Ciudad de residencia del solicitante",
                                value(data, "CALI", "Ciudad de residencia"));
                scrollToText("Barrio de residencia del solicitante", 0.15);
                if (!dropdownFilled("Barrio de residencia del solicitante")) {
                        findTextWithSearch(dropdownBelow("Barrio de residencia del solicitante"), SEARCH_INPUT,
                                        value(data, "LA INDEPENDENCIA", "Barrio de residencia"));
                }
                typeField("Dirección de residencia del solicitante",
                                value(data, "CALLE 10 # 20 30", "Dirección de residencia"));
                typeField("Referencia de la residencia del solicitante",
                                value(data, "CASA ESQUINERA", "Referencia de la residencia"));
                selectOption("Estrato", value(data, "Estrato 2", "Estrato"));
                selectOption("Tipo de vivienda", value(data, "Familiar", "Tipo de vivienda"));
                selectDate("Fecha de inicio de ocupación de la residencia", 22, 9, 2020);

                // --- Información del negocio ---
                typeField("Nombre del negocio del solicitante",
                                value(data, "TIENDA LA ESQUINA", "Nombre del negocio"));
                selectOption("Ubicación del negocio del solicitante",
                                value(data, "Local Comercial", "Ubicación del negocio"));

                // Negocio: si el Excel trae valor se escribe siempre (debe corresponder al
                // analista); si viene vacío se respeta lo que trae la app.
                scrollToText("Ciudad del negocio del solicitante", 0.15);
                String businessDept = data == null ? "" : data.get("Departamento del negocio", "Departamento");
                String businessCity = data == null ? "" : data.get("Ciudad del negocio", "Ciudad");
                String businessHood = data == null ? "" : data.get("Barrio del negocio", "Barrio");
                if (!businessDept.isBlank() || !dropdownFilled("Departamento del negocio del solicitante")) {
                        findTextInDropDown(BUSSINESS_DEPARTMENT_CLIENT,
                                        businessDept.isBlank() ? "VALLE DEL CAUCA" : businessDept);
                }
                if (!businessCity.isBlank() || !dropdownFilled("Ciudad del negocio del solicitante")) {
                        findTextInDropDown(BUSSINESS_CITY_CLIENT, businessCity.isBlank() ? "EL CERRITO" : businessCity);
                }
                if (!businessHood.isBlank() || !dropdownFilled("Barrio del negocio del solicitante")) {
                        findTextWithSearch(BUSSINESS_NEIGHBORHOOD_CLIENT, SEARCH_INPUT,
                                        businessHood.isBlank() ? "LA ESTRELLA" : businessHood);
                }
                typeField("Dirección del negocio del solicitante",
                                value(data, "CARRERA 5 # 10 20", "Dirección del negocio"));

                scrollToText("Coordenadas del negocio", 0.15);
                sleep(500);
                try {
                        forceClick(BUSSINESS_COORDINATES);
                } catch (Exception e) {
                        // ya tiene la ubicación
                }
                selectOption("Observaciones de posicionamiento", "No registro coordenadas por falla del sistema");

                scrollAndClickReliable("Selfie del analista y el negocio", CLIENT_ANALYST_IMAGE, 0.10);
                click(CAPTURE_BUTTON);
                click(GO_BACK);

                selectOption("Tipo de local del negocio", value(data, "Familiar", "Tipo de local"));

                String businessPhone = value(data, "", "Teléfono del negocio");
                scrollToText("Confirmar celular del negocio", 0.15);
                fillWithConfirmation("Celular del negocio", BUSSINESS_NUMBER_PHONE,
                                "Confirmar celular del negocio", CONFIRM_BUSSINESS_NUMBER_PHONE, businessPhone);

                // --- Centrales de riesgo ---
                scrollToText("Vista 360 del cliente", 0.15);
                clickWithRetry(GENERATE_VIEW360_BUTTON, CONSULTATION_VIEW360_RESULT, TIMEOUT_ERROR_VIEW360_MESSAGE);
                click(ACCEPT_CONSULT_VIEW360_BUTTON);

                // --- Actividades económicas ---
                selectOption("Sector actividad económica principal",
                                value(data, "SERVICIOS", "Sector actividad económica"));
                selectOption("Actividad económica principal",
                                value(data, "COMERCIALIZACIÓN DE ENERGÍA ELÉCTRICA", "Actividad económica"));
                typeField("Antigüedad del cliente con la actividad económica principal (meses)",
                                value(data, "60", "Antigüedad actividad (meses)"));
                typeField("Ventas brutas anuales actividad principal",
                                value(data, "15000000", "Ventas brutas anuales"));
                hideKeyboardQuietly();
                answerIfEmpty("¿Tiene otras actividades económicas?", "No");

                // --- Información laboral y normativa ---
                answerIfEmpty("¿Esta vinculado laboralmente?", "No");
                answerIfEmpty("¿El solicitante esta obligado a tributar en otro pais?", "No");
                answerIfEmpty("¿Realiza operaciones en moneda extranjera?", "No");
                answerIfEmpty("¿Posee productos en el exterior?", "No");

                // --- Autorizaciones y declaraciones ---
                for (String question : NEW_CLIENT_DECLARATIONS) {
                        answerIfEmpty(question, "No");
                }

                // --- Decisión ---
                scrollAndClickReliable("Decisión de la radicación", DECISION, 0.10);
                click(byVisibleText("Continuar a análisis"));
                scrollToText("Procesar la decisión", 0.10);
                processRadicationDecision(numberId);
        }


        // Errores de la app que se reintentan dejando evidencia: tiempo de espera o falla
        // del servicio (ej. "RSE02: El servicio 'FlexibleUpdate' ... generó el error 'InternalServerError'").
        private static final By RADICATION_RETRYABLE_ERROR = By.xpath(
                        "//*[contains(@text, 'tiempo excedido') or contains(@text, 'tiempo de espera')"
                                        + " or contains(@text, 'Tiempo de espera') or contains(@text, 'timeout')"
                                        + " or contains(@text, 'generó el error') or contains(@text, 'InternalServerError')]");

        // Mensajes de la app que rechazan la radicación por datos del Excel.
        private static final By RADICATION_REJECTION_MESSAGE = By.xpath(
                        "//android.widget.TextView[contains(@text, 'no corresponde al asignado')"
                                        + " or contains(@text, 'no es atendido por la agencia')]");

        /**
         * Toca "Procesar la decisión" y espera la respuesta. Si la app rechaza la
         * radicación (por ejemplo, el barrio del negocio no pertenece al analista) falla de
         * inmediato con el mensaje de la app y deja el diálogo abierto para que la
         * evidencia de error lo capture.
         */
        private void processRadicationDecision(String numberId) {
                By anyResponse = By.xpath("//*[@text=\"Actualización exitosa\"]"
                                + " | //android.widget.TextView[contains(@text, 'no corresponde al asignado')"
                                + " or contains(@text, 'no es atendido por la agencia')]");
                clickWithRetry(PROCCESS_DECISION, anyResponse, RADICATION_RETRYABLE_ERROR);

                java.util.List<org.openqa.selenium.WebElement> rejection = findNow(RADICATION_REJECTION_MESSAGE);
                if (!rejection.isEmpty()) {
                        String message = rejection.get(0).getText();
                        takeEvidence(numberId, "radicacion_rechazada");
                        throw new IllegalStateException("La app rechazó la radicación: \"" + message
                                        + "\". Revise el departamento, la ciudad y el barrio del negocio en el Excel"
                                        + " (deben corresponder al analista).");
                }
                takeEvidence(numberId, "decision_radicacion");
                waitForVisibility(ACCEPT_CONSULT_CLIENT_BUTTON);
                clickSafely(ACCEPT_CONSULT_CLIENT_BUTTON);
        }

        /** Valor del grupo étnico cuando el Excel no trae la columna "Grupo étnico". */
        private static final String ETHNIC_GROUP_DEFAULT = "Ningún grupo étnico";

        // Advertencia que muestra la app cuando la composición familiar está vacía.
        private static final By FAMILY_REQUIRED_WARNING = By.xpath(
                        "//android.widget.TextView[contains(@text, 'Debe ingresar por lo menos')]");

        /** Preguntas de "Autorizaciones y declaraciones", en el orden del formulario. */
        private static final String[] NEW_CLIENT_DECLARATIONS = {
                        "¿ Manifiesto que estoy obligado(a) a cumplir con el deber de presentar declaración de renta y complementarios por el año gravable anterior ?",
                        "¿Manifiesto que por mi actividad económica declarada en la sección Información General del presente formulario, me encuentro obligado(a) ante la DIAN a inscribirme en el Registro Único Tributario (RUT).?",
                        "¿Permanecí mas de 183 días en el último año, o en 183 días durante los tres últimos años, dentro de territorio de los Estados Unidos?",
                        "¿Posee tarjeta verde o \"Green Card\" de EEUU? (Tarjeta de residencia).",
                        "¿Recibe cualquier pago de intereses, dividendos, rentas, salarios, honorarios, primas, anualidades, compensaciones, remuneraciones, emolumentos y otras ganancias fijas u ocasionales e ingresos (Ingresos FDAP*) cuya fuente se encuentre dentro de los Estados Unidos?",
                        "¿El solicitante manifiesta ser reincorporado?",
                        "¿Recibo cualquier ingreso bruto procedente de la venta u otra disposición de cualquier propiedad que puede producir intereses o dividendos cuya fuente se encuentre dentro de los Estados Unidos?",
                        "¿El solicitante es persona expuesta políticamente (PEP)?" };

        /** Abre el desplegable debajo de la etiqueta y elige la opción (con scroll si la lista es larga). */
        private void selectOption(String label, String option) {
                scrollToText(label, 0.15);
                if (dropdownFilled(label)) {
                        return;
                }
                scrollAndClickReliable(label, dropdownBelow(label), 0.15);
                sleep(500);
                selectDropdownOption(option);
        }

        // ------------------------------------------------------------------
        // "Si ya viene lleno, no lo toco": un cliente renovado (o con mora menor) trae
        // campos precargados por el banco. Antes de llenar cada campo se revisa en
        // pantalla y solo se diligencia lo que esta vacio. Cliente nuevo: todo vacio.
        // ------------------------------------------------------------------

        private static final java.util.Set<String> EMPTY_TEXT_VALUES = java.util.Set.of(
                        "", "$0", "Selecciona una fecha.", "01/01/0001");

        /** Valor que muestra el desplegable de la etiqueta (la etiqueta debe estar en pantalla). */
        private String dropdownValue(String label) {
                java.util.List<org.openqa.selenium.WebElement> value = findNow(By.xpath(
                                "//android.widget.TextView[@text=" + xpathLiteral(label) + "]/following::android.widget.TextView[1]"));
                return value.isEmpty() ? "" : value.get(0).getText().trim();
        }

        private boolean dropdownFilled(String label) {
                String value = dropdownValue(label);
                boolean filled = !value.isEmpty() && !"Seleccionar".equals(value);
                if (filled) {
                        System.out.println("Ya diligenciado: " + label + " = " + value);
                }
                return filled;
        }

        private boolean textFilled(String label) {
                java.util.List<org.openqa.selenium.WebElement> field = findNow(textBelow(label));
                String value = field.isEmpty() ? "" : field.get(0).getText().trim();
                boolean filled = !EMPTY_TEXT_VALUES.contains(value);
                if (filled) {
                        System.out.println("Ya diligenciado: " + label + " = " + value);
                }
                return filled;
        }

        /**
         * Campo con confirmación (correo, celular del negocio): la confirmación siempre queda
         * igual al campo principal. Si el principal ya viene lleno se respeta y se copia en la
         * confirmación; si está vacío se escribe el valor del Excel en los dos.
         */
        private void fillWithConfirmation(String label, By field, String confirmLabel, By confirmField,
                        String excelValue) {
                String current = currentText(field);
                String value = EMPTY_TEXT_VALUES.contains(current) ? excelValue : current;
                if (value == null || value.isBlank()) {
                        throw new IllegalArgumentException("Falta el valor de '" + label + "' en el Excel y la app no lo trae.");
                }
                if (!value.equals(current)) {
                        type(field, value);
                } else {
                        System.out.println("Ya diligenciado: " + label + " = " + current);
                }
                if (!value.equals(currentText(confirmField))) {
                        type(confirmField, value);
                }
                hideKeyboardQuietly();
        }

        private String currentText(By field) {
                java.util.List<org.openqa.selenium.WebElement> found = findNow(field);
                return found.isEmpty() ? "" : found.get(0).getText().trim();
        }

        /** Responde Si/No solo si la pregunta aun no tiene ninguna opcion marcada. */
        private void answerIfEmpty(String question, String answer) {
                scrollToText(question, 0.20);
                boolean answered = !findNow(By.xpath("//android.widget.TextView[normalize-space(@text)="
                                + xpathLiteral(question.trim()) + "]/..//android.widget.RadioButton[@checked=\"true\"]")).isEmpty();
                if (answered) {
                        System.out.println("Ya respondida: " + question);
                        return;
                }
                answerQuestion(question, answer);
        }

        /** Escribe en el campo de texto que está debajo de la etiqueta. Si no hay valor, no lo toca. */
        private void typeField(String label, String text) {
                if (text == null || text.isBlank()) {
                        return;
                }
                scrollToText(label, 0.15);
                if (textFilled(label)) {
                        return;
                }
                type(textBelow(label), text);
                hideKeyboardQuietly();
        }

        private void selectDate(String label, int day, int month, int year) {
                scrollToText(label, 0.15);
                if (textFilled(label)) {
                        return;
                }
                scrollAndClickReliable(label, textBelow(label), 0.15);
                selectDateInCalendar(day, month, year);
        }

        private void hideKeyboardQuietly() {
                try {
                        driver.hideKeyboard();
                } catch (Exception e) {
                        /* el teclado no estaba abierto */ }
        }

        private static By dropdownBelow(String label) {
                return By.xpath("//android.widget.TextView[@text=" + xpathLiteral(label) + "]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
        }

        private static By textBelow(String label) {
                return By.xpath("//android.widget.TextView[@text=" + xpathLiteral(label) + "]/following::android.widget.EditText[1]");
        }

        private static String value(TestScenario data, String defaultValue, String... columns) {
                String value = data == null ? "" : data.get(columns);
                return value.isBlank() ? defaultValue : value.replaceAll("\\.0$", "");
        }

        private static int number(TestScenario data, int defaultValue, String... columns) {
                String value = value(data, "", columns);
                return value.isBlank() ? defaultValue : Integer.parseInt(value);
        }

        public void radicationProcess(String departmentBirth, String cityBirth, int expDay, int expMounth,
                        int expYear) {
                // Click en el boton de radicacion
                clickSafely(RADICATE_BUTTON);

                scrollAndClickReliable("Departamento de nacimiento del solicitante", DEPARTMENT_DROPDOWN, 0.15);
                selectDropdownOption(departmentBirth);

                scrollAndClickReliable("Ciudad de nacimiento del solicitante", CITY_DROPDOWN, 0.10);
                selectDropdownOption(cityBirth);

                // Hasta aqui esta bien

                scrollToText("Ciudad de expedición de identificación del solicitante", 0.10);

                clickSafely(EXPEDITION_DATE);
                selectDateInCalendar(expDay, expMounth, expYear);

                clickComposeDropdown(EXPEDITION_DEPARTMENT_CLIENT_DOCUMENT);
                selectDropdownOption(departmentBirth);
        }
}
