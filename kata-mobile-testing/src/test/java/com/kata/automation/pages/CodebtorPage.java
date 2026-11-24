package com.kata.automation.pages;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.kata.automation.utils.CodebtorCatalog;
import com.kata.automation.utils.TestScenario;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

/**
 * Registro del codeudor desde "Registro del producto" (botón "Codeudor(es)", que la app muestra
 * al pedir seguro deudores).
 *
 * <p>
 * El codeudor es un mini-flujo de 4 pestañas: Consulta preliminar → Información general,
 * contacto del negocio y/o laboral → Análisis financiero del codeudor → Decisión del codeudor
 * ("Validar codeudor"). Los datos salen de la hoja "Datos codeudor" del Excel (columna
 * "Codeudor" de la solicitud). Cada columna vacía de la hoja usa el valor por defecto de aquí.
 * </p>
 *
 * <p>
 * Extiende RadicationPage para reutilizar el llenado "solo lo vacío" (selectOption, typeField,
 * answerIfEmpty, selectDate…), que son los mismos controles de la radicación del cliente.
 * </p>
 */
public class CodebtorPage extends RadicationPage {

    private static final By CODEBTORS_BUTTON = By.xpath(
            "//android.widget.TextView[@text=\"Codeudor(es)\"]/following::android.view.View[1]");
    // Con la lista vacía es un texto; con registros es un botón flotante con content-desc.
    private static final By ADD_RECORD = By.xpath(
            "//*[@text=\"Agregar registro\" or @content-desc=\"Agregar registro\"]");
    private static final By GO_BACK = AppiumBy.accessibilityId("Ir atrás");
    private static final By FORM_TITLE = By.xpath("//android.widget.TextView[@text=\"Codeudor\"]");
    private static final By SEARCH_INPUT = By.xpath("//android.widget.EditText");
    private static final By ACCEPT = By.xpath("//android.widget.TextView[@text=\"ACEPTAR\"]");

    private static final String TAB_CONSULTATION = "Consulta preliminar";
    private static final String TAB_GENERAL = "Información general, contacto del negocio y/o laboral";
    private static final String TAB_FINANCIAL = "Análisis financiero del codeudor";
    private static final String TAB_DECISION = "Decisión del codeudor";

    private static final By CONSULT_BUTTON = By.xpath(
            "//android.widget.TextView[@text=\"Consultar codeudor\"]/following::android.widget.Button[1]");
    // Hay dos botones: "Vista 360 del codeudor" la genera (responde "Consulta Exitosa" y marca
    // sola la pregunta de centrales) y "Consulta de vista 360 del codeudor" solo la muestra.
    private static final By VIEW360_BUTTON = By.xpath(
            "//android.widget.TextView[@text=\"Vista 360 del codeudor\"]/following::android.widget.Button[1]");
    private static final By VALIDATE_BUTTON = By.xpath(
            "//android.widget.TextView[@text=\"Validar codeudor\"]/following::android.widget.Button[1]");

    private static final By ANY_DIALOG = By.xpath("//android.widget.TextView[@text=\"ACEPTAR\"]");
    private static final By RETRYABLE_ERROR = By.xpath(
            "//*[contains(@text, 'iempo excedido') or contains(@text, 'iempo de espera') or contains(@text, 'timeout')"
                    + " or contains(@text, 'generó el error') or contains(@text, 'InternalServerError')]");

