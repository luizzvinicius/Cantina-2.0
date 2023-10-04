package cantina;

public class Funcionario {
    private final String nome, email, senha;
    
    public Funcionario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public Funcionario(String nome, String email) {
        this.nome = nome;
        this.email = email;
        this.senha = null;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(nome);
        return builder.toString();
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