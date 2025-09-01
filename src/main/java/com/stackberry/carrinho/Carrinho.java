package com.stackberry.carrinho;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class Carrinho implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<ItemCarrinho> itensNoCarrinho = new ArrayList<>();

    public List<ItemCarrinho> getItensNoCarrinho() {
        return itensNoCarrinho;
    }

    public void addItensNoCarrinho(ItemCarrinho novoItem){
        if (novoItem == null){
            throw new IllegalArgumentException("O item não pode ser nulo");
        }
        for (ItemCarrinho item : itensNoCarrinho){
            if (item.mesmoProduto(novoItem)){
                item.setQuantidade(item.getQuantidade() + novoItem.getQuantidade());
                return;
            }
        }
        itensNoCarrinho.add(novoItem);

    }
    public void removeItemDoCarrinhoPeloId(String id){
        if (id == null){
            throw new IllegalArgumentException("O id inserido deve ser válido");
        }
        Iterator<ItemCarrinho> iterator = itensNoCarrinho.iterator();
        while(iterator.hasNext()){
            if (iterator.next().getId().equals(id)){
                iterator.remove();
                return;
            }
        }
    }

    public Optional<ItemCarrinho> buscaPorId(String id){
        return itensNoCarrinho.stream().filter(i -> i.getId().equals(id)).findFirst();
    }


    public void atualizaQuantidade(String id, int quantidadeNova){
        buscaPorId(id).ifPresent(i-> i.setQuantidade(Math.max(1,quantidadeNova)));
    }

    public BigDecimal getTotal(){
        return itensNoCarrinho.stream().map(ItemCarrinho :: subTotal).reduce(BigDecimal.ZERO,BigDecimal::add);
    }

    public void clear(){
        itensNoCarrinho.clear();
    }

    public int getTotalItens(){
        return itensNoCarrinho.stream().mapToInt(ItemCarrinho::getQuantidade).sum();
    }
}
