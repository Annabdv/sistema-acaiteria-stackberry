package com.stackberry.cardapio;

import com.stackberry.adicional.Adicional;
import com.stackberry.adicional.AdicionalService;
import com.stackberry.tamanho.Tamanho;
import com.stackberry.tamanho.TamanhoService;

import io.javalin.http.Context;
import java.util.List;

public class CardapioController {
    private final TamanhoService tamanhoService = new TamanhoService();
    private final AdicionalService adicionalService = new AdicionalService();

    public void mostrarCardapio(Context ctx) {
        // Buscar tamanhos e adicionais para exibir no cardápio
        List<Tamanho> tamanhos = tamanhoService.listarTamanhos();
        List<Adicional> adicionais = adicionalService.listarAdicionais();

        // Passar para o template
        ctx.render("cardapio.html", java.util.Map.of(
                "tamanhos", tamanhos,
                "adicionais", adicionais
        ));
    }
}
