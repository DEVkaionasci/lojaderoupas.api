package br.com.lojaroupas.model;

import java.math.BigDecimal;

public class Produto {

    private Long id;
    private String nome;
    private String descricao;
    private Categoria categoria;
    private String tamanho;
    private String cor;
    private BigDecimal preco;
    private int estoque;

    public Produto() {
    }

    public Produto(Long id, String nome, String descricao, Categoria categoria, String tamanho, String cor, BigDecimal preco, int estoque) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
        this.tamanho = tamanho;
        this.cor = cor;
        this.preco = preco;
        this.estoque = estoque;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String tamanho) {
        this.tamanho = tamanho;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

    public boolean temEstoque(int quantidade) {
        return estoque >= quantidade;
    }
}
