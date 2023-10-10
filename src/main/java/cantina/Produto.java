package cantina;

public class Produto {
    String nome;
    int codigo, qtdComprada, qtdVendida, qtdAtual, idFuncionario;
    double precoCompra, precoVenda;
    
    public Produto(String nome, int qtdComprada, double precoCompra, double precoVenda, int idFuncionario) {
        this.nome = nome;
        this.qtdComprada = qtdComprada;
        this.precoCompra = precoCompra;
        this.precoVenda = precoVenda;
        this.idFuncionario = idFuncionario;
    }

    public String getNome() {
        return nome;
    }

    public int getCodigo() {
        return codigo;
    }

    public int getQtdComprada() {
        return qtdComprada;
    }

    public int getQtdVendida() {
        return qtdVendida;
    }

    public int getQtdAtual() {
        return qtdAtual;
    }

    public double getPrecoCompra() {
        return precoCompra;
    }

    public double getPrecoVenda() {
        return precoVenda;
    }
    
    public int getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(int idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public void setQtdVendida(int qtdVendida) {
        this.qtdVendida = qtdVendida;
    }

    public void setQtdAtual(int qtdAtual) {
        this.qtdAtual = qtdAtual;
    }

    public void setPrecoCompra(double precoCompra) {
        this.precoCompra = precoCompra;
    }

    public void setPrecoVenda(double precoVenda) {
        this.precoVenda = precoVenda;
    }
}