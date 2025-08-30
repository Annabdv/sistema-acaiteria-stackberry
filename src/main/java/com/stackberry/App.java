package com.stackberry;

import com.stackberry.login.Usuario;
import com.stackberry.login.UsuarioController;
import com.stackberry.tamanho.Tamanho;
import com.stackberry.tamanho.TamanhoController;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.rendering.template.JavalinThymeleaf;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class App {

    public static void main(String[] args) {
        new App().iniciar();
    }

    // Metodo que vai orquestar a inicialização da aplicação
    public void iniciar() {
        System.out.println("Iniciando StackBerry...");

        // Cria o servidor Javalin usando nosso metodo de configuração
        Javalin app = Javalin.create(this::configureJavalin).start(7070);

        // Define as rotas (endpoints) da nossa aplicação
        definirRotas(app);

        System.out.println("Servidor StackBerry iniciado em http://localhost:7070");
    }

    // Configura os aspectos do servidor Javalin
    private void configureJavalin(JavalinConfig config) {
        // Configura o Thymeleaf
        TemplateEngine templateEngine = configurarThymeleaf();
        config.fileRenderer(new JavalinThymeleaf(templateEngine));

        // Aqui configura o local dos arquivos estáticos (CSS, JS, PNG...)
        // Verifiquem o direitorio resources para tentar associar e entender
        config.staticFiles.add("/public");
    }


    // Cria e configura a engine do Thymeleaf
    // Retorna uma instancia configurada do TemplateEngine
    private TemplateEngine configurarThymeleaf() {
        TemplateEngine templateEngine = new TemplateEngine();

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateEngine.addDialect(new LayoutDialect()); // Ferramenta que transforma o Thymeleaf em um sistema de templates muito mais profissional.
        templateResolver.setPrefix("/templates/"); // Nossos HTMLs ficam na pasta /templates/
        templateResolver.setSuffix(".html");      // Nossos arquivos terminam com .html
        templateResolver.setCharacterEncoding("UTF-8");

        templateEngine.setTemplateResolver(templateResolver);


        return templateEngine;
    }

    // Aqui define todas as rotas da aplicação
    private void definirRotas(Javalin app) {

        UsuarioController usuarioController = new UsuarioController();
        TamanhoController tamanhoController = new TamanhoController();

        // ----
//        app.before(ctx -> {
//            String path = ctx.path();
//
//            // rotas publicas
//            boolean rotaPublica = path.equals("/")
//                    || path.equals("/login")
//                    || path.equals("/cadastrar")
//                    || path.startsWith("/usuarios/cadastrar")
//                    || path.startsWith("/styles/")
//                    || path.startsWith("/js/")
//                    || path.startsWith("/images/");
//
//            Usuario usuarioLogado = ctx.sessionAttribute("usuarioLogado");
//
//            if (!rotaPublica) {
//                if (usuarioLogado == null) {
//                    ctx.redirect("/login"); // redirect para login
//                } else {
//                    // acesso por tipo de usuário
//                    switch (usuarioLogado.getTipoDeUsuario()) {
//                        case CLIENTE:
//                            if (path.equals("/dashboard_funcionario") || path.equals("/dashboard_admin")) {
//                                ctx.redirect("/pedidos");
//                            }
//                            break;
//                        case FUNCIONARIO:
//                            if (path.equals("/dashboard_admin")) {
//                                ctx.redirect("/dashboard_funcionario");
//                            }
//                            break;
//                        case ADMINISTRADOR:
//                            // não precisa de bloqueio
//                            break;
//                    }
//                }
//            }
//        }
//        );

        // Rota de teste para a página inicial
        app.get("/", ctx -> {
            ctx.render("index.html"); // Renderiza o templates/index.html
        });

        // tamanho API
        app.post("/api/tamanhos", tamanhoController::cadastrarTamanho);
        app.get("/api/tamanhos", tamanhoController::listarTamanhos);

        // tamanho view
        app.get("/admin/tamanhos", tamanhoController::exibirPaginaDeTamanhos);
        app.get("/admin/tamanhos/novo", tamanhoController::exibirFormularioCadastro);
        app.post("admin/tamanhos/salvar", tamanhoController::cadastrarTamanhoView);
        app.get("admin/tamanhos/editar/{id}", tamanhoController::exibirFormularioDeEdicao);
        app.post("admin/tamanhos/atualizar", tamanhoController::atualizarTamanho);
        app.post("admin/tamanhos/remover/{id}", tamanhoController::removerTamanho);
        // cadastro
        app.get("/cadastrar", usuarioController::exibirFormularioCadastro);
        app.post("/usuarios/cadastrar", usuarioController::processarCadastro);


        // login ---
        app.get("/login", usuarioController::exibirFormularioLogin);
        app.post("/login", usuarioController::processarLogin); // processa o login

        // logout ---
        app.get("/logout", usuarioController::logout);

        app.get("/pedidos", ctx -> ctx.render("pedidos.html")); // cliente
        app.get("/dashboard_funcionario", ctx -> ctx.render("dashboard_funcionario.html")); // funcionário
        app.get("/dashboard_admin", ctx -> ctx.render("dashboard_admin.html")); // admin
    }
}