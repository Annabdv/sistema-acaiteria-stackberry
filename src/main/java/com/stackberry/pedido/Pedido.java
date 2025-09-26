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
    private StatusPedido status;
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
        this.dataHora = LocalDateTime.now();
        this.status = StatusPedido.EM_PREPARO;
    }

    public String getDataHoraFormatada() {
        return this.dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public String getId() { return id; }
    public List<ItemCarrinho> getItens() { return itens; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public String getUsuarioId() { return usuarioId; }
    public String getUsuarioNome() { return usuarioNome; }
    public String getUsuarioEmail() { return usuarioEmail; }
    public LocalDateTime getDataHora() { return dataHora; }


    public String toWhatsAppMessage() {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        sb.append("*Novo Pedido StackBerry!*").append("\n\n");
        sb.append("*Cliente:* ").append(this.usuarioNome).append("\n");
        sb.append("*Data:* ").append(this.dataHora.format(formatter)).append("\n");
        sb.append("----------------------------").append("\n");
        sb.append("*ITENS:*").append("\n");

        if (this.itens != null && !this.itens.isEmpty()) {
            for (ItemCarrinho item : this.itens) {
                String descricaoItem = item.getAcai().getTamanho().getNome();
                if (item.getAcai().getAdicionais() != null && !item.getAcai().getAdicionais().isEmpty()) {
                    String adicionaisStr = item.getAcai().getAdicionais().stream()
                            .map(adicional -> adicional.getNome())
                            .collect(Collectors.joining(", "));
                    descricaoItem += " (" + adicionaisStr + ")";
                }
                sb.append(String.format("- %dx %s", item.getQuantidade(), descricaoItem)).append("\n");
            }
        }

        sb.append("----------------------------").append("\n");
        sb.append(String.format("*VALOR TOTAL: R$ %.2f*", this.valorTotal));

        return sb.toString();
    }
}