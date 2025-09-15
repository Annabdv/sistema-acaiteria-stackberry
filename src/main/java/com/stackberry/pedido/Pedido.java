package com.stackberry.pedido;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.stackberry.carrinho.Acai;
import com.stackberry.carrinho.ItemCarrinho;
import com.stackberry.login.Usuario;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Pedido {
    private String id;
    private List<ItemCarrinho> itens;
    private BigDecimal valorTotal;
    private String usuarioId;
    private String usuarioNome;
    private String usuarioEmail;
    private boolean realizado;
    private String status;
    private LocalDateTime dataHora;

    public Pedido() {
    }

    public Pedido(List<ItemCarrinho> itens, Usuario usuario) {
        this.id = UUID.randomUUID().toString();
        this.itens = itens.stream().map(i -> {
            Acai novoAcai = new Acai.Builder()
                    .tamanho(i.getAcai().getTamanho())
                    .preco(i.getAcai().getPreco())
                    .adicionais(i.getAcai().getAdicionais() != null ? new ArrayList<>(i.getAcai().getAdicionais()) : new ArrayList<>())
                    .build();

            return new ItemCarrinho(novoAcai, i.getQuantidade(), i.getPrecoUnitario());
        }).collect(Collectors.toList());

        this.valorTotal = itens.stream()
                .map(ItemCarrinho::subTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.usuarioId = usuario.getId();
        this.usuarioNome = usuario.getNome();
        this.usuarioEmail = usuario.getEmail();
        this.realizado = false;
        this.dataHora = LocalDateTime.now();
    }

    public String getDataHoraFormatada() {
        return this.dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getId() { return id; }
    public List<ItemCarrinho> getItens() { return itens; }
    public BigDecimal getValorTotal() { return valorTotal; }

    public String getUsuarioId() {
        return usuarioId;
    }

    public String getUsuarioNome() {
        return usuarioNome;
    }

    public String getUsuarioEmail() {
        return usuarioEmail;
    }

    public boolean isRealizado() {
        return realizado;
    }

    public void setRealizado(boolean realizado) {
        this.realizado = realizado;
    }

    public LocalDateTime getDataHora() {
        return dataHora; }

    public String getStatus() {
        return realizado ? "Concluído" : "Não concluído";
    }

    public void setStatus(String concluído) {
    }
}