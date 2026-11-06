package reservas.backend.data;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Database {

    public static final String PROPERTIES_FILE_NAME = "/reservas/backend/database.properties";

    private static Database theInstance;

    public static synchronized Database instance() {
        if (theInstance == null) theInstance = new Database();
        return theInstance;
    }

    private Connection cnx;

    private Database() {
        cnx = getConnection();
    }

    private Connection getConnection() {
        try {
            Properties prop = new Properties();
            URL resourceUrl = getClass().getResource(PROPERTIES_FILE_NAME);
            if (resourceUrl == null) {
                throw new IllegalStateException("No se encontro " + PROPERTIES_FILE_NAME + " en el classpath");
            }
            File file = new File(resourceUrl.toURI());
            prop.load(new BufferedInputStream(new FileInputStream(file)));

            String driver = prop.getProperty("database_driver");
            String server = prop.getProperty("database_server");
            String port = prop.getProperty("database_port");
            String user = prop.getProperty("database_user");
            String password = prop.getProperty("database_password");
            String database = prop.getProperty("database_name");

            String urlConexion = "jdbc:mysql://" + server + ":" + port + "/" + database
                    + "?user=" + user + "&password=" + password
                    + "&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";

            Class.forName(driver).getDeclaredConstructor().newInstance();
            return DriverManager.getConnection(urlConexion);
        } catch (Exception e) {
            System.err.println("No se pudo conectar a la base de datos: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public synchronized <T> List<T> query(String sql, RowMapper<T> mapper, Object... params) throws SQLException {
        List<T> resultado = new ArrayList<>();
        try (PreparedStatement stm = cnx.prepareStatement(sql)) {
            bind(stm, params);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapper.map(rs));
                }
            }
        }
        return resultado;
    }

    public synchronized <T> T queryOne(String sql, RowMapper<T> mapper, Object... params) throws SQLException {
        try (PreparedStatement stm = cnx.prepareStatement(sql)) {
            bind(stm, params);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) return mapper.map(rs);
                return null;
            }
        }
    }

    public synchronized int update(String sql, Object... params) throws SQLException {
        try (PreparedStatement stm = cnx.prepareStatement(sql)) {
            bind(stm, params);
            return stm.executeUpdate();
        }
    }

    private void bind(PreparedStatement stm, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stm.setObject(i + 1, params[i]);
        }
    }
}
