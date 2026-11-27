package br.com.lojaroupas.fake;

import br.com.lojaroupas.dao.ProdutoDAO;
import br.com.lojaroupas.model.Categoria;
import br.com.lojaroupas.model.Produto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** DAO "de mentira" que guarda tudo em memoria. Permite testar o Service sem banco de dados. */
public class FakeProdutoDAO implements ProdutoDAO {

    private final Map<Long, Produto> dados = new LinkedHashMap<>();
    private long sequencia = 1;

    @Override
    public void inserir(Produto produto) {
        produto.setId(sequencia++);
        dados.put(produto.getId(), produto);
    }

    @Override
    public boolean atualizar(Produto produto) {
        if (!dados.containsKey(produto.getId())) {
            return false;
        }
        dados.put(produto.getId(), produto);
        return true;
    }

    @Override
    public boolean remover(Long id) {
        return dados.remove(id) != null;
    }

    @Override
    public Optional<Produto> buscarPorId(Long id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<Produto> listar() {
        return new ArrayList<>(dados.values());
    }

    @Override
    public List<Produto> listarPorCategoria(Categoria categoria) {
        List<Produto> resultado = new ArrayList<>();
        for (Produto p : dados.values()) {
            if (p.getCategoria() == categoria) {
                resultado.add(p);
            }
        }
        return resultado;
    }
}
