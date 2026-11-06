package reservas.presentation.comm;

import reservas.ai.ReservaExtraccion;
import reservas.comm.Protocolo;
import reservas.logic.CategoriaRecurso;
import reservas.logic.Funcionario;
import reservas.logic.IService;
import reservas.logic.Recurso;
import reservas.logic.Reserva;
import reservas.logic.ResultadoReserva;
import reservas.logic.Usuario;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Frontend-Service (Proxy): representa localmente, para la capa de
 * Presentacion, al Service remoto que vive en el Backend. Implementa la misma
 * interface (IService) que la implementacion real (patron Proxy), y oculta
 * ante el resto del Frontend los detalles de sockets/protocolo: cada metodo
 * simplemente delega en invocar(...), que envia la operacion y sus argumentos
 * por el socket sincronico y espera la respuesta.
 *
 * El Frontend NO tiene ninguna referencia a la base de datos: todo lo que
 * necesita lo obtiene pidiendoselo al Backend a traves de esta clase.
 */
public class Service implements IService {

    private static Service theInstance;

    public static synchronized Service instance() {
        if (theInstance == null) theInstance = new Service();
        return theInstance;
    }

    private Socket s;
    private ObjectOutputStream os;
    private ObjectInputStream is;
    private String sid; // Session Id

    private Service() {
        try {
            s = new Socket(ConexionConfig.HOST, ConexionConfig.PUERTO);
            // Igual que en el Backend: primero el ObjectOutputStream y despues el
            // ObjectInputStream, en ambos extremos, para evitar el deadlock del
            // encabezado de ObjectInputStream.
            os = new ObjectOutputStream(s.getOutputStream());
            is = new ObjectInputStream(s.getInputStream());

            os.writeInt(Protocolo.SYNC);
            os.flush();
            sid = (String) is.readObject();
        } catch (Exception e) {
            throw new RuntimeException("No fue posible conectar con el Backend: " + e.getMessage(), e);
        }
    }

    public String getSid() {
        return sid;
    }

    /** Cierra la conexion sincronica con el Backend (se invoca al cerrar la ventana principal). */
    public void cerrar() {
        try { if (s != null) s.close(); } catch (Exception ignored) { }
    }

    /** Envia la operacion y sus argumentos, y espera sincronicamente la respuesta. */
    @SuppressWarnings("unchecked")
    private synchronized <T> T invocar(int operacion, Object... args) throws Exception {
        os.writeInt(operacion);
        for (Object arg : args) {
            os.writeObject(arg);
        }
        os.flush();
        os.reset();

        int estatus = is.readInt();
        Object resultado = is.readObject();
        if (estatus == Protocolo.ERROR_ERROR) {
            throw new Exception((String) resultado);
        }
        return (T) resultado;
    }

    /** Como invocar(...), pero para los metodos de solo-lectura de IService que no declaran "throws Exception". */
    private <T> T invocarRT(int operacion, Object... args) {
        try {
            return invocar(operacion, args);
        } catch (Exception e) {
            throw new RuntimeException("Error de comunicacion con el Backend: " + e.getMessage(), e);
        }
    }

    @Override
    public Usuario login(String id, String clave) throws Exception {
        return invocar(Protocolo.LOGIN, id, clave);
    }

    @Override
    public void cambiarClave(String id, String claveActual, String claveNueva) throws Exception {
        invocar(Protocolo.CAMBIAR_CLAVE, id, claveActual, claveNueva);
    }

    @Override
    public void create(Funcionario e) throws Exception {
        invocar(Protocolo.FUNCIONARIO_CREATE, e);
    }

    @Override
    public void update(Funcionario e) throws Exception {
        invocar(Protocolo.FUNCIONARIO_UPDATE, e);
    }

    @Override
    public void delete(Funcionario e) throws Exception {
        invocar(Protocolo.FUNCIONARIO_DELETE, e);
    }

    @Override
    public List<Funcionario> findAllFuncionarios() {
        return invocarRT(Protocolo.FUNCIONARIO_FIND_ALL);
    }

    @Override
    public List<Funcionario> search(Funcionario e) {
        return invocarRT(Protocolo.FUNCIONARIO_SEARCH, e);
    }

    @Override
    public void create(CategoriaRecurso e) throws Exception {
        CategoriaRecurso creada = invocar(Protocolo.CATEGORIA_CREATE, e);
        e.setId(creada.getId());
    }

    @Override
    public void update(CategoriaRecurso e) throws Exception {
        invocar(Protocolo.CATEGORIA_UPDATE, e);
    }

    @Override
    public void delete(CategoriaRecurso e) throws Exception {
        invocar(Protocolo.CATEGORIA_DELETE, e);
    }

    @Override
    public List<CategoriaRecurso> findAllCategorias() {
        return invocarRT(Protocolo.CATEGORIA_FIND_ALL);
    }

    @Override
    public List<CategoriaRecurso> search(CategoriaRecurso e) {
        return invocarRT(Protocolo.CATEGORIA_SEARCH, e);
    }

    @Override
    public void create(Recurso e) throws Exception {
        invocar(Protocolo.RECURSO_CREATE, e);
    }

    @Override
    public void update(Recurso e) throws Exception {
        invocar(Protocolo.RECURSO_UPDATE, e);
    }

    @Override
    public void delete(Recurso e) throws Exception {
        invocar(Protocolo.RECURSO_DELETE, e);
    }

    @Override
    public List<Recurso> findAllRecursos() {
        return invocarRT(Protocolo.RECURSO_FIND_ALL);
    }

    @Override
    public List<Recurso> search(Recurso e) {
        return invocarRT(Protocolo.RECURSO_SEARCH, e);
    }

    @Override
    public List<Recurso> recursosDeCategoria(CategoriaRecurso categoria) {
        return invocarRT(Protocolo.RECURSO_DE_CATEGORIA, categoria);
    }

    @Override
    public List<Reserva> reservasDe(Funcionario f) {
        return invocarRT(Protocolo.RESERVAS_DE, f);
    }

    @Override
    public ResultadoReserva crearReserva(String actividad, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                                          Funcionario funcionario, List<CategoriaRecurso> categoriasSolicitadas) throws Exception {
        return invocar(Protocolo.CREAR_RESERVA, actividad, fecha, horaInicio, horaFin, funcionario,
                new java.util.ArrayList<>(categoriasSolicitadas));
    }

    @Override
    public void cancelarReserva(String idReserva) throws Exception {
        invocar(Protocolo.CANCELAR_RESERVA, idReserva);
    }

    @Override
    public Reserva reservaEnHora(Recurso recurso, LocalDate fecha, int hora) {
        return invocarRT(Protocolo.RESERVA_EN_HORA, recurso, fecha, hora);
    }

    @Override
    public List<Reserva> actividadesDeLaSemana(LocalDate fechaReferencia) {
        return invocarRT(Protocolo.ACTIVIDADES_DE_LA_SEMANA, fechaReferencia);
    }

    @Override
    public Map<String, Integer> estadisticasRecursos(LocalDate desde, LocalDate hasta) {
        return invocarRT(Protocolo.ESTADISTICAS_RECURSOS, desde, hasta);
    }

    @Override
    public Map<String, Integer> estadisticasActividades(LocalDate desde, LocalDate hasta) {
        return invocarRT(Protocolo.ESTADISTICAS_ACTIVIDADES, desde, hasta);
    }

    @Override
    public ReservaExtraccion extraerReserva(String frase) throws Exception {
        return invocar(Protocolo.EXTRAER_RESERVA, frase);
    }
}
