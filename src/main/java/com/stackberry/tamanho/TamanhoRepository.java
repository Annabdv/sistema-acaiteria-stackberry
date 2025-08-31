package com.stackberry.tamanho;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TamanhoRepository {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String FILE_PATH = "src/main/resources/json/Tamanhos.json";

    // Usando novamente o ConcurrentHashMap, para lidar com multiplas operações.
    // Salva os tamanhos de açai na memória do nosso aplicativo web.
    private final Map<String, Tamanho> tamanhoEmMemoria = new ConcurrentHashMap<>();

    public TamanhoRepository() {
        carregarTamanhos();
    }

    public boolean atualizarTamanho(String id, Tamanho tamanhoAtualizado) {
        // Verificando se existe um usuario em memória com id passado
        if (tamanhoEmMemoria.containsKey(id)) { // Verificando se existe um tamanho em memória com id passado

            // Carimbando meu novo tamanho
            tamanhoAtualizado.setId(id);

            // Colocando em memória e susbtituindo
            tamanhoEmMemoria.put(id, tamanhoAtualizado);

            // Salvando no meu json
            salvaTudoNoArquivo();
            return true;
        }
        return false;
    }

    public List<Tamanho> listarTamanhos() {
        return new ArrayList<>(tamanhoEmMemoria.values());
    }

    public boolean removerTamanho(String id) {
        Tamanho tamanhoRemovido = tamanhoEmMemoria.remove(id);

        // Verificando se tamanho foi removido com sucesso.
        if (tamanhoRemovido != null) {
            System.out.println(tamanhoRemovido.getNome() + " foi removido."); // TODO: Trocar por logger
            salvaTudoNoArquivo(); // Salvando as alterações no json.
            return true;
        }
        System.out.println("Tamanho com ID " + id + " não foi encontrado."); // TODO: Trocar por logger
        return false;

    }

    public Optional<Tamanho> buscaPorId(String id) {
        return Optional.ofNullable(tamanhoEmMemoria.get(id));
    }

    public Tamanho salvar(Tamanho tamanho) {
        // Carimbando um novo ID para o meu tamanho.
        if (tamanho.getId() == null || tamanho.getId().trim().isEmpty()) {
            String novoId = java.util.UUID.randomUUID().toString();
            tamanho.setId(novoId);
        }

        tamanhoEmMemoria.put(tamanho.getId(), tamanho);
        salvaTudoNoArquivo(); // Salvando no meu json
        return tamanho;
    }

    private void carregarTamanhos() {
        // Carregar tamanhos e colocar tudo em memória.
        File file = new File(FILE_PATH);
        // Verificação se o file é vazio, se for, ele retorna pois a minha lista de tamanhos está começando agora.
        if (!file.exists() || file.length() == 0) {
            return; //
        }
        try {
            // Lendo os valores do meu Json.
            List<Tamanho> tamanhosDoJson = objectMapper.readValue(file, new TypeReference<>() {
            });
            tamanhosDoJson.forEach(tamanho -> tamanhoEmMemoria.put(tamanho.getId(), tamanho)); // Populando minha memória
        } catch (Exception e) {
            System.err.println("Ocorreu um erro ao tentar carregar Tamanhos." + e.getMessage());
        }

    }

    private synchronized void salvaTudoNoArquivo() {
        try {
            List<Tamanho> listaParaSalvar = new ArrayList<>(tamanhoEmMemoria.values());
            // Usando objectMapper para salvar
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), listaParaSalvar);
        } catch (Exception e) {
            System.err.println("Ocorreu um erro ao tentar salvar Tamanhos:" + e.getMessage());
        }
    }
}
