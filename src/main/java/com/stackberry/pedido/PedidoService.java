package com.stackberry.pedido;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoService() {
        this.pedidoRepository = PedidoRepository.getInstance();
    }

    public Pedido salvar(Pedido pedido) {
        return pedidoRepository.salvar(pedido);
    }

    public List<Pedido> buscarTodosOrdenados() {
        return pedidoRepository.buscarTodos()
                .stream()
                .sorted(Comparator.comparing(Pedido::getDataHora).reversed())
                .toList();
    }

    public Optional<Pedido> buscarPorId(String id) {
        return pedidoRepository.buscarPorId(id);
    }

    public boolean marcarComoEntregue(String id) {
        Optional<Pedido> optPedido = buscarPorId(id);
        if (optPedido.isEmpty()) {
            return false;
        }

        Pedido pedido = optPedido.get();
        if (pedido.getStatus() == StatusPedido.EM_PREPARO) {
            pedido.setStatus(StatusPedido.ENTREGUE);
            pedidoRepository.salvar(pedido);
            return true;
        }
        return false;
    }

    public boolean cancelarPedido(String id) {
        Optional<Pedido> optPedido = buscarPorId(id);
        if (optPedido.isEmpty()) {
            return false;
        }

        Pedido pedido = optPedido.get();
        if (pedido.getStatus() == StatusPedido.EM_PREPARO) {
            pedido.setStatus(StatusPedido.CANCELADO);
            pedidoRepository.salvar(pedido);

            return true;
        }
        return false;
    }

    public int contarPedidos() {
        return pedidoRepository.buscarTodos().size();
    }

    public List<Pedido> buscarPorUsuarioOrdenados(String usuarioId) {
        return pedidoRepository.buscarPorUsuario(usuarioId)
                .stream()
                .sorted(Comparator.comparing(Pedido::getDataHora).reversed())
                .toList();
    }
}