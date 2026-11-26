package br.com.lojaroupas.dao;

import br.com.lojaroupas.model.Cliente;

import java.util.List;
import java.util.Optional;

/** Contrato de acesso a dados de Cliente. */
public interface ClienteDAO {

    void inserir(Cliente cliente);

    boolean atualizar(Cliente cliente);

    boolean remover(Long id);

    Optional<Cliente> buscarPorId(Long id);

    List<Cliente> listar();
}
