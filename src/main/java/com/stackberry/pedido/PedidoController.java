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

    // Este método continua igual
    public void listarPedidos(Context ctx) {
        ctx.json(pedidoService.buscarTodosOrdenados());
    }

    // Este método continua igual
    public void totalPedidos(Context ctx) {
        ctx.json(Map.of("total", pedidoService.contarPedidos()));
    }

    // --- MÉTODO ANTIGO SUBSTITUÍDO ---
    // Renomeado para ficar mais claro. Este método será usado pelo admin.
    public void marcarComoEntregue(Context ctx) {
        String id = ctx.pathParam("id");
        // Chama o novo método do serviço
        boolean atualizado = pedidoService.marcarComoEntregue(id);

        if (atualizado) {
            ctx.json(Map.of("mensagem", "Status do pedido atualizado para ENTREGUE!"));
        } else {
            // Pode ser que o pedido não foi encontrado ou já tinha outro status
            ctx.status(404).json(Map.of("mensagem", "Pedido não encontrado ou não pôde ser atualizado."));
        }
    }

    // --- NOVO MÉTODO PARA O USUÁRIO CANCELAR O PRÓPRIO PEDIDO ---
    public void cancelarPedido(Context ctx) {
        // 1. Segurança: Pega o usuário logado da sessão
        Usuario usuarioLogado = ctx.sessionAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            ctx.status(401).json(Map.of("mensagem", "Você precisa estar logado para cancelar um pedido."));
            return;
        }

        // 2. Pega o ID do pedido da URL
        String pedidoId = ctx.pathParam("id");
        Optional<Pedido> optPedido = pedidoService.buscarPorId(pedidoId);

        // 3. Verifica se o pedido existe
        if (optPedido.isEmpty()) {
            ctx.status(404).json(Map.of("mensagem", "Pedido não encontrado."));
            return;
        }

        // 4. Segurança: Verifica se o usuário logado é o dono do pedido
        Pedido pedido = optPedido.get();
        if (!pedido.getUsuarioId().equals(usuarioLogado.getId())) {
            ctx.status(403).json(Map.of("mensagem", "Você não tem permissão para cancelar este pedido."));
            return;
        }

        // 5. Chama o serviço para tentar cancelar
        boolean cancelado = pedidoService.cancelarPedido(pedidoId);

        if (cancelado) {
            ctx.json(Map.of("mensagem", "Pedido cancelado com sucesso."));
        } else {
            // O serviço retorna 'false' se o status não for 'EM_PREPARO'
            ctx.status(400).json(Map.of("mensagem", "Este pedido não pode mais ser cancelado."));
        }
    }


    // Este método continua igual
    public void listarPedidosDoUsuarioLogado(Context ctx) {
        // ATENÇÃO: Verifique se o nome "usuarioLogado" é o mesmo que você usa ao fazer login!
        Usuario usuarioLogado = ctx.sessionAttribute("usuarioLogado");

        if (usuarioLogado == null) {
            ctx.status(401).json(Map.of("erro", "Usuário não autenticado."));
            return;
        }

        List<Pedido> pedidos = pedidoService.buscarPorUsuarioOrdenados(usuarioLogado.getId());
        ctx.json(pedidos);
    }

    // Este método continua igual
    public void exibirPaginaMeusPedidos(Context ctx) {
        ctx.render("seus_pedidos.html");
    }
}