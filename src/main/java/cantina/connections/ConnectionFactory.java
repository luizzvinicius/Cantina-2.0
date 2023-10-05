package cantina.connections;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory implements AutoCloseable {
    private static Connection conn = null;

    private ConnectionFactory() { }

    public static Connection getConnection() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost", "root", "root");
            criarBanco(conn);
        } catch (SQLException e) {
            System.out.println("Erro ao se conectar: " + e.getMessage());
        }
        return conn;
    }

    private static void criarBanco(Connection conn) {
        var path = Paths.get("src/main/java/cantina/connections/tables.sql");
        try (var stmt = conn.createStatement()) {
            var file = Files.readString(path).replace("\r\n", "").split(";");
            for (String comand : file) {
                stmt.addBatch(comand + ";");
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            System.out.println("Erro ao criar preparedStatement: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Erro ao ler arquivo: " + e.getMessage());
        }
    }

    @Override
    public void close() throws Exception {
        conn.close();
    }
}