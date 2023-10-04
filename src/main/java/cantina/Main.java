package cantina;

import cantina.connections.*;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // DriverManager, Conncection, ResultSet, Statement, PreparedStatement
        try (var conn = ConnectionFactory.getConnection()) {
            List<Funcionario> funcionarios = new FuncionarioDAO(conn).select();
            System.out.println(funcionarios);
            
        } catch (SQLException e) {
            System.out.println("Conexão vazia: " + e.getMessage());
        }
    }
}