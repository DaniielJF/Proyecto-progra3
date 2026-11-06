package reservas.presentation.login;

import reservas.presentation.Iconos;

import javax.swing.*;
import java.awt.*;

public class View extends JDialog {

    private final JTextField txtId = new JTextField(15);
    private final JPasswordField txtClave = new JPasswordField(15);
    private final JButton btnIngresar = new JButton("Ingresar", Iconos.get("login.png"));
    private final JButton btnCancelar = new JButton("Cancelar", Iconos.get("cancel.png"));
    private final JButton btnCambiar = new JButton("Cambiar", Iconos.get("clave.png"));
    private Controller controller;
    private JButton button1;
    private JButton button2;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JButton button3;

    public View() {
        setModal(true);
        setTitle("Sistema de Reservas - Ingreso");
        setIconImage(Iconos.getImagen("loginDialog.png"));
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel(
                "SISTEMA DE RESERVA DE RECURSOS",
                Iconos.get("loginDialog.png"),
                SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));
        titulo.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));

        JPanel panelCampos = new JPanel(new GridBagLayout());
        panelCampos.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.anchor = GridBagConstraints.WEST;

        gc.gridx = 0; gc.gridy = 0;
        panelCampos.add(new JLabel("ID:"), gc);
        gc.gridx = 1;
        panelCampos.add(txtId, gc);

        gc.gridx = 0; gc.gridy = 1;
        panelCampos.add(new JLabel("Clave:"), gc);
        gc.gridx = 1;
        panelCampos.add(txtClave, gc);

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnIngresar);
        panelBotones.add(btnCancelar);
        panelBotones.add(btnCambiar);

        add(titulo, BorderLayout.NORTH);
        add(panelCampos, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        btnIngresar.addActionListener(e ->
                controller.ingresar(txtId.getText().trim(), new String(txtClave.getPassword())));
        btnCancelar.addActionListener(e -> controller.cancelar());
        btnCambiar.addActionListener(e -> {
            reservas.presentation.cambiarclave.View dialogo =
                    new reservas.presentation.cambiarclave.View(txtId.getText().isBlank() ? null : txtId.getText().trim());
            new reservas.presentation.cambiarclave.Controller(dialogo);
            dialogo.setVisible(true);
        });
        getRootPane().setDefaultButton(btnIngresar);

        pack();
        setResizable(false);
    }

    public void setController(Controller controller) { this.controller = controller; }

    public void cerrar() {
        setVisible(false);
        dispose();
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
