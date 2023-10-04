package cantina.connections;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;

import cantina.Funcionario;

public class FuncionarioDAO {
    private Connection conn;

    public FuncionarioDAO(Connection conn) {
        this.conn = conn;
    }

    public void insert(Funcionario func) {
        final String sql = "insert into funcionario (nome) values (?)";
        try (var stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, func.getNome());
            stmt.execute();
            System.out.println("Funcionário inserido.");
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar funcionário: " + e.getMessage());
        }
    }

    public List<Funcionario> select() {
        final String sql = "select nome, email from funcionario";
        List<Funcionario> funcionarios = new ArrayList<>();
        try (var stmt = this.conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                funcionarios.add(new Funcionario(rs.getString("nome"), rs.getString("email")));
            }
        } catch (Exception e) {
            System.out.println("Erro ao selecionar funcionário(s): " + e.getMessage());
        }
        return funcionarios;
    }
}