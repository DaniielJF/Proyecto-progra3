package reservas.backend.data;

import reservas.logic.Administrador;
import reservas.logic.Funcionario;
import reservas.logic.Rol;
import reservas.logic.Usuario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class UsuarioDao {

    private final Database db = Database.instance();

    public Usuario findById(String id) throws SQLException {
        String sql = "select id, clave, rol, nombre, telefono from usuarios where id = ?";
        return db.queryOne(sql, this::from, id);
    }

    public boolean existe(String id) throws SQLException {
        return findById(id) != null;
    }

    public List<Funcionario> findAllFuncionarios() throws SQLException {
        String sql = "select id, clave, rol, nombre, telefono from usuarios where rol = ?";
        List<Funcionario> resultado = db.query(sql, rs -> (Funcionario) from(rs), Rol.FUNCIONARIO.name());
        resultado.sort(Comparator.comparing(Funcionario::getId));
        return resultado;
    }

    public void insertarFuncionario(Funcionario f) throws SQLException {
        String sql = "insert into usuarios (id, clave, rol, nombre, telefono) values (?, ?, ?, ?, ?)";
        db.update(sql, f.getId(), f.getClave(), Rol.FUNCIONARIO.name(), f.getNombre(), f.getTelefono());
    }

    public void actualizarDatosFuncionario(Funcionario f) throws SQLException {
        String sql = "update usuarios set nombre = ?, telefono = ? where id = ?";
        db.update(sql, f.getNombre(), f.getTelefono(), f.getId());
    }

    public void actualizarClave(String id, String claveNueva) throws SQLException {
        String sql = "update usuarios set clave = ? where id = ?";
        db.update(sql, claveNueva, id);
    }

    public void eliminar(String id) throws SQLException {
        db.update("delete from usuarios where id = ?", id);
    }

    public void insertarSiNoExiste(Usuario u) throws SQLException {
        if (existe(u.getId())) return;
        String nombre = (u instanceof Funcionario) ? ((Funcionario) u).getNombre() : null;
        String telefono = (u instanceof Funcionario) ? ((Funcionario) u).getTelefono() : null;
        String sql = "insert into usuarios (id, clave, rol, nombre, telefono) values (?, ?, ?, ?, ?)";
        db.update(sql, u.getId(), u.getClave(), u.getRol().name(), nombre, telefono);
    }

    public boolean hayUsuarios() throws SQLException {
        Integer n = db.queryOne("select count(*) as n from usuarios", rs -> rs.getInt("n"));
        return n != null && n > 0;
    }

    private Usuario from(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String clave = rs.getString("clave");
        Rol rol = Rol.valueOf(rs.getString("rol"));
        if (rol == Rol.ADMIN) {
            return new Administrador(id, clave);
        }
        return new Funcionario(id, clave, rs.getString("nombre"), rs.getString("telefono"));
    }
}
