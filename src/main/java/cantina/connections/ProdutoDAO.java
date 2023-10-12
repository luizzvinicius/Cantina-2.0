package cantina.connections;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

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
            stmt.setDouble(6, p.getQtdComprada());
            stmt.setDouble(7, p.getQtdVendida());
            stmt.setDouble(8, p.getQtdComprada());
            rowsAffect = stmt.executeUpdate();
            System.out.println(rowsAffect + " Produto inserido.");
        } catch (SQLException e) {
            System.out.println("Não foi possível inserir o produto: " + e.getMessage() + "\n");
        }
        return rowsAffect;
    }

    public List<Produto> select() {
        final String sql = "select * from produto";
        List<Produto> produtos = new ArrayList<>();
        try (var stmt = this.conn.prepareStatement(sql); var rs = stmt.executeQuery()) {
            while (rs.next()) {
                produtos.add(new Produto(
                    rs.getInt("codigo"),
                    rs.getInt("id_funcionario"),
                    rs.getString("nome"), 
                    rs.getDouble("preco_compra"),
                    rs.getDouble("preco_venda"),
                    rs.getDouble("quantidade_comprada"),
                    rs.getDouble("quantidade_vendida"),
                    rs.getDouble("quantidade_atual")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Não foi possível selecionar produto(s): " + e.getMessage() + "\n");
        }
        return produtos;
    }

    public void updatePreco(int optPreco, double novoPreco, int codigoProduto, int idFunc) {
        final String sql = optPreco == 1 ? 
        "update produto set preco_compra = ? where codigo = ?" : 
        "update produto set preco_venda = ? where codigo = ?";
        final String updateFK = "update produto set id_funcionario = ? where codigo = ?";
        int rowsAffect = 0;
        try (var stmt = this.conn.prepareStatement(sql); var stmt2 = this.conn.prepareStatement(updateFK)) {
            stmt2.setInt(1, idFunc);
            stmt2.setInt(2, codigoProduto);
            stmt2.executeUpdate();
            stmt.setDouble(1, novoPreco);
            stmt.setInt(2, codigoProduto);
            rowsAffect = stmt.executeUpdate();
            System.out.println(rowsAffect + " preço(s) alterado(s).");
        } catch (SQLException e) {
            System.out.println("Não foi possível alterar o preço: " + e.getMessage() + "\n");
        }
    }

    public void adicionaQuantidade(double quantidade, int codigoProduto, int idFunc) {
        final String sql = "update produto set quantidade_comprada = ? where codigo = ?";
        final String updateFK = "update produto set id_funcionario = ? where codigo = ?";
        try (var stmt = this.conn.prepareStatement(sql); var stmt2 = this.conn.prepareStatement(updateFK)) {
            stmt.setInt(1, codigoProduto);
            stmt.setDouble(2, quantidade);
            stmt.executeUpdate();
            stmt2.setInt(1, idFunc);
            stmt2.setInt(2, codigoProduto);
            stmt2.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Não foi possível adicionar quantidade: " + e.getMessage() + "\n");
        }
    }

    public void updateQuantidadePosVenda(int codProduto, double qtdVendida, double qtdAtual, int idFunc) {
        final String sql = "update produto set quantidade_vendida = ?, quantidade_atual = ?, id_funcionario = ? where codigo = ?";
        try (var stmt = this.conn.prepareStatement(sql)) {
            stmt.setDouble(1, qtdVendida);
            stmt.setDouble(2, qtdAtual);
            stmt.setInt(3, idFunc);
            stmt.setInt(4, codProduto);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Não foi possível atualizar a quantidade: " + e.getMessage() + "\n");
        }
    }
}