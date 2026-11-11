package com.kata.automation.pages;

import org.openqa.selenium.By;

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
                click(byVisibleText(affiliation));

                scrollAndClickReliable("Acceso a internet del solicitante",
                                NETWOK_ACCESS_CLIENT, 0.15);
                click(byVisibleText(networkAccess));

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

                // scrollAndClickReliable("Vista 360 del cliente", GENERATE_VIEW360_BUTTON,
                // 0.15);
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

                clickWithRetry(PROCCESS_DECISION, CONSULTATION_RESULT, TIMEOUT_ERROR_MESSAGE);
                takeEvidence(numberId, "decision_radicacion");
                waitForVisibility(ACCEPT_CONSULT_CLIENT_BUTTON);
                clickSafely(ACCEPT_CONSULT_CLIENT_BUTTON);
        }

        public void radicationProcess(String departmentBirth, String cityBirth, int expDay, int expMounth,
                        int expYear) {
                // Click en el boton de radicacion
                clickSafely(RADICATE_BUTTON);

                scrollAndClickReliable("Departamento de nacimiento del solicitante", DEPARTMENT_DROPDOWN, 0.15);
                clickSafely(byVisibleText(departmentBirth));

                scrollAndClickReliable("Ciudad de nacimiento del solicitante", CITY_DROPDOWN, 0.10);
                clickSafely(byVisibleText(cityBirth));

                // Hasta aqui esta bien

                scrollToText("Ciudad de expedición de identificación del solicitante", 0.10);

                clickSafely(EXPEDITION_DATE);
                selectDateInCalendar(expDay, expMounth, expYear);

                clickComposeDropdown(EXPEDITION_DEPARTMENT_CLIENT_DOCUMENT);
                clickSafely(byVisibleText(departmentBirth));
        }
}
