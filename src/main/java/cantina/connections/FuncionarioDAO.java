package cantina.connections;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

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
            stmt.setString(1, func.getNome());
            stmt.setString(2, func.getEmail());
            stmt.setString(3, func.getSenha());
            rows = stmt.executeUpdate();
            System.out.println("Funcionário inserido.");
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar funcionário: " + e.getMessage() + "\n");
        }
        return rows;
    }

    public List<Funcionario> select() {
        final String sql = "select id, nome, email from funcionario where email is not null";
        List<Funcionario> funcionarios = new ArrayList<>();
        try (var stmt = this.conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                funcionarios.add(new Funcionario(rs.getInt("id"), rs.getString("nome"), rs.getString("email"), null));
            }
        } catch (Exception e) {
            System.out.println("Erro ao selecionar funcionário(s): " + e.getMessage());
        }
        return funcionarios;
    }

    public Map<Integer, String> buscaFuncionario(String email, String senha) {
        final String sql = "select id, nome from funcionario where email = ? and senha = ?";
        Map<Integer, String> funcionarios = new HashMap<>();
        try (var stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                funcionarios.put(rs.getInt("id"), rs.getString("nome"));
            }
        } catch (Exception e) {
            System.out.println("Erro ao selecionar funcionário(s): " + e.getMessage());
        }
        return funcionarios;
    }

    public void excluirFuncionario(String email) {
        final String sql = "update funcionario set email = null where email = ?";
        int rowsAffect = 0;
        try (var stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            rowsAffect = stmt.executeUpdate();
            System.out.println(rowsAffect + " funcionário excluído.");
        } catch (Exception e) {
            System.out.println("Erro ao excluir funcionário: " + e.getMessage() + "\n");
        }
    }
}