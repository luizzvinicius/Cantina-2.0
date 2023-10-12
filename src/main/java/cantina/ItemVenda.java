package cantina;

public class ItemVenda {
    private int codigoItem, codigoVenda, codigoProduto;
    private double quantidade, preco;

    public ItemVenda(int codigoItem, int codigoVenda, int codigoProduto, double quantidade, double preco) {
        this.codigoVenda = codigoVenda;
        this.codigoItem = codigoItem;
        this.codigoProduto = codigoProduto;
        this.quantidade = quantidade;
        this.preco = preco;
    }

    public int getCodigoVenda() {
        return codigoVenda;
    }

    public int getCodigoItem() {
        return codigoItem;
    }

    public int getCodigoProduto() {
        return codigoProduto;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public double getPreco() {
        return preco;
    }
}