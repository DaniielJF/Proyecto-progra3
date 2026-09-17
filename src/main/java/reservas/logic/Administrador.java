package reservas.logic;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Administrador extends Usuario {

    public Administrador(String id, String clave) {
        super(id, clave, Rol.ADMIN);
    }

    public Administrador() {
        this("", "");
    }
}
