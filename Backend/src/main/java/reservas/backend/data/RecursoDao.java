package reservas.backend.data;

import reservas.logic.CategoriaRecurso;
import reservas.logic.Recurso;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class RecursoDao {

    private final Database db = Database.instance();
    private final CategoriaRecursoDao categoriaDao = new CategoriaRecursoDao();

    public List<Recurso> findAll() throws SQLException {
        Map<String, CategoriaRecurso> categorias = mapaCategorias();
        List<Recurso> resultado = db.query(
                "select id, categoria_id, descripcion from recursos",
                rs -> from(rs, categorias));
        resultado.sort(Comparator.comparing(Recurso::getId));
        return resultado;
    }

    public Recurso findById(String id) throws SQLException {
        Map<String, CategoriaRecurso> categorias = mapaCategorias();
        return db.queryOne("select id, categoria_id, descripcion from recursos where id = ?",
                rs -> from(rs, categorias), id);
    }

    public List<Recurso> findByCategoria(String categoriaId) throws SQLException {
        Map<String, CategoriaRecurso> categorias = mapaCategorias();
        List<Recurso> resultado = db.query(
                "select id, categoria_id, descripcion from recursos where categoria_id = ?",
                rs -> from(rs, categorias), categoriaId);
        resultado.sort(Comparator.comparing(Recurso::getId));
        return resultado;
    }

    public void insertar(Recurso r) throws SQLException {
        db.update("insert into recursos (id, categoria_id, descripcion) values (?, ?, ?)",
                r.getId(), r.getCategoria().getId(), r.getDescripcion());
    }

    public void actualizar(Recurso r) throws SQLException {
        db.update("update recursos set categoria_id = ?, descripcion = ? where id = ?",
                r.getCategoria().getId(), r.getDescripcion(), r.getId());
    }

    public void eliminar(String id) throws SQLException {
        db.update("delete from recursos where id = ?", id);
    }

    public int contarPorCategoria(String categoriaId) throws SQLException {
        Integer n = db.queryOne("select count(*) as n from recursos where categoria_id = ?",
                rs -> rs.getInt("n"), categoriaId);
        return n == null ? 0 : n;
    }

    private Map<String, CategoriaRecurso> mapaCategorias() throws SQLException {
        return categoriaDao.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(CategoriaRecurso::getId, c -> c));
    }

    private Recurso from(ResultSet rs, Map<String, CategoriaRecurso> categorias) throws SQLException {
        return new Recurso(rs.getString("id"), categorias.get(rs.getString("categoria_id")), rs.getString("descripcion"));
    }
}
