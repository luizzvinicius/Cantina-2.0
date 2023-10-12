package cantina;

import cantina.connections.*;
import cantina.validacao.Entrada;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// DriverManager, Conncection, ResultSet, Statement, PreparedStatement
public class Main {
    public static void main(String[] args) {
        Locale brasil = Locale.of("pt", "BR");
        List<String> opcoes = new ArrayList<>(List.of(
                "Mostrar cardápio", "Mostrar funcionários", "Mostrar produto ordenado pelo nome","Mostrar produtos com estoque baixo",
                "Cadastrar produto", "Cadastrar funcionário",
                "Alterar preço do produto", "Adicionar quantidade",
                "Excluir funcionário", "Vender Produto", "Mostrar resumo (dia / mês)"
        ));

        try (var conn = ConnectionFactory.getConnection(); var scan = new Entrada()) {
            for (int i = 0; i < opcoes.size(); i++) {
                System.out.printf("[ %d ] %s%n", i+1, opcoes.get(i));
            }
            int opt = scan.lerOption("Opção: ", 1, opcoes.size(), "Opção inválida") - 1;

            if (opt == 0) { // Mostra cardápio
                List<Produto> produtos = new ProdutoDAO(conn).select();
                System.out.printf("%n%-20s %-15s %-15s%n", "Nome", "Preço", "Quantidade disponível");
                produtos.forEach(p -> System.out.printf("%-20s %-15s %-15.2f%n", p.getNome(), NumberFormat.getCurrencyInstance(brasil).format(p.getPrecoVenda()), p.getQtdAtual()));
            } else if (opt == 1) { // Mostra funcionário
                var funcionarios = new FuncionarioDAO(conn).select();
                System.out.printf("%-4s %-25s %-30s%n", "Id", "Nome", "Email");
                funcionarios.forEach(func -> System.out.printf("%-4s %-25s %-15s%n", func.getId(), func.getNome(), func.getEmail()));
            } else if (opt == 2) { // Mostra produto ordenado pelo nome
                List<Produto> produtos = new ProdutoDAO(conn).select();
                System.out.printf("%n%-20s %-15s %-15s%n", "Nome", "Preço", "Quantidade disponível");
                produtos.stream().sorted(Comparator.comparing(Produto::getNome))
                    .forEach(p -> System.out.printf("%-20s %-15s %-15.2f%n", p.getNome(), NumberFormat.getCurrencyInstance(brasil).format(p.getPrecoVenda()), p.getQtdAtual()));
            } else if (opt == 3) { // Mostra Produto com estoque baixo
                List<Produto> produtos = new ProdutoDAO(conn).select();
                System.out.printf("%n%-20s %-15s%n", "Nome", "Quantidade disponível");

                produtos.stream().filter(produto -> produto.getQtdAtual() <= 50)
                .forEach(produto -> System.out.printf("%-20s %-15.2f%n", produto.getNome(), produto.getQtdAtual()));
            } else if (opt == 4) { // Cadastra produto
                Map<Integer, String> funcionarios = loginFuncionario(scan, conn);

                int idFunc = 0;
                for (int id : funcionarios.keySet()) {
                    idFunc = id;
                }

                int rowAffect = 0;
                while (rowAffect == 0) {
                    var produto = cadastraProduto(scan, idFunc);
                    rowAffect = new ProdutoDAO(conn).insert(produto);
                }
            } else if (opt == 5) { // Cadastra funcionário
                int rowAffect = 0;
                while (rowAffect == 0) {
                    var nome = scan.lerString("Digite o nome do funcionário: ", "Nome inválido");
                    var email = scan.lerEmail("Digite o email do funcionário: ");
                    var senha = scan.lerSenha("Digite a senha do funcionário: ");
                    rowAffect = new FuncionarioDAO(conn).insert(new Funcionario(0, nome, email, senha));
                }
            } else if (opt == 6) { // Alterar preço produto
                Map<Integer, String> funcionario = loginFuncionario(scan, conn);
                int idFunc = 0;
                for (int id : funcionario.keySet()) {
                    idFunc = id;
                }

                List<Produto> produtos = new ProdutoDAO(conn).select();
                System.out.printf("%n%-10s %-20s %-20s %-20s %-10s%n", "Código", "Nome", "Preço de compra", "Preço de venda", "Quantidade disponível");
                produtos.forEach(p -> System.out.printf("%-10d %-20s %-20s %-10s%n", p.getCodigo(), p.getNome(), NumberFormat.getCurrencyInstance(brasil).format(p.getPrecoCompra()), NumberFormat.getCurrencyInstance(brasil).format(p.getPrecoVenda()), p.getQtdComprada()));

                var produto = selecionaProdutoPorCodigo(scan, produtos);
                var optPreco = scan.lerOption("Você quer alterar o [ 1 ] preço de compra ou [ 2 ] preço de venda? ", 1, 2, "Tipo de preço inválido");
                var novoPreco = scan.lerDouble("Digite o novo preço: ");
                new ProdutoDAO(conn).updatePreco(optPreco, novoPreco, produto.getCodigo(), idFunc);

            } else if (opt == 7) {
                Map<Integer, String> funcionario = loginFuncionario(scan, conn);
                int idFunc = 0;
                for (int id : funcionario.keySet()) {
                    idFunc = id;
                }

                List<Produto> produtos = new ProdutoDAO(conn).select();
                System.out.printf("%n%-10s %-20s %-20s%n", "Código", "Nome", "Quantidade atual");
                produtos.forEach(p -> System.out.printf("%-10d %-20s %-10.2f%n", p.getCodigo(), p.getNome(), p.getQtdAtual()));
                var produto = selecionaProdutoPorCodigo(scan, produtos);
                var quantidadeComprada = scan.lerDouble("Digite a quantidade a adicionar: ");
                new ProdutoDAO(conn).adicionaQuantidade(quantidadeComprada, produto.getCodigo(), idFunc);

            } else if (opt == 8) {
                var email = scan.lerEmail("Digite o email do funcionário: ");
                new FuncionarioDAO(conn).excluirFuncionario(email);
            } else if (opt == 9) { // Venda
                Map<Integer, String> funcionario = loginFuncionario(scan, conn);
                int idFunc = 0;
                for (int id : funcionario.keySet()) {
                    idFunc = id;
                }

                var data = LocalDateTime.now();
                var venda = new Venda(idFunc, new Random().nextDouble(0, 11), data);
                var codVenda = new VendaDAO(conn).insert(venda);
                
                List<Produto> produtos = new ProdutoDAO(conn).select();
                if(produtos.size() == 0) {
                    System.out.println("Nenhum produdo disponível");
                    return;
                }
                System.out.printf("%n%-10s %-20s %-20s%n", "Código", "Nome", "Quantidade disponível");
                produtos.forEach(p -> System.out.printf("%-10d %-20s %-10.2f%n", p.getCodigo(), p.getNome(), p.getQtdAtual()));

                Map<String, ItemVenda> carrinho = new HashMap<>();
                var continuar = "s";
                while (continuar.equalsIgnoreCase("s")) {
                    var produtoSelecionado = selecionaProdutoPorCodigo(scan, produtos);
                    
                    int quantidade = 0;
                    while (true) {
                        quantidade = scan.lerInt("Digite a quantidade: ");
                        if (quantidade <= produtoSelecionado.getQtdAtual()) {
                            break;
                        }
                        System.out.println("Quantidade maior que a disponível.");
                    }
                    new ProdutoDAO(conn).updateQuantidadePosVenda(produtoSelecionado.getCodigo(), produtoSelecionado.getQtdVendida() + quantidade, produtoSelecionado.getQtdAtual() - quantidade, idFunc);

                    var item = new ItemVenda(0, codVenda, produtoSelecionado.getCodigo(), quantidade, produtoSelecionado.getPrecoVenda());
                    
                    int rowAffect = new ItemVendaDAO(conn).insert(item, codVenda);
                    if (rowAffect == 0) {continue;}
                    carrinho.put(produtoSelecionado.getNome(), item);
                    while (true) {
                        continuar = scan.lerString("Quer continuar [S/N]? ", "Opção inválida.");
                        if (!continuar.equalsIgnoreCase("s") && !continuar.equalsIgnoreCase("n")) {
                            System.out.println("Digite apenas [s/n].\n");
                            continue;
                        }
                        break;
                    }
                }
                final String[] formasPagamento = new String[] {"Cartão de crédito", "Cartão de débito", "Pix"};
                for (var i = 0; i < formasPagamento.length; i++) {
                    System.out.printf("[ %d ] %s%n", i+1, formasPagamento[i]);
                }
                var optPagamento = scan.lerOption("Qual a forma de pagamento? ", 1, formasPagamento.length, "Forma pagemento inválida.") - 1;
                var formaPagamento = formasPagamento[optPagamento];
                double total = 0;
                System.out.println("\n-------------- Resumo venda --------------");
                System.out.println("Vendedor: " + funcionario.get(idFunc));
                System.out.printf("%-15s %-10s %-5s%n", "Nome", "Quantidade", "Preço");
                for (Map.Entry<String, ItemVenda> produto : carrinho.entrySet()) {
                    var itemVenda = produto.getValue();
                    System.out.printf("%-15s %-10.2f %-5s%n", produto.getKey(), itemVenda.getQuantidade(), itemVenda.getQuantidade() * itemVenda.getPreco());
                    total += itemVenda.getQuantidade() * itemVenda.getPreco();
                }
                System.out.printf("Total da venda: %s com desconto de : %s", NumberFormat.getCurrencyInstance(brasil).format(total * venda.getDesconto()), NumberFormat.getCurrencyInstance(brasil).format(venda.getDesconto()));
                new VendaDAO(conn).updateVenda(codVenda, formaPagamento, total);
            }
        } catch (SQLException e) {
            System.out.println("Conexão vazia: " + e.getMessage());
        }
    }

