package cantina;

import cantina.connections.*;
import cantina.validacao.Entrada;
import java.util.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;

public class Main {
    static Locale brasil = Locale.of("pt", "BR");
    public static void main(String[] args) {
        var listaOpcoes = new String[] {
            "Vender Produto", "Mostrar cardápio", "Mostrar funcionários", 
            "Mostrar produto ordenado pelo nome", "Mostrar produtos com estoque baixo", "Cadastrar produto", 
            "Cadastrar funcionário", "Alterar preço do produto", "Adicionar quantidade a um produto", "Excluir funcionário", "Sair"
        };
        var metodos = new metodos[] {
            Main::venderProduto, Main::mostrarCardapio, Main::mostrarFuncionario, Main::mostrarProdutoNome, Main::mostrarProdutoEmFalta, Main::cadastrarProduto, Main::cadastrarFuncionario, Main::alterarPreco, Main::adicionarQuantidade, Main::excluirFuncionario, Main::sair
        };

        try (var conn = ConnectionFactory.getConnection(); var scan = new Entrada()) {
            for (int i = 0; i < listaOpcoes.length; i++) {
                System.out.printf("[ %d ] %s%n", i+1, listaOpcoes[i]);
            }

            var opt = 0;
            while (opt != listaOpcoes.length - 1) {
                opt = scan.lerOption("Opção: ", 1, listaOpcoes.length, "Opção inválida") - 1;
                metodos[opt].accept(conn, scan);
            }
        } catch (SQLException e) {
            System.out.println("Conexão vazia: " + e.getMessage());
        }
    }

    @FunctionalInterface
    public interface metodos {
        void accept(Connection conn, Entrada scan);
    }

