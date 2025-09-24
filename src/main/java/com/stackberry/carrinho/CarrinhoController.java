package com.stackberry.carrinho;

import com.stackberry.adicional.Adicional;
import com.stackberry.adicional.AdicionalService;
import com.stackberry.login.Usuario;
import com.stackberry.pedido.Pedido;
import com.stackberry.pedido.PedidoRepository;
import com.stackberry.tamanho.Tamanho;
import com.stackberry.tamanho.TamanhoService;
import io.javalin.http.Context;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CarrinhoController {

    private final TamanhoService tamanhoService = new TamanhoService();
    private final AdicionalService adicionalService = new AdicionalService();
    private final PedidoRepository pedidoRepository = new PedidoRepository();

    public void adicionarAoCarrinho(Context ctx) {
        Carrinho carrinho = ctx.sessionAttribute("carrinho");
        if (carrinho == null) {
            carrinho = new Carrinho();
        }

        String tamanhoId = ctx.formParam("tamanhoId"); // Ajustado para tamanhoId
        if (tamanhoId == null || tamanhoId.isEmpty()) {
            ctx.status(400).result("Tamanho inválido");
            return;
        }

        // Buscar tamanho pelo serviço (substitua pelo seu serviço real)
        Optional<Tamanho> tamanhoOpt = tamanhoService.buscarPorId(tamanhoId);
        if (tamanhoOpt.isEmpty()) {
            ctx.status(400).result("Tamanho não encontrado");
            return;
        }
        Tamanho tamanho = tamanhoOpt.get();

        List<String> adicionaisIds = ctx.formParams("adicionaisIds");
        List<Adicional> adicionais = new ArrayList<>();
        if (adicionaisIds != null) {
            for (String id : adicionaisIds) {
                adicionalService.buscarPorId(id).ifPresent(adicionais::add);
            }
        }

        // Calcular preço unitário (tamanho + adicionais)
        BigDecimal precoUnitario = tamanho.getPreco();
        for (Adicional a : adicionais) {
            precoUnitario = precoUnitario.add(a.getPreco());
        }

        Acai acai = new Acai.Builder()
                .tamanho(tamanho)
                .preco(precoUnitario)
                .adicionais(adicionais)
                .build();

        int quantidade = 1;
        try {
            quantidade = Integer.parseInt(ctx.formParam("quantidade"));
            if (quantidade < 1) quantidade = 1;
        } catch (NumberFormatException e) {
            // deixa quantidade como 1
        }

        ItemCarrinho novoItem = new ItemCarrinho(acai, quantidade, precoUnitario);

        // Se o item já existe no carrinho (mesmo produto), atualiza a quantidade somando
        boolean itemExistente = false;
        for (ItemCarrinho item : carrinho.getItensNoCarrinho()) {
            if (item.mesmoProduto(novoItem)) {
                item.setQuantidade(item.getQuantidade() + quantidade);
                itemExistente = true;
                break;
            }
        }
        if (!itemExistente) {
            carrinho.addItensNoCarrinho(novoItem);
        }

        ctx.sessionAttribute("carrinho", carrinho);

        ctx.redirect("/carrinho");
    }

    private Tamanho mapToTamanho(String id) {
        return tamanhoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Tamanho inválido: " + id));
    }

    private List<Adicional> mapFormAdicionais(List<String> ids) {
        if (ids == null) return java.util.Collections.emptyList();
        return ids.stream()
                .map(id -> adicionalService.buscarPorId(id)
                        .orElseThrow(() -> new IllegalArgumentException("Adicional inválido: " + id)))
                .collect(Collectors.toList());
    }

    private BigDecimal calculaPreco(Tamanho tamanho, List<Adicional> adicionais) {
        BigDecimal base = tamanho.getPreco();

        BigDecimal somaAdicionais = adicionais.stream()
                .map(Adicional::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return base.add(somaAdicionais);
    }
    public void verCarrinho(Context ctx) {
        Carrinho carrinho = ctx.sessionAttribute("carrinho");
        if (carrinho == null) carrinho = new Carrinho();
        ctx.render("carrinho.html", java.util.Map.of("carrinho", carrinho));
    }

    public void removerItem(Context ctx) {
        Carrinho carrinho = ctx.sessionAttribute("carrinho");
        if (carrinho != null) {
            String itemId = ctx.formParam("itemId");
            carrinho.removeItemDoCarrinhoPeloId(itemId);
            ctx.sessionAttribute("carrinho", carrinho);
        }
        ctx.redirect("/carrinho");
    }

    public void atualizarQuantidade(Context ctx) {
        Carrinho carrinho = ctx.sessionAttribute("carrinho");
        if (carrinho != null) {
            String itemId = ctx.formParam("itemId");
            int quantidade = Optional.ofNullable(ctx.formParam("quantidade"))
                    .map(q -> {
                        try {
                            return Integer.parseInt(q);
                        } catch (NumberFormatException e) {
                            return 1;
                        }
                    }).orElse(1);

            carrinho.atualizaQuantidade(itemId, quantidade);
            ctx.sessionAttribute("carrinho", carrinho);
        }
        ctx.redirect("/carrinho");
    }

    // finalização de compra

    public void finalizarCompra(Context ctx) {

        final String numeroWhatsapp = ""; // TODO: Add lógica de adicionar número de whatsapp.
        final String WHATSAPP_BASE_URL = "https://wa.me/";

        Carrinho carrinho = ctx.sessionAttribute("carrinho");
        Usuario usuario = ctx.sessionAttribute("usuarioLogado");

        if (usuario == null) {
            ctx.status(400).result("Usuário não logado");
            return;
        }
        if (carrinho == null || carrinho.getItensNoCarrinho().isEmpty()) {
            ctx.status(400).result("Carrinho vazio");
            return;
        }

        Pedido pedido = new Pedido(carrinho.getItensNoCarrinho(), usuario);
        pedidoRepository.salvar(pedido);

        carrinho.clear();
        ctx.sessionAttribute("carrinho", carrinho);

        try {
            // mensagem formatada para o WhatsApp
            String mensagem = pedido.toWhatsAppMessage();

            // codificando a mensagem
            String mensagemCodificada = URLEncoder.encode(mensagem, StandardCharsets.UTF_8);

            String urlCompleta = WHATSAPP_BASE_URL + numeroWhatsapp + "?text=" + mensagemCodificada;

            // Redirecionando para a mensagem
            ctx.redirect(urlCompleta);

        } catch (Exception e) {
            // Se houver um erro na codificação, redireciona para uma página de sucesso simples
            e.printStackTrace();
            ctx.redirect("/pedido/sucesso");
        }
    }


    // pag de sucesso do pedido
    public void verSucesso(Context ctx) {
        ctx.render("pedidoSucesso.html");
    }

}
