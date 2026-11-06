package reservas.comm;

/**
 * Libreria compartida entre Frontend y Backend: define host/puerto por defecto,
 * los dos tipos de conexion (sincronica/asincronica), el codigo de cada operacion
 * del servicio y los codigos de estatus de respuesta.
 *
 * Al inicio de cada peticion sincronica el Frontend escribe uno de los codigos de
 * OPERACION; el Worker responde escribiendo primero el ESTATUS (ERROR_NO_ERROR o
 * ERROR_ERROR) y luego el objeto correspondiente (resultado o el mensaje de error).
 *
 * Por el socket asincronico el Backend solo envia notificaciones "push"
 * (actualmente NOTIFICAR_RESERVA) que el Frontend atiende en el SocketListener.
 */
public class Protocolo {

    private Protocolo() { }

    public static final String SERVIDOR_POR_DEFECTO = "localhost";
    public static final int PUERTO_POR_DEFECTO = 5500;

    // --- Tipo de conexion (primer entero que se envia al conectar un socket) ---
    public static final int SYNC = 1;
    public static final int ASYNC = 2;

    // --- Estatus de respuesta (lo primero que escribe el Worker en cada respuesta) ---
    public static final int ERROR_NO_ERROR = 0;
    public static final int ERROR_ERROR = 1;

    // --- Notificaciones asincronicas (Backend -> Frontend) ---
    public static final int NOTIFICAR_RESERVA = 100;

    // --- Operaciones del servicio (Frontend -> Backend, via socket sincronico) ---
    public static final int LOGIN = 1;
    public static final int CAMBIAR_CLAVE = 2;

    public static final int FUNCIONARIO_CREATE = 10;
    public static final int FUNCIONARIO_UPDATE = 11;
    public static final int FUNCIONARIO_DELETE = 12;
    public static final int FUNCIONARIO_FIND_ALL = 13;
    public static final int FUNCIONARIO_SEARCH = 14;

    public static final int CATEGORIA_CREATE = 20;
    public static final int CATEGORIA_UPDATE = 21;
    public static final int CATEGORIA_DELETE = 22;
    public static final int CATEGORIA_FIND_ALL = 23;
    public static final int CATEGORIA_SEARCH = 24;

    public static final int RECURSO_CREATE = 30;
    public static final int RECURSO_UPDATE = 31;
    public static final int RECURSO_DELETE = 32;
    public static final int RECURSO_FIND_ALL = 33;
    public static final int RECURSO_SEARCH = 34;
    public static final int RECURSO_DE_CATEGORIA = 35;

    public static final int RESERVAS_DE = 40;
    public static final int CREAR_RESERVA = 41;
    public static final int CANCELAR_RESERVA = 42;
    public static final int RESERVA_EN_HORA = 43;
    public static final int ACTIVIDADES_DE_LA_SEMANA = 44;

    public static final int ESTADISTICAS_RECURSOS = 50;
    public static final int ESTADISTICAS_ACTIVIDADES = 51;

    public static final int EXTRAER_RESERVA = 60;
}
