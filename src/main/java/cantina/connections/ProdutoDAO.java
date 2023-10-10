package cantina.connections;

import java.sql.Connection;
import java.sql.SQLException;

import cantina.Produto;

public class ProdutoDAO {
    private Connection conn;

    public ProdutoDAO(Connection conn) {
        this.conn = conn;
    }

    public int insert(Produto p) {
        final String sql = "insert into produto (codigo, id_funcionario, nome, preco_compra, preco_venda, quantidade_comprada, quantidade_vendida, quantidade_atual) values (?, ?, ?, ?, ?, ?, ?, ?)";
        int rowsAffect = 0;
        try (var stmt = this.conn.prepareStatement(sql)) {
            stmt.setInt(1, p.getCodigo());
            stmt.setInt(2, p.getIdFuncionario());
            stmt.setString(3, p.getNome());
            stmt.setDouble(4, p.getPrecoCompra());
            stmt.setDouble(5, p.getPrecoVenda());
            stmt.setInt(6, p.getQtdComprada());
            stmt.setInt(7, p.getQtdVendida());
            stmt.setInt(8, p.getQtdComprada());
            rowsAffect = stmt.executeUpdate();
            System.out.println("Produto inserido.");
        } catch (SQLException e) {
            System.out.println("Não foi possível inserir o produto: " + e.getMessage() + "\n");
        }
        return rowsAffect;
    }
}