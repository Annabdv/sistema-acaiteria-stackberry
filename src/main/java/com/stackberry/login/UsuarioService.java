package com.stackberry.login;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Collection;

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

        // verifica se o email esta em uso por um usuario ativo
        if (usuarioRepository.buscarAtivoPorEmail(email).isPresent()) {
            throw new IllegalArgumentException("O email informado já está em uso por outro usuário ativo.");
        }

        // Criptografando a senha
        String senhaCriptografada = BCrypt.hashpw(senha, BCrypt.gensalt());

        Usuario novoUsuario = new Usuario(email, nome, senhaCriptografada);

        if (usuarioRepository.buscarTodosUsuarios().isEmpty()) {
            novoUsuario.setTipoDeUsuario(TipoDeUsuario.ADMINISTRADOR);
        } else {
            novoUsuario.setTipoDeUsuario(TipoDeUsuario.CLIENTE);
        }
        return usuarioRepository.salvar(novoUsuario);
    }

    // Autenticaçao de usuario --- anna
    public Usuario autenticaUsuario(String email, String senha) {
        if (email == null || senha == null) {
            throw new IllegalArgumentException("E-mail e senha são dados obrigatorios."); // TODO: Usar ValidacaoException
        }

        var usuarioOptional = usuarioRepository.buscarAtivoPorEmail(email);
        if (usuarioOptional.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado ou desativado.");
        }

        Usuario usuario = usuarioOptional.get();

        if (!usuario.isAtivo()) {
            throw new IllegalArgumentException("Usuário desativado.");
        }

        if (!BCrypt.checkpw(senha, usuario.getSenha())) {
            throw new IllegalArgumentException("Senha incorreta."); // TODO: Usar ValidacaoException
        }

        return usuario;
    }

    public Collection<Usuario> buscarTodos() {
        return usuarioRepository.buscarTodosUsuarios();
    }

    public int contarUsuarios() {
        return usuarioRepository.buscarTodosUsuarios().size();
    }

    public boolean desativarUsuario(String id) {
        return usuarioRepository.desativarUsuarioPorId(id);
    }

    public void recarregarUsuarios() {
        usuarioRepository.carregaUsariosArquivo();
    }
}