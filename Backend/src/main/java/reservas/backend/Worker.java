package reservas.backend;

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

public class Worker extends Thread {

    private final Server srv;
    private final Socket s;
    private final ObjectOutputStream os;
    private final ObjectInputStream is;
    private final IService service;
    private final String sid; // Session Id

    private Socket as;              // Asynchronous Socket
    private ObjectOutputStream aos;
    private ObjectInputStream ais;

    private boolean continuar = true;

    public Worker(Server srv, Socket s, ObjectOutputStream os, ObjectInputStream is, String sid, IService service) {
        this.srv = srv;
        this.s = s;
        this.os = os;
        this.is = is;
        this.sid = sid;
        this.service = service;
    }

    public void setAs(Socket as, ObjectOutputStream aos, ObjectInputStream ais) {
        this.as = as;
        this.aos = aos;
        this.ais = ais;
    }

    public String getSid() {
        return sid;
    }

    @Override
    public void run() {
        listen();
    }

    private void listen() {
        int metodo;
        while (continuar) {
            try {
                metodo = is.readInt();
                atender(metodo);
            } catch (Exception ex) {
                continuar = false;
            }
        }
        cerrar();
    }

    @SuppressWarnings("unchecked")
    private void atender(int metodo) {
        try {
            switch (metodo) {
                case Protocolo.LOGIN: {
                    String id = (String) is.readObject();
                    String clave = (String) is.readObject();
                    responder(service.login(id, clave));
                    break;
                }
                case Protocolo.CAMBIAR_CLAVE: {
                    String id = (String) is.readObject();
                    String actual = (String) is.readObject();
                    String nueva = (String) is.readObject();
                    service.cambiarClave(id, actual, nueva);
                    responder(null);
                    break;
                }
                case Protocolo.FUNCIONARIO_CREATE: {
                    Funcionario f = (Funcionario) is.readObject();
                    service.create(f);
                    responder(f);
                    break;
                }
                case Protocolo.FUNCIONARIO_UPDATE: {
                    Funcionario f = (Funcionario) is.readObject();
                    service.update(f);
                    responder(null);
                    break;
                }
                case Protocolo.FUNCIONARIO_DELETE: {
                    Funcionario f = (Funcionario) is.readObject();
                    service.delete(f);
                    responder(null);
                    break;
                }
                case Protocolo.FUNCIONARIO_FIND_ALL:
                    responder(service.findAllFuncionarios());
                    break;
                case Protocolo.FUNCIONARIO_SEARCH: {
                    Funcionario f = (Funcionario) is.readObject();
                    responder(service.search(f));
                    break;
                }
                case Protocolo.CATEGORIA_CREATE: {
                    CategoriaRecurso c = (CategoriaRecurso) is.readObject();
                    service.create(c);
                    responder(c);
                    break;
                }
                case Protocolo.CATEGORIA_UPDATE: {
                    CategoriaRecurso c = (CategoriaRecurso) is.readObject();
                    service.update(c);
                    responder(null);
                    break;
                }
                case Protocolo.CATEGORIA_DELETE: {
                    CategoriaRecurso c = (CategoriaRecurso) is.readObject();
                    service.delete(c);
                    responder(null);
                    break;
                }
                case Protocolo.CATEGORIA_FIND_ALL:
                    responder(service.findAllCategorias());
                    break;
                case Protocolo.CATEGORIA_SEARCH: {
                    CategoriaRecurso c = (CategoriaRecurso) is.readObject();
                    responder(service.search(c));
                    break;
                }
                case Protocolo.RECURSO_CREATE: {
                    Recurso r = (Recurso) is.readObject();
                    service.create(r);
                    responder(r);
                    break;
                }
                case Protocolo.RECURSO_UPDATE: {
                    Recurso r = (Recurso) is.readObject();
                    service.update(r);
                    responder(null);
                    break;
                }
                case Protocolo.RECURSO_DELETE: {
                    Recurso r = (Recurso) is.readObject();
                    service.delete(r);
                    responder(null);
                    break;
                }
                case Protocolo.RECURSO_FIND_ALL:
                    responder(service.findAllRecursos());
                    break;
                case Protocolo.RECURSO_SEARCH: {
                    Recurso r = (Recurso) is.readObject();
                    responder(service.search(r));
                    break;
                }
                case Protocolo.RECURSO_DE_CATEGORIA: {
                    CategoriaRecurso c = (CategoriaRecurso) is.readObject();
                    responder(service.recursosDeCategoria(c));
                    break;
                }
                case Protocolo.RESERVAS_DE: {
                    Funcionario f = (Funcionario) is.readObject();
                    responder(service.reservasDe(f));
                    break;
                }
                case Protocolo.CREAR_RESERVA: {
                    String actividad = (String) is.readObject();
                    LocalDate fecha = (LocalDate) is.readObject();
                    LocalTime horaInicio = (LocalTime) is.readObject();
                    LocalTime horaFin = (LocalTime) is.readObject();
                    Funcionario funcionario = (Funcionario) is.readObject();
                    List<CategoriaRecurso> categorias = (List<CategoriaRecurso>) is.readObject();
                    ResultadoReserva resultado = service.crearReserva(actividad, fecha, horaInicio, horaFin, funcionario, categorias);
                    responder(resultado);
                    if (resultado.isExito()) {
                        srv.deliverMessage(this, resultado.getReserva());
                    }
                    break;
                }
                case Protocolo.CANCELAR_RESERVA: {
                    String id = (String) is.readObject();
                    service.cancelarReserva(id);
                    responder(null);
                    break;
                }
                case Protocolo.RESERVA_EN_HORA: {
                    Recurso r = (Recurso) is.readObject();
                    LocalDate fecha = (LocalDate) is.readObject();
                    int hora = (Integer) is.readObject();
                    responder(service.reservaEnHora(r, fecha, hora));
                    break;
                }
                case Protocolo.ACTIVIDADES_DE_LA_SEMANA: {
                    LocalDate fecha = (LocalDate) is.readObject();
                    responder(service.actividadesDeLaSemana(fecha));
                    break;
                }
                case Protocolo.ESTADISTICAS_RECURSOS: {
                    LocalDate desde = (LocalDate) is.readObject();
                    LocalDate hasta = (LocalDate) is.readObject();
                    Map<String, Integer> r = service.estadisticasRecursos(desde, hasta);
                    responder(r);
                    break;
                }
                case Protocolo.ESTADISTICAS_ACTIVIDADES: {
                    LocalDate desde = (LocalDate) is.readObject();
                    LocalDate hasta = (LocalDate) is.readObject();
                    Map<String, Integer> r = service.estadisticasActividades(desde, hasta);
                    responder(r);
                    break;
                }
                case Protocolo.EXTRAER_RESERVA: {
                    String frase = (String) is.readObject();
                    ReservaExtraccion r = service.extraerReserva(frase);
                    responder(r);
                    break;
                }
                default:
                    throw new Exception("Operacion desconocida: " + metodo);
            }
        } catch (Exception ex) {
            responderError(ex);
        }
    }

    private void responder(Object resultado) throws Exception {
        synchronized (os) {
            os.writeInt(Protocolo.ERROR_NO_ERROR);
            os.writeObject(resultado);
            os.flush();
            os.reset();
        }
    }

    private void responderError(Exception ex) {
        try {
            synchronized (os) {
                os.writeInt(Protocolo.ERROR_ERROR);
                os.writeObject(ex.getMessage() == null ? ex.toString() : ex.getMessage());
                os.flush();
                os.reset();
            }
        } catch (Exception ignored) { }
    }

    public void notificarReserva(Reserva reserva) {
        if (aos == null) return;
        try {
            synchronized (aos) {
                aos.writeInt(Protocolo.NOTIFICAR_RESERVA);
                aos.writeObject(reserva);
                aos.flush();
                aos.reset();
            }
        } catch (Exception ignored) { }
    }

    public void cerrar() {
        try { if (s != null) s.close(); } catch (Exception ignored) { }
        try { if (as != null) as.close(); } catch (Exception ignored) { }
        srv.removerWorker(this);
    }
}
