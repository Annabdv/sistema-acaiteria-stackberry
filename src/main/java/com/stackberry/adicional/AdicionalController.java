package com.stackberry.adicional;

import com.stackberry.exception.ValidacaoException;
import io.javalin.http.Context;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AdicionalController {
    private final AdicionalService adicionalService;

    public AdicionalController() {
        this.adicionalService = new AdicionalService();
    }

    public void exibirFormularioCadastro(Context ctx){
        Adicional adicionalVazio = new Adicional();
        Map<String, Object> modelo = Map.of("adicional", adicionalVazio);
        ctx.render("/admin/adicionais/formulario-criar.html", modelo);
    }

    public void cadastrarAdicional(Context ctx){
        String nome = ctx.formParam("nome");
        BigDecimal preco = new BigDecimal(ctx.formParam("preco"));
        Adicional adicionalDoFormulario = new Adicional(nome, preco);
        try {
            adicionalService.cadastrarAdicional(adicionalDoFormulario);
            ctx.redirect("/admin/adicionais");
        }catch (ValidacaoException e) {
            ctx.status(400).result("Erro de validação: " + e.getMessage());
        }
    }

    public void exibirPaginaDeAdicionais(Context ctx){
        List<Adicional> adicionalList = adicionalService.listarAdicionais();
        Map<String, Object> modelo = Map.of("adicionais", adicionalList);
        ctx.render("admin/adicionais/adicional.html", modelo);
    }

    public void exibirFormularioDeEdicao(Context ctx){
        String id = ctx.pathParam("id");
        Optional<Adicional> adicionalOptional = adicionalService.buscarPorId(id);
        if(adicionalOptional.isPresent()){
            Map<String, Object> modelo = Map.of("adicional", adicionalOptional.get());
            ctx.render("/admin/adicionais/formulario-editar.html", modelo);
        } else {
            ctx.status(404).result("Adicional não encontrado");
        }
    }

    public void atualizarAdicional(Context ctx){
        String id = ctx.formParam("id");
        String nome = ctx.formParam("nome");
        BigDecimal preco = new BigDecimal(ctx.formParam("preco"));
        Adicional adicionalAtualizado = new Adicional(nome, preco);
        adicionalService.atualizarAdicional(id, adicionalAtualizado);
        ctx.redirect("/admin/adicionais");
    }

    public void removerAdicional(Context ctx){
        String id = ctx.pathParam("id");
        adicionalService.removerAdicional(id);
        ctx.redirect("/admin/adicionais");
    }
}