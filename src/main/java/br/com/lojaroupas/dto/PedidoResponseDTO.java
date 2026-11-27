package br.com.lojaroupas.dto;

import java.math.BigDecimal;
import java.util.List;

public class PedidoResponseDTO {

    private Long id;
    private Long clienteId;
    private String dataCriacao;
    private String status;
    private String formaPagamento;
    private String cepEntrega;
    private String ufEntrega;
    private List<ItemPedidoResponseDTO> itens;
    private BigDecimal subtotal;
    private BigDecimal desconto;
    private BigDecimal frete;
    private BigDecimal total;
    private String tipoFrete;
    private String descontosAplicados;

    public PedidoResponseDTO() {
    }

    public PedidoResponseDTO(Long id, Long clienteId, String dataCriacao, String status, String formaPagamento, String cepEntrega, String ufEntrega, List<ItemPedidoResponseDTO> itens, BigDecimal subtotal, BigDecimal desconto, BigDecimal frete, BigDecimal total, String tipoFrete, String descontosAplicados) {
        this.id = id;
        this.clienteId = clienteId;
        this.dataCriacao = dataCriacao;
        this.status = status;
        this.formaPagamento = formaPagamento;
        this.cepEntrega = cepEntrega;
        this.ufEntrega = ufEntrega;
        this.itens = itens;
        this.subtotal = subtotal;
        this.desconto = desconto;
        this.frete = frete;
        this.total = total;
        this.tipoFrete = tipoFrete;
        this.descontosAplicados = descontosAplicados;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(String dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public String getCepEntrega() {
        return cepEntrega;
    }

    public void setCepEntrega(String cepEntrega) {
        this.cepEntrega = cepEntrega;
    }

    public String getUfEntrega() {
        return ufEntrega;
    }

    public void setUfEntrega(String ufEntrega) {
        this.ufEntrega = ufEntrega;
    }

    public List<ItemPedidoResponseDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoResponseDTO> itens) {
        this.itens = itens;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getFrete() {
        return frete;
    }

    public void setFrete(BigDecimal frete) {
        this.frete = frete;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getTipoFrete() {
        return tipoFrete;
    }

    public void setTipoFrete(String tipoFrete) {
        this.tipoFrete = tipoFrete;
    }

    public String getDescontosAplicados() {
        return descontosAplicados;
    }

    public void setDescontosAplicados(String descontosAplicados) {
        this.descontosAplicados = descontosAplicados;
    }
}
