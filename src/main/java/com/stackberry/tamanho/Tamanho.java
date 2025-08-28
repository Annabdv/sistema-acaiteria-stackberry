package com.stackberry.tamanho;

import java.math.BigDecimal;

public class Tamanho {
    private String nome;
    private String id;
    private BigDecimal preco; // Usando a recomendação do professor para lidar com dinheiro

    public Tamanho(String nome, BigDecimal preco) {
        this.id = java.util.UUID.randomUUID().toString();
        this.nome = nome;
        this.preco = preco;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }
}
