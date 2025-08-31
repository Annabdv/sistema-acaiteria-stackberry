package com.stackberry.tamanho;

import com.stackberry.exception.ValidacaoException;
import io.javalin.http.Context;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TamanhoController {
    private final TamanhoService tamanhoService;

    public TamanhoController() {
        this.tamanhoService = new TamanhoService();
    }

    public void exibirFormularioCadastro(Context ctx){
        Tamanho tamanhoVazio = new Tamanho();

        Map<String, Object> modelo = Map.of("tamanho", tamanhoVazio);
        ctx.render("/admin/tamanhos/formulario-criar.html", modelo);
    }

    public void cadastrarTamanhoView(Context ctx){
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


    public void exibirPaginaDeTamanhos(Context ctx){

        List<Tamanho> tamanhoList = tamanhoService.listarTamanhos(); // Crio minha lista de tamanhos

        // Faço um modelo para ser usando pelo th:each lá no "tamanho.html"
        Map<String, Object> modelo = Map.of("tamanhos", tamanhoList);
        ctx.render("admin/tamanhos/tamanho.html", modelo); // Mostro a página e passo o modelo.
    }

   public void exibirFormularioDeEdicao(Context ctx){

        String id = ctx.pathParam("id"); // Recebe do tamanho.html
       Optional<Tamanho> tamanhoOptional = tamanhoService.buscarPorId(id);

       if(tamanhoOptional.isPresent()){
           // Basicamente, passo o model para já ficar preenchido bonitinho para meu formulario-editar.html
           Map<String, Object> modelo = Map.of("tamanho", tamanhoOptional.get());
           ctx.render("/admin/tamanhos/formulario-editar.html", modelo);
       } else {
           ctx.status(404).result("Tamanho não encontrado");
       }
   }

   public void atualizarTamanho(Context ctx){
        // TODO: Blindar com verificações de segurança
        String id = ctx.formParam("id");
        String nome = ctx.formParam("nome");
        BigDecimal preco = new BigDecimal(ctx.formParam("preco"));

        Tamanho tamanhoAtualizado = new Tamanho(nome, preco);
        tamanhoService.atualizarTamanho(id, tamanhoAtualizado);
        ctx.redirect("/admin/tamanhos");
   }

   public void removerTamanho(Context ctx){
        String id = ctx.pathParam("id");
        tamanhoService.removerTamanho(id);
        ctx.redirect("/admin/tamanhos");
   }




}
