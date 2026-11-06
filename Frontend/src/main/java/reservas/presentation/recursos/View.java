package reservas.presentation.recursos;

import reservas.logic.CategoriaRecurso;
import reservas.logic.Recurso;
import reservas.presentation.Iconos;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class View implements PropertyChangeListener {
    private JPanel panel;

    private JComboBox<CategoriaRecurso> buscarCategoriaFld;
    private JTextField buscarDescripcionFld;
    private JButton buscarBtn;
    private JButton imprimirBtn;

    private JTextField idFld;
    private JComboBox<CategoriaRecurso> categoriaFld;
    private JTextField descripcionFld;
    private JButton agregarBtn;
    private JButton modificarBtn;
    private JButton borrarBtn;
    private JButton limpiarBtn;
    private JTable recursosTable;

    Controller controller;
    Model model;
    private TableModel recursosTableModel;

    public View() {
        panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelFiltro = new JPanel();
        panelFiltro.setBorder(BorderFactory.createTitledBorder("Filtro"));
        buscarCategoriaFld = new JComboBox<>();
        buscarDescripcionFld = new JTextField(12);
        buscarBtn = new JButton("Buscar", Iconos.get("search.png"));
        imprimirBtn = new JButton("Imprimir", Iconos.get("pdf.png"));
        panelFiltro.add(new JLabel("Categoria:"));
        panelFiltro.add(buscarCategoriaFld);
        panelFiltro.add(new JLabel("Descripcion:"));
        panelFiltro.add(buscarDescripcionFld);
        panelFiltro.add(buscarBtn);
        panelFiltro.add(imprimirBtn);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Recurso"));
        idFld = new JTextField(15);
        categoriaFld = new JComboBox<>();
        descripcionFld = new JTextField(15);
        agregarBtn = new JButton("Agregar", Iconos.get("save.png"));
        modificarBtn = new JButton("Modificar", Iconos.get("check.png"));
        borrarBtn = new JButton("Borrar", Iconos.get("delete.png"));
        limpiarBtn = new JButton("Limpiar", Iconos.get("clear.png"));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 4, 4, 4);
        gc.anchor = GridBagConstraints.WEST;
        gc.gridx = 0; gc.gridy = 0; panelFormulario.add(new JLabel("Id / # Activo:"), gc);
        gc.gridx = 1; panelFormulario.add(idFld, gc);
        gc.gridx = 0; gc.gridy = 1; panelFormulario.add(new JLabel("Categoria:"), gc);
        gc.gridx = 1; panelFormulario.add(categoriaFld, gc);
        gc.gridx = 0; gc.gridy = 2; panelFormulario.add(new JLabel("Descripcion:"), gc);
        gc.gridx = 1; panelFormulario.add(descripcionFld, gc);
        JPanel panelBotones = new JPanel();
        panelBotones.add(agregarBtn);
        panelBotones.add(modificarBtn);
        panelBotones.add(borrarBtn);
        panelBotones.add(limpiarBtn);
        gc.gridx = 0; gc.gridy = 3; gc.gridwidth = 2;
        panelFormulario.add(panelBotones, gc);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelFiltro, BorderLayout.NORTH);
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);

        recursosTable = new JTable();
        JPanel panelListado = new JPanel(new BorderLayout());
        panelListado.setBorder(BorderFactory.createTitledBorder("Listado"));
        panelListado.add(new JScrollPane(recursosTable), BorderLayout.CENTER);

        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(panelListado, BorderLayout.CENTER);

        agregarBtn.addActionListener(e -> {
            try {
                Recurso r = tomarDatos();
                controller.agregarRecurso(r);
                limpiarCampos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        modificarBtn.addActionListener(e -> {
            try {
                Recurso r = tomarDatos();
                r.setId(model.getCurrent().getId());
                controller.modificarRecurso(r);
                limpiarCampos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        borrarBtn.addActionListener(e -> {
            if (model.getCurrent().getId() == null || model.getCurrent().getId().isBlank()) {
                JOptionPane.showMessageDialog(panel, "Debe seleccionar un recurso", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int op = JOptionPane.showConfirmDialog(panel, "Seguro que desea borrar este recurso?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (op != JOptionPane.YES_OPTION) return;
            try {
                controller.borrarRecurso(model.getCurrent());
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
            Recurso filtro = new Recurso();
            filtro.setCategoria((CategoriaRecurso) buscarCategoriaFld.getSelectedItem());
            filtro.setDescripcion(buscarDescripcionFld.getText());
            controller.buscar(filtro);
        });

        imprimirBtn.addActionListener(e -> controller.imprimir());

        recursosTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && recursosTable.getSelectedRow() != -1) {
                controller.seleccionarRecurso(recursosTable.getSelectedRow());
            }
        });
    }

    private Recurso tomarDatos() throws Exception {
        if (idFld.getText().isBlank()) throw new Exception("El id/numero de activo es obligatorio");
        if (categoriaFld.getSelectedItem() == null) throw new Exception("Debe seleccionar una categoria");
        if (descripcionFld.getText().isBlank()) throw new Exception("La descripcion es obligatoria");
        Recurso r = new Recurso();
        r.setId(idFld.getText());
        r.setCategoria((CategoriaRecurso) categoriaFld.getSelectedItem());
        r.setDescripcion(descripcionFld.getText());
        return r;
    }

    private void limpiarCampos() {
        idFld.setText("");
        idFld.setEditable(true);
        descripcionFld.setText("");
        recursosTable.clearSelection();
    }

    public JPanel getPanel() { return panel; }

    public void setController(Controller controller) { this.controller = controller; }

    public void setModel(Model model) {
        this.model = model;
        recursosTableModel = new TableModel(new int[]{TableModel.ID, TableModel.CATEGORIA, TableModel.DESCRIPCION}, model.getList());
        recursosTable.setModel(recursosTableModel);
        model.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Model.CATEGORIAS.equals(evt.getPropertyName())) {
            buscarCategoriaFld.removeAllItems();
            buscarCategoriaFld.addItem(null); // "todas"
            categoriaFld.removeAllItems();
            for (CategoriaRecurso c : model.getCategorias()) {
                buscarCategoriaFld.addItem(c);
                categoriaFld.addItem(c);
            }
        }
        if (Model.LIST.equals(evt.getPropertyName())) {
            recursosTableModel.setRows(model.getList());
        }
        if (Model.CURRENT.equals(evt.getPropertyName())) {
            Recurso current = model.getCurrent();
            boolean esNuevo = current.getId() == null || current.getId().isBlank();
            idFld.setText(esNuevo ? "" : current.getId());
            idFld.setEditable(esNuevo);
            categoriaFld.setSelectedItem(current.getCategoria());
            descripcionFld.setText(esNuevo ? "" : current.getDescripcion());
        }
    }
}
