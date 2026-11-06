package reservas.logic;

import reservas.ai.ReservaExtraccion;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Contrato del servicio de negocio (capas Logica+Datos). Tanto el Backend real
 * (reservas.backend.ServiceImpl, que habla con MySql) como el Proxy del Frontend
 * (reservas.presentation.comm.Service, que habla por sockets con el Backend)
 * implementan esta misma interfaz (patron Proxy).
 */
public interface IService {

    Usuario login(String id, String clave) throws Exception;

    void cambiarClave(String id, String claveActual, String claveNueva) throws Exception;

    void create(Funcionario e) throws Exception;
    void update(Funcionario e) throws Exception;
    void delete(Funcionario e) throws Exception;
    List<Funcionario> findAllFuncionarios();
    List<Funcionario> search(Funcionario e);

    void create(CategoriaRecurso e) throws Exception;
    void update(CategoriaRecurso e) throws Exception;
    void delete(CategoriaRecurso e) throws Exception;
    List<CategoriaRecurso> findAllCategorias();
    List<CategoriaRecurso> search(CategoriaRecurso e);

    void create(Recurso e) throws Exception;
    void update(Recurso e) throws Exception;
    void delete(Recurso e) throws Exception;
    List<Recurso> findAllRecursos();
    List<Recurso> search(Recurso e);
    List<Recurso> recursosDeCategoria(CategoriaRecurso categoria);

    List<Reserva> reservasDe(Funcionario f);
    ResultadoReserva crearReserva(String actividad, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                                   Funcionario funcionario, List<CategoriaRecurso> categoriasSolicitadas) throws Exception;
    void cancelarReserva(String idReserva) throws Exception;
    Reserva reservaEnHora(Recurso recurso, LocalDate fecha, int hora);
    List<Reserva> actividadesDeLaSemana(LocalDate fechaReferencia);

    Map<String, Integer> estadisticasRecursos(LocalDate desde, LocalDate hasta);
    Map<String, Integer> estadisticasActividades(LocalDate desde, LocalDate hasta);

    ReservaExtraccion extraerReserva(String frase) throws Exception;
}
