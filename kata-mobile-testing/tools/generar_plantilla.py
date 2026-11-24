"""Genera la plantilla única de solicitudes (microcrédito + inmueble).

Uso (desde kata-mobile-testing):
    python tools/generar_plantilla.py <plantilla_original.xlsx> <salida.xlsx> [filas_extra.xlsx ...]

- Columnas en el orden de la app: consulta preliminar -> radicación -> registro del
  producto -> decisión del análisis. Sin columnas repetidas: cada dato se escribe una vez
  y el robot lo usa en todas las pantallas donde se pide (p. ej. Celular, Correo).
- "Tipo de orden" decide el flujo. Las columnas que solo usa un tipo de orden lo dicen
  en el comentario del encabezado.
- Cada desplegable de la app tiene su lista (hoja ListasApp). Producto depende de Tipo de
  producto, Destino depende de Producto y Actividad económica depende del sector.
- Conserva las hojas Parametros y Listas (FNG / Fianza) de la plantilla original y migra
  sus filas renombrando las columnas viejas.
"""
import json
import os
import sys
from copy import copy

import openpyxl
from openpyxl.comments import Comment
from openpyxl.styles import Alignment, Font, PatternFill
from openpyxl.utils import get_column_letter as L
from openpyxl.worksheet.datavalidation import DataValidation

HERE = os.path.dirname(os.path.abspath(__file__))
APP = json.load(open(os.path.join(HERE, "listas_app.json"), encoding="utf-8"))
SI_NO = ["Si", "No"]
DEPARTAMENTOS = APP["Departamento de nacimiento del solicitante"]
PAISES = APP["País de origen de la identificación del solicitante"]
CIUDADES_VALLE = APP["Ciudad de nacimiento del solicitante"]
MICRO_PRODUCTS = ["Crédito productivo urbano360", "Crédito prod mayor monto360", "Crédito popular prod Urbano360",
                  "Crédito pop prod Urban Gota Sem360", "Crédito pop prod Urban Gota Quin360",
                  "Credito popular productivo rural360", "Credito productivo rural360",
                  "Credito popular productivo rural quincenal360", "Credito popular productivo rural semanal360",
                  "Credito Inmediato"]
MICROPYME_DESTINOS = ["Capital de Trabajo", "Activos fijos para el negocio (mobiliarios)",
                      "Vehículos con destino productivo", "Remodelación del local comercial"]
MICRO_DESTINOS = ["Capital de Trabajo", "Activo Fijo", "Yo construyo Estructura y Obra Negra", "Ola Invernal"]

# (columna, etapa, lista, completa, nota)
# lista: lista de opciones | "FNG"/"FIANZA" (rango con nombre existente) | ("DEP", columna_padre)
CONSULTA, RADICACION, PRODUCTO, DECISION, CONTROL = (
    "Consulta preliminar", "Radicación", "Registro del producto", "Decisión del análisis", "Control")
