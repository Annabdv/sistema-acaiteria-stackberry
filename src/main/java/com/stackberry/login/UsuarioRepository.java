package com.stackberry.login;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UsuarioRepository {
    private final ObjectMapper objectMapper = new ObjectMapper(); // Meu tradutor do Json
    private static final String FILE_PATH = "src/main/resources/json/Usuarios.json"; // O local onde ficar meu arquivo json.

    // ConcurrentHashMap serve para lidar com múltiplas operações ao mesmo tempo de forma segura.
    // é como se transformasse um hashmap em "thread-safe".ç
    private final Map<String, Usuario> usuariosEmMemoria = new ConcurrentHashMap<>();


    public UsuarioRepository(){

    }

    public Usuario salvar(Usuario usuario){
        usuariosEmMemoria.put(usuario.getId(), usuario);
        salvarTudoNoArquivo(); // Salvo no meu Json.

        return usuario;

    }

    public Optional<Usuario> buscarPorEmail(String email){
        return usuariosEmMemoria.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    private void carregaUsariosArquivo() {
        File file = new File(FILE_PATH);
        if(!file.exists() || file.length() == 0){
            return; // Começa com a memória vazia se o arquivo não existir.
        }
        try {
            // lẽ o json e converte ele para uma lista de usuarios
            List<Usuario> usuariosDoArquivo = objectMapper.readValue(file, new TypeReference<List<Usuario>>() {});

            // Popula o mapa em mémoria com minha lista
            usuariosDoArquivo.forEach(usuario -> usuariosEmMemoria.put(usuario.getId(), usuario));
        } catch (Exception e) {
            System.err.println("Ocorreu um erro ao tentar carregar usuários:" + e.getMessage());
        }
    }

    private synchronized void salvarTudoNoArquivo() {
        try {

            List<Usuario> listaParaSalvar = new ArrayList<>(usuariosEmMemoria.values());

            // salvando meus usuarios de forma "Legivel"
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), listaParaSalvar);
        } catch (Exception e) {
            System.err.println("Ocorreu um erro ao tentar salvar usuários:" + e.getMessage());
        }
    }
}
