package cantina;

public record Produto(int codigo, int idFuncionario, String nome, double precoCompra, double precoVenda, double qtdComprada, double qtdVendida, double qtdAtual) { }