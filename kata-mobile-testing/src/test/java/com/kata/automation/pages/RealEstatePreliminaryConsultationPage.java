package com.kata.automation.pages;

import com.kata.automation.utils.TestScenario;

import io.appium.java_client.android.AndroidDriver;

/**
 * Consulta preliminar de la orden "Originación de inmueble".
 *
 * <p>
 * Reutiliza la consulta de {@link OrdersPage}, porque los campos son los mismos que en
 * microcrédito. Lo propio de inmueble es:
 * </p>
 * <ul>
 * <li>El proceso tiene un solo formulario ({@value #FORM}), así que la app salta
 * directo a la confirmación al crear la orden.</li>
 * <li>Los datos se leen por nombre de columna desde el Excel, no por posición.</li>
 * <li>Devuelve el tipo de resultado (cliente nuevo, renovado, mora menor, mora mayor o
 * listas) para que el test decida si la solicitud sigue o termina aquí.</li>
 * </ul>
 */
public class RealEstatePreliminaryConsultationPage extends OrdersPage {

    public static final String ORDER_TYPE = "Originación de inmueble";
    public static final String FORM = "SolicitudInmueblePersonaNatural";

    private static final String DEFAULT_COUNTRY = "COLOMBIA";
    private static final String DEFAULT_ORIGIN = "Gestión front o contact center";

    public RealEstatePreliminaryConsultationPage(AndroidDriver driver) {
        super(driver);
    }

    /** Crea la orden de inmueble desde la lista de órdenes. */
    public void createRealEstateOrder() {
        createOrder(ORDER_TYPE, FORM);
    }

    /**
     * Diligencia la consulta preliminar con los datos de la fila y consulta al cliente.
     * Si el cliente está bloqueado (mora mayor o listas) deja la decisión de rechazo
     * guardada; en los demás casos deja la decisión "Continuar" para pasar a radicación.
     */
    public ConsultationOutcome completePreliminaryConsultation(TestScenario data) {
        setBirthCountry(data.get("País de nacimiento"));
        preliminaryConsultation(
                required(data, "Tipo de identificación", "idType"),
                required(data, "Número de identificación", "numberId"),
                orDefault(data, DEFAULT_COUNTRY, "País de origen de la identificación", "País", "country"),
                required(data, "Primer apellido", "Nombres", "firstName"),
                data.get("Segundo apellido", "segundoApellido"),
                required(data, "Primer nombre", "Apellidos", "name"),
                data.get("Segundo nombre", "segundoNombre"),
                number(data, "Día de nacimiento", "birthDay"),
                number(data, "Mes de nacimiento", "birthMonth"),
                number(data, "Año de nacimiento", "birthYear"),
                required(data, "Celular", "numberPhone"),
                orDefault(data, "No", "Refinanciación o reestructuración", "restructuring"),
                number(data, "Día constitución negocio", "corpDay"),
                number(data, "Mes constitución negocio", "corpMonth"),
                number(data, "Año constitución negocio", "corpYear"),
                required(data, "Antigüedad del negocio (meses)", "longevity").replace(".0", ""),
                orDefault(data, DEFAULT_ORIGIN, "Origen de solicitud", "origin"));
        return getLastConsultationOutcome();
    }

    private static String required(TestScenario data, String... columns) {
        String value = data.get(columns);
        if (value.isBlank()) {
            throw new IllegalArgumentException("Falta el valor de la columna '" + columns[0]
                    + "' en el caso " + data.getCaseId());
        }
        return value;
    }

    private static String orDefault(TestScenario data, String defaultValue, String... columns) {
        String value = data.get(columns);
        return value.isBlank() ? defaultValue : value;
    }

    private static int number(TestScenario data, String... columns) {
        String value = required(data, columns).replace(".0", "");
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("La columna '" + columns[0] + "' debe ser un número entero: "
                    + value, exception);
        }
    }
}
