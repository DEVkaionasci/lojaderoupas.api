package br.com.lojaroupas.service;

import br.com.lojaroupas.dao.ProdutoDAO;
import br.com.lojaroupas.dto.ProdutoRequestDTO;
import br.com.lojaroupas.dto.ProdutoResponseDTO;
import br.com.lojaroupas.exception.NegocioException;
import br.com.lojaroupas.exception.RecursoNaoEncontradoException;
import br.com.lojaroupas.mapper.ProdutoMapper;
import br.com.lojaroupas.model.Categoria;
import br.com.lojaroupas.model.Produto;
import br.com.lojaroupas.util.Validacao;

import java.util.List;
import java.util.stream.Collectors;

/** Regras de negocio do catalogo de produtos. */
public class ProdutoService {

    private final ProdutoDAO produtoDAO;

    public ProdutoService(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
    }

    public List<ProdutoResponseDTO> listar(String categoria) {
        List<Produto> produtos;
        if (categoria == null || categoria.isBlank()) {
            produtos = produtoDAO.listar();
        } else {
            produtos = produtoDAO.listarPorCategoria(Validacao.converterEnum(Categoria.class, categoria, "categoria"));
        }
        return produtos.stream().map(ProdutoMapper::toResponse).collect(Collectors.toList());
    }

    public ProdutoResponseDTO buscar(Long id) {
        return ProdutoMapper.toResponse(obter(id));
    }

    public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {
        validar(dto);
        Produto produto = ProdutoMapper.toEntity(dto);
        produtoDAO.inserir(produto);
        return ProdutoMapper.toResponse(produto);
    }

    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        validar(dto);
        Produto produto = ProdutoMapper.toEntity(dto);
        produto.setId(id);
        if (!produtoDAO.atualizar(produto)) {
            throw new RecursoNaoEncontradoException("Produto não encontrado: " + id);
        }
        return ProdutoMapper.toResponse(produto);
    }

    public void remover(Long id) {
        if (!produtoDAO.remover(id)) {
            throw new RecursoNaoEncontradoException("Produto não encontrado: " + id);
        }
    }

    private Produto obter(Long id) {
        return produtoDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado: " + id));
    }

    private void validar(ProdutoRequestDTO dto) {
        if (dto == null) {
            throw new NegocioException("Dados do produto não informados");
        }
        dto.setNome(Validacao.exigirTexto(dto.getNome(), "nome", 150));
        dto.setDescricao(Validacao.textoOpcional(dto.getDescricao(), "descricao", 500));
        Validacao.converterEnum(Categoria.class, dto.getCategoria(), "categoria");
        dto.setTamanho(Validacao.textoOpcional(dto.getTamanho(), "tamanho", 10));
        dto.setCor(Validacao.textoOpcional(dto.getCor(), "cor", 30));
        Validacao.exigirPrecoPositivo(dto.getPreco(), "preco");
        Validacao.exigirNaoNegativo(dto.getEstoque(), "estoque");
    }
}
