package reservas.presentation.actividades;

import reservas.logic.Reserva;
import reservas.logic.Service;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.time.LocalTime;

public class ActividadesTableModel extends AbstractTableModel {
    private final Model model;

    public ActividadesTableModel(Model model) { this.model = model; }

    @Override
    public int getRowCount() { return Model.HORA_FIN - Model.HORA_INICIO + 1; }

    @Override
    public int getColumnCount() { return 8; }

    @Override
    public String getColumnName(int column) {
        if (column == 0) return "Hora";
        LocalDate lunes = Service.lunesDe(model.getFechaReferencia());
        return lunes.plusDays(column - 1).toString();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        int hora = Model.HORA_INICIO + rowIndex;
        if (columnIndex == 0) return String.format("%02d:00", hora);

        LocalDate lunes = Service.lunesDe(model.getFechaReferencia());
        LocalDate dia = lunes.plusDays(columnIndex - 1);
        LocalTime inicioHora = LocalTime.of(hora, 0);
        LocalTime finHora = LocalTime.of(hora, 59);

        StringBuilder sb = new StringBuilder();
        for (Reserva r : model.getReservasSemana()) {
            if (!r.getFecha().equals(dia)) continue;
            boolean seSolapan = inicioHora.isBefore(r.getHoraFin()) && finHora.isAfter(r.getHoraInicio());
            if (!seSolapan) continue;
            if (sb.length() > 0) sb.append(" | ");
            String nombreFuncionario = r.getFuncionario() != null ? r.getFuncionario().getNombre() : "";
            sb.append(r.getActividad()).append(" (").append(nombreFuncionario).append(")");
        }
        return sb.toString();
    }

    public void refrescar() { fireTableStructureChanged(); }
}
