package reservas.presentation.categorias;

import reservas.logic.CategoriaRecurso;
import reservas.presentation.Iconos;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class View implements PropertyChangeListener {
    private JPanel panel;

    private JTextField buscarDescripcionFld;
    private JButton buscarBtn;
    private JButton imprimirBtn;

    private JTextField idFld;
    private JTextField descripcionFld;
    private JButton agregarBtn;
    private JButton modificarBtn;
    private JButton borrarBtn;
    private JButton limpiarBtn;
    private JTable categoriasTable;

    Controller controller;
    Model model;
    private TableModel categoriasTableModel;
    private JTextField textField1;
    private JButton button1;
    private JButton button2;
    private JTextField textField2;
    private JTextField textField3;
    private JButton button3;
    private JButton button4;
    private JButton button5;
    private JTable table1;

    public View() {
        panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelBusqueda = new JPanel();
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Busqueda"));
        buscarDescripcionFld = new JTextField(15);
        buscarBtn = new JButton("Buscar", Iconos.get("search.png"));
        imprimirBtn = new JButton("Imprimir", Iconos.get("pdf.png"));
        panelBusqueda.add(new JLabel("Descripcion:"));
        panelBusqueda.add(buscarDescripcionFld);
        panelBusqueda.add(buscarBtn);
        panelBusqueda.add(imprimirBtn);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Categoria"));
        idFld = new JTextField(15);
        idFld.setEditable(false);
        descripcionFld = new JTextField(15);
        agregarBtn = new JButton("Agregar", Iconos.get("save.png"));
        modificarBtn = new JButton("Modificar", Iconos.get("check.png"));
        borrarBtn = new JButton("Borrar", Iconos.get("delete.png"));
        limpiarBtn = new JButton("Limpiar", Iconos.get("clear.png"));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 4, 4, 4);
        gc.anchor = GridBagConstraints.WEST;
        gc.gridx = 0; gc.gridy = 0; panelFormulario.add(new JLabel("Id:"), gc);
        gc.gridx = 1; panelFormulario.add(idFld, gc);
        gc.gridx = 0; gc.gridy = 1; panelFormulario.add(new JLabel("Descripcion:"), gc);
        gc.gridx = 1; panelFormulario.add(descripcionFld, gc);
        JPanel panelBotones = new JPanel();
        panelBotones.add(agregarBtn);
        panelBotones.add(modificarBtn);
        panelBotones.add(borrarBtn);
        panelBotones.add(limpiarBtn);
        gc.gridx = 0; gc.gridy = 2; gc.gridwidth = 2;
        panelFormulario.add(panelBotones, gc);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelBusqueda, BorderLayout.NORTH);
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);

        categoriasTable = new JTable();
        JPanel panelListado = new JPanel(new BorderLayout());
        panelListado.setBorder(BorderFactory.createTitledBorder("Listado"));
        panelListado.add(new JScrollPane(categoriasTable), BorderLayout.CENTER);

        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(panelListado, BorderLayout.CENTER);

        agregarBtn.addActionListener(e -> {
            try {
                CategoriaRecurso c = tomarDatos();
                controller.agregarCategoria(c);
                limpiarCampos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        modificarBtn.addActionListener(e -> {
            try {
                CategoriaRecurso c = tomarDatos();
                c.setId(model.getCurrent().getId());
                controller.modificarCategoria(c);
                limpiarCampos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        borrarBtn.addActionListener(e -> {
            if (model.getCurrent().getId() == null || model.getCurrent().getId().isBlank()) {
                JOptionPane.showMessageDialog(panel, "Debe seleccionar una categoria", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int op = JOptionPane.showConfirmDialog(panel, "Seguro que desea borrar esta categoria?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (op != JOptionPane.YES_OPTION) return;
            try {
                controller.borrarCategoria(model.getCurrent());
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
            CategoriaRecurso filtro = new CategoriaRecurso();
            filtro.setDescripcion(buscarDescripcionFld.getText());
            controller.buscar(filtro);
        });

        imprimirBtn.addActionListener(e -> controller.imprimir());

        categoriasTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && categoriasTable.getSelectedRow() != -1) {
                controller.seleccionarCategoria(categoriasTable.getSelectedRow());
            }
        });
    }

    private CategoriaRecurso tomarDatos() throws Exception {
        if (descripcionFld.getText().isBlank()) throw new Exception("La descripcion es obligatoria");
        CategoriaRecurso c = new CategoriaRecurso();
        c.setDescripcion(descripcionFld.getText());
        return c;
    }

    private void limpiarCampos() {
        idFld.setText("");
        descripcionFld.setText("");
        categoriasTable.clearSelection();
    }

    public JPanel getPanel() { return panel; }

    public void setController(Controller controller) { this.controller = controller; }

    public void setModel(Model model) {
        this.model = model;
        categoriasTableModel = new TableModel(new int[]{TableModel.ID, TableModel.DESCRIPCION}, model.getList());
        categoriasTable.setModel(categoriasTableModel);
        model.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Model.LIST.equals(evt.getPropertyName())) {
            categoriasTableModel.setRows(model.getList());
        }
        if (Model.CURRENT.equals(evt.getPropertyName())) {
            CategoriaRecurso current = model.getCurrent();
            boolean esNuevo = current.getId() == null || current.getId().isBlank();
            idFld.setText(esNuevo ? "" : current.getId());
            descripcionFld.setText(esNuevo ? "" : current.getDescripcion());
        }
    }
}
