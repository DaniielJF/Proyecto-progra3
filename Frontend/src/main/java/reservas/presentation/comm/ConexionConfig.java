package reservas.presentation.comm;

import reservas.comm.Protocolo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

/** Lee una sola vez host/puerto del Backend desde conexion.properties. */
final class ConexionConfig {

    private static final String PROPERTIES_FILE_NAME = "/reservas/presentation/comm/conexion.properties";

    static final String HOST;
    static final int PUERTO;

    static {
        String host = Protocolo.SERVIDOR_POR_DEFECTO;
        int puerto = Protocolo.PUERTO_POR_DEFECTO;
        try {
            Properties prop = new Properties();
            URL resourceUrl = ConexionConfig.class.getResource(PROPERTIES_FILE_NAME);
            if (resourceUrl != null) {
                File file = new File(resourceUrl.toURI());
                prop.load(new BufferedInputStream(new FileInputStream(file)));
                host = prop.getProperty("backend_host", host);
                puerto = Integer.parseInt(prop.getProperty("backend_port", String.valueOf(puerto)));
            }
        } catch (Exception ignored) {
            // si no hay archivo de propiedades se usan host/puerto por defecto
        }
        HOST = host;
        PUERTO = puerto;
    }

    private ConexionConfig() { }
}
