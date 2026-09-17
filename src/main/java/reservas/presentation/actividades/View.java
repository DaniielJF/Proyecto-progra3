package reservas.presentation.actividades;

import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;
import reservas.presentation.Iconos;

import java.awt.*;
import java.time.LocalDate;

public class View {
    private final JPanel panel = new JPanel(new BorderLayout(10, 10));
    private final DatePicker fechaPicker = new DatePicker();
    private final JButton btnCargar = new JButton("Cargar", Iconos.get("search.png"));
    private final JButton btnImprimir = new JButton("Imprimir", Iconos.get("pdf.png"));
    private final JTable tabla = new JTable();
    private Controller controller;
    private ActividadesTableModel tableModel;
    private JSpinner spinner1;
    private JButton button1;
    private JButton button2;
    private JTable table1;

    public View() {
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        fechaPicker.setDate(LocalDate.now());

        JPanel panelFiltros = new JPanel();
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Semana"));
        panelFiltros.add(new JLabel("Fecha de referencia:"));
        panelFiltros.add(fechaPicker);
        panelFiltros.add(btnCargar);
        panelFiltros.add(btnImprimir);

        panel.add(panelFiltros, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnCargar.addActionListener(e -> {
            try {
                controller.cargar(fechaPicker.getDate());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnImprimir.addActionListener(e -> controller.imprimir());
    }

    public JPanel getPanel() { return panel; }

    public void setController(Controller controller) { this.controller = controller; }

    public void setModel(Model model) {
        this.tableModel = new ActividadesTableModel(model);
        tabla.setModel(tableModel);
    }

    public void refrescarTabla() { tableModel.refrescar(); }
}
