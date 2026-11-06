package reservas.logic;

import java.io.Serializable;

public abstract class Usuario implements Serializable {

    protected String id;
    protected String clave;
    protected Rol rol;

    public Usuario(String id, String clave, Rol rol) {
        this.id = id;
        this.clave = clave;
        this.rol = rol;
    }

    public Usuario() {
        this("", "", null);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
}
