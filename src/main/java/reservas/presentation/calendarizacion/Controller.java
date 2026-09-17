package reservas.presentation.calendarizacion;

import reservas.logic.CategoriaRecurso;
import reservas.logic.Service;
import reservas.presentation.Pdf;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private final View view;
    private final Model model;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        view.setController(this);
        view.setModel(model);
        model.setCategoriasDisponibles(Service.instance().findAllCategorias());
        view.poblarCategorias(model.getCategoriasDisponibles());
    }

    public void cargar(LocalDate fecha, CategoriaRecurso categoria) throws Exception {
        if (fecha == null || categoria == null) throw new Exception("Debe indicar fecha y categoria");
        model.setFecha(fecha);
        model.setCategoria(categoria);
        model.setRecursosDeCategoria(Service.instance().recursosDeCategoria(categoria));
        view.refrescarTabla();
    }

    public void imprimir() throws Exception {
        if (model.getCategoria() == null) throw new Exception("Primero debe cargar una calendarizacion");
        List<String[]> filas = new ArrayList<>();
        for (int h = Model.HORA_INICIO; h <= Model.HORA_FIN; h++) {
            List<String> fila = new ArrayList<>();
            fila.add(String.format("%02d:00", h));
            for (var recurso : model.getRecursosDeCategoria()) {
                var r = Service.instance().reservaEnHora(recurso, model.getFecha(), h);
                fila.add(r == null ? "" : r.getActividad());
            }
            filas.add(fila.toArray(new String[0]));
        }
        String[] encabezados = new String[1 + model.getRecursosDeCategoria().size()];
        encabezados[0] = "Hora";
        for (int i = 0; i < model.getRecursosDeCategoria().size(); i++) {
            encabezados[i + 1] = model.getRecursosDeCategoria().get(i).toString();
        }
        Pdf.imprimir("Calendarizacion de Recursos - " + model.getFecha(), encabezados, filas, "calendarizacion.pdf");
    }
}
