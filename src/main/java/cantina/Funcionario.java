package cantina;

public class Funcionario {
    private String nome, email;
    private int id, senha;

    public Funcionario(int id, String nome, String email, int senha) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
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

    public int getSenha() {
        return senha;
    }
}