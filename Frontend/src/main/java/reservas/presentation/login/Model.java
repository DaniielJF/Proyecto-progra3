package reservas.presentation.login;

import reservas.presentation.AbstractModel;

public class Model extends AbstractModel {
    private String id;
    private String clave;

    public static final String ID = "id";
    public static final String CLAVE = "clave";

    public String getId() { return id; }
    public void setId(String id) {
        this.id = id;
        firePropertyChange(ID);
    }

    public String getClave() { return clave; }
    public void setClave(String clave) {
        this.clave = clave;
        firePropertyChange(CLAVE);
    }
}
