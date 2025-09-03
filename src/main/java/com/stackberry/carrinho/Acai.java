package com.stackberry.carrinho;

import com.stackberry.adicional.Adicional;
import com.stackberry.tamanho.Tamanho;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Acai implements Serializable {
    private Tamanho tamanho;
    private BigDecimal preco;
    private List<Adicional> adicionais;

    public Acai(Builder builder) {
        this.tamanho = builder.tamanho;
        this.preco = builder.preco;
        this.adicionais = builder.adicionais;
    }

    public Tamanho getTamanho() {
        return tamanho;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public List<Adicional> getAdicionais() {
        return adicionais;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Acai acai = (Acai) o;
        return Objects.equals(tamanho, acai.tamanho) && Objects.equals(adicionais, acai.adicionais);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tamanho, adicionais);
    }

    @Override
    public String toString() {
        return "Acai{" +
                "tamanho=" + tamanho +
                ", preco=" + preco +
                ", adicionais=" + adicionais +
                '}';
    }

    public static class Builder{
        private Tamanho tamanho;
        private BigDecimal preco = BigDecimal.ZERO;
        private List<Adicional> adicionais = new ArrayList<>();

        public Builder tamanho(Tamanho tamanho){
            if (tamanho == null){
                throw new IllegalArgumentException("O tamanho deve ser válido");
            }
            this.tamanho = tamanho;
            return this;
        }

        public Builder preco( BigDecimal preco){
            if (preco == null){
                throw new IllegalArgumentException("Preco não pode ser negativo");
            }
            this.preco = preco;
            return this;
        }

        public Builder adicionais(List<Adicional> adicinais){
            this.adicionais.addAll(adicinais);
            return this;
        }

        public Builder addAdicional(Adicional adicional){
            this.adicionais.add(adicional);
            return this;
        }

        public Acai build(){
            return new Acai(this);
        }

    }

}
