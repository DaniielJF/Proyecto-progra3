package reservas.backend.data;

import reservas.logic.CategoriaRecurso;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class CategoriaRecursoDao {

    private final Database db = Database.instance();

    public List<CategoriaRecurso> findAll() throws SQLException {
        List<CategoriaRecurso> resultado = db.query(
                "select id, descripcion from categorias", this::from);
        resultado.sort(Comparator.comparing(CategoriaRecurso::getId));
        return resultado;
    }

    public CategoriaRecurso findById(String id) throws SQLException {
        return db.queryOne("select id, descripcion from categorias where id = ?", this::from, id);
    }

    public void insertar(CategoriaRecurso c) throws SQLException {
        db.update("insert into categorias (id, descripcion) values (?, ?)", c.getId(), c.getDescripcion());
    }

    public void actualizar(CategoriaRecurso c) throws SQLException {
        db.update("update categorias set descripcion = ? where id = ?", c.getDescripcion(), c.getId());
    }

    public void eliminar(String id) throws SQLException {
        db.update("delete from categorias where id = ?", id);
    }

    public String generarSiguienteId() throws SQLException {
        Integer max = db.queryOne(
                "select max(cast(substring(id, 5) as unsigned)) as m from categorias where id like 'CAT-%'",
                rs -> rs.getInt("m"));
        int siguiente = (max == null ? 0 : max) + 1;
        return String.format("CAT-%06d", siguiente);
    }

    private CategoriaRecurso from(ResultSet rs) throws SQLException {
        return new CategoriaRecurso(rs.getString("id"), rs.getString("descripcion"));
    }
}
