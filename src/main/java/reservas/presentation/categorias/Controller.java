package reservas.presentation.categorias;

import reservas.logic.CategoriaRecurso;
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
        model.setList(Service.instance().findAllCategorias());
    }

    public void agregarCategoria(CategoriaRecurso e) throws Exception {
        Service.instance().create(e);
        model.setList(Service.instance().findAllCategorias());
        model.setCurrent(new CategoriaRecurso());
    }

    public void modificarCategoria(CategoriaRecurso e) throws Exception {
        Service.instance().update(e);
        model.setList(Service.instance().findAllCategorias());
        model.setCurrent(new CategoriaRecurso());
    }

    public void borrarCategoria(CategoriaRecurso e) throws Exception {
        Service.instance().delete(e);
        model.setList(Service.instance().findAllCategorias());
        model.setCurrent(new CategoriaRecurso());
    }

    public void seleccionarCategoria(int row) {
        model.setCurrent(model.getList().get(row));
    }

    public void limpiar() {
        model.setCurrent(new CategoriaRecurso());
    }

    public void buscar(CategoriaRecurso filtro) {
        model.setList(Service.instance().search(filtro));
    }

    public void imprimir() {
        List<String[]> filas = new ArrayList<>();
        for (CategoriaRecurso c : model.getList()) {
            filas.add(new String[]{c.getId(), c.getDescripcion()});
        }
        Pdf.imprimir("Listado de Categorias", new String[]{"Id", "Descripcion"}, filas, "categorias.pdf");
    }
}
