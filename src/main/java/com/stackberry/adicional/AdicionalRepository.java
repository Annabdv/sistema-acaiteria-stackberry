package com.stackberry.adicional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AdicionalRepository {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String FILE_PATH = "src/main/resources/json/Adicionais.json";
    private final Map<String, Adicional> adicionalEmMemoria = new ConcurrentHashMap<>();

    public AdicionalRepository() {
        carregarAdicionais();
    }

    public boolean atualizarAdicional(String id, Adicional adicionalAtualizado) {
        if (adicionalEmMemoria.containsKey(id)) {
            adicionalAtualizado.setId(id);
            adicionalEmMemoria.put(id, adicionalAtualizado);
            salvaTudoNoArquivo();
            return true;
        }
        return false;
    }

    public List<Adicional> listarAdicionais() {
        return new ArrayList<>(adicionalEmMemoria.values());
    }

    public boolean removerAdicional(String id) {
        Adicional adicionalRemovido = adicionalEmMemoria.remove(id);
        if (adicionalRemovido != null) {
            salvaTudoNoArquivo();
            return true;
        }
        return false;
    }

    public Optional<Adicional> buscaPorId(String id) {
        return Optional.ofNullable(adicionalEmMemoria.get(id));
    }

    public Adicional salvar(Adicional adicional) {
        if (adicional.getId() == null || adicional.getId().trim().isEmpty()) {
            String novoId = java.util.UUID.randomUUID().toString();
            adicional.setId(novoId);
        }
        adicionalEmMemoria.put(adicional.getId(), adicional);
        salvaTudoNoArquivo();
        return adicional;
    }

    private void carregarAdicionais() {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return;
        }
        try {
            List<Adicional> adicionaisDoJson = objectMapper.readValue(file, new TypeReference<List<Adicional>>() {});
            adicionaisDoJson.forEach(adicional -> adicionalEmMemoria.put(adicional.getId(), adicional));
        } catch (Exception e) {
            System.err.println("Ocorreu um erro ao tentar carregar Adicionais." + e.getMessage());
        }
    }

    private synchronized void salvaTudoNoArquivo() {
        try {
            List<Adicional> listaParaSalvar = new ArrayList<>(adicionalEmMemoria.values());
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), listaParaSalvar);
        } catch (Exception e) {
            System.err.println("Ocorreu um erro ao tentar salvar Adicionais:" + e.getMessage());
        }
    }
}