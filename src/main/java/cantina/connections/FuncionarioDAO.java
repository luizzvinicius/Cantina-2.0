package cantina.connections;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import cantina.Funcionario;

public class FuncionarioDAO {
    private Connection conn;

    public FuncionarioDAO(Connection conn) {
        this.conn = conn;
    }

    public int insert(Funcionario func) {
        final String sql = "insert into funcionario (nome, email, senha) values (?, ?, ?)";
        int rows = 0;
        try (var stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, func.nome());
            stmt.setString(2, func.email());
            stmt.setString(3, func.senha());
            rows = stmt.executeUpdate();
            System.out.println("Funcionário inserido.");
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar funcionário: " + e.getMessage() + "\n");
        }
        return rows;
    }

    public List<Funcionario> select() {
        final String sql = "select id, nome, email from funcionario where email is not null";
        List<Funcionario> funcionarios = new ArrayList<>();
        try (var stmt = this.conn.prepareStatement(sql)) {
            var rs = stmt.executeQuery();
            while (rs.next()) {
                funcionarios.add(new Funcionario(rs.getInt("id"), rs.getString("nome"), rs.getString("email"), null));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao selecionar funcionário(s): " + e.getMessage());
        }
        return funcionarios;
    }

    public Funcionario buscaFuncionario(String email, String senha) {
        final String sql = "select id, nome from funcionario where email = ? and senha = ?";
        var id = 0;
        var nome = "";
        try (var stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, senha);
            var rs = stmt.executeQuery();
            rs.next();
            id = rs.getInt("id");
            nome = rs.getString("nome");
        } catch (SQLException e) {
            System.out.println("Erro ao selecionar funcionário(s): " + e.getMessage());
        }
        return new Funcionario(id, nome, null, null);
    }

    public void excluirFuncionario(String email) {
        final String sql = "update funcionario set email = null, senha = null where email = ?";
        int rowsAffect = 0;
        try (var stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            rowsAffect = stmt.executeUpdate();
            System.out.println(rowsAffect + " funcionário excluído.");
        } catch (SQLException e) {
            System.out.println("Erro ao excluir funcionário: " + e.getMessage() + "\n");
        }
    }
}