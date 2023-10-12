package cantina;

public class Produto {
    String nome;
    int codigo, idFuncionario;
    double precoCompra, precoVenda, qtdComprada, qtdVendida, qtdAtual;

    public Produto(int codigo, int idFuncionario, String nome, double precoCompra, double precoVenda, double qtdComprada, double qtdVendida, double qtdAtual) {
        this.codigo = codigo;
        this.idFuncionario = idFuncionario;
        this.nome = nome;
        this.precoCompra = precoCompra;
        this.precoVenda = precoVenda;
        this.qtdComprada = qtdComprada;
        this.qtdVendida = qtdVendida;
        this.qtdAtual = qtdAtual;
    }

    public String getNome() {
        return nome;
    }

    public int getCodigo() {
        return codigo;
    }

    public double getQtdComprada() {
        return qtdComprada;
    }

    public double getQtdVendida() {
        return qtdVendida;
    }

    public double getQtdAtual() {
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

    public void setQtdVendida(double qtdVendida) {
        this.qtdVendida = qtdVendida;
    }

    public void setQtdAtual(double qtdAtual) {
        this.qtdAtual = qtdAtual;
    }

    public void setPrecoCompra(double precoCompra) {
        this.precoCompra = precoCompra;
    }

    public void setPrecoVenda(double precoVenda) {
        this.precoVenda = precoVenda;
    }
}