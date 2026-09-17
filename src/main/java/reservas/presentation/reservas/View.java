package reservas.presentation.reservas;

import com.github.lgooddatepicker.components.DatePicker;
import reservas.logic.CategoriaRecurso;
import reservas.logic.DatosExtraidosIA;
import reservas.logic.Reserva;
import reservas.logic.ResultadoReserva;
import reservas.presentation.Iconos;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class View implements PropertyChangeListener {
    private final JPanel panel = new JPanel(new BorderLayout(10, 10));

    private final JTextField txtFrase = new JTextField(30);
    private final JButton btnExtraer = new JButton("Extraer con IA", Iconos.get("ai.png"));

    private final JTextField txtActividad = new JTextField(20);
    private final DatePicker fechaPicker = new DatePicker();
    private final JSpinner spHoraInicio = crearSpinnerHora();
    private final JSpinner spHoraFin = crearSpinnerHora();
    private final DefaultListModel<CategoriaRecurso> modeloListaCategorias = new DefaultListModel<>();
    private final JList<CategoriaRecurso> listaCategorias = new JList<>(modeloListaCategorias);

    private final JButton btnReservar = new JButton("Reservar", Iconos.get("save.png"));
    private final JButton btnCancelarReserva = new JButton("Cancelar reserva seleccionada", Iconos.get("descartar.png"));
    private final JButton btnLimpiar = new JButton("Limpiar", Iconos.get("clear.png"));
    private final JButton btnImprimir = new JButton("Imprimir", Iconos.get("pdf.png"));

    private final JTable tabla = new JTable();
    Controller controller;
    Model model;
    private TableModel tableModel;
    private JTable tablero;
    private JSpinner spinner1;
    private JSpinner spinner2;
    private JList list1;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JTextField textField1;
    private JTextField textField2;
    private JButton button5;

    public View() {
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelFrase = new JPanel(new BorderLayout(5, 5));
        panelFrase.setBorder(BorderFactory.createTitledBorder("Usando IA (opcional)"));
        panelFrase.add(new JLabel("Frase:"), BorderLayout.WEST);
        panelFrase.add(txtFrase, BorderLayout.CENTER);
        panelFrase.add(btnExtraer, BorderLayout.EAST);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Nueva reserva"));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 4, 4, 4);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0; gc.gridy = 0; panelFormulario.add(new JLabel("Actividad:"), gc);
        gc.gridx = 1; panelFormulario.add(txtActividad, gc);

        gc.gridx = 0; gc.gridy = 1; panelFormulario.add(new JLabel("Fecha:"), gc);
        gc.gridx = 1; panelFormulario.add(fechaPicker, gc);

        gc.gridx = 0; gc.gridy = 2; panelFormulario.add(new JLabel("Hora inicio:"), gc);
        gc.gridx = 1; panelFormulario.add(spHoraInicio, gc);

        gc.gridx = 0; gc.gridy = 3; panelFormulario.add(new JLabel("Hora fin:"), gc);
        gc.gridx = 1; panelFormulario.add(spHoraFin, gc);

        gc.gridx = 0; gc.gridy = 4; panelFormulario.add(new JLabel("Categorias requeridas (seleccion multiple):"), gc);
        gc.gridx = 1; gc.gridy = 4;
        listaCategorias.setVisibleRowCount(4);
        panelFormulario.add(new JScrollPane(listaCategorias), gc);

        JPanel panelBotonesForm = new JPanel();
        panelBotonesForm.add(btnReservar);
        panelBotonesForm.add(btnCancelarReserva);
        panelBotonesForm.add(btnLimpiar);
        gc.gridx = 0; gc.gridy = 5; gc.gridwidth = 2;
        panelFormulario.add(panelBotonesForm, gc);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelFrase, BorderLayout.NORTH);
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);

        JPanel panelListado = new JPanel(new BorderLayout());
        JPanel panelListadoHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelListadoHeader.add(btnImprimir);
        panelListado.setBorder(BorderFactory.createTitledBorder("Mis reservas"));
        panelListado.add(panelListadoHeader, BorderLayout.NORTH);
        panelListado.add(new JScrollPane(tabla), BorderLayout.CENTER);

        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(panelListado, BorderLayout.CENTER);

        btnExtraer.addActionListener(e -> controller.extraerConIA(txtFrase.getText().trim()));

        btnReservar.addActionListener(e -> {
            try {
                String actividad = txtActividad.getText().trim();
                LocalDate fecha = fechaPicker.getDate();
                LocalTime horaInicio = horaDeSpinner(spHoraInicio);
                LocalTime horaFin = horaDeSpinner(spHoraFin);
                List<CategoriaRecurso> categorias = categoriasSeleccionadas();

                ResultadoReserva resultado = controller.reservar(actividad, fecha, horaInicio, horaFin, categorias);
                if (resultado.isExito()) {
                    JOptionPane.showMessageDialog(panel, "Reserva registrada: " + resultado.getReserva().getId(), "", JOptionPane.INFORMATION_MESSAGE);
                    limpiarFormulario();
                } else {
                    StringBuilder sb = new StringBuilder("No hubo disponibilidad para las siguientes categorias:\n");
                    for (CategoriaRecurso c : resultado.getCategoriasNoDisponibles()) {
                        sb.append(" - ").append(c.getDescripcion()).append("\n");
                    }
                    sb.append("\nPuede modificar los datos e intentar de nuevo.");
                    JOptionPane.showMessageDialog(panel, sb.toString(), "Sin disponibilidad", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelarReserva.addActionListener(e -> {
            if (tabla.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(panel, "Debe seleccionar una reserva", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Reserva r = tableModel.getRowAt(tabla.getSelectedRow());
            int op = JOptionPane.showConfirmDialog(panel, "Seguro que desea cancelar la reserva " + r.getId() + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (op != JOptionPane.YES_OPTION) return;
            try {
                controller.cancelar(r);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnImprimir.addActionListener(e -> controller.imprimir());
    }

    private JSpinner crearSpinnerHora() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        spinner.setEditor(new JSpinner.DateEditor(spinner, "HH:mm"));
        return spinner;
    }

    private LocalTime horaDeSpinner(JSpinner spinner) {
        Date d = (Date) spinner.getValue();
        return d.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime().withSecond(0).withNano(0);
    }

    private List<CategoriaRecurso> categoriasSeleccionadas() {
        return new ArrayList<>(listaCategorias.getSelectedValuesList());
    }

    public JPanel getPanel() { return panel; }

    public void setController(Controller controller) { this.controller = controller; }

    public void setModel(Model model) {
        this.model = model;
        tableModel = new TableModel(new int[]{TableModel.ID, TableModel.ACTIVIDAD, TableModel.FECHA,
                TableModel.HORARIO, TableModel.RECURSOS, TableModel.ESTADO}, model.getMisReservas());
        tabla.setModel(tableModel);
        model.addPropertyChangeListener(this);
    }

    public void poblarCategorias(List<CategoriaRecurso> categorias) {
        modeloListaCategorias.clear();
        for (CategoriaRecurso c : categorias) modeloListaCategorias.addElement(c);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Model.MIS_RESERVAS.equals(evt.getPropertyName())) {
            tableModel.setRows(model.getMisReservas());
        }
        if (Model.CATEGORIAS.equals(evt.getPropertyName())) {
            poblarCategorias(model.getCategoriasDisponibles());
        }
    }

    public void mostrarDatosExtraidos(DatosExtraidosIA datos) {
        if (datos.getActividad() != null) txtActividad.setText(datos.getActividad());
        if (datos.getFecha() != null) fechaPicker.setDate(datos.getFecha());
        if (datos.getHoraInicio() != null) spHoraInicio.setValue(java.sql.Time.valueOf(datos.getHoraInicio()));
        if (datos.getHoraFin() != null) spHoraFin.setValue(java.sql.Time.valueOf(datos.getHoraFin()));

        listaCategorias.clearSelection();
        if (datos.getDescripcionesCategorias() != null && !datos.getDescripcionesCategorias().isEmpty()) {
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < modeloListaCategorias.size(); i++) {
                if (datos.getDescripcionesCategorias().contains(modeloListaCategorias.get(i).getDescripcion())) {
                    indices.add(i);
                }
            }
            int[] arr = indices.stream().mapToInt(Integer::intValue).toArray();
            listaCategorias.setSelectedIndices(arr);
        }
    }

    public void limpiarFormulario() {
        txtFrase.setText("");
        txtActividad.setText("");
        fechaPicker.setDate(LocalDate.now());
        listaCategorias.clearSelection();
        tabla.clearSelection();
    }
}
