package reservas.presentation.calendarizacion;

import reservas.logic.Recurso;
import reservas.logic.Reserva;
import reservas.logic.Service;

import javax.swing.table.AbstractTableModel;

public class CalendarizacionTableModel extends AbstractTableModel {
    private final Model model;

    public CalendarizacionTableModel(Model model) { this.model = model; }

    @Override
    public int getRowCount() { return Model.HORA_FIN - Model.HORA_INICIO + 1; }

    @Override
    public int getColumnCount() { return 1 + model.getRecursosDeCategoria().size(); }

    @Override
    public String getColumnName(int column) {
        if (column == 0) return "Hora";
        return model.getRecursosDeCategoria().get(column - 1).toString();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        int hora = Model.HORA_INICIO + rowIndex;
        if (columnIndex == 0) return String.format("%02d:00", hora);

        Recurso recurso = model.getRecursosDeCategoria().get(columnIndex - 1);
        if (model.getFecha() == null) return "";
        Reserva r = Service.instance().reservaEnHora(recurso, model.getFecha(), hora);
        if (r == null) return "";
        String nombreFuncionario = r.getFuncionario() != null ? r.getFuncionario().getNombre() : "";
        return r.getActividad() + " - " + nombreFuncionario;
    }

    public void refrescar() { fireTableStructureChanged(); }
}
