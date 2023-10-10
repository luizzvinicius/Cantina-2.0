package cantina;

import cantina.connections.*;
import cantina.validacao.Entrada;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// DriverManager, Conncection, ResultSet, Statement, PreparedStatement
public class Main {
    public static void main(String[] args) {
        List<String> opcoes = new ArrayList<>(List.of(
                "Mostrar cardápio", "Mostrar funcionários", "Mostrar produtos com estoque baixo",
                "Cadastrar produto", "Cadastrar funcionário",
                "Alterar nome produto",
                "Excluir produto", "Excluir funcionário"));
        
        try (var conn = ConnectionFactory.getConnection(); var scan = new Entrada()) {
            for (int i = 0; i < opcoes.size(); i++) {
                System.out.printf("[ %d ] %s%n", i + 1, opcoes.get(i));
            }

            int opt = scan.lerOption("Opção: ", 1, opcoes.size(), "Opção inválida") - 1;
            if (opt == 0) {

            } else if (opt == 1) { // Mostra funcionário
                var funcionarios = new FuncionarioDAO(conn).select();
                System.out.printf("%-4s %-25s %-30s%n", "Id", "Nome", "Email");
                funcionarios.forEach(
                        func -> System.out.printf("%-4s %-25s %-15s%n", func.getId(), func.getNome(), func.getEmail()));
            } else if (opt == 3) { // Cadastra produto
                Map<Integer, String> funcionarios;
                do {
                    var email = scan.lerEmail("Digite seu email: ");
                    var senha = scan.lerSenha("Digite sua senha: ");
                    funcionarios = new FuncionarioDAO(conn).buscaFuncionario(email, senha);
                    if (funcionarios.size() == 0) {
                        System.out.println("Login incorreto ou duplicado\n");
                    }
                } while (funcionarios.size() != 1);

                int idFunc = 0;
                for (int id : funcionarios.keySet()) {
                    idFunc = id;
                }

                int rowAffect;
                do {
                    var produto = cadastraProduto(scan, idFunc);
                    rowAffect = new ProdutoDAO(conn).insert(produto);
                } while (rowAffect == 0);

            } else if (opt == 4) { // Cadastra funcionário
                int rowAffect;
                do {
                    var funcionario = cadastraFuncionario(scan);
                    rowAffect = new FuncionarioDAO(conn).insert(funcionario);
                } while (rowAffect == 0);
            }
        } catch (SQLException e) {
            System.out.println("Conexão vazia: " + e.getMessage());
        }
    }

    private static Funcionario cadastraFuncionario(Entrada scan) {
        var nome = scan.lerString("Digite o nome do funcionário: ", "Nome inválido");
        var email = scan.lerEmail("Digite o email do funcionário: ");
        var senha = scan.lerSenha("Digite a senha do funcionário: ");
        return new Funcionario(0, nome, email, senha);
    }

    private static Produto cadastraProduto(Entrada scan, int idFunc) {
        while (true) {
            var nome = scan.lerString("Digite o nome do produto: ", "Nome inválido");
            var precoCompra = scan.lerDouble("Digite o preço de compra: ");
            var precoVenda = scan.lerDouble("Digite o preço de venda: ");
            if (precoVenda <= precoCompra) {
                System.out.println("Preço de venda não pode ser menor que o preço de compra\n");
                continue;
            }
            var qtdComprada = scan.lerInt("Digite a quantidade comprada: ");

            return new Produto(nome, qtdComprada, precoCompra, precoVenda, idFunc);
        }
    }
}