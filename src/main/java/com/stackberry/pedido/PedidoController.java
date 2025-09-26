package com.stackberry.pedido;
import com.stackberry.login.Usuario;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

public class PedidoController {
    private final PedidoService pedidoService;

    public PedidoController() {
        this.pedidoService = new PedidoService();
    }

    // lista todos os pedidos em JSON
    public void listarPedidos(Context ctx) {
        ctx.json(pedidoService.buscarTodosOrdenados());
    }

    // retorna total de pedidos
    public void totalPedidos(Context ctx) {
        ctx.json(Map.of("total", pedidoService.contarPedidos()));
    }

    // alternar status do pedido
    public void alterarStatus(Context ctx) {
        String id = ctx.pathParam("id");
        boolean updated = pedidoService.alternarStatus(id);

        if (updated) {
            ctx.json(Map.of("mensagem", "Status do pedido atualizado com sucesso!"));
        } else {
            ctx.status(404).json(Map.of("mensagem", "Pedido não encontrado."));
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