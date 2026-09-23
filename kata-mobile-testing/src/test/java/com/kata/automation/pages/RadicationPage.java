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
    private static final By DEPARTMENT_DROPDOWN = By.xpath(
            "//android.widget.TextView[@text=\"Departamento de nacimiento del solicitante\"]/following-sibling::android.view.View[@content-desc=\"Menú desplegable\"]");
    private static final By CITY_DROPDOWN = By.xpath(
            "//android.widget.TextView[@text=\"Ciudad de nacimiento del solicitante\"]/following-sibling::android.view.View[@content-desc=\"Menú desplegable\"]");
    private static final By EXPEDITION_DATE = By.xpath(
            "//android.widget.TextView[@text=\"Fecha de expedición de identificación del solicitante\"]/following-sibling::android.widget.EditText");
    private static final By EXPEDITION_DEPARTMENT_CLIENT_DOCUMENT = By.xpath(
            "//android.widget.TextView[@text=\"Departamento de expedición de identificación del solicitante\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
    private static final By LAST_YEAR_COMPLETED = By.xpath(
            "//android.widget.TextView[@text=\"Último año cursado del solicitante\"]/following::android.widget.EditText");
    private static final By AFFILIATION_CLIENT = By.xpath(
            "//android.widget.TextView[@text=\"Afiliación salud del solicitante\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
    private static final By NETWOK_ACCESS_CLIENT = By.xpath(
            "//android.widget.TextView[@text=\"Acceso a internet del solicitante\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
    // --- LOCALIZADORES COMPOSICIÓN FAMILIAR ---
    // NOTA: Ajusta el texto "AGREGAR" o "GUARDAR" según lo que diga exactamente el
    // botón en tu app
    private static final By FAMILY_COMPOSITION = By
            .xpath("//android.widget.TextView[@text=\"Composición familiar\"]/following::android.widget.Button[1]");
    private static final By ADD_RECORD_BUTTON = By.xpath("//android.widget.Button");
    private static final By FAMILY_MEMBER_TYPE = By.xpath(
            "//android.widget.TextView[@text=\"Tipo de familiar\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
    private static final By FAMILY_MEMBER_AGE = By
            .xpath("//android.widget.TextView[@text=\"Edad del familiar\"]/following::android.widget.EditText");
    private static final By FAMILY_MEMBER_GENDER = By.xpath(
            "//android.widget.TextView[@text=\"Género del familiar\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
    private static final By FAMILY_MEMBER_EDUCATIONAL_LEVEL = By.xpath(
            "//android.widget.TextView[@text=\"Nivel de educación del familiar\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
    private static final By FAMILY_MEMBER_LAST_YEAR = By.xpath(
            "//android.widget.TextView[@text=\"Último año cursado del familiar (Año/Semestre)\"]/following::android.widget.EditText");
    private static final By SAVE_FAMILY_BUTTON = By.xpath("//android.widget.Button");
    private static final By CONFIRM_BUTTON = By.xpath("//android.widget.TextView[@text=\"GUARDAR\"]");
    private static final By GO_BACK = AppiumBy.accessibilityId("Ir atrás");

    private static final By EMAIL = By.xpath(
            "//android.widget.TextView[@text=\"Correo electrónico del solicitante\"]/following::android.widget.EditText");
    private static final By CONFIRM_EMAIL = By.xpath(
            "//android.widget.TextView[@text=\"Confirmar correo electrónico del solicitante\"]/following::android.widget.EditText");

    private static final By RESIDENCE_ZONE_CLIENT = By.xpath(
            "//android.widget.TextView[@text=\"Zona de residencia del solicitante\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

    private static final By RESIDENCE_OCUPATION_DATE = By.xpath(
            "//android.widget.TextView[@text=\"Fecha de inicio de ocupación de la residencia\"]/following::android.widget.EditText");

    // DEPARTAMENTO, CIUDAD Y BARRIO DEL ANALISTA
    private static final By BUSSINESS_DEPARTMENT_CLIENT = By.xpath(
            "//android.widget.TextView[@text=\"Departamento del negocio del solicitante\"]/following::android.view.View[@content-desc=\"Menú desplegable\"]");
    private static final By BUSSINESS_CITY_CLIENT = By.xpath(
            "//android.widget.TextView[@text=\"Ciudad del negocio del solicitante\"]/following::android.view.View[@content-desc=\"Menú desplegable\"]");
    private static final By BUSSINESS_NEIGHBORHOOD_CLIENT = By.xpath(
            "//android.widget.TextView[@text=\"Barrio del negocio del solicitante\"]/following::android.view.View[@content-desc=\"Menú desplegable\"]");

    private static final By BUSSINESS_COORDINATES = By
            .xpath("//android.widget.TextView[@text=\"Coordenadas del negocio\"]/following::android.widget.Button[1]");
    private static final By OBSERVATION_COORDINATES = By.xpath(
            "//android.widget.TextView[@text=\"Observaciones de posicionamiento\"]/following::android.view.View[@content-desc=\"Menú desplegable\"]");

    private static final By CLIENT_ANALYST_IMAGE = By.xpath(
            "//android.widget.TextView[@text=\"Selfie del analista y el negocio\"]/following::android.widget.Button");
    private static final By CAPTURE_BUTTON = By.xpath(
            "//androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View/android.view.View/android.view.View[5]");
    private static final By BUSSINESS_TYPE = By.xpath(
            "//android.widget.TextView[@text=\"Tipo de local del negocio\"]/following::android.view.View[@content-desc=\"Menú desplegable\"]");

    // CELULAR DEL NEGOCIO

    private static final By BUSSINESS_NUMBER_PHONE = By
            .xpath("//android.widget.TextView[@text=\"Celular del negocio\"]/following::android.widget.EditText");
    private static final By CONFIRM_BUSSINESS_NUMBER_PHONE = By.xpath(
            "//android.widget.TextView[@text=\"Confirmar celular del negocio\"]/following::android.widget.EditText");

    // GENERAR VITA360
    private static final By GENERATE_VIEW360_BUTTON = By.xpath(
            "//android.widget.TextView[@text=\"Vista 360 del cliente\"]/following::android.view.View[.//android.widget.Button][1]");
    private static final By ACCEPT_CONSULT_CLIENT_BUTTON = By.xpath("//android.widget.TextView[@text=\"ACEPTAR\"]");

    // ACTIVIDADES ECONOMICAS
    private static final By MAIN_SECTOR_ACTIVITY = By.xpath("//android.widget.TextView[@text=\"Sector actividad económica principal\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
    private static final By MAIN_ECONOMICAL_ACTIVITY = By.xpath("//android.widget.TextView[@text=\"Actividad económica principal\"]/following::android.view.View[@content-desc=\"Menú desplegable\"]");
    private static final By MAIN_ECONOMICAL_ACTIVITY_ANTIQUE = By.xpath("//android.widget.TextView[@text=\"Antigüedad del cliente con la actividad económica principal (meses)\"]/following::android.widget.EditText");
    private static final By GROSS_SALES = By.xpath("//android.widget.TextView[@text=\"Ventas brutas anuales actividad principal\"]/following::android.widget.EditText");

    //DECISIÓN
    private static final By DECISION = By.xpath("//android.widget.TextView[@text=\"Decisión de la radicación\"]/following::android.view.View[@content-desc=\"Menú desplegable\"]");
    private static final By PROCCESS_DECISION = By.xpath("//android.widget.TextView[@text=\"Procesar la decisión\"]/following::android.view.View[.//android.widget.Button][1]");

    public void fillFamilyComposition(By openSectionLocator) {
        if (openSectionLocator != null) {
            scrollAndClickReliable("Composición familiar", openSectionLocator, 0.15);
        }

        sleep(1000);

        // 2. Ejecuta el llenado exactamente 2 veces
        for (int i = 1; i <= 2; i++) {
            System.out.println("Agregando familiar #" + i);

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

            clickSafely(SAVE_FAMILY_BUTTON);

            sleep(200);
            clickSafely(CONFIRM_BUTTON);
            sleep(200);

            System.out.println("familiar #" + i + " Terminado...");
        }

        clickSafely(GO_BACK);
    }

    public void completeRadicationExistingClient(String numberId, String lastYearCompleted,
            String affiliation, String networkAccess, String email, String bussinessDepartmentAnalist,
            String bussinessCityAnalist,
            String bussinessNeighborhoodAnalist, String bussinesNumberPhone) {
        clickSafely(RADICATE_BUTTON);
        takeEvidence(numberId, "04_radicacion");

        scrollToText("El solicitante tiene otra nacionalidad", 0.30);

        answerQuestion("¿El solicitante tiene otra nacionalidad?", "No");

        scrollToText("Último año cursado del solicitante", 0.20);
        type(LAST_YEAR_COMPLETED, lastYearCompleted);

        scrollAndClickReliable("Afiliación salud del solicitante",
                AFFILIATION_CLIENT, 0.20);
        click(byVisibleText(affiliation));

        scrollAndClickReliable("Acceso a internet del solicitante",
                NETWOK_ACCESS_CLIENT, 0.20);
        click(byVisibleText(networkAccess));

        sleep(100);

        scrollToText("¿El solicitante tiene propiedad Raiz?", 0.15);

        answerQuestion("¿El solicitante tiene propiedad Raiz?", "No");

        fillFamilyComposition(FAMILY_COMPOSITION);

        scrollToText("Confirmar correo electrónico del solicitante", 0.15);

        type(EMAIL, email);
        type(CONFIRM_EMAIL, email);

        takeEvidence(numberId, "05_correo_electronico_registrado");

        try {
            driver.hideKeyboard();
        } catch (Exception e) {
            /* Ignorado */ }

        scrollToText("Zona de residencia del solicitante", 0.20);

        clickComposeDropdown(RESIDENCE_ZONE_CLIENT);
        click(byVisibleText("Urbano"));

        scrollToText("Fecha de inicio de ocupación de la residencia", 0.15);

        click(RESIDENCE_OCUPATION_DATE);
        selectDateInCalendar(22, 9, 2020);

        // ciudad del analista para que la orden pueda ser validad
        scrollToText("Ciudad del negocio del solicitante", 0.20);

        // PROBAR SI ENCUENTRA EL DEPARTAMENTO EN LA LISTA DESPLAGABLE

        findTextInDropDown(BUSSINESS_DEPARTMENT_CLIENT, bussinessDepartmentAnalist);
        findTextInDropDown(BUSSINESS_CITY_CLIENT, bussinessCityAnalist);
        findTextInDropDown(BUSSINESS_NEIGHBORHOOD_CLIENT, bussinessNeighborhoodAnalist);

        // Hace scroll hasta que el botón sea visible
        scrollToText("Coordenadas del negocio", 0.15);
        sleep(500); // Pausa breve para estabilizar la pantalla

        // Ejecuta el toque físico real
        try {
            forceClick(BUSSINESS_COORDINATES);
        } catch (Exception e) {
            // ignora si ya tiene la ubicacion
            System.out.println("Ubicacion ya registrada");
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

        scrollAndClickReliable("Vista 360 del cliente", GENERATE_VIEW360_BUTTON, 0.15);
        click(ACCEPT_CONSULT_CLIENT_BUTTON);

        scrollToText("Actividad económica principal", 0.15);

        clickComposeDropdown(MAIN_SECTOR_ACTIVITY);
        click(byVisibleText("SERVICIOS"));

        clickComposeDropdown(MAIN_ECONOMICAL_ACTIVITY);
        scrollToTextAndClick("COMERCIALIZACIÓN DE ENERGÍA ELÉCTRICA");

        scrollToText("Antigüedad del cliente con la actividad económica principal (meses)", 0.15);
        type(MAIN_ECONOMICAL_ACTIVITY_ANTIQUE, "60");

        scrollToText("Ventas brutas anuales actividad principal", 0.10);
        type(GROSS_SALES, "15000000");

        scrollToText("¿Permanecí mas de 183 días en el último año, o en 183 días durante los tres últimos años, dentro de territorio de los Estados Unidos?", 0.25);

        answerQuestion("¿Manifiesto que por mi actividad económica declarada en la sección Información General del presente formulario, me encuentro obligado(a) ante la DIAN a inscribirme en el Registro Único Tributario (RUT).?", "No");
        answerQuestion("¿Permanecí mas de 183 días en el último año, o en 183 días durante los tres últimos años, dentro de territorio de los Estados Unidos?", "No");
        answerQuestion("¿Posee tarjeta verde o \"Green Card\" de EEUU? (Tarjeta de residencia).", "No");
        answerQuestion("¿Recibe cualquier pago de intereses, dividendos, rentas, salarios, honorarios, primas, anualidades, compensaciones, remuneraciones, emolumentos y otras ganancias fijas u ocasionales e ingresos (Ingresos FDAP*) cuya fuente se encuentre dentro de los Estados Unidos?", "No");
        answerQuestion("¿El solicitante manifiesta ser reincorporado?", "No");
        answerQuestion("¿Recibo cualquier ingreso bruto procedente de la venta u otra disposición de cualquier propiedad que puede producir intereses o dividendos cuya fuente se encuentre dentro de los Estados Unidos?", "No");

        scrollToText("Decisión de la radicación", 0.10);
        clickComposeDropdown(DECISION);
        click(byVisibleText("Continuar a análisis"));

        scrollToText("Faltan por completar campos esperados para poder continuar con esta consulta", 0.10);

        click(PROCCESS_DECISION);
        takeEvidence(numberId, "06_decision_radicacion");
        click(ACCEPT_CONSULT_CLIENT_BUTTON);
    }

    public void radicationProcess(String departmentBirth, String cityBirth, int expDay, int expMounth, int expYear) {
        // Click en el boton de radicacion
        clickSafely(RADICATE_BUTTON);

        scrollAndClickReliable("Departamento de nacimiento del solicitante", DEPARTMENT_DROPDOWN, 0.251);
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