    /** Preguntas Si/No de la pestaña de información general, en el orden del formulario. */
    private static final String[] DECLARATIONS = {
            "¿Tiene otras actividades económicas?",
            "¿Esta vinculado laboralmente?",
            "¿El codeudor esta obligado a tributar en otro pais?",
            "¿Realiza operaciones en moneda extranjera?",
            "¿Posee productos en moneda extranjera?",
            "¿Manifiesto que estoy obligado(a) a cumplir con el deber de presentar declaración de renta y complementarios por el año gravable anterior?",
            "¿Manifiesto que por mi actividad económica declarada en la sección Información General del presente formulario, me encuentro obligado(a) ante la DIAN a inscribirme en el Registro Único Tributario (RUT).?",
            "¿Permanecí más de 183 días en el último año, o 183 días durante los tres últimos años, dentro de territorio de los Estados Unidos?",
            "¿Posee tarjeta verde o \"Green Card\" de EEUU? (Tarjeta de residencia).?",
            "¿Recibe cualquier pago de intereses, dividendos, rentas, salarios, honorarios, primas, anualidades, compensaciones, remuneraciones, emolumentos y otras ganancias fijas u ocasionales e ingresos (Ingresos FDAP*) cuya fuente se encuentre dentro de los Estados Unidos?",
            "¿El codeudor manifiesta ser reincorporado?",
            "¿Recibo cualquier ingreso bruto procedente de la venta u otra disposición de cualquier propiedad que puede producir intereses o dividendos cuya fuente se encuentre dentro de los Estados Unidos?",
            "¿El codeudor es persona expuesta politicamente (PEP)?" };

    /**
     * Campos editables del análisis financiero (columna de la hoja, valor por defecto). La app
     * valida que la cuota del crédito quepa en la liquidez del codeudor ("El valor de la cuota
     * supera el autorizado según la liquidez"): con estos valores la liquidez para la cuota es
     * ~$23M. Para montos mayores, suba las ventas en la hoja "Datos codeudor".
     */
    private static final String[][] FINANCIAL_FIELDS = {
            { "Total ventas", "40000000" },
            { "Total compras", "12000000" },
            { "Gastos del negocio", "1000000" },
            { "Gastos financieros", "0" },
            { "Total otros ingresos", "0" },
            { "Otros ingresos fllujo de caja (según políticas)", "0" },
            { "Ingresos deudor solidario", "0" },
            { "Gastos familiares", "1500000" },
            { "Total bienes del negocio", "80000000" },
            { "Pasivos del negocio", "0" },
            { "Cuentas W", "0" },
            { "Total activos", "120000000" },
            { "Pasivos totales", "0" } };

    private Map<String, String> codebtor;
    /** Registro recién creado: todo está vacío, así que se omite la revisión "¿ya está lleno?". */
    private boolean fresh;

    public CodebtorPage(AndroidDriver driver) {
        super(driver);
    }

    /** True si la solicitud del Excel trae codeudor (columna "Codeudor"). */
    public static boolean requested(TestScenario data) {
        return !CodebtorCatalog.selected(data).isBlank();
    }

    /**
     * Abre "Codeudor(es)" en Registro del producto, agrega el codeudor de la hoja y vuelve al
     * registro del producto. Si la solicitud no trae codeudor no hace nada.
     */
    public void registerCodebtor(String numberId, TestScenario data) {
        if (!requested(data)) {
            return;
        }
        codebtor = CodebtorCatalog.forScenario(data);
        String id = v("Número de identificación", "");
        System.out.println("Registrando codeudor " + id + " para la solicitud " + numberId + ".");

        try {
            scrollAndClickReliable("Codeudor(es)", CODEBTORS_BUTTON, 0.15);
        } catch (RuntimeException notShown) {
            throw new IllegalStateException("La solicitud trae codeudor (" + id + ") pero la app no muestra el botón "
                    + "'Codeudor(es)' en el registro del producto. Revise que el seguro deudores quede en Si.", notShown);
        }
        waitForVisibility(ADD_RECORD);
        // La lista muestra apellidos y nombre (no la cédula).
        String surname = v("Primer apellido", "");
        if (!surname.isBlank() && isPresentWithin(By.xpath("//android.widget.TextView[@text="
                + xpathLiteral(surname.toUpperCase()) + "]"), Duration.ofSeconds(1))) {
            System.out.println("El codeudor " + id + " ya está registrado en la orden.");
            click(GO_BACK);
            return;
        }
        click(ADD_RECORD);
        waitForVisibility(FORM_TITLE);
        fresh = true;
        fillCodebtorForm(numberId);
        returnToProductRegistration();
    }

