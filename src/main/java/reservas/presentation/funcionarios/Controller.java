package reservas.presentation.funcionarios;

import reservas.logic.Funcionario;
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
        model.setList(Service.instance().findAllFuncionarios());
    }

    public void agregarFuncionario(Funcionario e) throws Exception {
        Service.instance().create(e);
        model.setList(Service.instance().findAllFuncionarios());
        model.setCurrent(new Funcionario());
    }

    public void modificarFuncionario(Funcionario e) throws Exception {
        Service.instance().update(e);
        model.setList(Service.instance().findAllFuncionarios());
        model.setCurrent(new Funcionario());
    }

    public void borrarFuncionario(Funcionario e) throws Exception {
        Service.instance().delete(e);
        model.setList(Service.instance().findAllFuncionarios());
        model.setCurrent(new Funcionario());
    }

    public void seleccionarFuncionario(int row) {
        Funcionario e = model.getList().get(row);
        model.setCurrent(e);
    }

    public void limpiar() {
        model.setCurrent(new Funcionario());
    }

    public void buscar(Funcionario filtro) {
        model.setList(Service.instance().search(filtro));
    }

    public void imprimir() {
        List<String[]> filas = new ArrayList<>();
        for (Funcionario f : model.getList()) {
            filas.add(new String[]{f.getId(), f.getNombre(), f.getTelefono()});
        }
        Pdf.imprimir("Listado de Funcionarios", new String[]{"Id", "Nombre", "Telefono"}, filas, "funcionarios.pdf");
    }
}
