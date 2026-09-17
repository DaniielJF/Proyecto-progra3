package reservas.presentation;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class Iconos {

    public static ImageIcon get(String nombreArchivo) {
        URL url = Iconos.class.getResource("/reservas/presentation/icons/" + nombreArchivo);
        return url != null ? new ImageIcon(url) : null;
    }

    public static Image getImagen(String nombreArchivo) {
        ImageIcon icono = get(nombreArchivo);
        return icono != null ? icono.getImage() : null;
    }
}
