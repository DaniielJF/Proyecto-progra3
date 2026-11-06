package reservas.backend.data;

import reservas.logic.CategoriaRecurso;
import reservas.logic.EstadoReserva;
import reservas.logic.Funcionario;
import reservas.logic.Recurso;
import reservas.logic.Reserva;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ReservaDao {

    private final Database db = Database.instance();
    private final UsuarioDao usuarioDao = new UsuarioDao();
    private final CategoriaRecursoDao categoriaDao = new CategoriaRecursoDao();
    private final RecursoDao recursoDao = new RecursoDao();

    public List<Reserva> findAll() throws SQLException {
        Map<String, CategoriaRecurso> categorias = mapaCategorias();
        Map<String, Recurso> recursos = mapaRecursos();
        List<Reserva> resultado = db.query(
                "select id, actividad, fecha, hora_inicio, hora_fin, funcionario_id, estado from reservas",
                rs -> from(rs, categorias, recursos));
        resultado.sort(Comparator.comparing(Reserva::getFecha).thenComparing(Reserva::getHoraInicio));
        return resultado;
    }

    public List<Reserva> findByFuncionario(String funcionarioId) throws SQLException {
        Map<String, CategoriaRecurso> categorias = mapaCategorias();
        Map<String, Recurso> recursos = mapaRecursos();
        List<Reserva> resultado = db.query(
                "select id, actividad, fecha, hora_inicio, hora_fin, funcionario_id, estado " +
                        "from reservas where funcionario_id = ?",
                rs -> from(rs, categorias, recursos), funcionarioId);
        resultado.sort(Comparator.comparing(Reserva::getFecha).thenComparing(Reserva::getHoraInicio));
        return resultado;
    }

    public Reserva findById(String id) throws SQLException {
        Map<String, CategoriaRecurso> categorias = mapaCategorias();
        Map<String, Recurso> recursos = mapaRecursos();
        return db.queryOne(
                "select id, actividad, fecha, hora_inicio, hora_fin, funcionario_id, estado from reservas where id = ?",
                rs -> from(rs, categorias, recursos), id);
    }

    public void insertar(Reserva r) throws SQLException {
        db.update("insert into reservas (id, actividad, fecha, hora_inicio, hora_fin, funcionario_id, estado) " +
                        "values (?, ?, ?, ?, ?, ?, ?)",
                r.getId(), r.getActividad(), java.sql.Date.valueOf(r.getFecha()),
                java.sql.Time.valueOf(r.getHoraInicio()), java.sql.Time.valueOf(r.getHoraFin()),
                r.getFuncionario().getId(), r.getEstado().name());

        for (CategoriaRecurso c : r.getCategoriasSolicitadas()) {
            db.update("insert into reserva_categoria (reserva_id, categoria_id) values (?, ?)", r.getId(), c.getId());
        }
        for (Recurso rec : r.getRecursosAsignados()) {
            db.update("insert into reserva_recurso (reserva_id, recurso_id) values (?, ?)", r.getId(), rec.getId());
        }
    }

    public void actualizarEstado(String id, EstadoReserva estado) throws SQLException {
        db.update("update reservas set estado = ? where id = ?", estado.name(), id);
    }

    public String generarSiguienteId() throws SQLException {
        Integer max = db.queryOne(
                "select max(cast(substring(id, 5) as unsigned)) as m from reservas where id like 'RES-%'",
                rs -> rs.getInt("m"));
        int siguiente = (max == null ? 0 : max) + 1;
        return String.format("RES-%06d", siguiente);
    }

    private List<CategoriaRecurso> categoriasDe(String reservaId, Map<String, CategoriaRecurso> categorias) throws SQLException {
        List<String> ids = db.query("select categoria_id from reserva_categoria where reserva_id = ?",
                rs -> rs.getString("categoria_id"), reservaId);
        List<CategoriaRecurso> resultado = new ArrayList<>();
        for (String id : ids) {
            CategoriaRecurso c = categorias.get(id);
            if (c != null) resultado.add(c);
        }
        return resultado;
    }

    private List<Recurso> recursosDe(String reservaId, Map<String, Recurso> recursos) throws SQLException {
        List<String> ids = db.query("select recurso_id from reserva_recurso where reserva_id = ?",
                rs -> rs.getString("recurso_id"), reservaId);
        List<Recurso> resultado = new ArrayList<>();
        for (String id : ids) {
            Recurso r = recursos.get(id);
            if (r != null) resultado.add(r);
        }
        return resultado;
    }

    private Map<String, CategoriaRecurso> mapaCategorias() throws SQLException {
        return categoriaDao.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(CategoriaRecurso::getId, c -> c));
    }

    private Map<String, Recurso> mapaRecursos() throws SQLException {
        return recursoDao.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(Recurso::getId, r -> r));
    }

    private Reserva from(ResultSet rs, Map<String, CategoriaRecurso> categorias, Map<String, Recurso> recursos) throws SQLException {
        String id = rs.getString("id");
        LocalDate fecha = rs.getDate("fecha").toLocalDate();
        LocalTime horaInicio = rs.getTime("hora_inicio").toLocalTime();
        LocalTime horaFin = rs.getTime("hora_fin").toLocalTime();
        EstadoReserva estado = EstadoReserva.valueOf(rs.getString("estado"));
        Funcionario funcionario = (Funcionario) usuarioDao.findById(rs.getString("funcionario_id"));

        Reserva r = new Reserva(id, rs.getString("actividad"), fecha, horaInicio, horaFin, funcionario, estado);
        r.setCategoriasSolicitadas(categoriasDe(id, categorias));
        r.setRecursosAsignados(recursosDe(id, recursos));
        return r;
    }
}
