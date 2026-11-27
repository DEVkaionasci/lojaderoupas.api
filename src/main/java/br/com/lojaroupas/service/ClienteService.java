package br.com.lojaroupas.service;

import br.com.lojaroupas.dao.ClienteDAO;
import br.com.lojaroupas.dto.ClienteRequestDTO;
import br.com.lojaroupas.dto.ClienteResponseDTO;
import br.com.lojaroupas.exception.NegocioException;
import br.com.lojaroupas.exception.RecursoNaoEncontradoException;
import br.com.lojaroupas.integration.CepGateway;
import br.com.lojaroupas.integration.EnderecoCep;
import br.com.lojaroupas.mapper.ClienteMapper;
import br.com.lojaroupas.model.Cliente;
import br.com.lojaroupas.util.SenhaUtil;
import br.com.lojaroupas.util.Validacao;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Regras de negocio de clientes.
 * O cliente informa so o CEP; o endereco e preenchido consultando a API externa (ViaCEP).
 */
public class ClienteService {

    private static final int SENHA_MINIMA = 6;
    private static final int SENHA_MAXIMA = 72;

    private final ClienteDAO clienteDAO;
    private final CepGateway cepGateway;

    public ClienteService(ClienteDAO clienteDAO, CepGateway cepGateway) {
        this.clienteDAO = clienteDAO;
        this.cepGateway = cepGateway;
    }

    public ClienteResponseDTO criar(ClienteRequestDTO dto) {
        if (dto == null) {
            throw new NegocioException("Dados do cliente não informados");
        }
        String nome = Validacao.exigirTexto(dto.getNome(), "nome", 120);
        String email = Validacao.exigirEmail(dto.getEmail());
        validarSenha(dto.getSenha());
        EnderecoCep endereco = cepGateway.consultar(dto.getCep());

        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setEmail(email);
        cliente.setSenhaHash(SenhaUtil.hash(dto.getSenha()));
        aplicarEndereco(cliente, endereco);

        clienteDAO.inserir(cliente);
        return ClienteMapper.toResponse(cliente);
    }

    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {
        if (dto == null) {
            throw new NegocioException("Dados do cliente não informados");
        }
        Cliente cliente = obter(id);
        cliente.setNome(Validacao.exigirTexto(dto.getNome(), "nome", 120));
        cliente.setEmail(Validacao.exigirEmail(dto.getEmail()));
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) { // na edicao a senha e opcional
            validarSenha(dto.getSenha());
            cliente.setSenhaHash(SenhaUtil.hash(dto.getSenha()));
        }
        aplicarEndereco(cliente, cepGateway.consultar(dto.getCep()));

        if (!clienteDAO.atualizar(cliente)) {
            throw new RecursoNaoEncontradoException("Cliente não encontrado: " + id);
        }
        return ClienteMapper.toResponse(cliente);
    }

    public ClienteResponseDTO buscar(Long id) {
        return ClienteMapper.toResponse(obter(id));
    }

    public List<ClienteResponseDTO> listar() {
        return clienteDAO.listar().stream().map(ClienteMapper::toResponse).collect(Collectors.toList());
    }

    public void remover(Long id) {
        if (!clienteDAO.remover(id)) {
            throw new RecursoNaoEncontradoException("Cliente não encontrado: " + id);
        }
    }

    private Cliente obter(Long id) {
        return clienteDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado: " + id));
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.length() < SENHA_MINIMA || senha.length() > SENHA_MAXIMA) {
            throw new NegocioException("A senha deve ter entre " + SENHA_MINIMA + " e " + SENHA_MAXIMA + " caracteres");
        }
    }

    private void aplicarEndereco(Cliente cliente, EnderecoCep endereco) {
        cliente.setCep(endereco.getCep());
        cliente.setLogradouro(endereco.getLogradouro());
        cliente.setBairro(endereco.getBairro());
        cliente.setCidade(endereco.getCidade());
        cliente.setUf(endereco.getUf());
    }
}
