package reservas.presentation.alertas;

import reservas.logic.Recurso;
import reservas.logic.Reserva;
import reservas.presentation.Iconos;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * Area de Alertas: lista, de manera solo informativa, las reservas que otros
 * usuarios han hecho durante la sesion actual. Es independiente de las
 * pestañas (tabs) y siempre esta visible, sin importar la pestaña que el
 * usuario tenga seleccionada (ver Application, que la ubica en BorderLayout.EAST).
 */
public class View implements PropertyChangeListener {
    private static final DateTimeFormatter HH_MM = DateTimeFormatter.ofPattern("HH:mm");

    private final JPanel panel = new JPanel(new BorderLayout());
    private final JTable tabla = new JTable();
    private TableModel tableModel;

    Controller controller;
    Model model;

    public View() {
        panel.setBorder(BorderFactory.createTitledBorder("Alertas"));
        panel.setPreferredSize(new Dimension(260, 0));

        tabla.setRowHeight(26);
        tabla.setShowGrid(false);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    public JPanel getPanel() { return panel; }

    public void setController(Controller controller) { this.controller = controller; }

    public void setModel(Model model) {
        this.model = model;
        tableModel = new TableModel(new int[]{TableModel.ACTIVIDAD, TableModel.VER, TableModel.DESCARTAR}, model.getAlertas());
        tabla.setModel(tableModel);

        tabla.getColumnModel().getColumn(TableModel.VER).setMaxWidth(36);
        tabla.getColumnModel().getColumn(TableModel.VER).setCellRenderer(new BotonIconoRenderer(Iconos.get("detalles.png")));
        tabla.getColumnModel().getColumn(TableModel.VER).setCellEditor(new BotonIconoEditor(Iconos.get("detalles.png"), this::verAlerta));

        tabla.getColumnModel().getColumn(TableModel.DESCARTAR).setMaxWidth(36);
        tabla.getColumnModel().getColumn(TableModel.DESCARTAR).setCellRenderer(new BotonIconoRenderer(Iconos.get("descartar.png")));
        tabla.getColumnModel().getColumn(TableModel.DESCARTAR).setCellEditor(new BotonIconoEditor(Iconos.get("descartar.png"), this::descartarAlerta));

        model.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Model.ALERTAS.equals(evt.getPropertyName())) {
            tableModel.setRows(model.getAlertas());
        }
    }

    private void verAlerta(Reserva r) {
        String recursos = r.getRecursosAsignados().stream().map(Recurso::toString).collect(Collectors.joining(", "));
        JPanel contenido = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 4, 4, 4);
        gc.anchor = GridBagConstraints.WEST;

        String[][] filas = {
                {"Encargado:", r.getFuncionario() != null ? r.getFuncionario().getNombre() : ""},
                {"Fecha:", r.getFecha() != null ? r.getFecha().toString() : ""},
                {"Desde:", r.getHoraInicio() != null ? r.getHoraInicio().format(HH_MM) : ""},
                {"Hasta:", r.getHoraFin() != null ? r.getHoraFin().format(HH_MM) : ""},
                {"Recursos:", recursos},
        };
        for (int i = 0; i < filas.length; i++) {
            gc.gridx = 0; gc.gridy = i;
            contenido.add(new JLabel(filas[i][0]), gc);
            gc.gridx = 1;
            contenido.add(new JLabel(filas[i][1]), gc);
        }

        JOptionPane.showMessageDialog(panel, contenido, r.getActividad(), JOptionPane.PLAIN_MESSAGE);
    }

    private void descartarAlerta(Reserva r) {
        int op = JOptionPane.showConfirmDialog(panel,
                r.getActividad(), "Descartar?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (op == JOptionPane.YES_OPTION) {
            controller.descartar(r);
        }
    }

    @FunctionalInterface
    private interface AccionFila {
        void ejecutar(Reserva reserva);
    }

    /** Renderer que solo dibuja el icono (sin texto) en la celda, como un boton. */
    private static class BotonIconoRenderer extends JButton implements TableCellRenderer {
        BotonIconoRenderer(Icon icono) {
            setIcon(icono);
            setBorderPainted(false);
            setFocusPainted(false);
            setContentAreaFilled(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            return this;
        }
    }

    /** Editor que dispara la accion correspondiente (ver/descartar) al hacer click. */
    private class BotonIconoEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton boton = new JButton();
        private Reserva actual;

        BotonIconoEditor(Icon icono, AccionFila accion) {
            boton.setIcon(icono);
            boton.setBorderPainted(false);
            boton.setFocusPainted(false);
            boton.setContentAreaFilled(false);
            boton.addActionListener(e -> {
                fireEditingStopped();
                if (actual != null) accion.ejecutar(actual);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            actual = (Reserva) value;
            return boton;
        }

        @Override
        public Object getCellEditorValue() {
            return actual;
        }
    }
}
