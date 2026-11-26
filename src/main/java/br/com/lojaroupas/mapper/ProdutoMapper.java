package br.com.lojaroupas.mapper;

import br.com.lojaroupas.dto.ProdutoRequestDTO;
import br.com.lojaroupas.dto.ProdutoResponseDTO;
import br.com.lojaroupas.model.Categoria;
import br.com.lojaroupas.model.Produto;
import br.com.lojaroupas.util.Validacao;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Converte entre Produto (dominio) e seus DTOs. */
public final class ProdutoMapper {

    private ProdutoMapper() {
    }

    public static Produto toEntity(ProdutoRequestDTO dto) {
        Produto produto = new Produto();
        produto.setNome(dto.getNome().trim());
        produto.setDescricao(dto.getDescricao());
        produto.setCategoria(Validacao.converterEnum(Categoria.class, dto.getCategoria(), "categoria"));
        produto.setTamanho(dto.getTamanho());
        produto.setCor(dto.getCor());
        produto.setPreco(dto.getPreco().setScale(2, RoundingMode.HALF_UP));
        produto.setEstoque(dto.getEstoque());
        return produto;
    }

    public static ProdutoResponseDTO toResponse(Produto produto) {
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getCategoria().name(),
                produto.getTamanho(),
                produto.getCor(),
                produto.getPreco(),
                produto.getEstoque());
    }
}