    /** True si el formulario "Codeudor" está abierto en pantalla. */
    public boolean isFormOpen() {
        return !findNow(FORM_TITLE).isEmpty();
    }

    /** Completa el formulario de codeudor que ya está abierto y vuelve al registro del producto. */
    public void completeOpenForm(String numberId, TestScenario data) {
        codebtor = CodebtorCatalog.forScenario(data);
        fillCodebtorForm(numberId);
        returnToProductRegistration();
    }

    /**
     * Llena las 4 pestañas del formulario "Codeudor" ya abierto. Si la consulta preliminar ya se
     * hizo (la app muestra la pestaña de información general), no se repite.
     */
    public void fillCodebtorForm(String numberId) {
        if (codebtor == null) {
            codebtor = Map.of();
        }
        long t = System.currentTimeMillis();
        if (findNow(byVisibleText(TAB_GENERAL)).isEmpty()) {
            consultation(numberId);
            t = logTime("consulta preliminar", t);
        }
        generalInformation(numberId);
        t = logTime("información general", t);
        financialAnalysis(numberId);
        t = logTime("análisis financiero", t);
        decision(numberId);
        logTime("decisión y validación", t);
    }

    // ------------------------------------------------------------------ pestañas

    private void consultation(String numberId) {
        openTab(TAB_CONSULTATION);
        String id = v("Número de identificación", "");
        if (id.isBlank()) {
            throw new IllegalArgumentException("Falta el número de identificación del codeudor en la hoja '"
                    + CodebtorCatalog.SHEET + "'.");
        }
        selectOption("Tipo de identificación del codeudor", v("Tipo de identificación", "Cedula ciudadanía"));
        typeField("Número de identificación del codeudor", id);
        typeField("Confirmar número de identificación del codeudor", id);
        selectOption("País de origen de la identificación del codeudor",
                v("País de origen de la identificación", "COLOMBIA"));
        typeField("Primer apellido codeudor", v("Primer apellido", ""));
        typeField("Segundo apellido codeudor", v("Segundo apellido", ""));
        typeField("Primer nombre codeudor", v("Primer nombre", ""));
        typeField("Segundo nombre codeudor", v("Segundo nombre", ""));
        selectDate("Fecha de nacimiento del codeudor", n("Día de nacimiento", 15), n("Mes de nacimiento", 5),
                n("Año de nacimiento", 1985));
        selectOption("País de nacimiento del codeudor", v("País de nacimiento", "COLOMBIA"));
        // La app exige que sea distinto al celular del negocio del solicitante.
        String phone = v("Celular", "3157778899");
        typeField("Celular del codeudor", phone);
        typeField("Confirmar Celular codeudor", phone);
        answerIfEmpty("¿Autoriza el tratamiento de datos personales?", "Si");
        answerIfEmpty("¿Autoriza la consulta y reporte a las centrales de información?", "Si");
        answerIfEmpty("¿Acepta autorización de canales, horarios de contacto y visitas?", "Si");
        // Tras la consulta la app bloquea estos campos: si una confirmación quedó vacía ya no
        // se puede corregir y el codeudor no se deja guardar. Se revisan justo antes de consultar.
        ensureConfirmed("Confirmar número de identificación del codeudor", id);
        ensureConfirmed("Confirmar Celular codeudor", phone);
        takeEvidence(numberId, "codeudor_consulta");

        scrollToText("Consultar codeudor", 0.15);
        pressAndCheck(numberId, CONSULT_BUTTON, "consulta del codeudor", "codeudor_consulta_respuesta");
        if (!isPresentWithin(byVisibleText(TAB_GENERAL), Duration.ofSeconds(5))) {
            throw new IllegalStateException("La consulta del codeudor no habilitó la pestaña '" + TAB_GENERAL + "'.");
        }
    }

