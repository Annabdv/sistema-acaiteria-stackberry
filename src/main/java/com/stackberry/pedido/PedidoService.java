package com.stackberry.pedido;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoService() {
        this.pedidoRepository = new PedidoRepository();
    }

    public Pedido salvar(Pedido pedido) {
        return pedidoRepository.salvar(pedido);
    }

    public List<Pedido> buscarTodosOrdenados() {
        pedidoRepository.recarregarDoArquivo(); // garante dados atualizados
        return pedidoRepository.buscarTodos()
                .stream()
                .sorted(Comparator.comparing(Pedido::getDataHora).reversed())
                .toList();
    }

    public Optional<Pedido> buscarPorId(String id) {
        pedidoRepository.recarregarDoArquivo();
        return pedidoRepository.buscarTodos()
                .stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public boolean alternarStatus(String id) {
        pedidoRepository.recarregarDoArquivo();
        Optional<Pedido> optPedido = buscarPorId(id);
        if (optPedido.isEmpty()) return false;

        Pedido pedido = optPedido.get();
        if (!pedido.isRealizado()) {
            pedido.setRealizado(true);
            salvar(pedido);
            return true;
        }
        return false;
    }

    public int contarPedidos() {
        pedidoRepository.recarregarDoArquivo();
        return pedidoRepository.buscarTodos().size();
    }
}