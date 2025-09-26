package com.stackberry.pedido;

import com.stackberry.login.Usuario;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PedidoController {
    private final PedidoService pedidoService;

    public PedidoController() {
        this.pedidoService = new PedidoService();
    }

    public void listarPedidos(Context ctx) {
        ctx.json(pedidoService.buscarTodosOrdenados());
    }

    public void totalPedidos(Context ctx) {
        ctx.json(Map.of("total", pedidoService.contarPedidos()));
    }

    public void marcarComoEntregue(Context ctx) {
        String id = ctx.pathParam("id");
        boolean atualizado = pedidoService.marcarComoEntregue(id);

        if (atualizado) {
            ctx.json(Map.of("mensagem", "Status do pedido atualizado para ENTREGUE!"));
        } else {
            ctx.status(404).json(Map.of("mensagem", "Pedido não encontrado ou não pôde ser atualizado."));
        }
    }

    public void cancelarPedido(Context ctx) {
        Usuario usuarioLogado = ctx.sessionAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            ctx.status(401).json(Map.of("mensagem", "Você precisa estar logado para cancelar um pedido."));
            return;
        }

        String pedidoId = ctx.pathParam("id");
        Optional<Pedido> optPedido = pedidoService.buscarPorId(pedidoId);

        if (optPedido.isEmpty()) {
            ctx.status(404).json(Map.of("mensagem", "Pedido não encontrado."));
            return;
        }

        Pedido pedido = optPedido.get();
        if (!pedido.getUsuarioId().equals(usuarioLogado.getId())) {
            ctx.status(403).json(Map.of("mensagem", "Você não tem permissão para cancelar este pedido."));
            return;
        }

        boolean cancelado = pedidoService.cancelarPedido(pedidoId);

        if (cancelado) {
            ctx.json(Map.of("mensagem", "Pedido cancelado com sucesso."));
        } else {
            ctx.status(400).json(Map.of("mensagem", "Este pedido não pode mais ser cancelado."));
        }
    }

    public void listarPedidosDoUsuarioLogado(Context ctx) {
        Usuario usuarioLogado = ctx.sessionAttribute("usuarioLogado");

        if (usuarioLogado == null) {
            ctx.status(401).json(Map.of("erro", "Usuário não autenticado."));
            return;
        }

        List<Pedido> pedidos = pedidoService.buscarPorUsuarioOrdenados(usuarioLogado.getId());
        ctx.json(pedidos);
    }

    public void exibirPaginaMeusPedidos(Context ctx) {
        ctx.render("seus_pedidos.html");
    }
}