SCHEMA = [
    ("ID caso", CONTROL, None, False, "Identificador del caso (evidencias)."),
    # --- Consulta preliminar ---
    ("Tipo de orden", CONSULTA, ["Originación de microcrédito", "Originación de inmueble",
                                 "Originación de micropyme"], True,
     "Define el flujo: microcrédito, inmueble o micropyme (micropyme = microcrédito sin validación de identidad)."),
    ("Tipo de persona", CONSULTA, ["SolicitudPersonaNatural", "SolicitudMicroPymePersonaNatural"], False,
     "Formulario: microcrédito SolicitudPersonaNatural, micropyme SolicitudMicroPymePersonaNatural (inmueble tiene un único formulario)."),
    ("Tipo de identificación", CONSULTA, APP["Tipo de identificación"], True, None),
    ("Número de identificación", CONSULTA, None, False, None),
    ("País de origen de la identificación", CONSULTA, PAISES, True, None),
    ("Primer apellido", CONSULTA, None, False, None),
    ("Segundo apellido", CONSULTA, None, False, "Opcional."),
    ("Primer nombre", CONSULTA, None, False, None),
    ("Segundo nombre", CONSULTA, None, False, "Opcional."),
    ("Día de nacimiento", CONSULTA, None, False, None),
    ("Mes de nacimiento", CONSULTA, None, False, None),
    ("Año de nacimiento", CONSULTA, None, False, None),
    ("País de nacimiento", CONSULTA, PAISES, True, "Vacío = el mismo país de la identificación."),
    ("Celular", CONSULTA, None, False, "Se usa en consulta y en radicación."),
    ("Refinanciación o reestructuración", CONSULTA, SI_NO, True, None),
    ("Día constitución negocio", CONSULTA, None, False, None),
    ("Mes constitución negocio", CONSULTA, None, False, None),
    ("Año constitución negocio", CONSULTA, None, False, None),
    ("Antigüedad del negocio (meses)", CONSULTA, None, False, None),
    ("Origen de solicitud", CONSULTA, APP["Origen de solicitud"], True, None),
    # --- Radicación ---
    ("Departamento de nacimiento", RADICACION, DEPARTAMENTOS, True, "Solo cliente nuevo."),
    ("Ciudad de nacimiento", RADICACION, CIUDADES_VALLE, False, "Solo cliente nuevo. Lista: ciudades del Valle."),
    ("Día de expedición", RADICACION, None, False, "Solo cliente nuevo."),
    ("Mes de expedición", RADICACION, None, False, "Solo cliente nuevo."),
    ("Año de expedición", RADICACION, None, False, "Solo cliente nuevo."),
    ("Departamento de expedición", RADICACION, DEPARTAMENTOS, True, "Solo cliente nuevo."),
    ("Ciudad de expedición", RADICACION, CIUDADES_VALLE, False, "Solo cliente nuevo."),
    ("Nacionalidad", RADICACION, PAISES, True, "Solo cliente nuevo."),
    ("Género", RADICACION, ["Masculino", "Femenino", "No binario", "Transgénero"], True, "Solo cliente nuevo."),
    ("Grupo étnico", RADICACION, APP["Grupo étnico del solicitante"], True, "Solo cliente nuevo."),
    ("Estado civil", RADICACION, ["Soltero(a)", "Casado(a)", "Separado(a)", "Unión Libre", "Viudo(a)",
                                  "Divorciado(a)"], True, "Solo cliente nuevo."),
    ("Cónyuge", RADICACION, "CONYUGE", True,
     "Solo con Casado(a) o Unión Libre. Número de identificación de un cónyuge de la hoja 'Datos conyuge'. "
     "Vacío = el primero de esa hoja."),
    ("Nivel de educación", RADICACION, ["Primaria", "Secundaria", "Profesional", "Técnico", "Sin estudios",
                                        "Tecnológico"], True, "Solo cliente nuevo."),
    ("Último año cursado", RADICACION, None, False, None),
    ("Profesión", RADICACION, APP["Profesión del solicitante"], True, "Solo cliente nuevo."),
    ("Ocupación", RADICACION, APP["Ocupación del solicitante"], True, "Solo cliente nuevo."),
    ("Afiliación", RADICACION, APP["Afiliación salud del solicitante"], True, None),
    ("Acceso a internet", RADICACION, APP["Acceso a internet del solicitante"], True, None),
    ("Correo electrónico", RADICACION, None, False, None),
    ("Zona de residencia", RADICACION, APP["Zona de residencia del solicitante"], True, "Solo cliente nuevo."),
    ("País de residencia", RADICACION, PAISES, True, "Solo cliente nuevo."),
    ("Departamento de residencia", RADICACION, DEPARTAMENTOS, True, "Solo cliente nuevo."),
    ("Ciudad de residencia", RADICACION, CIUDADES_VALLE, False, "Solo cliente nuevo."),
    ("Barrio de residencia", RADICACION, None, False, "Solo cliente nuevo. Se busca en la app."),
    ("Dirección de residencia", RADICACION, None, False, "Solo cliente nuevo."),
    ("Referencia de la residencia", RADICACION, None, False, "Solo cliente nuevo."),
    ("Estrato", RADICACION, APP["Estrato"], True, "Solo cliente nuevo."),
    ("Tipo de vivienda", RADICACION, APP["Tipo de vivienda"], True, "Solo cliente nuevo."),
    ("Nombre del negocio", RADICACION, None, False, "Solo cliente nuevo."),
    ("Ubicación del negocio", RADICACION, APP["Ubicación del negocio del solicitante"], True, "Solo cliente nuevo."),
    ("Departamento del negocio", RADICACION, DEPARTAMENTOS, True, "Debe corresponder al analista."),
    ("Ciudad del negocio", RADICACION, CIUDADES_VALLE, False, "Debe corresponder al analista."),
    ("Barrio del negocio", RADICACION, None, False, "Debe corresponder al analista. Se busca en la app."),
    ("Dirección del negocio", RADICACION, None, False, "Solo cliente nuevo."),
    ("Tipo de local", RADICACION, APP["Tipo de local del negocio"], True, None),
    ("Teléfono del negocio", RADICACION, None, False, None),
    ("Sector actividad económica", RADICACION, APP["Sector actividad económica principal"], True, None),
    ("Actividad económica", RADICACION, ("DEP", "Sector actividad económica"), False, None),
    ("Antigüedad actividad (meses)", RADICACION, None, False, None),
    ("Ventas brutas anuales", RADICACION, None, False, None),
    # --- Registro del producto ---
    ("Tipo de producto", PRODUCTO, ["Microcrédito", "Cartera comercial"], True,
     "Microcrédito o Cartera comercial (inmueble)."),
    ("Producto", PRODUCTO, ("DEP", "Tipo de producto"), True, None),
    ("Monto", PRODUCTO, None, False, None),
    ("Plazo", PRODUCTO, None, False, None),
    ("Frecuencia de pago", PRODUCTO, ["30", "7", "14"], True, None),
    ("Destino del crédito", PRODUCTO, ("DEP", "Producto"), True, None),
    ("Seguro familiar", PRODUCTO, SI_NO, True, "Solo microcrédito."),
    ("Seguro hogar y negocio", PRODUCTO, SI_NO, True, "Solo microcrédito."),
    ("Seguro microempresario", PRODUCTO, SI_NO, True, "Solo microcrédito."),
    ("Seguro de vida", PRODUCTO, SI_NO, True, "Solo microcrédito."),
    ("Plan exequial", PRODUCTO, SI_NO, True, "Solo microcrédito."),
    ("Ahorro", PRODUCTO, SI_NO, True, "Solo microcrédito."),
    ("Día de desembolso", PRODUCTO, None, False, "Inmueble: vacío = hoy + 15 días."),
    ("Mes de desembolso", PRODUCTO, None, False, None),
    ("Año de desembolso", PRODUCTO, None, False, None),
    ("Garantía FNG", PRODUCTO, "FNG", True, "No o el tipo tal como aparece en la app."),
    ("Fianza", PRODUCTO, "FIANZA", True, "Solo microcrédito."),
    ("Garantía real", PRODUCTO, ["Si", "No", "hipoteca"], True, "Microcrédito: Si/No. Inmueble: hipoteca."),
    ("Valor de la garantía", PRODUCTO, None, False, "Solo inmueble."),
    ("Seguro todo riesgo", PRODUCTO, SI_NO, True, "Solo inmueble."),
    ("Seguro deudores", PRODUCTO, SI_NO, True, "Solo inmueble. Con codeudor se responde Si (así la app muestra el botón Codeudor(es))."),
    ("Codeudor", PRODUCTO, "CODEUDOR", True,
     "Opcional. Número de identificación de un codeudor de la hoja 'Datos codeudor'. Vacío = sin codeudor."),
    ("Comisión Data Crédito", PRODUCTO, SI_NO, True, "Solo inmueble."),
    ("Comisión Cifin", PRODUCTO, SI_NO, True, "Solo inmueble."),
    ("Comisión MiPyme", PRODUCTO, SI_NO, True, "Solo inmueble."),
    # --- Decisión del análisis ---
    ("Lugar de la entrevista", DECISION, ["Negocio del Cliente"], False, "Lista parcial: falta capturar."),
    ("Resultado de la entrevista", DECISION, ["Satisfactorio"], False, "Lista parcial: falta capturar."),
    ("Propósito de la relación contractual", DECISION, ["Capital de trabajo"], False, "Lista parcial."),
    ("Recepción de extractos", DECISION, ["Correo electrónico P"], False, "Lista parcial."),
    ("Concepto del analista", DECISION, None, False, None),
    ("Decisión de análisis", DECISION, ["Continuar"], False, "Lista parcial."),
    # --- Control del caso ---
    ("Tipo de prueba", CONTROL, ["VALID", "BOUNDARY", "INVALID", "CONFIGURATION"], True, None),
    ("Resultado esperado", CONTROL, ["SUCCESS", "REJECTED", "CONTROLLED_ERROR", "TECHNICAL_ERROR"], True, None),
    ("Continuar flujo", CONTROL, ["SI", "NO"], True, "NO = la fila se salta."),
]
DEPENDENT = {
    "Tipo de producto": {"Microcrédito": MICRO_PRODUCTS,
                         "Cartera comercial": ["Inmueble Productivo", "Inmueble Productivo Remodelación",
                                               "Micropyme - Tasa Variable", "Micropyme - Garantia Real",
                                               "Micropyme - Tasa Fija"]},
    "Producto": {**{p: MICRO_DESTINOS for p in MICRO_PRODUCTS},
                 "Inmueble Productivo": ["Compra de Inmueble"],
                 # Micropyme (capturado de la app el 2026-11-23)
                 "Micropyme - Tasa Variable": MICROPYME_DESTINOS,
                 "Micropyme - Tasa Fija": MICROPYME_DESTINOS,
                 "Micropyme - Garantia Real": ["Capital de Trabajo", "Activo fijo", "Yo construyo Instalaciones Técnicas",
                                               "Yo construyo Estructura y Obra Negra", "Ola Invernal"],
                 "Inmueble Productivo Remodelación": ["Capital de Trabajo", "Activo fijo",
                                                      "Yo construyo Instalaciones Técnicas",
                                                      "Yo construyo Estructura y Obra Negra", "Ola Invernal"]},
    "Sector actividad económica": {k.split("|", 1)[1]: v for k, v in APP.items() if k.startswith("Actividad|")},
}
# Nombres de la plantilla anterior -> columna nueva
RENAMES = {"País": "País de origen de la identificación", "Nombres": "Primer nombre", "Apellidos": "Primer apellido",
           "Departamento": "Departamento del negocio", "Ciudad": "Ciudad del negocio", "Barrio": "Barrio del negocio",
           "Antigüedad actividad (meses)": "Antigüedad actividad (meses)"}