    public static void venderProduto(Connection conn, Entrada scan) {
        var funcionario = loginFuncionario(scan, conn);
        var idFunc = funcionario.id();
        var venda = new Venda(0, idFunc, new Random().nextDouble(0, 11) / 100, null, 0, LocalDateTime.now());
        var codVenda = new VendaDAO(conn).insert(venda);
        
        List<Produto> produtos = new ProdutoDAO(conn).select();
        if (produtos.size() == 0) {
            System.out.println("Nenhum produdo disponível");
            return;
        }
        System.out.printf("%-10s %-20s %-10s %-10s%n", "Código", "Nome", "Preço", "Quantidade disponível");
        produtos.forEach(p -> System.out.printf("%-10d %-20s %-20s %-10.2f%n", p.codigo(), p.nome(), NumberFormat.getCurrencyInstance(brasil).format(p.precoVenda()), p.qtdAtual()));

        List<Map.Entry<String, ItemVenda>> carrinho = new ArrayList<>();
        var continuar = "s";
        while (continuar.equalsIgnoreCase("s")) {
            List<Produto> produtos2 = new ProdutoDAO(conn).select();
            var produtoSelecionado = selecionarProdutoPorCodigo(scan, produtos2);

            int quantidade = 0;
            while (true) {
                quantidade = scan.lerInt("Digite a quantidade: ");
                if (quantidade <= produtoSelecionado.qtdAtual()) {
                    break;
                }
                System.out.println("Quantidade maior que a disponível.");
            }
            new ProdutoDAO(conn).updateQuantidadePosVenda(produtoSelecionado.codigo(), produtoSelecionado.qtdVendida() + quantidade, produtoSelecionado.qtdAtual() - quantidade, idFunc);

            var item = new ItemVenda(0, codVenda, produtoSelecionado.codigo(), quantidade, produtoSelecionado.precoVenda());
            
            int rowAffect = new ItemVendaDAO(conn).insert(item, codVenda);
            if (rowAffect == 0) {continue;}
            carrinho.add(new AbstractMap.SimpleEntry<String, ItemVenda>(produtoSelecionado.nome(), item));
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
        var total = 0;
        System.out.println("\n-------------- Resumo venda --------------");
        System.out.println("Vendedor: " + funcionario.nome());
        System.out.printf("%-15s %-10s %-5s%n", "Nome", "Quantidade", "Preço");
        for (Map.Entry<String, ItemVenda> produto : carrinho) {
            var itemVenda = produto.getValue();
            System.out.printf("%-15s %-10.2f %-5s%n", produto.getKey(), itemVenda.quantidade(), itemVenda.quantidade() * itemVenda.preco());
            total += itemVenda.quantidade() * itemVenda.preco();
        }
        System.out.printf("Total da venda: %s com desconto de : %s%n", NumberFormat.getCurrencyInstance(brasil).format(total - (total * venda.desconto())), NumberFormat.getCurrencyInstance(brasil).format(total * venda.desconto()));
        new VendaDAO(conn).updateVenda(codVenda, formaPagamento, total);
        total = 0;
    }

    // public static void mostrarResumo(Connection conn, Entrada scan) {
    // }

    public static void mostrarCardapio(Connection conn, Entrada scan) {
        List<Produto> produtos = new ProdutoDAO(conn).select();
        System.out.printf("%n%-20s %-15s %-15s%n", "Nome", "Preço", "Quantidade disponível");
        produtos.forEach(p -> System.out.printf("%-20s %-15s %-15.2f%n", p.nome(), NumberFormat.getCurrencyInstance(brasil).format(p.precoVenda()), p.qtdAtual()));
    }
    
    public static void mostrarFuncionario(Connection conn, Entrada scan) {
        var funcionarios = new FuncionarioDAO(conn).select();
        System.out.printf("%-4s %-25s %-30s%n", "Id", "Nome", "Email");
        funcionarios.forEach(func -> System.out.printf("%-4s %-25s %-15s%n", func.id(), func.nome(), func.email()));
    }
    
    public static void mostrarProdutoNome(Connection conn, Entrada scan) {
        List<Produto> produtos = new ProdutoDAO(conn).select();
        System.out.printf("%n%-20s %-15s %-15s%n", "Nome", "Preço", "Quantidade disponível");
        produtos.stream().sorted(Comparator.comparing(Produto::nome))
            .forEach(p -> System.out.printf("%-20s %-15s %-15.2f%n", p.nome(), NumberFormat.getCurrencyInstance(brasil).format(p.precoVenda()), p.qtdAtual()));
    }
    
    public static void mostrarProdutoEmFalta(Connection conn, Entrada scan) {
        List<Produto> produtos = new ProdutoDAO(conn).select();
        System.out.printf("%n%-20s %-15s%n", "Nome", "Quantidade disponível");
        produtos.stream().filter(produto -> produto.qtdAtual() <= 50)
           .forEach(produto -> System.out.printf("%-20s %-15.2f%n", produto.nome(), produto.qtdAtual()));
    }
    
    public static void cadastrarProduto(Connection conn, Entrada scan) {
        var funcionario = loginFuncionario(scan, conn);
        var idFunc = funcionario.id();

        int rowAffect = 0;
        while (rowAffect == 0) {
            var produto = criarProduto(scan, idFunc);
            rowAffect = new ProdutoDAO(conn).insert(produto);
        }
    }
    
    public static void cadastrarFuncionario(Connection conn, Entrada scan) {
        int rowAffect = 0;
        while (rowAffect == 0) {
            var nome = scan.lerString("Digite o nome do funcionário: ", "Nome inválido");
            var email = scan.lerEmail("Digite o email do funcionário: ");
            var senha = scan.lerSenha("Digite a senha do funcionário: ");
            rowAffect = new FuncionarioDAO(conn).insert(new Funcionario(0, nome, email, senha));
        }
    }

    public static void alterarPreco(Connection conn, Entrada scan) {
        var funcionario = loginFuncionario(scan, conn);
        var idFunc = funcionario.id();

        List<Produto> produtos = new ProdutoDAO(conn).select();
        System.out.printf("%n%-10s %-20s %-20s %-20s %-10s%n", "Código", "Nome", "Preço de compra", "Preço de venda", "Quantidade disponível");
        produtos.forEach(p -> System.out.printf("%-10d %-20s %-20s %-10s%n", p.codigo(), p.nome(), NumberFormat.getCurrencyInstance(brasil).format(p.precoCompra()), NumberFormat.getCurrencyInstance(brasil).format(p.precoVenda()), p.qtdComprada()));

        var produto = selecionarProdutoPorCodigo(scan, produtos);
        var optPreco = scan.lerOption("Você quer alterar o [ 1 ] preço de compra ou [ 2 ] preço de venda? ", 1, 2, "Tipo de preço inválido");
        var novoPreco = scan.lerDouble("Digite o novo preço: ");
        new ProdutoDAO(conn).updatePreco(optPreco, novoPreco, produto.codigo(), idFunc);
    }
    
    public static void adicionarQuantidade(Connection conn, Entrada scan) {
        var funcionario = loginFuncionario(scan, conn);
        var idFunc = funcionario.id();

        List<Produto> produtos = new ProdutoDAO(conn).select();
        System.out.printf("%-10s %-20s %-20s%n", "Código", "Nome", "Quantidade atual");
        produtos.forEach(p -> System.out.printf("%-10d %-20s %-10.2f%n", p.codigo(), p.nome(), p.qtdAtual()));
        var produto = selecionarProdutoPorCodigo(scan, produtos);
        var quantidadeComprada = 0d;
        while (quantidadeComprada == 0) {
            quantidadeComprada = scan.lerDouble("Digite a quantidade a adicionar: ");
            if (quantidadeComprada <= 0) {
                System.out.println("Quantidade inválida.\n");
            }
        }
        new ProdutoDAO(conn).adicionaQuantidade(produto, quantidadeComprada, idFunc);
    }
    
    public static void excluirFuncionario(Connection conn, Entrada scan) {
        var email = scan.lerEmail("Digite o email do funcionário: ");
        new FuncionarioDAO(conn).excluirFuncionario(email);
    }
    
    public static void sair(Connection conn, Entrada scan) {
        System.out.println("Cantina encerrada.");
        System.exit(1);
    }

    // Métodos auxiliares
    private static Produto criarProduto(Entrada scan, int idFunc) {
        var nome = scan.lerString("Digite o nome do produto: ", "Nome inválido");
        var precoCompra = 0d;
        while (precoCompra <= 0) {
            precoCompra = scan.lerDouble("Digite o preço de compra: ");
            if (precoCompra <= 0) {
                System.out.println("Preço de compra inválido\n");
                continue;
            }
        }
        var precoVenda = 0d;
        while (precoVenda <= precoCompra) {
            precoVenda = scan.lerDouble("Digite o preço de venda: ");
            if (precoVenda <= precoCompra) {
                System.out.println("Preço de venda não pode ser menor ou igual ao preço de compra\n");
                continue;
            }
        }
        var qtdComprada = 0d;
        while (qtdComprada <= 0) {
            qtdComprada = scan.lerDouble("Digite a quantidade comprada: ");
            if (qtdComprada <= 0) {
                System.out.println("Quantidade inválida\n");
                continue;
            }
        }
        return new Produto(0, idFunc, nome, precoCompra, precoVenda, qtdComprada, 0, qtdComprada);
    }

    private static Funcionario loginFuncionario(Entrada scan, Connection conn) {
        Funcionario funcionario;
        do {
            var email = scan.lerEmail("Digite seu email: ");
            var senha = scan.lerSenha("Digite sua senha: ");
            funcionario = new FuncionarioDAO(conn).buscaFuncionario(email, senha);
            if (funcionario.id() == 0) {
                System.out.println("Login incorreto ou duplicado\n");
            }
        } while (funcionario.id() == 0);
        System.out.println("Login realizado\n");
        return funcionario;
    }

    private static Produto selecionarProdutoPorCodigo(Entrada scan, List<Produto> produtos) {
        Produto produto = null;
        while (produto == null) { // Garantido que não vai ser nulo
            var codigo = scan.lerInt("Digite o código do produto: ");
            for (Produto p : produtos) {
                if (p.codigo() == codigo) {
                    return p;
                }
            }
            System.out.println("Produto não encontrado.\n");
        }
        return produto;
    }
}