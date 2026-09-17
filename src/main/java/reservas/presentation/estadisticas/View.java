package reservas.presentation.estadisticas;

import com.github.lgooddatepicker.components.DatePicker;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import reservas.presentation.Iconos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.Map;

public class View {
    private final JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));

    // --- Seccion Recursos ---
    private final DatePicker fechaDesdeRecursos = new DatePicker();
    private final DatePicker fechaHastaRecursos = new DatePicker();
    private final JButton btnCargarRecursos = new JButton("Cargar", Iconos.get("search.png"));
    private final JButton btnImprimirRecursos = new JButton("Imprimir", Iconos.get("pdf.png"));
    private final DefaultTableModel modeloTablaRecursos = new DefaultTableModel(new Object[]{"Categoria", "Cantidad"}, 0);
    private final JTable tablaRecursos = new JTable(modeloTablaRecursos);
    private final DefaultCategoryDataset datasetRecursos = new DefaultCategoryDataset();
    private final ChartPanel chartPanelRecursos;

    // --- Seccion Actividades ---
    private final DatePicker fechaDesdeActividades = new DatePicker();
    private final DatePicker fechaHastaActividades = new DatePicker();
    private final JButton btnCargarActividades = new JButton("Cargar", Iconos.get("search.png"));
    private final JButton btnImprimirActividades = new JButton("Imprimir", Iconos.get("pdf.png"));
    private final DefaultTableModel modeloTablaActividades = new DefaultTableModel(new Object[]{"Semana", "Cantidad"}, 0);
    private final JTable tablaActividades = new JTable(modeloTablaActividades);
    private final DefaultCategoryDataset datasetActividades = new DefaultCategoryDataset();
    private final ChartPanel chartPanelActividades;

    private Controller controller;
    private Model model;
    private JSpinner spinner1;
    private JSpinner spinner2;
    private JButton button1;
    private JTable table1;
    private JTable table2;
    private JSpinner spinner3;
    private JSpinner spinner4;
    private JButton button2;
    private JButton button3;

    public View() {
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        fechaDesdeRecursos.setDate(LocalDate.now().minusWeeks(2));
        fechaHastaRecursos.setDate(LocalDate.now().plusWeeks(2));
        fechaDesdeActividades.setDate(LocalDate.now().minusWeeks(2));
        fechaHastaActividades.setDate(LocalDate.now().plusWeeks(2));

        JFreeChart chartRecursos = ChartFactory.createBarChart(
                "Recursos Usados", "Categoria", "Cantidad", datasetRecursos, PlotOrientation.VERTICAL, false, true, false);
        chartPanelRecursos = new ChartPanel(chartRecursos);

        JFreeChart chartActividades = ChartFactory.createBarChart(
                "Actividades Realizadas", "Semana", "Cantidad", datasetActividades, PlotOrientation.VERTICAL, false, true, false);
        chartPanelActividades = new ChartPanel(chartActividades);

        panel.add(construirPanelRecursos());
        panel.add(construirPanelActividades());

        btnCargarRecursos.addActionListener(e -> {
            try {
                controller.cargarRecursos(fechaDesdeRecursos.getDate(), fechaHastaRecursos.getDate());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnImprimirRecursos.addActionListener(e -> controller.imprimirRecursos());
        btnCargarActividades.addActionListener(e -> {
            try {
                controller.cargarActividades(fechaDesdeActividades.getDate(), fechaHastaActividades.getDate());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnImprimirActividades.addActionListener(e -> controller.imprimirActividades());
    }

    private JPanel construirPanelRecursos() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(BorderFactory.createTitledBorder("Recursos"));

        JPanel panelFechas = new JPanel();
        panelFechas.add(new JLabel("Desde:"));
        panelFechas.add(fechaDesdeRecursos);
        panelFechas.add(new JLabel("Hasta:"));
        panelFechas.add(fechaHastaRecursos);
        panelFechas.add(btnCargarRecursos);
        panelFechas.add(btnImprimirRecursos);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tablaRecursos), chartPanelRecursos);
        split.setResizeWeight(0.35);

        p.add(panelFechas, BorderLayout.NORTH);
        p.add(split, BorderLayout.CENTER);
        return p;
    }

    private JPanel construirPanelActividades() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(BorderFactory.createTitledBorder("Actividades"));

        JPanel panelFechas = new JPanel();
        panelFechas.add(new JLabel("Desde:"));
        panelFechas.add(fechaDesdeActividades);
        panelFechas.add(new JLabel("Hasta:"));
        panelFechas.add(fechaHastaActividades);
        panelFechas.add(btnCargarActividades);
        panelFechas.add(btnImprimirActividades);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tablaActividades), chartPanelActividades);
        split.setResizeWeight(0.35);

        p.add(panelFechas, BorderLayout.NORTH);
        p.add(split, BorderLayout.CENTER);
        return p;
    }

    public JPanel getPanel() { return panel; }

    public void setController(Controller controller) { this.controller = controller; }
    public void setModel(Model model) { this.model = model; }

    public void refrescarRecursos() {
        modeloTablaRecursos.setRowCount(0);
        datasetRecursos.clear();
        for (Map.Entry<String, Integer> e : model.getEstadisticasRecursos().entrySet()) {
            modeloTablaRecursos.addRow(new Object[]{e.getKey(), e.getValue()});
            datasetRecursos.addValue(e.getValue(), "Recurso", e.getKey());
        }
    }

    public void refrescarActividades() {
        modeloTablaActividades.setRowCount(0);
        datasetActividades.clear();
        for (Map.Entry<String, Integer> e : model.getEstadisticasActividades().entrySet()) {
            modeloTablaActividades.addRow(new Object[]{e.getKey(), e.getValue()});
            datasetActividades.addValue(e.getValue(), "Semana", e.getKey());
        }
    }
}
