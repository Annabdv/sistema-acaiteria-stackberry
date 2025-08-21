package com.stackberry;

import com.stackberry.login.UsuarioController;
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

        // Rota de teste para a página inicial
        app.get("/", ctx -> {
            ctx.render("index.html"); // Renderiza o templates/index.html
        });
        app.get("/cadastrar", usuarioController::exibirFormularioCadastro);
        app.post("/usuarios/cadastrar", usuarioController::processarCadastro);

        app.get("/login", ctx -> {
            ctx.render("login.html");
        });

    }
}