package cantina.connections;

import java.sql.Connection;
import java.sql.SQLException;

import cantina.ItemVenda;

public class ItemVendaDAO {
    private Connection conn;

    public ItemVendaDAO(Connection conn) {
        this.conn = conn;
    }

    public int insert(ItemVenda item, int codVenda) {
        final String sql = "insert into item_venda (codigo_item, codigo_produto, codigo_venda, preco_venda, quantidade) values (?,?,?,?,?)";
        int rowsAffect = 0;
        try (var stmt = this.conn.prepareStatement(sql)) {
            stmt.setInt(1, item.codigoItem());
            stmt.setInt(2, item.codigoProduto());
            stmt.setInt(3, codVenda);
            stmt.setDouble(4, item.preco());
            stmt.setDouble(5, item.quantidade());
            rowsAffect = stmt.executeUpdate();
            System.out.println(rowsAffect + " item da venda inserido");
        } catch (SQLException e) {
            System.out.println("Não foi possível inserir o item: " + e.getMessage() + "\n");
        }
        return rowsAffect;
    }
}