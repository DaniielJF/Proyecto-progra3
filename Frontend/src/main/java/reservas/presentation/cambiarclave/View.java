package reservas.presentation.cambiarclave;

import reservas.presentation.Iconos;

import javax.swing.*;
import java.awt.*;


public class View extends JDialog {

    private final JTextField txtId = new JTextField(15);
    private final JPasswordField txtClaveActual = new JPasswordField(15);
    private final JPasswordField txtClaveNueva = new JPasswordField(15);
    private final JPasswordField txtClaveNuevaConfirmar = new JPasswordField(15);
    private final JButton btnAplicar = new JButton("Aplicar", Iconos.get("ok.png"));
    private final JButton btnCancelar = new JButton("Cancelar", Iconos.get("cancel.png"));
    private Controller controller;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JButton button1;
    private JButton button2;

    public View(String idFijo) {
        setModal(true);
        setTitle("Cambiar Clave");
        setIconImage(Iconos.getImagen("clave.png"));
        setLayout(new BorderLayout(10, 10));

        JPanel panelCampos = new JPanel(new GridBagLayout());
        panelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.anchor = GridBagConstraints.WEST;

        gc.gridx = 0; gc.gridy = 0; panelCampos.add(new JLabel("ID:"), gc);
        gc.gridx = 1; panelCampos.add(txtId, gc);

        gc.gridx = 0; gc.gridy = 1; panelCampos.add(new JLabel("Clave Actual:"), gc);
        gc.gridx = 1; panelCampos.add(txtClaveActual, gc);

        gc.gridx = 0; gc.gridy = 2; panelCampos.add(new JLabel("Clave Nueva:"), gc);
        gc.gridx = 1; panelCampos.add(txtClaveNueva, gc);

        gc.gridx = 0; gc.gridy = 3; panelCampos.add(new JLabel("Clave Nueva (confirmar):"), gc);
        gc.gridx = 1; panelCampos.add(txtClaveNuevaConfirmar, gc);

        if (idFijo != null) {
            txtId.setText(idFijo);
            txtId.setEditable(false);
        }

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnAplicar);
        panelBotones.add(btnCancelar);

        add(panelCampos, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        btnAplicar.addActionListener(e -> {
            try {
                controller.aplicar(txtId.getText().trim(), new String(txtClaveActual.getPassword()),
                        new String(txtClaveNueva.getPassword()), new String(txtClaveNuevaConfirmar.getPassword()));
                JOptionPane.showMessageDialog(this, "Clave actualizada correctamente", "", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancelar.addActionListener(e -> controller.cancelar());

        setSize(360, 240);
        setLocationRelativeTo(null);
    }

    public void setController(Controller controller) { this.controller = controller; }

    public void cerrar() {
        setVisible(false);
        dispose();
    }
}
