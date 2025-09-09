package com.stackberry.login;
import io.javalin.http.Context;

import java.util.Map;

public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController() {
        this.usuarioService = new UsuarioService();
    }

    // Mostrar página de cadastro.
    public void exibirFormularioCadastro(Context ctx){
        ctx.render("formulario_signup.html");
    }

    // metodo para processar cadastro.
    public void processarCadastro(Context ctx){
        String nome = ctx.formParam("nome");
        String email = ctx.formParam("email");
        String senha = ctx.formParam("senha");

        try {
            usuarioService.cadastrarUsuario(email, nome, senha);
            // Se o cadastro deu certo, só redireciona para pagina de login
            ctx.redirect("/login");
        } catch (Exception e) {
            ctx.attribute("erro", e.getMessage());
            // Volta para a pagina de cadastro caso ocorra algum erro.
            ctx.render("formulario_signup.html");
        }
    }

    // Mostrar página de login --- anna
    public void exibirFormularioLogin(Context ctx) {
        ctx.render("login.html");
    }

    // Processamento de login --- anna
    public void processarLogin(Context ctx) {
        String email = ctx.formParam("email");
        if (email == null) email = ctx.formParam("username");
        String senha = ctx.formParam("senha");
        if (senha == null) senha = ctx.formParam("password");

        try {
            Usuario usuario = usuarioService.autenticaUsuario(email, senha);
            ctx.sessionAttribute("usuarioLogado", usuario);

            // Redirect por tipo de usuario
            switch (usuario.getTipoDeUsuario()) {
                case CLIENTE -> ctx.redirect("/cardapio");
                case ADMINISTRADOR -> ctx.redirect("/admin/dashboard_admin");
            }
        } catch (Exception e) {
            ctx.attribute("erro", e.getMessage());
            ctx.render("login.html");
        }
    }

    public void promoverUsuarioParaAdmin(Context ctx) {
        String email = ctx.pathParam("email");

        boolean sucesso = usuarioService.promoverParaAdmin(email);

        if (sucesso) {
            ctx.json(Map.of("mensagem", "Usuário promovido para ADMIN com sucesso!"));
        } else {
            ctx.status(404).json(Map.of("mensagem", "Usuário não encontrado."));
        }
    }


    // Logout --- anna
    public void logout(Context ctx) {
        ctx.sessionAttribute("usuarioLogado", null);
        ctx.req().getSession().invalidate();
        ctx.redirect("/login");
    }

    // lista de usuários em JSON
    public void listarUsuarios(Context ctx) {
        usuarioService.recarregarUsuarios(); // cria esse método no service se ainda não existir
        ctx.json(usuarioService.buscarTodos());
    }

    public void totalUsuarios(Context ctx) {
        int total = usuarioService.buscarTodos().size();
        ctx.json(Map.of("total", total));
    }

    public void removerUsuario(Context ctx) {
        String id = ctx.pathParam("id");
        boolean removed = usuarioService.desativarUsuario(id);

        if (removed) {
            ctx.json(Map.of("mensagem", "Usuário removido com sucesso!"));
        } else {
            ctx.status(404).json(Map.of("mensagem", "Usuário não encontrado."));
        }
    }


}
