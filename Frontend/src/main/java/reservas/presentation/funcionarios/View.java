package reservas.presentation.funcionarios;

import reservas.logic.Funcionario;
import reservas.presentation.Iconos;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class View implements PropertyChangeListener {
    private JPanel panel;

    private JTextField buscarIdFld;
    private JTextField buscarNombreFld;
    private JButton buscarBtn;
    private JButton imprimirBtn;

    private JTextField idFld;
    private JTextField nombreFld;
    private JTextField telefonoFld;
    private JButton agregarBtn;
    private JButton modificarBtn;
    private JButton borrarBtn;
    private JButton limpiarBtn;
    private JTable funcionariosTable;

    Controller controller;
    Model model;
    private TableModel funcionariosTableModel;

    public View() {
        panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelBusqueda = new JPanel();
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Busqueda"));
        buscarIdFld = new JTextField(10);
        buscarNombreFld = new JTextField(10);
        buscarBtn = new JButton("Buscar", Iconos.get("search.png"));
        imprimirBtn = new JButton("Imprimir", Iconos.get("pdf.png"));
        panelBusqueda.add(new JLabel("Id:"));
        panelBusqueda.add(buscarIdFld);
        panelBusqueda.add(new JLabel("Nombre:"));
        panelBusqueda.add(buscarNombreFld);
        panelBusqueda.add(buscarBtn);
        panelBusqueda.add(imprimirBtn);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Funcionario"));
        idFld = new JTextField(15);
        nombreFld = new JTextField(15);
        telefonoFld = new JTextField(15);
        agregarBtn = new JButton("Agregar", Iconos.get("save.png"));
        modificarBtn = new JButton("Modificar", Iconos.get("check.png"));
        borrarBtn = new JButton("Borrar", Iconos.get("delete.png"));
        limpiarBtn = new JButton("Limpiar", Iconos.get("clear.png"));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 4, 4, 4);
        gc.anchor = GridBagConstraints.WEST;
        gc.gridx = 0; gc.gridy = 0; panelFormulario.add(new JLabel("Id:"), gc);
        gc.gridx = 1; panelFormulario.add(idFld, gc);
        gc.gridx = 0; gc.gridy = 1; panelFormulario.add(new JLabel("Nombre:"), gc);
        gc.gridx = 1; panelFormulario.add(nombreFld, gc);
        gc.gridx = 0; gc.gridy = 2; panelFormulario.add(new JLabel("Telefono:"), gc);
        gc.gridx = 1; panelFormulario.add(telefonoFld, gc);
        JPanel panelBotones = new JPanel();
        panelBotones.add(agregarBtn);
        panelBotones.add(modificarBtn);
        panelBotones.add(borrarBtn);
        panelBotones.add(limpiarBtn);
        gc.gridx = 0; gc.gridy = 3; gc.gridwidth = 2;
        panelFormulario.add(panelBotones, gc);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelBusqueda, BorderLayout.NORTH);
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);

        funcionariosTable = new JTable();
        JPanel panelListado = new JPanel(new BorderLayout());
        panelListado.setBorder(BorderFactory.createTitledBorder("Listado"));
        panelListado.add(new JScrollPane(funcionariosTable), BorderLayout.CENTER);

        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(panelListado, BorderLayout.CENTER);

        agregarBtn.addActionListener(e -> {
            try {
                Funcionario f = tomarDatos();
                controller.agregarFuncionario(f);
                JOptionPane.showMessageDialog(panel, "Funcionario agregado", "", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        modificarBtn.addActionListener(e -> {
            try {
                Funcionario f = tomarDatos();
                controller.modificarFuncionario(f);
                JOptionPane.showMessageDialog(panel, "Funcionario modificado", "", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        borrarBtn.addActionListener(e -> {
            if (model.getCurrent().getId() == null || model.getCurrent().getId().isBlank()) {
                JOptionPane.showMessageDialog(panel, "Debe seleccionar un funcionario", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int op = JOptionPane.showConfirmDialog(panel, "Seguro que desea borrar este funcionario?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (op != JOptionPane.YES_OPTION) return;
            try {
                controller.borrarFuncionario(model.getCurrent());
                limpiarCampos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        limpiarBtn.addActionListener(e -> {
            controller.limpiar();
            limpiarCampos();
        });

        buscarBtn.addActionListener(e -> {
            Funcionario filtro = new Funcionario();
            filtro.setId(buscarIdFld.getText());
            filtro.setNombre(buscarNombreFld.getText());
            controller.buscar(filtro);
        });

        imprimirBtn.addActionListener(e -> controller.imprimir());

        funcionariosTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && funcionariosTable.getSelectedRow() != -1) {
                controller.seleccionarFuncionario(funcionariosTable.getSelectedRow());
            }
        });
    }

    private Funcionario tomarDatos() throws Exception {
        if (idFld.getText().isBlank()) throw new Exception("El id es obligatorio");
        if (nombreFld.getText().isBlank()) throw new Exception("El nombre es obligatorio");
        if (telefonoFld.getText().isBlank()) throw new Exception("El telefono es obligatorio");
        Funcionario f = new Funcionario();
        f.setId(idFld.getText());
        f.setNombre(nombreFld.getText());
        f.setTelefono(telefonoFld.getText());
        return f;
    }

    private void limpiarCampos() {
        idFld.setText("");
        idFld.setEditable(true);
        nombreFld.setText("");
        telefonoFld.setText("");
        funcionariosTable.clearSelection();
    }

    public JPanel getPanel() { return panel; }

    public void setController(Controller controller) { this.controller = controller; }

    public void setModel(Model model) {
        this.model = model;
        funcionariosTableModel = new TableModel(new int[]{TableModel.ID, TableModel.NOMBRE, TableModel.TELEFONO}, model.getList());
        funcionariosTable.setModel(funcionariosTableModel);
        model.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Model.LIST.equals(evt.getPropertyName())) {
            funcionariosTableModel.setRows(model.getList());
        }
        if (Model.CURRENT.equals(evt.getPropertyName())) {
            Funcionario current = model.getCurrent();
            boolean esNuevo = current.getId() == null || current.getId().isBlank();
            idFld.setText(esNuevo ? "" : current.getId());
            idFld.setEditable(esNuevo);
            nombreFld.setText(esNuevo ? "" : current.getNombre());
            telefonoFld.setText(esNuevo ? "" : current.getTelefono());
        }
    }
}