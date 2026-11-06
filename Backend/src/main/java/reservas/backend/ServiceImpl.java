package reservas.backend;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import reservas.ai.ReservaExtraccion;
import reservas.ai.ReservaExtractorService;
import reservas.backend.data.CategoriaRecursoDao;
import reservas.backend.data.RecursoDao;
import reservas.backend.data.ReservaDao;
import reservas.backend.data.UsuarioDao;
import reservas.logic.Administrador;
import reservas.logic.CategoriaRecurso;
import reservas.logic.EstadoReserva;
import reservas.logic.Funcionario;
import reservas.logic.IService;
import reservas.logic.Recurso;
import reservas.logic.Reserva;
import reservas.logic.ResultadoReserva;
import reservas.logic.Rol;
import reservas.logic.Usuario;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServiceImpl implements IService {

    private static ServiceImpl theInstance;

    public static synchronized ServiceImpl instance() {
        if (theInstance == null) theInstance = new ServiceImpl();
        return theInstance;
    }

    private final UsuarioDao usuarioDao = new UsuarioDao();
    private final CategoriaRecursoDao categoriaDao = new CategoriaRecursoDao();
    private final RecursoDao recursoDao = new RecursoDao();
    private final ReservaDao reservaDao = new ReservaDao();

    private ServiceImpl() {
        try {
            if (!usuarioDao.hayUsuarios()) {
                usuarioDao.insertarSiNoExiste(new Administrador("admin", "admin"));
            }
        } catch (Exception e) {
            System.err.println("No se pudo verificar el usuario semilla: " + e.getMessage());
        }
    }

    @Override
    public Usuario login(String id, String clave) throws Exception {
        Usuario u = usuarioDao.findById(id);
        if (u == null || !u.getClave().equals(clave)) {
            throw new Exception("Usuario o clave incorrectos");
        }
        return u;
    }

    @Override
    public void cambiarClave(String id, String claveActual, String claveNueva) throws Exception {
        Usuario u = usuarioDao.findById(id);
        if (u == null) throw new Exception("Usuario o clave incorrectos");
        if (!u.getClave().equals(claveActual)) {
            throw new Exception("La clave actual no es correcta");
        }
        if (claveNueva == null || claveNueva.isBlank()) {
            throw new Exception("La clave nueva no puede estar vacia");
        }
        usuarioDao.actualizarClave(id, claveNueva);
    }

    @Override
    public void create(Funcionario e) throws Exception {
        if (e.getId() == null || e.getId().isBlank()) throw new Exception("El id es obligatorio");
        if (e.getNombre() == null || e.getNombre().isBlank()) throw new Exception("El nombre es obligatorio");
        if (e.getTelefono() == null || e.getTelefono().isBlank()) throw new Exception("El telefono es obligatorio");
        if (usuarioDao.existe(e.getId())) throw new Exception("Ya existe un usuario con ese id");
        e.setClave(e.getId()); // la clave inicial queda igual al id (el funcionario la cambia despues)
        e.setRol(Rol.FUNCIONARIO);
        usuarioDao.insertarFuncionario(e);
    }

    @Override
    public void update(Funcionario e) throws Exception {
        Usuario actual = usuarioDao.findById(e.getId());
        if (!(actual instanceof Funcionario)) throw new Exception("El funcionario no existe");
        if (e.getNombre() == null || e.getNombre().isBlank()) throw new Exception("El nombre es obligatorio");
        if (e.getTelefono() == null || e.getTelefono().isBlank()) throw new Exception("El telefono es obligatorio");
        usuarioDao.actualizarDatosFuncionario(e);
    }

    @Override
    public void delete(Funcionario e) throws Exception {
        boolean tieneReservas = reservaDao.findByFuncionario(e.getId()).stream()
                .anyMatch(r -> r.getEstado() == EstadoReserva.ACTIVA);
        if (tieneReservas) throw new Exception("No se puede borrar: tiene reservas activas");
        usuarioDao.eliminar(e.getId());
    }

    @Override
    public List<Funcionario> findAllFuncionarios() {
        try {
            return usuarioDao.findAllFuncionarios();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<Funcionario> search(Funcionario e) {
        return findAllFuncionarios().stream()
                .filter(f -> f.getId().toLowerCase().contains(e.getId().toLowerCase()))
                .filter(f -> f.getNombre().toLowerCase().contains(e.getNombre().toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public void create(CategoriaRecurso e) throws Exception {
        if (e.getDescripcion() == null || e.getDescripcion().isBlank()) throw new Exception("La descripcion es obligatoria");
        e.setId(categoriaDao.generarSiguienteId());
        categoriaDao.insertar(e);
    }

    @Override
    public void update(CategoriaRecurso e) throws Exception {
        CategoriaRecurso actual = categoriaDao.findById(e.getId());
        if (actual == null) throw new Exception("La categoria no existe");
        if (e.getDescripcion() == null || e.getDescripcion().isBlank()) throw new Exception("La descripcion es obligatoria");
        categoriaDao.actualizar(e);
    }

    @Override
    public void delete(CategoriaRecurso e) throws Exception {
        boolean enUso = recursoDao.contarPorCategoria(e.getId()) > 0;
        if (enUso) throw new Exception("No se puede borrar: hay recursos de esa categoria");
        categoriaDao.eliminar(e.getId());
    }

    @Override
    public List<CategoriaRecurso> findAllCategorias() {
        try {
            return categoriaDao.findAll();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<CategoriaRecurso> search(CategoriaRecurso e) {
        return findAllCategorias().stream()
                .filter(c -> c.getDescripcion().toLowerCase().contains(e.getDescripcion().toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public void create(Recurso e) throws Exception {
        if (e.getId() == null || e.getId().isBlank()) throw new Exception("El id/numero de activo es obligatorio");
        if (e.getCategoria() == null) throw new Exception("Debe seleccionar una categoria");
        if (e.getDescripcion() == null || e.getDescripcion().isBlank()) throw new Exception("La descripcion es obligatoria");
        if (recursoDao.findById(e.getId()) != null) throw new Exception("Ya existe un recurso con ese id");
        recursoDao.insertar(e);
    }

    @Override
    public void update(Recurso e) throws Exception {
        Recurso actual = recursoDao.findById(e.getId());
        if (actual == null) throw new Exception("El recurso no existe");
        if (e.getCategoria() == null) throw new Exception("Debe seleccionar una categoria");
        if (e.getDescripcion() == null || e.getDescripcion().isBlank()) throw new Exception("La descripcion es obligatoria");
        recursoDao.actualizar(e);
    }

    @Override
    public void delete(Recurso e) throws Exception {
        boolean enUso = reservaDao.findAll().stream()
                .filter(r -> r.getEstado() == EstadoReserva.ACTIVA)
                .anyMatch(r -> r.getRecursosAsignados().stream().anyMatch(rec -> rec.getId().equals(e.getId())));
        if (enUso) throw new Exception("No se puede borrar: esta asignado a una reserva activa");
        recursoDao.eliminar(e.getId());
    }

    @Override
    public List<Recurso> findAllRecursos() {
        try {
            return recursoDao.findAll();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<Recurso> search(Recurso e) {
        return findAllRecursos().stream()
                .filter(r -> e.getCategoria() == null || (r.getCategoria() != null && r.getCategoria().getId().equals(e.getCategoria().getId())))
                .filter(r -> r.getDescripcion().toLowerCase().contains(e.getDescripcion().toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Recurso> recursosDeCategoria(CategoriaRecurso categoria) {
        try {
            return recursoDao.findByCategoria(categoria.getId());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<Reserva> reservasDe(Funcionario f) {
        try {
            return reservaDao.findByFuncionario(f.getId());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public ResultadoReserva crearReserva(String actividad, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                                          Funcionario funcionario, List<CategoriaRecurso> categoriasSolicitadas) throws Exception {
        if (actividad == null || actividad.isBlank()) throw new Exception("La actividad es obligatoria");
        if (fecha == null) throw new Exception("La fecha es obligatoria");
        if (fecha.isBefore(LocalDate.now())) throw new Exception("La fecha no puede ser anterior a hoy");
        if (horaInicio == null || horaFin == null) throw new Exception("Debe indicar hora de inicio y fin");
        if (!horaInicio.isBefore(horaFin)) throw new Exception("La hora de inicio debe ser anterior a la hora de fin");
        if (categoriasSolicitadas == null || categoriasSolicitadas.isEmpty()) throw new Exception("Debe seleccionar al menos una categoria de recurso");

        List<Reserva> reservasActivas = reservaDao.findAll().stream()
                .filter(r -> r.getEstado() == EstadoReserva.ACTIVA)
                .collect(Collectors.toList());

        ResultadoReserva resultado = new ResultadoReserva();
        List<Recurso> asignados = new ArrayList<>();

        for (CategoriaRecurso categoria : categoriasSolicitadas) {
            Recurso disponible = buscarRecursoDisponible(categoria, fecha, horaInicio, horaFin, asignados, reservasActivas);
            if (disponible == null) {
                resultado.getCategoriasNoDisponibles().add(categoria);
            } else {
                asignados.add(disponible);
            }
        }

        if (!resultado.getCategoriasNoDisponibles().isEmpty()) {
            resultado.setExito(false);
            return resultado;
        }

        Reserva reserva = new Reserva(reservaDao.generarSiguienteId(), actividad, fecha, horaInicio, horaFin, funcionario, EstadoReserva.ACTIVA);
        reserva.setCategoriasSolicitadas(new ArrayList<>(categoriasSolicitadas));
        reserva.setRecursosAsignados(asignados);
        reservaDao.insertar(reserva);

        resultado.setExito(true);
        resultado.setReserva(reserva);
        return resultado;
    }

    @Override
    public void cancelarReserva(String idReserva) throws Exception {
        Reserva r = reservaDao.findById(idReserva);
        if (r == null) throw new Exception("No existe la reserva");
        if (r.getFecha().isBefore(LocalDate.now())) throw new Exception("No se puede cancelar una reserva que ya paso");
        reservaDao.actualizarEstado(idReserva, EstadoReserva.CANCELADA);
    }

    private Recurso buscarRecursoDisponible(CategoriaRecurso categoria, LocalDate fecha, LocalTime ini, LocalTime fin,
                                             List<Recurso> yaAsignadosEnEstaReserva, List<Reserva> reservasActivas) throws Exception {
        for (Recurso r : recursosDeCategoria(categoria)) {
            if (yaAsignadosEnEstaReserva.contains(r)) continue;
            if (!estaOcupado(r, fecha, ini, fin, reservasActivas)) return r;
        }
        return null;
    }

    private boolean estaOcupado(Recurso recurso, LocalDate fecha, LocalTime ini, LocalTime fin, List<Reserva> reservasActivas) {
        for (Reserva r : reservasActivas) {
            if (!r.getFecha().equals(fecha)) continue;
            boolean seSolapan = ini.isBefore(r.getHoraFin()) && fin.isAfter(r.getHoraInicio());
            if (!seSolapan) continue;
            for (Recurso asignado : r.getRecursosAsignados()) {
                if (asignado.getId().equals(recurso.getId())) return true;
            }
        }
        return false;
    }

    @Override
    public Reserva reservaEnHora(Recurso recurso, LocalDate fecha, int hora) {
        LocalTime inicioHora = LocalTime.of(hora, 0);
        LocalTime finHora = LocalTime.of(hora, 59);
        for (Reserva r : findAllReservas()) {
            if (r.getEstado() != EstadoReserva.ACTIVA) continue;
            if (!r.getFecha().equals(fecha)) continue;
            boolean seSolapan = inicioHora.isBefore(r.getHoraFin()) && finHora.isAfter(r.getHoraInicio());
            if (!seSolapan) continue;
            for (Recurso asignado : r.getRecursosAsignados()) {
                if (asignado.getId().equals(recurso.getId())) return r;
            }
        }
        return null;
    }

    @Override
    public List<Reserva> actividadesDeLaSemana(LocalDate fechaReferencia) {
        LocalDate lunes = lunesDe(fechaReferencia);
        LocalDate domingo = lunes.plusDays(6);
        return findAllReservas().stream()
                .filter(r -> r.getEstado() == EstadoReserva.ACTIVA)
                .filter(r -> !r.getFecha().isBefore(lunes) && !r.getFecha().isAfter(domingo))
                .collect(Collectors.toList());
    }

    private List<Reserva> findAllReservas() {
        try {
            return reservaDao.findAll();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static LocalDate lunesDe(LocalDate fecha) {
        return fecha.with(DayOfWeek.MONDAY);
    }

    @Override
    public Map<String, Integer> estadisticasRecursos(LocalDate desde, LocalDate hasta) {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        for (Reserva r : findAllReservas()) {
            if (r.getEstado() != EstadoReserva.ACTIVA) continue;
            if (r.getFecha().isBefore(desde) || r.getFecha().isAfter(hasta)) continue;
            for (Recurso rec : r.getRecursosAsignados()) {
                String desc = rec.getCategoria() != null ? rec.getCategoria().getDescripcion() : "(sin categoria)";
                resultado.merge(desc, 1, Integer::sum);
            }
        }
        return resultado;
    }

    @Override
    public Map<String, Integer> estadisticasActividades(LocalDate desde, LocalDate hasta) {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        LocalDate semana = lunesDe(desde);
        LocalDate ultimaSemana = lunesDe(hasta);
        while (!semana.isAfter(ultimaSemana)) {
            resultado.put(semana.toString(), 0);
            semana = semana.plusWeeks(1);
        }
        for (Reserva r : findAllReservas()) {
            if (r.getEstado() != EstadoReserva.ACTIVA) continue;
            if (r.getFecha().isBefore(desde) || r.getFecha().isAfter(hasta)) continue;
            String claveSemana = lunesDe(r.getFecha()).toString();
            resultado.merge(claveSemana, 1, Integer::sum);
        }
        return resultado;
    }

    @Override
    public ReservaExtraccion extraerReserva(String frase) throws Exception {
        OpenAiChatModel aiModel = OpenAiChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1") // LangChain4j free proxy
                .apiKey("demo")                                    // Free demo key
                .modelName("gpt-4o-mini")                          // Restricted model
                .build();
        ReservaExtractorService aiService = AiServices.create(ReservaExtractorService.class, aiModel);
        String listaCategorias = formatearCategorias(findAllCategorias().stream()
                .map(CategoriaRecurso::getDescripcion).collect(Collectors.toList()));
        return aiService.extraer(frase, listaCategorias, LocalDate.now().toString());
    }

    private String formatearCategorias(List<String> categorias) {
        return categorias.stream().map(c -> "- " + c).collect(Collectors.joining("\n"));
    }
}