STAGE_FILL = {CONTROL: "4A5864", CONSULTA: "2F6F8F", RADICACION: "3E7D4F", PRODUCTO: "8A5A2B", DECISION: "6B4C8A"}


def read_rows(path):
    ws = openpyxl.load_workbook(path, data_only=True)["Hoja1"]
    headers = [c.value for c in ws[1]]
    rows = []
    for values in ws.iter_rows(min_row=2, values_only=True):
        if not any(v not in (None, "") for v in values):
            continue
        row = {}
        for h, v in zip(headers, values):
            if h is None:
                continue
            row[RENAMES.get(h, h)] = v
        row.setdefault("País de nacimiento", row.get("País de origen de la identificación"))
        rows.append(row)
    return rows


original, output = sys.argv[1], sys.argv[2]
# KATA_SOLO_EXTRA=1: la hoja queda solo con las filas de los archivos extra (sirve para
# reemplazar los casos sin perder hojas, listas ni rangos de la plantilla original).
rows = [] if os.environ.get("KATA_SOLO_EXTRA") == "1" else read_rows(original)
for extra in sys.argv[3:]:
    for row in read_rows(extra):
        # Filas del Excel de inmueble (no traían "Tipo de orden").
        row.setdefault("Tipo de orden", "Originación de inmueble")
        if not row.get("Tipo de orden"):
            row["Tipo de orden"] = "Originación de inmueble"
        if "inmueble" in str(row["Tipo de orden"]).lower():
            for key in ("Tipo de persona", "Día de desembolso", "Mes de desembolso", "Año de desembolso"):
                row[key] = None
            row.setdefault("Garantía FNG", "No")
            row.setdefault("Tipo de prueba", "VALID")
            row.setdefault("Resultado esperado", "SUCCESS")
            row["Continuar flujo"] = "SI" if str(row.get("Continuar flujo", "Si")).lower() in ("si", "sí") else "NO"
        rows.append(row)

