package com.stackberry.login;

public class Usuario {
    private String id;
    private String email;
    private String nome;
    private String senha;
    private TipoDeUsuario tipoDeUsuario;

    public Usuario(String email, String nome, String senha){
        this.id = java.util.UUID.randomUUID().toString(); // TODO: Troca lógica, fazer o ID ser gerado pelo repository
        this.email = email;
        this.nome = nome;
        this.senha = senha;
        tipoDeUsuario = TipoDeUsuario.CLIENTE;
    }
    // Construtor vazio
    public Usuario(){}

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public String getSenha() {
        return senha;
    }

    public TipoDeUsuario getTipoDeUsuario() {
        return tipoDeUsuario;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setTipoDeUsuario(TipoDeUsuario tipoDeUsuario) {
        this.tipoDeUsuario = tipoDeUsuario;
    }


}
