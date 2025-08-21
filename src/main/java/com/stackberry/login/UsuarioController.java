package com.stackberry.login;
import io.javalin.http.Context;

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
}
