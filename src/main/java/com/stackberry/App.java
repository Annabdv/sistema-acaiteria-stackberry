package com.stackberry;

import com.stackberry.adicional.AdicionalController;
import com.stackberry.cardapio.CardapioController;
import com.stackberry.carrinho.CarrinhoController;
import com.stackberry.login.Usuario;
import com.stackberry.login.UsuarioController;
import com.stackberry.tamanho.TamanhoController;
import com.stackberry.admin.DashboardController;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.rendering.template.JavalinThymeleaf;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.logging.Logger;

public class App {
    public static void main(String[] args) {
        new App().iniciar();
    }

    // Metodo que vai orquestar a inicialização da aplicação
    public void iniciar() {
        System.out.println("Iniciando StackBerry...");

        // Cria o servidor Javalin usando nosso metodo de configuração
        Javalin app = Javalin.create(this::configureJavalin).start(7070);
        configurarPaginasDeErro(app);

        // Define as rotas (endpoints) da nossa aplicação
        definirRotas(app);

        System.out.println("Servidor StackBerry iniciado em http://localhost:7070");

    }

    private void configurarPaginasDeErro(Javalin app) {
        app.error(404, ctx -> ctx.render("erro_404.html"));
        app.error(500, ctx -> ctx.render("erro_500.html"));
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
        AdicionalController adicionalController = new AdicionalController();
        CarrinhoController carrinhoController = new CarrinhoController();
        CardapioController cardapioController = new CardapioController();
        DashboardController dashboardController = new DashboardController();

        //----
        app.before(ctx -> {
                    String path = ctx.path();

                    // rotas publicas
                    boolean rotaPublica = path.equals("/")
                            || path.equals("/login")
                            || path.equals("/cadastrar")
                            || path.startsWith("/usuarios/cadastrar")
                            || path.startsWith("/styles/")
                            || path.startsWith("/js/")
                            || path.startsWith("/images/");

                    Usuario usuarioLogado = ctx.sessionAttribute("usuarioLogado");

                    if (!rotaPublica) {
                        if (usuarioLogado == null) {
                            ctx.redirect("/login"); // redirect para login
                        } else {
                            // acesso por tipo de usuário
                            switch (usuarioLogado.getTipoDeUsuario()) {
                                case CLIENTE:
                                    if (path.startsWith("/admin")) {
                                        ctx.redirect("/cardapio");
                                    }
                                    break;
                                case ADMINISTRADOR:
                                    // não precisa de bloqueio
                                    break;
                            }
                        }
                    }
                }
        );

        // Rota de teste para a página inicial
        app.get("/", ctx -> {
            ctx.render("index.html"); // Renderiza o templates/index.html
        });

        // Rota temporária para testar o erro 500
        app.get("/testar-erro", ctx -> {
            throw new RuntimeException("Erro forçado para teste.");
        });


        // cadastro
        app.get("/cadastrar", usuarioController::exibirFormularioCadastro);
        app.post("/usuarios/cadastrar", usuarioController::processarCadastro);


        // login ---
        app.get("/login", usuarioController::exibirFormularioLogin);
        app.post("/login", usuarioController::processarLogin); // processa o login

        // logout ---
        app.get("/logout", usuarioController::logout);

        // tamanho
        app.get("/admin/tamanhos", tamanhoController::exibirPaginaDeTamanhos);
        app.get("/admin/tamanhos/novo", tamanhoController::exibirFormularioCadastro);
        app.post("admin/tamanhos/salvar", tamanhoController::cadastrarTamanhoView);
        app.get("admin/tamanhos/editar/{id}", tamanhoController::exibirFormularioDeEdicao);
        app.post("admin/tamanhos/atualizar", tamanhoController::atualizarTamanho);
        app.post("admin/tamanhos/remover/{id}", tamanhoController::removerTamanho);

        // adcional
        app.get("/admin/adicionais", adicionalController::exibirPaginaDeAdicionais);
        app.get("/admin/adicionais/novo", adicionalController::exibirFormularioCadastro);
        app.post("/admin/adicionais/salvar", adicionalController::cadastrarAdicional);
        app.get("/admin/adicionais/editar/{id}", adicionalController::exibirFormularioDeEdicao);
        app.post("/admin/adicionais/atualizar", adicionalController::atualizarAdicional);
        app.post("/admin/adicionais/remover/{id}", adicionalController::removerAdicional);

        //cardapio e carrinho
        app.get("/cardapio", cardapioController:: mostrarCardapio);
        app.post("/carrinho/adicionar", carrinhoController::adicionarAoCarrinho);
        app.get("/carrinho", carrinhoController::verCarrinho);
        app.post("/carrinho/remover", carrinhoController::removerItem);
        app.post("/carrinho/atualizar", carrinhoController::atualizarQuantidade);

        // usuario

        app.get("/admin/api/usuarios", usuarioController::listarUsuarios);
        app.get("/admin/totalUsuarios", usuarioController::totalUsuarios);
        app.delete("/admin/api/usuarios/{id}", usuarioController::removerUsuario);

        // TODO: ADICIONE LÓGICA DE CONTROLLER PARA DASHBOARD.
        app.get("/pedidos", ctx -> ctx.render("cardapio.html")); // cliente

        app.get("/admin/dashboard_admin", dashboardController:: exibirDashboard);

        // pedido
        app.post("/carrinho/finalizarCompra", carrinhoController::finalizarCompra);

        app.get("/pedido/sucesso", carrinhoController::verSucesso);
    }
}