package reservas.logic;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import reservas.ai.ReservaExtraccion;
import reservas.ai.ReservaExtractorService;
import reservas.data.Data;
import reservas.data.XmlPersister;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class Service {
    private static Service theInstance;

    public static Service instance() {
        if (theInstance == null) theInstance = new Service();
        return theInstance;
    }

    private Data data;

    private Service() {
        try {
            data = XmlPersister.instance().load();
        } catch (Exception e) {
            data = new Data();
        }
        if (data.getUsuarios().isEmpty()) {
            // Usuario semilla para poder ingresar la primera vez (antes de que exista data.xml)
            data.getUsuarios().add(new Administrador("admin", "admin"));
        }
    }

    public void stop() {
        try {
            XmlPersister.instance().store(data);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public Usuario login(String id, String clave) throws Exception {
        Usuario u = data.getUsuarios().stream()
                .filter(us -> us.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (u == null || !u.getClave().equals(clave)) {
            throw new Exception("Usuario o clave incorrectos");
        }
        return u;
    }

    public void cambiarClave(Usuario usuario, String claveActual, String claveNueva) throws Exception {
        if (!usuario.getClave().equals(claveActual)) {
            throw new Exception("La clave actual no es correcta");
        }
        if (claveNueva == null || claveNueva.isBlank()) {
            throw new Exception("La clave nueva no puede estar vacia");
        }
        usuario.setClave(claveNueva);
    }

    public void cambiarClave(String id, String claveActual, String claveNueva) throws Exception {
        Usuario u = data.getUsuarios().stream()
                .filter(us -> us.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new Exception("Usuario o clave incorrectos"));
        cambiarClave(u, claveActual, claveNueva);
    }

    public void create(Funcionario e) throws Exception {
        if (e.getId() == null || e.getId().isBlank()) throw new Exception("El id es obligatorio");
        if (e.getNombre() == null || e.getNombre().isBlank()) throw new Exception("El nombre es obligatorio");
        if (e.getTelefono() == null || e.getTelefono().isBlank()) throw new Exception("El telefono es obligatorio");
        boolean existe = data.getUsuarios().stream().anyMatch(u -> u.getId().equals(e.getId()));
        if (existe) throw new Exception("Ya existe un usuario con ese id");
        e.setClave(e.getId()); // la clave inicial queda igual al id (el funcionario la cambia despues)
        e.setRol(Rol.FUNCIONARIO);
        data.getUsuarios().add(e);
    }

    public void update(Funcionario e) throws Exception {
        Funcionario actual = data.getUsuarios().stream()
                .filter(u -> u instanceof Funcionario && u.getId().equals(e.getId()))
                .map(u -> (Funcionario) u)
                .findFirst()
                .orElse(null);
        if (actual == null) throw new Exception("El funcionario no existe");
        if (e.getNombre() == null || e.getNombre().isBlank()) throw new Exception("El nombre es obligatorio");
        if (e.getTelefono() == null || e.getTelefono().isBlank()) throw new Exception("El telefono es obligatorio");
        actual.setNombre(e.getNombre());
        actual.setTelefono(e.getTelefono());
    }

    public void delete(Funcionario e) throws Exception {
        boolean tieneReservas = data.getReservas().stream()
                .anyMatch(r -> r.getFuncionario() != null && r.getFuncionario().getId().equals(e.getId())
                        && r.getEstado() == EstadoReserva.ACTIVA);
        if (tieneReservas) throw new Exception("No se puede borrar: tiene reservas activas");
        data.getUsuarios().removeIf(u -> u.getId().equals(e.getId()));
    }

    public List<Funcionario> findAllFuncionarios() {
        return data.getUsuarios().stream()
                .filter(u -> u instanceof Funcionario)
                .map(u -> (Funcionario) u)
                .sorted(Comparator.comparing(Funcionario::getId))
                .collect(Collectors.toList());
    }

    public List<Funcionario> search(Funcionario e) {
        return findAllFuncionarios().stream()
                .filter(f -> f.getId().toLowerCase().contains(e.getId().toLowerCase()))
                .filter(f -> f.getNombre().toLowerCase().contains(e.getNombre().toLowerCase()))
                .collect(Collectors.toList());
    }

    public void create(CategoriaRecurso e) throws Exception {
        if (e.getDescripcion() == null || e.getDescripcion().isBlank()) throw new Exception("La descripcion es obligatoria");
        e.setId(generarIdCategoria());
        data.getCategorias().add(e);
    }

    public void update(CategoriaRecurso e) throws Exception {
        CategoriaRecurso actual = data.getCategorias().stream()
                .filter(c -> c.getId().equals(e.getId()))
                .findFirst()
                .orElse(null);
        if (actual == null) throw new Exception("La categoria no existe");
        if (e.getDescripcion() == null || e.getDescripcion().isBlank()) throw new Exception("La descripcion es obligatoria");
        actual.setDescripcion(e.getDescripcion());
    }

    public void delete(CategoriaRecurso e) throws Exception {
        boolean enUso = data.getRecursos().stream()
                .anyMatch(r -> r.getCategoria() != null && r.getCategoria().getId().equals(e.getId()));
        if (enUso) throw new Exception("No se puede borrar: hay recursos de esa categoria");
        data.getCategorias().removeIf(c -> c.getId().equals(e.getId()));
    }

    public List<CategoriaRecurso> findAllCategorias() {
        return data.getCategorias().stream()
                .sorted(Comparator.comparing(CategoriaRecurso::getId))
                .collect(Collectors.toList());
    }

    public List<CategoriaRecurso> search(CategoriaRecurso e) {
        return findAllCategorias().stream()
                .filter(c -> c.getDescripcion().toLowerCase().contains(e.getDescripcion().toLowerCase()))
                .collect(Collectors.toList());
    }

    private String generarIdCategoria() {
        int max = 0;
        for (CategoriaRecurso c : data.getCategorias()) {
            try { max = Math.max(max, Integer.parseInt(c.getId().replace("CAT-", ""))); }
            catch (Exception ignored) { }
        }
        return String.format("CAT-%06d", max + 1);
    }

    public void create(Recurso e) throws Exception {
        if (e.getId() == null || e.getId().isBlank()) throw new Exception("El id/numero de activo es obligatorio");
        if (e.getCategoria() == null) throw new Exception("Debe seleccionar una categoria");
        if (e.getDescripcion() == null || e.getDescripcion().isBlank()) throw new Exception("La descripcion es obligatoria");
        boolean existe = data.getRecursos().stream().anyMatch(r -> r.getId().equals(e.getId()));
        if (existe) throw new Exception("Ya existe un recurso con ese id");
        data.getRecursos().add(e);
    }

    public void update(Recurso e) throws Exception {
        Recurso actual = data.getRecursos().stream()
                .filter(r -> r.getId().equals(e.getId()))
                .findFirst()
                .orElse(null);
        if (actual == null) throw new Exception("El recurso no existe");
        if (e.getCategoria() == null) throw new Exception("Debe seleccionar una categoria");
        if (e.getDescripcion() == null || e.getDescripcion().isBlank()) throw new Exception("La descripcion es obligatoria");
        actual.setCategoria(e.getCategoria());
        actual.setDescripcion(e.getDescripcion());
    }

    public void delete(Recurso e) throws Exception {
        boolean enUso = data.getReservas().stream()
                .filter(r -> r.getEstado() == EstadoReserva.ACTIVA)
                .anyMatch(r -> r.getRecursosAsignados().stream().anyMatch(rec -> rec.getId().equals(e.getId())));
        if (enUso) throw new Exception("No se puede borrar: esta asignado a una reserva activa");
        data.getRecursos().removeIf(r -> r.getId().equals(e.getId()));
    }

    public List<Recurso> findAllRecursos() {
        return data.getRecursos().stream()
                .sorted(Comparator.comparing(Recurso::getId))
                .collect(Collectors.toList());
    }

    public List<Recurso> search(Recurso e) {
        return findAllRecursos().stream()
                .filter(r -> e.getCategoria() == null || (r.getCategoria() != null && r.getCategoria().getId().equals(e.getCategoria().getId())))
                .filter(r -> r.getDescripcion().toLowerCase().contains(e.getDescripcion().toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Reserva> reservasDe(Funcionario f) {
        return data.getReservas().stream()
                .filter(r -> r.getFuncionario() != null && r.getFuncionario().getId().equals(f.getId()))
                .sorted(Comparator.comparing(Reserva::getFecha).thenComparing(Reserva::getHoraInicio))
                .collect(Collectors.toList());
    }

    public ResultadoReserva crearReserva(String actividad, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                                          Funcionario funcionario, List<CategoriaRecurso> categoriasSolicitadas) throws Exception {
        if (actividad == null || actividad.isBlank()) throw new Exception("La actividad es obligatoria");
        if (fecha == null) throw new Exception("La fecha es obligatoria");
        if (fecha.isBefore(LocalDate.now())) throw new Exception("La fecha no puede ser anterior a hoy");
        if (horaInicio == null || horaFin == null) throw new Exception("Debe indicar hora de inicio y fin");
        if (!horaInicio.isBefore(horaFin)) throw new Exception("La hora de inicio debe ser anterior a la hora de fin");
        if (categoriasSolicitadas == null || categoriasSolicitadas.isEmpty()) throw new Exception("Debe seleccionar al menos una categoria de recurso");

        ResultadoReserva resultado = new ResultadoReserva();
        List<Recurso> asignados = new ArrayList<>();

        for (CategoriaRecurso categoria : categoriasSolicitadas) {
            Recurso disponible = buscarRecursoDisponible(categoria, fecha, horaInicio, horaFin, asignados);
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

        Reserva reserva = new Reserva(generarIdReserva(), actividad, fecha, horaInicio, horaFin, funcionario, EstadoReserva.ACTIVA);
        reserva.setCategoriasSolicitadas(new ArrayList<>(categoriasSolicitadas));
        reserva.setRecursosAsignados(asignados);
        data.getReservas().add(reserva);

        resultado.setExito(true);
        resultado.setReserva(reserva);
        return resultado;
    }

    public void cancelarReserva(String idReserva) throws Exception {
        Reserva r = data.getReservas().stream()
                .filter(x -> x.getId().equals(idReserva))
                .findFirst()
                .orElseThrow(() -> new Exception("No existe la reserva"));
        if (r.getFecha().isBefore(LocalDate.now())) throw new Exception("No se puede cancelar una reserva que ya paso");
        r.setEstado(EstadoReserva.CANCELADA);
    }

    private Recurso buscarRecursoDisponible(CategoriaRecurso categoria, LocalDate fecha, LocalTime ini, LocalTime fin,
                                             List<Recurso> yaAsignadosEnEstaReserva) {
        for (Recurso r : recursosDeCategoria(categoria)) {
            if (yaAsignadosEnEstaReserva.contains(r)) continue;
            if (!estaOcupado(r, fecha, ini, fin)) return r;
        }
        return null;
    }

    private boolean estaOcupado(Recurso recurso, LocalDate fecha, LocalTime ini, LocalTime fin) {
        for (Reserva r : data.getReservas()) {
            if (r.getEstado() != EstadoReserva.ACTIVA) continue;
            if (!r.getFecha().equals(fecha)) continue;
            boolean seSolapan = ini.isBefore(r.getHoraFin()) && fin.isAfter(r.getHoraInicio());
            if (!seSolapan) continue;
            for (Recurso asignado : r.getRecursosAsignados()) {
                if (asignado.getId().equals(recurso.getId())) return true;
            }
        }
        return false;
    }

    private String generarIdReserva() {
        int max = 0;
        for (Reserva r : data.getReservas()) {
            try { max = Math.max(max, Integer.parseInt(r.getId().replace("RES-", ""))); }
            catch (Exception ignored) { }
        }
        return String.format("RES-%06d", max + 1);
    }

    public List<Recurso> recursosDeCategoria(CategoriaRecurso categoria) {
        return data.getRecursos().stream()
                .filter(r -> r.getCategoria() != null && r.getCategoria().getId().equals(categoria.getId()))
                .sorted(Comparator.comparing(Recurso::getId))
                .collect(Collectors.toList());
    }

    public Reserva reservaEnHora(Recurso recurso, LocalDate fecha, int hora) {
        LocalTime inicioHora = LocalTime.of(hora, 0);
        LocalTime finHora = LocalTime.of(hora, 59);
        for (Reserva r : data.getReservas()) {
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

    public List<Reserva> actividadesDeLaSemana(LocalDate fechaReferencia) {
        LocalDate lunes = lunesDe(fechaReferencia);
        LocalDate domingo = lunes.plusDays(6);
        return data.getReservas().stream()
                .filter(r -> r.getEstado() == EstadoReserva.ACTIVA)
                .filter(r -> !r.getFecha().isBefore(lunes) && !r.getFecha().isAfter(domingo))
                .collect(Collectors.toList());
    }

    public static LocalDate lunesDe(LocalDate fecha) {
        return fecha.with(DayOfWeek.MONDAY);
    }

    // ---------------------------------------------------------------
    // Estadisticas (administrador y funcionario)
    // ---------------------------------------------------------------

    /** Cantidad de recursos reservados por categoria en el periodo [desde, hasta]. */
    public Map<String, Integer> estadisticasRecursos(LocalDate desde, LocalDate hasta) {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        for (Reserva r : data.getReservas()) {
            if (r.getEstado() != EstadoReserva.ACTIVA) continue;
            if (r.getFecha().isBefore(desde) || r.getFecha().isAfter(hasta)) continue;
            for (Recurso rec : r.getRecursosAsignados()) {
                String desc = rec.getCategoria() != null ? rec.getCategoria().getDescripcion() : "(sin categoria)";
                resultado.merge(desc, 1, Integer::sum);
            }
        }
        return resultado;
    }

    public Map<String, Integer> estadisticasActividades(LocalDate desde, LocalDate hasta) {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        LocalDate semana = lunesDe(desde);
        LocalDate ultimaSemana = lunesDe(hasta);
        while (!semana.isAfter(ultimaSemana)) {
            resultado.put(semana.toString(), 0);
            semana = semana.plusWeeks(1);
        }
        for (Reserva r : data.getReservas()) {
            if (r.getEstado() != EstadoReserva.ACTIVA) continue;
            if (r.getFecha().isBefore(desde) || r.getFecha().isAfter(hasta)) continue;
            String claveSemana = lunesDe(r.getFecha()).toString();
            resultado.merge(claveSemana, 1, Integer::sum);
        }
        return resultado;
    }

    public ReservaExtraccion extraerReserva(String frase) throws Exception {
        OpenAiChatModel aiModel = OpenAiChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1") // LangChain4j free proxy
                .apiKey("demo")                                    // Free demo key
                .modelName("gpt-4o-mini")                          // Restricted model
                .build();
        ReservaExtractorService aiService = AiServices.create(ReservaExtractorService.class, aiModel);
        String listaCategorias = formatearCategorias(data.getCategorias().stream()
                .map(CategoriaRecurso::getDescripcion).collect(Collectors.toList()));
        return aiService.extraer(frase, listaCategorias, LocalDate.now().toString());
    }

    private String formatearCategorias(List<String> categorias) {
        return categorias.stream().map(c -> "- " + c).collect(Collectors.joining("\n"));
    }
}
