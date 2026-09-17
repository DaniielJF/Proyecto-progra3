package reservas.presentation.recursos;

import reservas.logic.CategoriaRecurso;
import reservas.logic.Recurso;
import reservas.logic.Service;
import reservas.presentation.Pdf;

import java.util.ArrayList;
import java.util.List;

public class Controller {
    View view;
    Model model;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        view.setController(this);
        view.setModel(model);
        model.setCategorias(Service.instance().findAllCategorias());
        model.setList(Service.instance().search(new Recurso()));
    }

    public void agregarRecurso(Recurso e) throws Exception {
        Service.instance().create(e);
        model.setList(Service.instance().search(new Recurso()));
        model.setCurrent(new Recurso());
    }

    public void modificarRecurso(Recurso e) throws Exception {
        Service.instance().update(e);
        model.setList(Service.instance().search(new Recurso()));
        model.setCurrent(new Recurso());
    }

    public void borrarRecurso(Recurso e) throws Exception {
        Service.instance().delete(e);
        model.setList(Service.instance().search(new Recurso()));
        model.setCurrent(new Recurso());
    }

    public void seleccionarRecurso(int row) {
        model.setCurrent(model.getList().get(row));
    }

    public void limpiar() {
        model.setCurrent(new Recurso());
    }

    public void buscar(Recurso filtro) {
        model.setList(Service.instance().search(filtro));
    }

    public void imprimir() {
        List<String[]> filas = new ArrayList<>();
        for (Recurso r : model.getList()) {
            filas.add(new String[]{r.getId(), r.getCategoria() != null ? r.getCategoria().getDescripcion() : "", r.getDescripcion()});
        }
        Pdf.imprimir("Listado de Recursos", new String[]{"Id", "Categoria", "Descripcion"}, filas, "recursos.pdf");
    }
}
