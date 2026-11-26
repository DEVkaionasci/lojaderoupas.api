package br.com.lojaroupas.dao;

import br.com.lojaroupas.model.Categoria;
import br.com.lojaroupas.model.Produto;

import java.util.List;
import java.util.Optional;

/** Contrato de acesso a dados de Produto. A camada de servico so conhece esta interface. */
public interface ProdutoDAO {

    void inserir(Produto produto);

    boolean atualizar(Produto produto);

    boolean remover(Long id);

    Optional<Produto> buscarPorId(Long id);

    List<Produto> listar();

    List<Produto> listarPorCategoria(Categoria categoria);
}
