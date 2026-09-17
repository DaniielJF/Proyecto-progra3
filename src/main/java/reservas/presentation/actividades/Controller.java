package reservas.presentation.actividades;

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
    }

    public void cargar(LocalDate fechaReferencia) throws Exception {
        if (fechaReferencia == null) throw new Exception("Debe indicar una fecha de referencia");
        model.setFechaReferencia(fechaReferencia);
        model.setReservasSemana(Service.instance().actividadesDeLaSemana(fechaReferencia));
        view.refrescarTabla();
    }

    public void imprimir() {
        List<String[]> filas = new ArrayList<>();
        var tableModel = new ActividadesTableModel(model);
        for (int r = 0; r < tableModel.getRowCount(); r++) {
            String[] fila = new String[8];
            for (int c = 0; c < 8; c++) fila[c] = String.valueOf(tableModel.getValueAt(r, c));
            filas.add(fila);
        }
        String[] encabezados = new String[8];
        for (int c = 0; c < 8; c++) encabezados[c] = tableModel.getColumnName(c);
        Pdf.imprimir("Programacion de Actividades - semana de " + Service.lunesDe(model.getFechaReferencia()),
                encabezados, filas, "actividades.pdf");
    }
}