wb = openpyxl.load_workbook(original)  # conserva Parametros, Listas y los rangos con nombre
old = wb["Hoja1"]
header_style = copy(old.cell(1, 1)._style)
body_style = copy(old.cell(2, 1)._style)
idx = wb.sheetnames.index("Hoja1")
wb.remove(old)
ws = wb.create_sheet("Hoja1", idx)
if "ListasApp" in wb.sheetnames:
    wb.remove(wb["ListasApp"])
lists = wb.create_sheet("ListasApp")

columns = [c[0] for c in SCHEMA]
for col, (name, stage, _, _, note) in enumerate(SCHEMA, start=1):
    cell = ws.cell(1, col, name)
    cell._style = copy(header_style)
    cell.fill = PatternFill("solid", fgColor=STAGE_FILL[stage])
    cell.font = Font(bold=True, color="FFFFFF")
    cell.alignment = Alignment(wrap_text=True, vertical="center")
    cell.comment = Comment(f"Pantalla: {stage}." + (f" {note}" if note else ""), "Robot Kata")
    ws.column_dimensions[L(col)].width = 22
ws.freeze_panes = "C2"
for r, row in enumerate(rows, start=2):
    for col, name in enumerate(columns, start=1):
        cell = ws.cell(r, col, row.get(name))
        cell._style = copy(body_style)

