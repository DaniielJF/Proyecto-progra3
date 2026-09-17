package reservas.presentation.cambiarclave;

import reservas.logic.Service;

public class Controller {
    private final View view;

    public Controller(View view) {
        this.view = view;
        view.setController(this);
    }

    public void aplicar(String id, String claveActual, String claveNueva, String claveNuevaConfirmar) throws Exception {
        if (id == null || id.isBlank()) throw new Exception("El id es obligatorio");
        if (claveActual == null || claveActual.isBlank()) throw new Exception("Debe indicar la clave actual");
        if (claveNueva == null || claveNueva.isBlank()) throw new Exception("Debe indicar la clave nueva");
        if (!claveNueva.equals(claveNuevaConfirmar)) throw new Exception("La confirmacion no coincide con la clave nueva");
        Service.instance().cambiarClave(id, claveActual, claveNueva);
        view.cerrar();
    }

    public void cancelar() {
        view.cerrar();
    }
}
