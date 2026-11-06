package reservas.presentation.calendarizacion;

import com.github.lgooddatepicker.components.DatePicker;
import reservas.logic.CategoriaRecurso;
import reservas.presentation.Iconos;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class View {
    private final JPanel panel = new JPanel(new BorderLayout(10, 10));
    private final DatePicker fechaPicker = new DatePicker();
    private final JComboBox<CategoriaRecurso> comboCategoria = new JComboBox<>();
    private final JButton btnCargar = new JButton("Cargar", Iconos.get("search.png"));
    private final JButton btnImprimir = new JButton("Imprimir", Iconos.get("pdf.png"));
    private final JTable tabla = new JTable();
    private Controller controller;
    private CalendarizacionTableModel tableModel;
    private JSpinner spinner1;
    private JComboBox comboBox1;
    private JButton button1;
    private JButton button2;
    private JTable table1;

    public View() {
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        fechaPicker.setDate(LocalDate.now());

        JPanel panelFiltros = new JPanel();
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros"));
        panelFiltros.add(new JLabel("Fecha:"));
        panelFiltros.add(fechaPicker);
        panelFiltros.add(new JLabel("Categoria:"));
        panelFiltros.add(comboCategoria);
        panelFiltros.add(btnCargar);
        panelFiltros.add(btnImprimir);

        panel.add(panelFiltros, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnCargar.addActionListener(e -> {
            try {
                controller.cargar(fechaPicker.getDate(), (CategoriaRecurso) comboCategoria.getSelectedItem());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnImprimir.addActionListener(e -> {
            try {
                controller.imprimir();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public JPanel getPanel() { return panel; }

    public void setController(Controller controller) { this.controller = controller; }

    public void setModel(Model model) {
        this.tableModel = new CalendarizacionTableModel(model);
        tabla.setModel(tableModel);
    }

    public void poblarCategorias(List<CategoriaRecurso> categorias) {
        comboCategoria.removeAllItems();
        for (CategoriaRecurso c : categorias) comboCategoria.addItem(c);
    }

    public void refrescarTabla() { tableModel.refrescar(); }
}
