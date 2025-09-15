package com.stackberry.pedido;
import io.javalin.http.Context;
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
}