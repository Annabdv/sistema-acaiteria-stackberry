package com.stackberry.pedido;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.stackberry.login.Usuario;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PedidoRepository {

    private static final PedidoRepository INSTANCE = new PedidoRepository();

    private static final String FILE_PATH = "src/main/resources/json/Pedidos.json";
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private final Map<String, Pedido> pedidosEmMemoria = new ConcurrentHashMap<>();

    private PedidoRepository() {
        carregarPedidosArquivo();
    }

    public static PedidoRepository getInstance() {
        return INSTANCE;
    }

    public synchronized Pedido salvar(Pedido pedido) {
        pedidosEmMemoria.put(pedido.getId(), pedido);
        salvarTudoNoArquivo();
        return pedido;
    }

    public List<Pedido> buscarTodos() {
        return new ArrayList<>(pedidosEmMemoria.values());
    }

    public Optional<Pedido> buscarPorId(String id) {
        return Optional.ofNullable(this.pedidosEmMemoria.get(id));
    }

    public List<Pedido> buscarPorUsuario(String usuarioId) {
        return pedidosEmMemoria.values().stream()
                .filter(p -> p.getUsuarioId() != null && p.getUsuarioId().equals(usuarioId))
                .collect(Collectors.toList());
    }

    private void carregarPedidosArquivo() {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) return;

        try {
            List<Pedido> pedidosDoArquivo = objectMapper.readValue(file, new TypeReference<List<Pedido>>() {});
            pedidosDoArquivo.forEach(p -> pedidosEmMemoria.put(p.getId(), p));
        } catch (Exception e) {
            System.err.println("Erro ao carregar pedidos: " + e.getMessage());
        }
    }

    private synchronized void salvarTudoNoArquivo() {
        try {
            List<Pedido> listaParaSalvar = new ArrayList<>(pedidosEmMemoria.values());
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), listaParaSalvar);
        } catch (Exception e) {
            System.err.println("Erro ao salvar pedidos: " + e.getMessage());
        }
    }

    public Map<String, Pedido> carregandoPedidos() {
        Map<String, Pedido> pedidosMap = new ConcurrentHashMap<>();
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) return pedidosMap;

        try {
            List<Pedido> pedidosDoArquivo = objectMapper.readValue(file, new TypeReference<List<Pedido>>() {});
            pedidosDoArquivo.forEach(p -> pedidosMap.put(p.getId(), p));
        } catch (Exception e) {
            System.err.println("Erro ao carregar pedidos: " + e.getMessage());
        }
        return pedidosMap;
    }

    public synchronized void recarregarDoArquivo() {
        Map<String, Pedido> carregados = carregandoPedidos();
        pedidosEmMemoria.clear();
        pedidosEmMemoria.putAll(carregados);
    }
}