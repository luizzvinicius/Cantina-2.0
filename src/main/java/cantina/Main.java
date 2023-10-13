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
        List<String> listaOpcoes = new ArrayList<>(List.of(
            "Vender Produto", "Mostrar resumo (dia / mês)",
                "Mostrar cardápio", "Mostrar funcionários", "Mostrar produto ordenado pelo nome","Mostrar produtos com estoque baixo",
                "Cadastrar produto", "Cadastrar funcionário",
                "Alterar preço do produto", "Adicionar quantidade",
                "Excluir funcionário"
        ));
        opcoes[] metodos = new opcoes[] {
            Main::venderProduto, Main::mostraResumo, Main::mostraCardapio, Main::mostraFuncionario, Main::mostraProdutoNome, Main::mostraProdutoEmFalta, Main::cadastraProduto, Main::cadastraFuncionario, Main::alteraPreco, Main::adicionarQuantidade, Main:: excluirFuncionario
        };

        try (var conn = ConnectionFactory.getConnection(); var scan = new Entrada()) {
            for (int i = 0; i < listaOpcoes.size(); i++) {
                System.out.printf("[ %d ] %s%n", i+1, listaOpcoes.get(i));
            }
            int opt = scan.lerOption("Opção: ", 1, listaOpcoes.size(), "Opção inválida") - 1;

            metodos[opt].executar(conn, scan);
            
        } catch (SQLException e) {
            System.out.println("Conexão vazia: " + e.getMessage());
        }
    }
    
    @FunctionalInterface
    public interface opcoes {
        void executar(Connection conn, Entrada scan);
    }

    public static void venderProduto(Connection conn, Entrada scan) {
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
        produtos.forEach(p -> System.out.printf("%-10d %-20s %-10.2f%n", p.codigo(), p.nome(), p.qtdAtual()));

        Map<String, ItemVenda> carrinho = new HashMap<>();
        var continuar = "s";
        while (continuar.equalsIgnoreCase("s")) {
            var produtoSelecionado = selecionaProdutoPorCodigo(scan, produtos);
            
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
            carrinho.put(produtoSelecionado.nome(), item);
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
            System.out.printf("%-15s %-10.2f %-5s%n", produto.getKey(), itemVenda.quantidade(), itemVenda.quantidade() * itemVenda.preco());
            total += itemVenda.quantidade() * itemVenda.preco();
        }
        System.out.printf("Total da venda: %s com desconto de : %s", NumberFormat.getCurrencyInstance(brasil).format(total * venda.getDesconto()), NumberFormat.getCurrencyInstance(brasil).format(venda.getDesconto()));
        new VendaDAO(conn).updateVenda(codVenda, formaPagamento, total);
    }
    
    public static void mostraResumo(Connection conn, Entrada scan) {
        
    }
    
    public static void mostraCardapio(Connection conn, Entrada scan) {
        List<Produto> produtos = new ProdutoDAO(conn).select();
        System.out.printf("%n%-20s %-15s %-15s%n", "Nome", "Preço", "Quantidade disponível");
        produtos.forEach(p -> System.out.printf("%-20s %-15s %-15.2f%n", p.nome(), NumberFormat.getCurrencyInstance(brasil).format(p.precoVenda()), p.qtdAtual()));
    }
    
    public static void mostraFuncionario(Connection conn, Entrada scan) {
        var funcionarios = new FuncionarioDAO(conn).select();
        System.out.printf("%-4s %-25s %-30s%n", "Id", "Nome", "Email");
        funcionarios.forEach(func -> System.out.printf("%-4s %-25s %-15s%n", func.id(), func.nome(), func.email()));
    }
    
    public static void mostraProdutoNome(Connection conn, Entrada scan) {
        List<Produto> produtos = new ProdutoDAO(conn).select();
        System.out.printf("%n%-20s %-15s %-15s%n", "Nome", "Preço", "Quantidade disponível");
        produtos.stream().sorted(Comparator.comparing(Produto::nome))
            .forEach(p -> System.out.printf("%-20s %-15s %-15.2f%n", p.nome(), NumberFormat.getCurrencyInstance(brasil).format(p.precoVenda()), p.qtdAtual()));
    }
    
    public static void mostraProdutoEmFalta(Connection conn, Entrada scan) {
        List<Produto> produtos = new ProdutoDAO(conn).select();
        System.out.printf("%n%-20s %-15s%n", "Nome", "Quantidade disponível");
        produtos.stream().filter(produto -> produto.qtdAtual() <= 50)
           .forEach(produto -> System.out.printf("%-20s %-15.2f%n", produto.nome(), produto.qtdAtual()));
    }
    
    public static void cadastraProduto(Connection conn, Entrada scan) {
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
    }
    
    public static void cadastraFuncionario(Connection conn, Entrada scan) {
        int rowAffect = 0;
        while (rowAffect == 0) {
            var nome = scan.lerString("Digite o nome do funcionário: ", "Nome inválido");
            var email = scan.lerEmail("Digite o email do funcionário: ");
            var senha = scan.lerSenha("Digite a senha do funcionário: ");
            rowAffect = new FuncionarioDAO(conn).insert(new Funcionario(0, nome, email, senha));
        }
    }
    
    public static void alteraPreco(Connection conn, Entrada scan) {
        Map<Integer, String> funcionario = loginFuncionario(scan, conn);
        int idFunc = 0;
        for (int id : funcionario.keySet()) {
            idFunc = id;
        }

        List<Produto> produtos = new ProdutoDAO(conn).select();
        System.out.printf("%n%-10s %-20s %-20s %-20s %-10s%n", "Código", "Nome", "Preço de compra", "Preço de venda", "Quantidade disponível");
        produtos.forEach(p -> System.out.printf("%-10d %-20s %-20s %-10s%n", p.codigo(), p.nome(), NumberFormat.getCurrencyInstance(brasil).format(p.precoCompra()), NumberFormat.getCurrencyInstance(brasil).format(p.precoVenda()), p.qtdComprada()));

        var produto = selecionaProdutoPorCodigo(scan, produtos);
        var optPreco = scan.lerOption("Você quer alterar o [ 1 ] preço de compra ou [ 2 ] preço de venda? ", 1, 2, "Tipo de preço inválido");
        var novoPreco = scan.lerDouble("Digite o novo preço: ");
        new ProdutoDAO(conn).updatePreco(optPreco, novoPreco, produto.codigo(), idFunc);
    }
    
    public static void adicionarQuantidade(Connection conn, Entrada scan) {
        Map<Integer, String> funcionario = loginFuncionario(scan, conn);
        int idFunc = 0;
        for (int id : funcionario.keySet()) {
            idFunc = id;
        }

        List<Produto> produtos = new ProdutoDAO(conn).select();
        System.out.printf("%n%-10s %-20s %-20s%n", "Código", "Nome", "Quantidade atual");
        produtos.forEach(p -> System.out.printf("%-10d %-20s %-10.2f%n", p.codigo(), p.nome(), p.qtdAtual()));
        var produto = selecionaProdutoPorCodigo(scan, produtos);
        var quantidadeComprada = scan.lerDouble("Digite a quantidade a adicionar: ");
        new ProdutoDAO(conn).adicionaQuantidade(quantidadeComprada, produto.codigo(), idFunc);
    }
    
    public static void excluirFuncionario(Connection conn, Entrada scan) {
        var email = scan.lerEmail("Digite o email do funcionário: ");
        new FuncionarioDAO(conn).excluirFuncionario(email);
    }
    
    
    // Métodos auxiliares
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
                if (p.codigo() == codigo) {
                    produto = p;
                    break;
                }
                System.out.println("Produto não encontrado.\n");
            }
        }
        return produto;
    }
}