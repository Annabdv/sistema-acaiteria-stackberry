package com.stackberry.tamanho;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class TamanhoService {
    private final TamanhoRepository tamanhoRepository;

    public TamanhoService() {
        this.tamanhoRepository = new TamanhoRepository();
    }

    public Tamanho cadastrarTamanho(Tamanho tamanho){
        validar(tamanho);
        return tamanhoRepository.salvar(tamanho);
    }

    public List<Tamanho> listarTamanhos(){
        return tamanhoRepository.listarTamanhos();
    }

    public Optional<Tamanho> atualizarTamanho(String id, Tamanho tamanhoDodosNovos){
        validar(tamanhoDodosNovos);
        // Lembrar que meu metodo atualizarTamanho retorna um booleano
        boolean atualizadoComSucesso = tamanhoRepository.atualizarTamanho(id, tamanhoDodosNovos);

        if (atualizadoComSucesso){
            return tamanhoRepository.buscaPorId(id);
        }
        return Optional.empty();
    }

    public boolean removerTamanho(String id){
        return tamanhoRepository.removerTamanho(id);
    }

    // Metodo privado para evitar repeticao de codigo.
    private void validar(Tamanho tamanho) {
        if (tamanho.getPreco() == null || tamanho.getPreco().compareTo(BigDecimal.ZERO) < 0){
            throw new ValidacaoException("O preço do tamanho não pode ser nulo/negativo.");
        }

        if (tamanho.getNome() == null || tamanho.getNome().isEmpty()) {
            throw new ValidacaoException("Nome não pode ser nulo/vazio.");
        }
    }
}
