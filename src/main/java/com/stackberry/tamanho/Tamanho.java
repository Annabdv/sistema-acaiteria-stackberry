package com.stackberry.tamanho;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tamanho {
    private String id;
    private String nome;
    private BigDecimal preco;
    private String volume;
    private String descricao;
    private String imagem;

    public Tamanho() {
    }

    public Tamanho(String nome, BigDecimal preco) {
        this.nome = nome;
        this.preco = preco;
    }

    public Tamanho(String nome, BigDecimal preco, String volume, String descricao, String imagem) {
        this.nome = nome;
        this.preco = preco;
        this.volume = volume;
        this.descricao = descricao;
        this.imagem = imagem;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public String getVolume() {
        if (this.volume != null && !this.volume.trim().isEmpty()) {
            return this.volume.trim();
        }
        // Fallback: tentar extrair do nome se tiver formato como (300ml), (500ml) etc.
        if (this.nome != null) {
            if (this.nome.contains("300")) return "300ml";
            if (this.nome.contains("500")) return "500ml";
            if (this.nome.contains("700")) return "700ml";
            if (this.nome.toLowerCase().contains("combo")) return "Combo";
        }
        return "Especial";
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getDescricao() {
        if (this.descricao != null && !this.descricao.trim().isEmpty()) {
            return this.descricao.trim();
        }
        return "Delicioso açaí artesanal batido na cremosidade perfeita com polpa pura selecionada.";
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getImagem() {
        if (this.imagem != null && !this.imagem.trim().isEmpty()) {
            return this.imagem.trim();
        }
        // Fallback baseado no nome
        if (this.nome != null) {
            String n = this.nome.toLowerCase();
            if (n.contains("300") || n.contains("pequeno")) return "/images/acai_copo_tampa.png";
            if (n.contains("500") || n.contains("medio") || n.contains("médio")) return "/images/acai_copo_camadas.png";
            if (n.contains("700") || n.contains("grande") || n.contains("tigela")) return "/images/acai_tigela_branca_frutas.jpg";
            if (n.contains("combo")) return "/images/acai_tigela_palmeira_roxo.jpg";
        }
        return "/images/acai_copo_perfeito.jpg";
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }
}
