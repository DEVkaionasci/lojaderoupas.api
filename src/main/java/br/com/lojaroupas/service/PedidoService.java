package br.com.lojaroupas.service;

import br.com.lojaroupas.dao.ClienteDAO;
import br.com.lojaroupas.dao.PedidoDAO;
import br.com.lojaroupas.dao.ProdutoDAO;
import br.com.lojaroupas.decorator.PrecoPedido;
import br.com.lojaroupas.decorator.PrecoPedidoFactory;
import br.com.lojaroupas.dto.ItemPedidoRequestDTO;
import br.com.lojaroupas.dto.PedidoRequestDTO;
import br.com.lojaroupas.dto.PedidoResponseDTO;
import br.com.lojaroupas.dto.StatusRequestDTO;
import br.com.lojaroupas.exception.ConflitoException;
import br.com.lojaroupas.exception.NegocioException;
import br.com.lojaroupas.exception.RecursoNaoEncontradoException;
import br.com.lojaroupas.mapper.PedidoMapper;
import br.com.lojaroupas.model.Cliente;
import br.com.lojaroupas.model.FormaPagamento;
import br.com.lojaroupas.model.ItemPedido;
import br.com.lojaroupas.model.Pedido;
import br.com.lojaroupas.model.Produto;
import br.com.lojaroupas.model.StatusPedido;
import br.com.lojaroupas.strategy.FreteStrategy;
import br.com.lojaroupas.strategy.FreteStrategyFactory;
import br.com.lojaroupas.util.Validacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Coracao do sistema: monta o pedido, aplica descontos (Decorator),
 * calcula o frete (Strategy) e pede ao DAO para gravar.
 */
public class PedidoService {

    private static final int MAX_ITENS = 50;
    private static final int MAX_QUANTIDADE_POR_ITEM = 100;

    private final PedidoDAO pedidoDAO;
    private final ProdutoDAO produtoDAO;
    private final ClienteDAO clienteDAO;

    public PedidoService(PedidoDAO pedidoDAO, ProdutoDAO produtoDAO, ClienteDAO clienteDAO) {
        this.pedidoDAO = pedidoDAO;
        this.produtoDAO = produtoDAO;
        this.clienteDAO = clienteDAO;
    }

    public PedidoResponseDTO criar(PedidoRequestDTO dto) {
        validar(dto);
        Cliente cliente = clienteDAO.buscarPorId(dto.getClienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado: " + dto.getClienteId()));
        FormaPagamento forma = Validacao.converterEnum(FormaPagamento.class, dto.getFormaPagamento(), "formaPagamento");

        Pedido pedido = new Pedido();
        pedido.setClienteId(cliente.getId());
        pedido.setDataCriacao(LocalDateTime.now());
        pedido.setStatus(StatusPedido.CRIADO);
        pedido.setFormaPagamento(forma);
        pedido.setCepEntrega(cliente.getCep());
        pedido.setUfEntrega(cliente.getUf());

        for (Map.Entry<Long, Integer> entrada : agruparItens(dto).entrySet()) {
            Produto produto = produtoDAO.buscarPorId(entrada.getKey())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado: " + entrada.getKey()));
            if (!produto.temEstoque(entrada.getValue())) {
                throw new ConflitoException("Estoque insuficiente para '" + produto.getNome()
                        + "' (disponível: " + produto.getEstoque() + ")");
            }
            // o preco e "congelado" no item: mudar o preco do produto depois nao altera pedidos antigos
            pedido.getItens().add(new ItemPedido(null, produto.getId(), produto.getNome(),
                    entrada.getValue(), produto.getPreco()));
        }

        // DECORATOR: empilha os descontos sobre o subtotal
        BigDecimal subtotal = pedido.calcularSubtotal();
        PrecoPedido preco = PrecoPedidoFactory.criar(subtotal, forma, pedido.calcularQuantidadeTotal());
        BigDecimal valorComDesconto = preco.getValor();

        // STRATEGY: escolhe a regra de frete pela UF (vinda do ViaCEP) e pelo valor
        FreteStrategy estrategiaFrete = FreteStrategyFactory.para(pedido.getUfEntrega(), valorComDesconto);
        BigDecimal frete = estrategiaFrete.calcular(valorComDesconto);

        pedido.setSubtotal(subtotal);
        pedido.setDesconto(subtotal.subtract(valorComDesconto));
        pedido.setFrete(frete);
        pedido.setTotal(valorComDesconto.add(frete));
        pedido.setTipoFrete(estrategiaFrete.getNome());
        pedido.setDescontosAplicados(preco.getDescricao().isEmpty() ? "Nenhum" : preco.getDescricao());

        pedidoDAO.inserir(pedido);
        return PedidoMapper.toResponse(pedido);
    }

    public PedidoResponseDTO buscar(Long id) {
        return PedidoMapper.toResponse(obter(id));
    }

    public List<PedidoResponseDTO> listar() {
        return pedidoDAO.listar().stream().map(PedidoMapper::toResponse).collect(Collectors.toList());
    }

    public List<PedidoResponseDTO> listarPorCliente(Long clienteId) {
        clienteDAO.buscarPorId(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado: " + clienteId));
        return pedidoDAO.listarPorCliente(clienteId).stream()
                .map(PedidoMapper::toResponse).collect(Collectors.toList());
    }

    public PedidoResponseDTO atualizarStatus(Long id, StatusRequestDTO dto) {
        Pedido pedido = obter(id);
        StatusPedido novo = Validacao.converterEnum(StatusPedido.class, dto == null ? null : dto.getStatus(), "status");
        if (!pedido.getStatus().podeIrPara(novo)) {
            throw new NegocioException("Transição de status inválida: " + pedido.getStatus() + " -> " + novo);
        }
        if (novo == StatusPedido.CANCELADO) {
            pedidoDAO.cancelar(pedido); // devolve os itens ao estoque
        } else {
            pedidoDAO.atualizarStatus(id, novo);
        }
        pedido.setStatus(novo);
        return PedidoMapper.toResponse(pedido);
    }

    // ------------------------------------------------------------------ privados

    private Pedido obter(Long id) {
        return pedidoDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado: " + id));
    }

    private void validar(PedidoRequestDTO dto) {
        if (dto == null) {
            throw new NegocioException("Dados do pedido não informados");
        }
        if (dto.getClienteId() == null) {
            throw new NegocioException("O campo 'clienteId' é obrigatório");
        }
        if (dto.getItens() == null || dto.getItens().isEmpty()) {
            throw new NegocioException("O pedido precisa ter pelo menos um item");
        }
        if (dto.getItens().size() > MAX_ITENS) {
            throw new NegocioException("O pedido pode ter no máximo " + MAX_ITENS + " itens");
        }
        for (ItemPedidoRequestDTO item : dto.getItens()) {
            if (item == null || item.getProdutoId() == null) {
                throw new NegocioException("Cada item precisa informar 'produtoId'");
            }
            Validacao.exigirPositivo(item.getQuantidade(), "quantidade");
            if (item.getQuantidade() > MAX_QUANTIDADE_POR_ITEM) {
                throw new NegocioException("A quantidade máxima por item é " + MAX_QUANTIDADE_POR_ITEM);
            }
        }
    }

    /** Se o mesmo produto aparecer duas vezes, soma as quantidades. */
    private Map<Long, Integer> agruparItens(PedidoRequestDTO dto) {
        Map<Long, Integer> agrupados = new LinkedHashMap<>();
        for (ItemPedidoRequestDTO item : dto.getItens()) {
            agrupados.merge(item.getProdutoId(), item.getQuantidade(), Integer::sum);
        }
        return agrupados;
    }
}
