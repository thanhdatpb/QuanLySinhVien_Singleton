import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() throws SQLException, IOException {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.ini")) {
            if (input == null) {
                throw new IOException("Unable to find config.ini");
            }
            props.load(input);
        }

        String host = props.getProperty("mysql.host");
        String database = props.getProperty("mysql.database");
        String user = props.getProperty("mysql.user");
        String password = props.getProperty("mysql.password");
        String url = "jdbc:mysql://" + host + "/" + database;

        this.connection = DriverManager.getConnection(url, user, password);
    }

    public Connection getConnection() {
        return connection;
    }

    public static DatabaseConnection getInstance() throws SQLException, IOException {
        if (instance == null) {
            instance = new DatabaseConnection();
        } else if (instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }

        return instance;
    }
}