package cantina.connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory implements AutoCloseable {
    private static Connection conn = null;

    private ConnectionFactory() {
    }

    public static Connection getConnection() {
        try {
            if (conn == null) {
                conn = DriverManager.getConnection("jdbc:mysql://localhost/cantina", "root", "root");
            }
            return conn;
        } catch (SQLException e) {
            System.out.println("Erro ao se conectar: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        conn.close();
    }
}