    private void generalInformation(String numberId) {
        openTab(TAB_GENERAL);
        selectOption("Departamento de nacimiento del codeudor", v("Departamento de nacimiento", "VALLE DEL CAUCA"));
        selectOption("Ciudad de nacimiento del codeudor", v("Ciudad de nacimiento", "CALI"));
        selectDate("Fecha de expedición de identificación del codeudor", n("Día de expedición", 1),
                n("Mes de expedición", 6), n("Año de expedición", 2005));
        selectOption("Departamento de expedición de la identificación del codeudor",
                v("Departamento de expedición", "VALLE DEL CAUCA"));
        selectOption("Ciudad de expedición de la identificación del codeudor", v("Ciudad de expedición", "CALI"));
        selectOption("Nacionalidad del codeudor", v("Nacionalidad", "COLOMBIA"));
        answerIfEmpty("¿El codeudor tiene otra nacionalidad?", "No");
        selectOption("Género del codeudor", v("Género", "Masculino"));
        selectOption("Grupo étnico del codeudor", v("Grupo étnico", "Ningún grupo étnico"));
        selectOption("Estado civil del codeudor", v("Estado civil", "Soltero(a)"));
        selectOption("Nivel de educación del codeudor", v("Nivel de educación", "Secundaria"));
        typeField("Último año cursado por el codeudor", v("Último año cursado", "11"));
        selectOption("Profesión del codeudor", v("Profesión", "Administrador"));
        selectOption("Ocupación del codeudor", v("Ocupación", "MICROEMPRESARIO"));
        answerIfEmpty("¿El codeudor es cónyuge del cliente?", v("Es cónyuge del cliente", "No"));

        typeField("Número de hijos del codeudor", v("Número de hijos", "0"));
        typeField("Número de personas a cargo del codeudor", v("Personas a cargo", "0"));

        String email = v("Correo electrónico", "codeudor.prueba@gmail.com");
        typeField("Correo electrónico del codeudor", email);
        typeField("Confirmar Correo electrónico del Codeudor", email);
        // Fijos: máximo 7 dígitos (con 10 la app deja el campo vacío).
        typeField("Teléfono fijo del codeudor", v("Teléfono fijo", "3334455"));
        typeField("Celular del codeudor", v("Celular", "3157778899"));
        typeField("Contacto alternativo del codeudor", v("Contacto alternativo", "3001112233"));
        selectOption("Zona de Residencia del codeudor", v("Zona de residencia", "Urbano"));
        // "cedeudor": así está escrito en la app.
        selectOption("País de residencia del cedeudor", v("País de residencia", "COLOMBIA"));
        selectOption("Departamento de residencia codeudor", v("Departamento de residencia", "VALLE DEL CAUCA"));
        selectOption("Ciudad de residencia codeudor", v("Ciudad de residencia", "CALI"));
        scrollToText("Barrio de residencia codeudor", 0.15);
        if (!dropdownFilled("Barrio de residencia codeudor")) {
            findTextWithSearch(dropdownBelow("Barrio de residencia codeudor"), SEARCH_INPUT,
                    v("Barrio de residencia", "LA INDEPENDENCIA"));
        }
        typeField("Dirección de residencia del codeudor", v("Dirección de residencia", "CALLE 15 # 30 40"));
        typeField("Referencia de la residencia del codeudor", v("Referencia de la residencia", "CASA DE DOS PISOS"));
        selectOption("Estrato del codeudor", v("Estrato", "Estrato 2"));
        selectOption("Tipo de vivienda del codeudor", v("Tipo de vivienda", "Familiar"));

        // --- Centrales de riesgo: Vista 360 del codeudor ---
        scrollToText("Consulta de vista 360 del codeudor", 0.15); // deja visible el botón de arriba
        pressAndCheck(numberId, VIEW360_BUTTON, "vista 360 del codeudor", "codeudor_vista360");
        answerIfEmpty("¿Se realizó consulta a centrales de riesgo, data y cifin?", "Si");

        // --- Negocio del codeudor: la app lo pide cuando la ocupación tiene negocio
        // (p. ej. MICROEMPRESARIO). La sección queda entre centrales de riesgo y actividades.
        if (labelBelow("Nombre del negocio del codeudor")) {
            typeField("Nombre del negocio del codeudor", v("Nombre del negocio", "TIENDA DON LUIS"));
            selectOption("Ubicación del negocio del codeudor", v("Ubicación del negocio", "Local Comercial"));
            selectOption("País del negocio del codeudor", v("País del negocio", "COLOMBIA"));
            selectOption("Departamento del negocio del codeudor", v("Departamento del negocio", "VALLE DEL CAUCA"));
            selectOption("Ciudad del negocio del codeudor", v("Ciudad del negocio", "CALI"));
            scrollToText("Barrio del negocio del codeudor", 0.15);
            if (!dropdownFilled("Barrio del negocio del codeudor")) {
                findTextWithSearch(dropdownBelow("Barrio del negocio del codeudor"), SEARCH_INPUT,
                        v("Barrio del negocio", "LA INDEPENDENCIA"));
            }
            typeField("Dirección de negocio del codeudor", v("Dirección del negocio", "CARRERA 8 # 12 20"));
            typeField("Referencia de negocio del codeudor", v("Referencia del negocio", "FRENTE AL PARQUE"));
            // Un solo flujo: "Familiar" (igual que en la radicación del cliente) no abre campos extra.
            // "Arrendada"/"Alquilada" abren los datos del arrendador; si el Excel los elige, se llenan abajo.
            selectOption("Tipo de local del negocio del codeudor", v("Tipo de local", "Familiar"));
            // Con local arrendado/alquilado la app pide los datos del arrendador.
            for (int i = 0; i < 2; i++) {
                scrollDownPercent(-0.3);
            }
            if (labelBelow("Nombre del arrendador del local")) {
                typeField("Nombre del arrendador del local", v("Nombre del arrendador", "PEDRO PEREZ"));
                typeField("Número de contacto del arrendador del local", v("Celular del arrendador", "3205556677"));
                typeAmount("Canon de arrendamiento del local", v("Canon de arrendamiento", "800000"));
            } else {
                for (int i = 0; i < 2; i++) {
                    scrollDownPercent(-0.3);
                }
            }
            typeField("Teléfono fijo del negocio del codeudor", v("Teléfono fijo del negocio", "4445566"));
            String businessPhone = v("Celular del negocio", "3168889900");
            typeField("Celular del negocio del codeudor", businessPhone);
            typeField("Confirmar celular del negocio del codeudor", businessPhone);
            answerIfEmpty("¿Registro de camara y comercio?", v("Registro de cámara y comercio", "No"));
            selectDate("Fecha de constitución del negocio", n("Día de constitución del negocio", 1),
                    n("Mes de constitución del negocio", 3), n("Año de constitución del negocio", 2015));
        }

        selectOption("Sector actividad económica principal", v("Sector actividad económica", "SERVICIOS"));
        selectOption("Actividad económica principal",
                v("Actividad económica", "COMERCIALIZACIÓN DE ENERGÍA ELÉCTRICA"));
        for (String question : DECLARATIONS) {
            answerIfEmpty(question, "No");
        }
        takeEvidence(numberId, "codeudor_informacion_general");
    }

