package cantina;

public class Funcionario {
    private final String nome, email, senha;
    private int id;
    private static int geraID = 1;

    public Funcionario(String nome, String email, String senha) {
        this.id = geraID++;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public Funcionario(int id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(nome);
        return builder.toString();
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }
}