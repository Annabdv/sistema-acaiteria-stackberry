package com.stackberry.tamanho;

import io.javalin.http.Context;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TamanhoController {
    private final TamanhoService tamanhoService;

    public TamanhoController() {
        this.tamanhoService = new TamanhoService();
    }

    // ignore, isso vai ser util para testar via postman
    public void cadastrarTamanho(Context ctx){
        Tamanho novoTamanho = ctx.bodyAsClass(Tamanho.class); // Cadastrando novo tamanho com o Json enviado.
        try {
            Tamanho tamanhoCadastrado = tamanhoService.cadastrarTamanho(novoTamanho);
            // retornar json vai ser util para view, serve para comunicar o id gerado.
            ctx.status(201).json(tamanhoCadastrado); // 201 significa "Created"
        } catch (ValidacaoException e){
            ctx.status(400).result(e.getMessage()); // 400 Conteúdo inválido.
        }
    }

    public void cadastrarTamanhoFormulario(Context ctx){
        String nome = ctx.formParam("nome");
        BigDecimal preco = new BigDecimal(ctx.formParam("preco"));

        Tamanho tamanhoDoFormulario = new Tamanho(nome, preco);

        try {
            tamanhoService.cadastrarTamanho(tamanhoDoFormulario);
            ctx.redirect("/admin/tamanhos");
        }catch (ValidacaoException e) {
            ctx.status(400).result("Erro de validação: " + e.getMessage());
        }
    }
    // Ignore, isso é pra ser usado via postman
    public void listarTamanhos(Context ctx){
        List<Tamanho> tamanhoList = tamanhoService.listarTamanhos();
        ctx.status(200).json(tamanhoList);
    }

    public void exibirPaginaDeTamanhos(Context ctx){

        List<Tamanho> tamanhoList = tamanhoService.listarTamanhos(); // Crio minha lista de tamanhos

        // Faço um modelo para ser usando pelo th:each lá no "tamanho.html"
        Map<String, Object> modelo = Map.of("tamanhos", tamanhoList);
        ctx.render("admin/tamanho.html", modelo); // Renderizo a página, e passo o modelo.

    }
}
