package cantina;

import java.time.LocalDateTime;

public class Venda {
    private int codigoVenda, idFuncionario;
    private String formaPagamento;
    private double desconto, total;
    private LocalDateTime data;
    
    public Venda(int idFuncionario, double desconto, LocalDateTime data) {
        this.idFuncionario = idFuncionario;
        this.desconto = desconto;
        this.data = data;
    }

    public int getCodigoVenda() {
        return codigoVenda;
    }

    public int getIdFuncionario() {
        return idFuncionario;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public double getDesconto() {
        return desconto;
    }

    public double getTotal() {
        return total;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}