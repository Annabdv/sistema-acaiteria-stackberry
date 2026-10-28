package com.stackberry.carrinho;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemCarrinho implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id = UUID.randomUUID().toString();
    private Acai acai;
    private int quantidade;
    private BigDecimal precoUnitario;

    public ItemCarrinho(Acai acai, int quantidade, BigDecimal precoUnitario) {
        this.acai = Objects.requireNonNull(acai);
        this.quantidade = Math.max(1,quantidade);
        validaPreco(precoUnitario);
        this.precoUnitario = precoUnitario;
    }

    public ItemCarrinho() {
    }

    public String getId() {
        return id;
    }

    public Acai getAcai() {
        return acai;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    private static void validaPreco(BigDecimal precoUnitario) {
        if (precoUnitario == null){
            throw new IllegalArgumentException("Preço inválido");
        }
    }

    public BigDecimal subTotal(){
        return precoUnitario.multiply(java.math.BigDecimal.valueOf(quantidade));
    }

    @JsonIgnore
    public BigDecimal getSubTotal() {
        return subTotal();
    }

    @JsonIgnore
    public BigDecimal getPrecoTotal() {
        return subTotal();
    }

    public boolean mesmoProduto(ItemCarrinho outro){
        if (outro == null){
           return false;
        }
        return this.acai.equals(outro.getAcai());
    }
}