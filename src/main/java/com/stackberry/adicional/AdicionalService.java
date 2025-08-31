package com.stackberry.adicional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import com.stackberry.exception.ValidacaoException;

public class AdicionalService {
    private final AdicionalRepository adicionalRepository;

    public AdicionalService() {
        this.adicionalRepository = new AdicionalRepository();
    }

    public Adicional cadastrarAdicional(Adicional adicional){
        validar(adicional);
        return adicionalRepository.salvar(adicional);
    }

    public List<Adicional> listarAdicionais(){
        return adicionalRepository.listarAdicionais();
    }

    public Optional<Adicional> atualizarAdicional(String id, Adicional adicionalComNovosDados){
        validar(adicionalComNovosDados);
        boolean atualizadoComSucesso = adicionalRepository.atualizarAdicional(id, adicionalComNovosDados);
        if (atualizadoComSucesso){
            return adicionalRepository.buscaPorId(id);
        }
        return Optional.empty();
    }

    public boolean removerAdicional(String id){
        return adicionalRepository.removerAdicional(id);
    }

    public Optional<Adicional> buscarPorId(String id){
        return adicionalRepository.buscaPorId(id);
    }

    private void validar(Adicional adicional) {
        if (adicional.getPreco() == null || adicional.getPreco().compareTo(BigDecimal.ZERO) < 0){
            throw new ValidacaoException("O preço do adicional não pode ser nulo ou negativo.");
        }
        if (adicional.getNome() == null || adicional.getNome().trim().isEmpty()) {
            throw new ValidacaoException("O nome do adicional não pode ser nulo ou vazio.");
        }
    }
}