package cantina;

import java.time.LocalDateTime;

public record Venda(int codigoVenda, int idFuncionario, double desconto, String formaPagamento, double total, LocalDateTime data) { }