    private void financialAnalysis(String numberId) {
        openTab(TAB_FINANCIAL);
        selectOption("País de origen de los recursos", v("País de origen de los recursos", "Colombia"));
        for (String[] field : FINANCIAL_FIELDS) {
            typeAmount(field[0], v(field[0], field[1]));
        }
        // Al escribir un valor la app recalcula los totales y a veces borra otro campo ya
        // escrito (pasó con Total compras y Gastos familiares): se repasan todos y se
        // vuelven a escribir los que quedaron vacíos.
        for (int pass = 1; pass <= 2; pass++) {
            boolean rewrote = false;
            // Lectura rápida: se lee la pantalla completa (page source) en vez de buscar
            // campo por campo, y solo se vuelve a escribir lo que quedó vacío.
            java.util.Set<String> empty = emptyTextFields(FINANCIAL_FIELDS);
            for (String[] field : FINANCIAL_FIELDS) {
                if (empty.contains(field[0])) {
                    System.out.println("El campo '" + field[0] + "' quedó vacío; se escribe de nuevo.");
                    scrollToTop();
                    typeAmount(field[0], v(field[0], field[1]));
                    rewrote = true;
                }
            }
            if (!rewrote) {
                break;
            }
            if (pass == 2) {
                for (String[] field : FINANCIAL_FIELDS) {
                    scrollToText(field[0], 0.15);
                    if (amountDigits(field[0]).isEmpty()) {
                        throw new IllegalStateException("No se pudo llenar el campo '" + field[0] + "' del análisis financiero del codeudor.");
                    }
                }
            }
        }
        // Aparece al diligenciar "Total otros ingresos" (aunque sea $0) y es obligatoria.
        scrollToTop();
        if (labelBelow("Descripción de otros ingresos")) {
            typeField("Descripción de otros ingresos", v("Descripción de otros ingresos", "NO TIENE OTROS INGRESOS"));
        }
        takeEvidence(numberId, "codeudor_analisis_financiero");
    }

