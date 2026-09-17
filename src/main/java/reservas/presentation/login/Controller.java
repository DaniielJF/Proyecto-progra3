package reservas.presentation.login;

import reservas.logic.Service;
import reservas.logic.Usuario;
import reservas.presentation.Sesion;

public class Controller {
    private final View view;
    private final Model model;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        view.setController(this);
    }

    public void ingresar(String id, String clave) {
        try {
            model.setId(id);
            model.setClave(clave);
            Usuario u = Service.instance().login(id, clave);
            Sesion.setUsuario(u);
            view.cerrar();
        } catch (Exception e) {
            view.mostrarError(e.getMessage());
        }
    }

    public void cancelar() {
        Sesion.setUsuario(null);
        view.cerrar();
    }
}
