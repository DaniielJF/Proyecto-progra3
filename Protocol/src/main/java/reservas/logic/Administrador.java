package reservas.logic;

public class Administrador extends Usuario {

    public Administrador(String id, String clave) {
        super(id, clave, Rol.ADMIN);
    }

    public Administrador() {
        this("", "");
    }
}