    private static Produto cadastraProduto(Entrada scan, int idFunc) {
        var nome = scan.lerString("Digite o nome do produto: ", "Nome inválido");
        double precoCompra = 0;
        while (precoCompra <= 0) {
            precoCompra = scan.lerDouble("Digite o preço de compra: ");
            if (precoCompra <= 0) {
                System.out.println("Preço de compra inválido\n");
                continue;
            }
        }
        double precoVenda = 0;
        while (precoVenda <= precoCompra) {
            precoVenda = scan.lerDouble("Digite o preço de venda: ");
            if (precoVenda <= precoCompra) {
                System.out.println("Preço de venda não pode ser menor ou igual ao preço de compra\n");
                continue;
            }
        }
        double qtdComprada = 0;
        while (qtdComprada <= 0) {
            qtdComprada = scan.lerDouble("Digite a quantidade comprada: ");
            if (qtdComprada <= 0) {
                System.out.println("Quantidade comprada inválida\n");
                continue;
            }
        }
        return new Produto(0, idFunc, nome, precoCompra, precoVenda, qtdComprada, 0, qtdComprada);
    }

    private static Map<Integer, String> loginFuncionario(Entrada scan, Connection conn) {
        Map<Integer, String> funcionarios;
        do {
            var email = scan.lerEmail("Digite seu email: ");
            var senha = scan.lerSenha("Digite sua senha: ");
            funcionarios = new FuncionarioDAO(conn).buscaFuncionario(email, senha);
            if (funcionarios.size() == 0) {
                System.out.println("Login incorreto ou duplicado\n");
            }
        } while (funcionarios.size() != 1);
        System.out.println("Login realizado");
        return funcionarios;
    }

    private static Produto selecionaProdutoPorCodigo(Entrada scan, List<Produto> produtos) {
        Produto produto = null;
        while (produto == null) { // Garantido que não vai ser nulo
            var codigo = scan.lerInt("Digite o código do produto: ");
            for (Produto p : produtos) {
                if (p.getCodigo() == codigo) {
                    produto = p;
                    break;
                }
                System.out.println("Produto não encontrado.\n");
            }
        }
        return produto;
    }
}