    private void decision(String numberId) {
        openTab(TAB_DECISION);
        typeField("Concepto sobre el codeudor del analista",
                v("Concepto del analista", "Codeudor con capacidad de pago y buenas referencias"));
        selectOption("Decisión sobre el codeudor", v("Decisión sobre el codeudor", "Aprobar codeudor"));
        selectOption("Lugar de entrevista del codeudor", v("Lugar de entrevista", "Negocio del Cliente"));
        selectOption("Resultado de la entrevista del codeudor", v("Resultado de la entrevista", "Satisfactorio"));
        selectOption("Propósito de la relación contractual del codeudor",
                v("Propósito de la relación contractual", "Capital de trabajo"));
        takeEvidence(numberId, "codeudor_decision");
        scrollToText("Validar codeudor", 0.15);
        pressAndCheck(numberId, VALIDATE_BUTTON, "validación del codeudor", "codeudor_validacion");
    }

    // ------------------------------------------------------------------ apoyo

    /**
     * Toca el botón y espera el mensaje de la app. Las fallas temporales (tiempo de espera,
     * servicio caído) las reintenta clickWithRetry; cualquier otro mensaje que no sea de éxito
     * es un rechazo: se deja evidencia y se falla con el texto de la app.
     */
    private void pressAndCheck(String numberId, By button, String step, String evidence) {
        hideKeyboardQuietly();
        clickWithRetry(button, ANY_DIALOG, RETRYABLE_ERROR);
        String message = visibleDialogText();
        takeEvidence(numberId, evidence);
        String lower = message.toLowerCase();
        boolean ok = lower.contains("exitos") || lower.contains("procesad") || lower.contains("guardad")
                || lower.contains("aprobad");
        if (!ok) {
            throw new IllegalStateException("La app rechazó la " + step + ": \"" + message + "\".");
        }
        System.out.println("Respuesta " + step + ": " + message);
        click(ACCEPT);
        sleep(800);
    }

    private void openTab(String tab) {
        dismissMessageIfPresent();
        click(byVisibleText(tab));
        sleep(500);
        // Cada pestaña se recorre de arriba hacia abajo (scrollToText solo baja). Una pestaña
        // que se abre por primera vez ya está arriba.
        if (!fresh) {
            scrollToTop();
        }
    }

