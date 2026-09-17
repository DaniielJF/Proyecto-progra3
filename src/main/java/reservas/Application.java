package reservas;

import reservas.logic.Service;
import reservas.presentation.Iconos;
import reservas.presentation.Sesion;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Application {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        doLogin();
        if (Sesion.isLoggedIn()) {
            doRun();
        }
    }

    private static void doLogin() {
        reservas.presentation.login.View loginView = new reservas.presentation.login.View();
        loginView.setLocationRelativeTo(null);
        reservas.presentation.login.Model loginModel = new reservas.presentation.login.Model();
        new reservas.presentation.login.Controller(loginView, loginModel);
        loginView.setVisible(true);
    }

    private static void doRun() {
        JFrame window = new JFrame("Sistema de Reservas");
        window.setIconImage(Iconos.getImagen("icon.png"));
        JTabbedPane tabbedPane = new JTabbedPane();
        window.setContentPane(tabbedPane);
        window.setTitle("Sistema de Reservas - " + Sesion.getUsuario().getId() + " (" + Sesion.getUsuario().getRol() + ")");

        JMenuBar menuBar = new JMenuBar();
        JMenu menuCuenta = new JMenu("Cuenta");
        JMenuItem itemCambiarClave = new JMenuItem("Cambiar Clave", Iconos.get("clave.png"));
        itemCambiarClave.addActionListener(e -> {
            reservas.presentation.cambiarclave.View dialogo =
                    new reservas.presentation.cambiarclave.View(Sesion.getUsuario().getId());
            new reservas.presentation.cambiarclave.Controller(dialogo);
            dialogo.setVisible(true);
        });
        menuCuenta.add(itemCambiarClave);
        menuBar.add(menuCuenta);
        window.setJMenuBar(menuBar);

        // Modulos comunes a ambos roles
        reservas.presentation.calendarizacion.Model calModel = new reservas.presentation.calendarizacion.Model();
        reservas.presentation.calendarizacion.View calView = new reservas.presentation.calendarizacion.View();
        new reservas.presentation.calendarizacion.Controller(calView, calModel);

        reservas.presentation.actividades.Model actModel = new reservas.presentation.actividades.Model();
        reservas.presentation.actividades.View actView = new reservas.presentation.actividades.View();
        new reservas.presentation.actividades.Controller(actView, actModel);

        reservas.presentation.estadisticas.Model estModel = new reservas.presentation.estadisticas.Model();
        reservas.presentation.estadisticas.View estView = new reservas.presentation.estadisticas.View();
        new reservas.presentation.estadisticas.Controller(estView, estModel);

        switch (Sesion.getUsuario().getRol()) {
            case ADMIN -> {
                reservas.presentation.funcionarios.Model funcModel = new reservas.presentation.funcionarios.Model();
                reservas.presentation.funcionarios.View funcView = new reservas.presentation.funcionarios.View();
                new reservas.presentation.funcionarios.Controller(funcView, funcModel);
                tabbedPane.addTab("Funcionarios", Iconos.get("funcionarios.png"), funcView.getPanel());

                reservas.presentation.categorias.Model catModel = new reservas.presentation.categorias.Model();
                reservas.presentation.categorias.View catView = new reservas.presentation.categorias.View();
                new reservas.presentation.categorias.Controller(catView, catModel);
                tabbedPane.addTab("Categorias", Iconos.get("categorias.png"), catView.getPanel());

                reservas.presentation.recursos.Model recModel = new reservas.presentation.recursos.Model();
                reservas.presentation.recursos.View recView = new reservas.presentation.recursos.View();
                new reservas.presentation.recursos.Controller(recView, recModel);
                tabbedPane.addTab("Recursos", Iconos.get("recursos.png"), recView.getPanel());

                tabbedPane.addTab("Calendarizacion", Iconos.get("calendarizacion.png"), calView.getPanel());
                tabbedPane.addTab("Actividades", Iconos.get("actividades.png"), actView.getPanel());
                tabbedPane.addTab("Estadisticas", Iconos.get("statistics.png"), estView.getPanel());
            }
            case FUNCIONARIO -> {
                reservas.presentation.reservas.Model resModel = new reservas.presentation.reservas.Model();
                reservas.presentation.reservas.View resView = new reservas.presentation.reservas.View();
                new reservas.presentation.reservas.Controller(resView, resModel);
                tabbedPane.addTab("Reservas", Iconos.get("reservas.png"), resView.getPanel());

                tabbedPane.addTab("Calendarizacion", Iconos.get("calendarizacion.png"), calView.getPanel());
                tabbedPane.addTab("Actividades", Iconos.get("actividades.png"), actView.getPanel());
                tabbedPane.addTab("Estadisticas", Iconos.get("statistics.png"), estView.getPanel());
            }
        }

        window.setSize(1100, 700);
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Service.instance().stop(); // unica vez que se persiste todo a data.xml
                System.exit(0);
            }
        });
        window.setVisible(true);
    }
}