LAST = 500
font, fill = Font(bold=True, color="FFFFFF"), PatternFill("solid", fgColor="4A5864")
next_col = 1


def write_list(title, options):
    global next_col
    c = next_col
    lists.cell(1, c, title).font = font
    lists.cell(1, c).fill = fill
    for i, option in enumerate(options, start=2):
        lists.cell(i, c, option)
    lists.column_dimensions[L(c)].width = 30
    next_col += 1
    return c


for col, (name, stage, source, complete, _) in enumerate(SCHEMA, start=1):
    if source is None:
        continue
    target = f"{L(col)}2:{L(col)}{LAST}"
    style = "stop" if complete else "warning"
    if source == "CONYUGE":
        formula = "'Datos conyuge'!$F$2:$F$200"
    elif source == "CODEUDOR":
        formula = "'Datos codeudor'!$B$2:$B$200"
    elif source in ("FNG", "FIANZA"):
        formula = "ListaGarantiaFNG" if source == "FNG" else "ListaFianza"
    elif isinstance(source, tuple):
        parent = source[1]
        groups = DEPENDENT[parent]
        first = next_col
        for key, options in groups.items():
            write_list(key, options)
        last = next_col - 1
        next_col += 1
        p = L(columns.index(parent) + 1)
        a, b = L(first), L(last)
        match = f"MATCH(${p}2,ListasApp!${a}$1:${b}$1,0)-1"
        formula = (f"OFFSET(ListasApp!${a}$1,1,{match},"
                   f"MAX(1,COUNTA(OFFSET(ListasApp!${a}$2,0,{match},400,1))),1)")
    else:
        c = write_list(name, source)
        formula = f"ListasApp!${L(c)}$2:${L(c)}${len(source) + 1}"
    dv = DataValidation(type="list", formula1="=" + formula, allow_blank=True, showErrorMessage=True,
                        errorStyle=style, errorTitle=name,
                        error=("Elija una opción de la lista (tal como aparece en la app)." if complete
                               else "El valor no está en la lista capturada; verifique que exista en la app."))
    ws.add_data_validation(dv)
    dv.add(target)

SPOUSE_HEADERS = ["Primer apellido", "Segundo apellido", "Primer nombre", "Segundo nombre",
                  "Tipo de identificación", "Número de identificación", "País de origen de la identificación",
                  "Ocupación", "Celular"]
if "Datos conyuge" not in wb.sheetnames:
    sp = wb.create_sheet("Datos conyuge")
    sp.append(SPOUSE_HEADERS)
    # Personas que ya pasaron las validaciones de listas en QA.
    sp.append(["COLORADO", "VARGAS", "ESTEBAN", None, "Cedula ciudadanía", 1116070669, "COLOMBIA", "EMPLEADO", 3185551310])
    sp.append(["MOSQUERA", None, "LUIS", None, "Cedula ciudadanía", 94512378, "COLOMBIA", "INDEPENDIENTE INFORMAL", 3104455662])
    for c, _ in enumerate(SPOUSE_HEADERS, start=1):
        cell = sp.cell(1, c)
        cell.font = Font(bold=True, color="FFFFFF")
        cell.fill = PatternFill("solid", fgColor=STAGE_FILL[RADICACION])
        sp.column_dimensions[L(c)].width = 24
    sp.cell(1, 6).comment = Comment("Use cédulas de personas SIN listas ni mora: la app valida al cónyuge "
                                    "contra listas restrictivas en la Decisión del análisis.", "Robot Kata")
    for col, opts in ((5, APP["Tipo de identificación"]), (7, PAISES), (8, APP["Ocupación del cónyuge"])):
        c = write_list("Cónyuge: " + SPOUSE_HEADERS[col - 1], opts)
        dv = DataValidation(type="list", formula1=f"=ListasApp!${L(c)}$2:${L(c)}${len(opts) + 1}", allow_blank=True)
        sp.add_data_validation(dv)
        dv.add(f"{L(col)}2:{L(col)}200")

