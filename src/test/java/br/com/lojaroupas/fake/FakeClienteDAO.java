package br.com.lojaroupas.fake;

import br.com.lojaroupas.dao.ClienteDAO;
import br.com.lojaroupas.exception.ConflitoException;
import br.com.lojaroupas.model.Cliente;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** DAO em memoria que simula a restricao UNIQUE do e-mail. */
public class FakeClienteDAO implements ClienteDAO {

    private final Map<Long, Cliente> dados = new LinkedHashMap<>();
    private long sequencia = 1;

    @Override
    public void inserir(Cliente cliente) {
        for (Cliente existente : dados.values()) {
            if (existente.getEmail().equals(cliente.getEmail())) {
                throw new ConflitoException("Já existe um cliente cadastrado com esse e-mail");
            }
        }
        cliente.setId(sequencia++);
        dados.put(cliente.getId(), cliente);
    }

    @Override
    public boolean atualizar(Cliente cliente) {
        if (!dados.containsKey(cliente.getId())) {
            return false;
        }
        dados.put(cliente.getId(), cliente);
        return true;
    }

    @Override
    public boolean remover(Long id) {
        return dados.remove(id) != null;
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<Cliente> listar() {
        return new ArrayList<>(dados.values());
    }
}