    /**
     * Sale del formulario ("¿Desea guardar los cambios?" → SI) y de la lista de codeudores hasta
     * volver al registro del producto. Si la app no deja salir es porque quedó un campo
     * obligatorio vacío (la pestaña muestra un contador rojo): se falla con evidencia.
     */
    private void returnToProductRegistration() {
        returnOnce();
        if (!findNow(FORM_TITLE).isEmpty() && findNow(ADD_RECORD).isEmpty()) {
            List<WebElement> badges = findNow(By.xpath("//android.widget.TextView[string-length(@text) <= 2"
                    + " and translate(@text, '0123456789', '') = '' and string-length(@text) > 0]"
                    + "/preceding-sibling::android.widget.TextView[1]"));
            StringBuilder pending = new StringBuilder();
            for (WebElement tab : badges) {
                pending.append(pending.length() > 0 ? ", " : "").append(tab.getText());
            }
            takeEvidence("codeudor", "codeudor_no_guardado");
            throw new IllegalStateException("La app no dejó guardar el codeudor: quedan campos obligatorios vacíos"
                    + (pending.length() > 0 ? " en: " + pending : "") + ".");
        }
    }

    private void returnOnce() {
        for (int i = 0; i < 3; i++) {
            if (!findNow(By.xpath("//android.widget.TextView[@text=\"Registro del producto\"]")).isEmpty()) {
                return;
            }
            dismissMessageIfPresent();
            List<WebElement> back = findNow(GO_BACK);
            if (back.isEmpty()) {
                return;
            }
            back.get(0).click();
            sleep(1200);
            List<WebElement> yes = findNow(By.xpath("//android.widget.TextView[@text=\"SI\" or @text=\"Si\" or @text=\"SÍ\"]"));
            if (!yes.isEmpty() && findNow(ADD_RECORD).isEmpty()) {
                yes.get(0).click();
                sleep(800);
            }
        }
    }

    /**
     * Campo de dinero: la app muestra "$0" cuando el valor es cero, y a veces deja el campo vacío
     * al escribir. Un "$0" ya escrito se respeta para el valor 0; si queda vacío se escribe
     * tecla por tecla (igual que el monto del producto).
     */
    private void typeAmount(String label, String value) {
        String digits = value.replaceAll("[^0-9]", "");
        scrollToText(label, 0.15);
        String current = amountDigits(label);
        if (current.equals(digits.replaceFirst("^0+(?=.)", "")) || (!current.isEmpty() && !"0".equals(current))) {
            return; // ya tiene el valor, o un valor que no es cero (no se pisa)
        }
        WebElement field = driver.findElement(textBelow(label));
        field.click();
        field.clear();
        field.sendKeys(digits);
        if (amountDigits(label).isEmpty()) {
            field = driver.findElement(textBelow(label));
            field.click();
            for (char digit : digits.toCharArray()) {
                field.sendKeys(String.valueOf(digit));
            }
        }
        hideKeyboardQuietly();
    }

    /** Dígitos que muestra el campo de dinero ("" si está vacío). */
    private String amountDigits(String label) {
        return currentText(textBelow(label)).replaceAll("[^0-9]", "");
    }

    // ------------------------------------------------------------------ velocidad

    private static long logTime(String step, long started) {
        long now = System.currentTimeMillis();
        System.out.println("⏱ Codeudor - " + step + ": " + (now - started) / 1000 + " s");
        return now;
    }

    /**
     * Sube al inicio de la pestaña con gestos rápidos de UiAutomator (cada uno recorre una
     * pantalla entera y avisa si queda más por subir), en vez de 12 deslizamientos fijos.
     */
    private void scrollToTop() {
        org.openqa.selenium.Dimension size = driver.manage().window().getSize();
        Map<String, Object> area = Map.of("left", 20, "top", (int) (size.height * 0.28),
                "width", size.width - 40, "height", (int) (size.height * 0.62),
                "direction", "up", "percent", 1.0, "speed", 10000);
        for (int i = 0; i < 15; i++) {
            Object more;
            try {
                more = driver.executeScript("mobile: scrollGesture", area);
            } catch (RuntimeException unsupported) {
                for (int j = 0; j < 12; j++) {
                    scrollDownPercent(-0.8);
                }
                return;
            }
            if (!Boolean.TRUE.equals(more)) {
                return;
            }
        }
    }

