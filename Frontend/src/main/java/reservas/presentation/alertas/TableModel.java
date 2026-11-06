package reservas.presentation.alertas;

import reservas.logic.Reserva;
import reservas.presentation.AbstractTableModel;

import java.util.List;

public class TableModel extends AbstractTableModel<Reserva> implements javax.swing.table.TableModel {

    public static final int ACTIVIDAD = 0;
    public static final int VER = 1;
    public static final int DESCARTAR = 2;

    public TableModel(int[] cols, List<Reserva> rows) {
        super(cols, rows);
    }

    @Override
    protected void initColNames() {
        colNames = new String[3];
        colNames[ACTIVIDAD] = "Actividad";
        colNames[VER] = "Ver";
        colNames[DESCARTAR] = "Desc.";
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        // Solo "Ver" y "Descartar" son "editables": es lo que le permite a Swing
        // invocar el TableCellEditor (el icono-boton) al hacer click sobre ellas.
        return cols[col] == VER || cols[col] == DESCARTAR;
    }

    @Override
    protected Object getPropetyAt(Reserva e, int col) {
        switch (cols[col]) {
            case ACTIVIDAD:
                String encargado = e.getFuncionario() != null ? e.getFuncionario().getNombre() : "?";
                return encargado + " - " + e.getActividad();
            case VER:
            case DESCARTAR:
                return e;
            default:
                return "";
        }
    }

    public void setRows(List<Reserva> rows) {
        this.rows = rows;
        fireTableDataChanged();
    }

    public void removerFila(int row) {
        rows.remove(row);
        fireTableDataChanged();
    }
}