# --- Codeudores: hoja aparte, cada solicitud elige uno en la columna "Codeudor" ---
# (encabezado, valor de ejemplo, lista). Las columnas vacías toman el valor por defecto del robot.
GENEROS = ["Masculino", "Femenino", "No binario", "Transgénero"]
ESTADOS_CIVILES = ["Soltero(a)", "Casado(a)", "Separado(a)", "Unión Libre", "Viudo(a)", "Divorciado(a)"]
EDUCACION = ["Primaria", "Secundaria", "Profesional", "Técnico", "Sin estudios", "Tecnológico"]
CODEBTOR_COLUMNS = [
    ("Tipo de identificación", "Cedula ciudadanía", APP["Tipo de identificación"]),
    ("Número de identificación", 94512378, None),
    ("País de origen de la identificación", "COLOMBIA", PAISES),
    ("Primer apellido", "MOSQUERA", None),
    ("Segundo apellido", "RIVAS", None),
    ("Primer nombre", "LUIS", None),
    ("Segundo nombre", None, None),
    ("Día de nacimiento", 15, None),
    ("Mes de nacimiento", 11, None),
    ("Año de nacimiento", 1985, None),
    ("País de nacimiento", "COLOMBIA", PAISES),
    ("Celular", 3157778899, None),
    ("Departamento de nacimiento", "VALLE DEL CAUCA", DEPARTAMENTOS),
    ("Ciudad de nacimiento", "CALI", None),
    ("Día de expedición", 1, None),
    ("Mes de expedición", 6, None),
    ("Año de expedición", 2005, None),
    ("Departamento de expedición", "VALLE DEL CAUCA", DEPARTAMENTOS),
    ("Ciudad de expedición", "CALI", None),
    ("Nacionalidad", "COLOMBIA", PAISES),
    ("Género", "Masculino", GENEROS),
    ("Grupo étnico", "Ningún grupo étnico", APP["Grupo étnico del solicitante"]),
    ("Estado civil", "Soltero(a)", ESTADOS_CIVILES),
    ("Nivel de educación", "Secundaria", EDUCACION),
    ("Último año cursado", 11, None),
    ("Profesión", "Administrador", APP["Profesión del solicitante"]),
    ("Ocupación", "MICROEMPRESARIO", APP["Ocupación del solicitante"]),
    ("Es cónyuge del cliente", "No", SI_NO),
    ("Número de hijos", 0, None),
    ("Personas a cargo", 0, None),
    ("Correo electrónico", "codeudor.prueba@gmail.com", None),
    ("Teléfono fijo", 3334455, None),
    ("Contacto alternativo", 3001112233, None),
    ("Zona de residencia", "Urbano", APP["Zona de residencia del solicitante"]),
    ("País de residencia", "COLOMBIA", PAISES),
    ("Departamento de residencia", "VALLE DEL CAUCA", DEPARTAMENTOS),
    ("Ciudad de residencia", "CALI", None),
    ("Barrio de residencia", "LA INDEPENDENCIA", None),
    ("Dirección de residencia", "CALLE 15 # 30 40", None),
    ("Referencia de la residencia", "CASA DE DOS PISOS", None),
    ("Estrato", "Estrato 2", APP["Estrato"]),
    ("Tipo de vivienda", "Familiar", APP["Tipo de vivienda"]),
    ("Nombre del negocio", "TIENDA DON LUIS", None),
    ("Ubicación del negocio", "Local Comercial",
     ["Ambulante", "Campo", "Intinerante", "Local Comercial", "Plaza de Mercado", "Residencia"]),
    ("País del negocio", "COLOMBIA", PAISES),
    ("Departamento del negocio", "VALLE DEL CAUCA", DEPARTAMENTOS),
    ("Ciudad del negocio", "CALI", None),
    ("Barrio del negocio", "LA INDEPENDENCIA", None),
    ("Dirección del negocio", "CARRERA 8 # 12 20", None),
    ("Referencia del negocio", "FRENTE AL PARQUE", None),
    ("Tipo de local", "Familiar", APP["Tipo de vivienda"]),
    ("Nombre del arrendador", "PEDRO PEREZ", None),
    ("Celular del arrendador", 3205556677, None),
    ("Canon de arrendamiento", 800000, None),
    ("Teléfono fijo del negocio", 4445566, None),
    ("Celular del negocio", 3168889900, None),
    ("Registro de cámara y comercio", "No", SI_NO),
    ("Día de constitución del negocio", 1, None),
    ("Mes de constitución del negocio", 3, None),
    ("Año de constitución del negocio", 2015, None),
    ("Sector actividad económica", "SERVICIOS", APP["Sector actividad económica principal"]),
    ("Actividad económica", "COMERCIALIZACIÓN DE ENERGÍA ELÉCTRICA", None),
    ("País de origen de los recursos", "Colombia", ["Colombia", "Otro"]),
    ("Total ventas", 40000000, None),
    ("Total compras", 12000000, None),
    ("Gastos del negocio", 1000000, None),
    ("Gastos financieros", 0, None),
    ("Total otros ingresos", 0, None),
    ("Otros ingresos fllujo de caja (según políticas)", 0, None),
    ("Ingresos deudor solidario", 0, None),
    ("Gastos familiares", 1500000, None),
    ("Total bienes del negocio", 80000000, None),
    ("Pasivos del negocio", 0, None),
    ("Cuentas W", 0, None),
    ("Total activos", 120000000, None),
    ("Pasivos totales", 0, None),
    ("Concepto del analista", "Codeudor con capacidad de pago y buenas referencias", None),
    ("Decisión sobre el codeudor", "Aprobar codeudor", ["Aprobar codeudor", "Rechazar codeudor"]),
    ("Lugar de entrevista", "Negocio del Cliente", ["Negocio del Cliente", "Oficina Banco", "Otro"]),
    ("Resultado de la entrevista", "Satisfactorio", ["Satisfactorio", "No Satisfactorio"]),
    ("Propósito de la relación contractual", "Capital de trabajo",
     ["Capital de trabajo", "Compra de inventario/mercancía", "Compra de materia prima",
      "Compra de maquinaria y equipos", "Compra de herramientas", "Adecuación o mejora del local"]),
]
if "Datos codeudor" not in wb.sheetnames:
    cd = wb.create_sheet("Datos codeudor")
    cd.append([c[0] for c in CODEBTOR_COLUMNS])
    cd.append([c[1] for c in CODEBTOR_COLUMNS])
    for c, _ in enumerate(CODEBTOR_COLUMNS, start=1):
        cell = cd.cell(1, c)
        cell.font = Font(bold=True, color="FFFFFF")
        cell.fill = PatternFill("solid", fgColor=STAGE_FILL[PRODUCTO])
        cell.alignment = Alignment(wrap_text=True, vertical="center")
        cd.column_dimensions[L(c)].width = 22
    cd.freeze_panes = "C2"
    cd.cell(1, 2).comment = Comment("Use cédulas de personas SIN listas ni mora: la app consulta al codeudor en "
                                    "centrales y listas. El celular debe ser distinto al celular del negocio del "
                                    "cliente.", "Robot Kata")
else:
    cd = wb["Datos codeudor"]
    cd.data_validations.dataValidation = []  # se regeneran abajo, sin duplicarlas
for col, (header, _, opts) in enumerate(CODEBTOR_COLUMNS, start=1):
    if not opts:
        continue
    c = write_list("Codeudor: " + header, opts)
    dv = DataValidation(type="list", formula1=f"=ListasApp!${L(c)}$2:${L(c)}${len(opts) + 1}", allow_blank=True)
    cd.add_data_validation(dv)
    dv.add(f"{L(col)}2:{L(col)}200")

wb.save(output)
print(f"Plantilla: {len(columns)} columnas, {len(rows)} filas -> {output}")
for row in rows:
    print(" -", row.get("ID caso"), "|", row.get("Tipo de orden"), "|", row.get("Número de identificación"),
          "|", row.get("Producto"))