    /** Campos de texto de la lista que están vacíos, leyendo la pestaña completa por pantallas. */
    private java.util.Set<String> emptyTextFields(String[][] fields) {
        java.util.Set<String> wanted = new java.util.HashSet<>();
        for (String[] field : fields) {
            wanted.add(field[0]);
        }
        java.util.Map<String, String> seen = new java.util.HashMap<>();
        scrollToTop();
        String previous = "";
        for (int screen = 0; screen < 12 && seen.size() < wanted.size(); screen++) {
            String source = driver.getPageSource();
            if (source.equals(previous)) {
                break;
            }
            previous = source;
            java.util.regex.Matcher m = java.util.regex.Pattern.compile(
                    "<android\\.widget\\.(TextView|EditText) [^>]*?text=\"([^\"]*)\"").matcher(source);
            String label = null;
            while (m.find()) {
                if ("TextView".equals(m.group(1))) {
                    label = m.group(2);
                } else if (label != null && wanted.contains(label) && !seen.containsKey(label)) {
                    seen.put(label, m.group(2).replaceAll("[^0-9]", ""));
                }
            }
            scrollDownPercent(0.6);
        }
        java.util.Set<String> empty = new java.util.HashSet<>();
        for (Map.Entry<String, String> entry : seen.entrySet()) {
            if (entry.getValue().isEmpty()) {
                empty.add(entry.getKey());
            }
        }
        return empty;
    }

    @Override
    protected void typeField(String label, String text) {
        if (!fresh) {
            super.typeField(label, text);
            return;
        }
        if (text == null || text.isBlank()) {
            return;
        }
        scrollToText(label, 0.15);
        type(textBelow(label), text);
        hideKeyboardQuietly();
        verifyTyped(label, text);
    }

    @Override
    protected void selectOption(String label, String option) {
        if (!fresh) {
            super.selectOption(label, option);
            return;
        }
        scrollAndClickReliable(label, dropdownBelow(label), 0.15);
        sleep(300);
        selectDropdownOption(option);
    }

    /** Busca la etiqueta bajando la pantalla; false si no existe en la pestaña. */
    private boolean labelBelow(String label) {
        try {
            scrollToText(label, 0.15);
            return true;
        } catch (RuntimeException notFound) {
            return false;
        }
    }

    /** Deja el campo de confirmación igual al valor; lo reescribe sin ocultar el teclado. */
    private void ensureConfirmed(String label, String expected) {
        String digits = expected.replaceAll("[^0-9]", "");
        for (int attempt = 1; attempt <= 2; attempt++) {
            scrollToTop();
            scrollToText(label, 0.15);
            String current = currentText(textBelow(label)).replaceAll("[^0-9]", "");
            if (current.equals(digits)) {
                return;
            }
            System.out.println("La confirmación '" + label + "' quedó como '" + current + "'; se escribe de nuevo.");
            type(textBelow(label), expected);
        }
        String current = currentText(textBelow(label)).replaceAll("[^0-9]", "");
        if (!current.equals(digits)) {
            throw new IllegalStateException("No se pudo llenar '" + label + "' con " + expected + ".");
        }
    }

    private String v(String column, String defaultValue) {
        return CodebtorCatalog.get(codebtor, column, defaultValue);
    }

    private int n(String column, int defaultValue) {
        String value = v(column, "");
        return value.isBlank() ? defaultValue : Integer.parseInt(value.replaceAll("[^0-9]", ""));
    }
}
