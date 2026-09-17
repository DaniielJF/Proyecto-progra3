package reservas.presentation.estadisticas;

import reservas.logic.Service;
import reservas.presentation.Pdf;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Controller {
    private final View view;
    private final Model model;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        view.setController(this);
        view.setModel(model);
    }

    public void cargarRecursos(LocalDate desde, LocalDate hasta) throws Exception {
        if (desde == null || hasta == null || desde.isAfter(hasta)) throw new Exception("Fechas invalidas");
        model.setDesdeRecursos(desde);
        model.setHastaRecursos(hasta);
        model.setEstadisticasRecursos(Service.instance().estadisticasRecursos(desde, hasta));
        view.refrescarRecursos();
    }

    public void cargarActividades(LocalDate desde, LocalDate hasta) throws Exception {
        if (desde == null || hasta == null || desde.isAfter(hasta)) throw new Exception("Fechas invalidas");
        model.setDesdeActividades(desde);
        model.setHastaActividades(hasta);
        model.setEstadisticasActividades(Service.instance().estadisticasActividades(desde, hasta));
        view.refrescarActividades();
    }

    public void imprimirRecursos() {
        List<String[]> filas = new ArrayList<>();
        for (Map.Entry<String, Integer> e : model.getEstadisticasRecursos().entrySet()) {
            filas.add(new String[]{e.getKey(), String.valueOf(e.getValue())});
        }
        Pdf.imprimir("Estadisticas de Recursos Reservados", new String[]{"Categoria", "Cantidad"}, filas, "estadisticas_recursos.pdf");
    }

    public void imprimirActividades() {
        List<String[]> filas = new ArrayList<>();
        for (Map.Entry<String, Integer> e : model.getEstadisticasActividades().entrySet()) {
            filas.add(new String[]{e.getKey(), String.valueOf(e.getValue())});
        }
        Pdf.imprimir("Estadisticas de Actividades Calendarizadas", new String[]{"Semana (lunes)", "Cantidad"}, filas, "estadisticas_actividades.pdf");
    }
}
