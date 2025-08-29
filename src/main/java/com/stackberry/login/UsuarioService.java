package com.stackberry.login;

import org.mindrot.jbcrypt.BCrypt;

public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService() {
        this.usuarioRepository = new UsuarioRepository();
    }

    public Usuario cadastrarUsuario(String email, String nome, String senha){
        // Verificando se campos não são nulos
        if(email == null || nome == null || senha == null) {
            throw new IllegalArgumentException("Nome, email e senha são obrigatorios."); // TODO: Usar ValidacaoException
        }

        // Verificando se o email já está sendo usado por outra pessoa.
        if (usuarioRepository.buscarPorEmail(email).isPresent()){
            throw new IllegalArgumentException("O email informado já está em uso."); // TODO: Usar ValidacaoException
        }

        // Criptografando a senha
        String senhaCriptografada = BCrypt.hashpw(senha, BCrypt.gensalt());

        Usuario novoUsuario = new Usuario(email, nome, senhaCriptografada);
        return usuarioRepository.salvar(novoUsuario);
    }

    // Autenticaçao de usuario --- anna
    public Usuario autenticaUsuario(String email, String senha) {
        if (email == null || senha == null) {
            throw new IllegalArgumentException("E-mail e senha são dados obrigatorios."); // TODO: Usar ValidacaoException
        }
        var usuarioOptional = usuarioRepository.buscarPorEmail(email);
        if (usuarioOptional.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado."); // TODO: Usar ValidacaoException
        }

        Usuario usuario = usuarioOptional.get();

        if (!BCrypt.checkpw(senha, usuario.getSenha())) {
            throw new IllegalArgumentException("Senha incorreta."); // TODO: Usar ValidacaoException
        }

        return usuario;
    }
}