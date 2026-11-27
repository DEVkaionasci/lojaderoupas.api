package br.com.lojaroupas.service;

import br.com.lojaroupas.dto.ItemPedidoRequestDTO;
import br.com.lojaroupas.dto.PedidoRequestDTO;
import br.com.lojaroupas.dto.PedidoResponseDTO;
import br.com.lojaroupas.dto.StatusRequestDTO;
import br.com.lojaroupas.exception.ConflitoException;
import br.com.lojaroupas.exception.NegocioException;
import br.com.lojaroupas.exception.RecursoNaoEncontradoException;
import br.com.lojaroupas.fake.FakeClienteDAO;
import br.com.lojaroupas.fake.FakePedidoDAO;
import br.com.lojaroupas.fake.FakeProdutoDAO;
import br.com.lojaroupas.model.Categoria;
import br.com.lojaroupas.model.Cliente;
import br.com.lojaroupas.model.Produto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PedidoServiceTest {

    private final FakeProdutoDAO produtoDAO = new FakeProdutoDAO();
    private final FakeClienteDAO clienteDAO = new FakeClienteDAO();
    private final FakePedidoDAO pedidoDAO = new FakePedidoDAO(produtoDAO);
    private final PedidoService service = new PedidoService(pedidoDAO, produtoDAO, clienteDAO);

    private Cliente clienteEm(String uf) {
        Cliente c = new Cliente(null, "Ana", "ana" + uf + "@loja.com", "hash", "40020000", "Rua A", "Centro", "Cidade", uf);
        clienteDAO.inserir(c);
        return c;
    }

    private Produto produto(String nome, String preco, int estoque) {
        Produto p = new Produto(null, nome, null, Categoria.CAMISETA, "M", "Branca", new BigDecimal(preco), estoque);
        produtoDAO.inserir(p);
        return p;
    }

    private PedidoRequestDTO pedido(Long clienteId, String pagamento, Object... produtoEQuantidade) {
        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setClienteId(clienteId);
        dto.setFormaPagamento(pagamento);
        List<ItemPedidoRequestDTO> itens = new ArrayList<>();
        for (int i = 0; i < produtoEQuantidade.length; i += 2) {
            ItemPedidoRequestDTO item = new ItemPedidoRequestDTO();
            item.setProdutoId((Long) produtoEQuantidade[i]);
            item.setQuantidade((Integer) produtoEQuantidade[i + 1]);
            itens.add(item);
        }
        dto.setItens(itens);
        return dto;
    }

    @Test
    void pixComTresPecasAplicaDoisDescontosEFreteLocal() {
        Cliente cliente = clienteEm("BA");
        Produto a = produto("Camiseta A", "100.00", 10);
        Produto b = produto("Camiseta B", "50.00", 10);

        PedidoResponseDTO r = service.criar(pedido(cliente.getId(), "PIX", a.getId(), 2, b.getId(), 1));

        assertEquals("250.00", r.getSubtotal().toPlainString());
        // 250 -10% = 225,00 ; 225 -5% = 213,75
        assertEquals("36.25", r.getDesconto().toPlainString());
        assertEquals("PIX 10% + 3+ peças 5%", r.getDescontosAplicados());
        assertEquals("LOCAL", r.getTipoFrete());
        assertEquals("12.00", r.getFrete().toPlainString());
        assertEquals("225.75", r.getTotal().toPlainString());
        assertEquals("CRIADO", r.getStatus());
        assertEquals(2, r.getItens().size());
    }

    @Test
    void cartaoSemDescontoENordesteFazFreteRegional() {
        Cliente cliente = clienteEm("PE");
        Produto a = produto("Camiseta A", "100.00", 10);

        PedidoResponseDTO r = service.criar(pedido(cliente.getId(), "CARTAO", a.getId(), 1));

        assertEquals("0.00", r.getDesconto().toPlainString());
        assertEquals("Nenhum", r.getDescontosAplicados());
        assertEquals("REGIONAL", r.getTipoFrete());
        assertEquals("122.00", r.getTotal().toPlainString());
    }

    @Test
    void outroEstadoPagaFreteNacional() {
        Cliente cliente = clienteEm("SP");
        Produto a = produto("Camiseta A", "100.00", 10);

        PedidoResponseDTO r = service.criar(pedido(cliente.getId(), "BOLETO", a.getId(), 1));

        assertEquals("NACIONAL", r.getTipoFrete());
        assertEquals("138.00", r.getTotal().toPlainString());
    }

    @Test
    void pedidoDeValorAltoTemFreteGratis() {
        Cliente cliente = clienteEm("SP");
        Produto casaco = produto("Casaco", "320.00", 5);

        PedidoResponseDTO r = service.criar(pedido(cliente.getId(), "CARTAO", casaco.getId(), 1));

        assertEquals("GRATIS", r.getTipoFrete());
        assertEquals("0.00", r.getFrete().toPlainString());
        assertEquals("320.00", r.getTotal().toPlainString());
    }

    @Test
    void criarPedidoBaixaOEstoque() {
        Cliente cliente = clienteEm("BA");
        Produto a = produto("Camiseta A", "100.00", 10);

        service.criar(pedido(cliente.getId(), "CARTAO", a.getId(), 4));

        assertEquals(6, produtoDAO.buscarPorId(a.getId()).get().getEstoque());
    }

    @Test
    void mesmoProdutoRepetidoTemAsQuantidadesSomadas() {
        Cliente cliente = clienteEm("BA");
        Produto a = produto("Camiseta A", "100.00", 10);

        PedidoResponseDTO r = service.criar(pedido(cliente.getId(), "CARTAO", a.getId(), 1, a.getId(), 2));

        assertEquals(1, r.getItens().size());
        assertEquals(3, r.getItens().get(0).getQuantidade());
    }

    @Test
    void estoqueInsuficienteGeraConflito() {
        Cliente cliente = clienteEm("BA");
        Produto a = produto("Camiseta A", "100.00", 2);

        assertThrows(ConflitoException.class,
                () -> service.criar(pedido(cliente.getId(), "PIX", a.getId(), 3)));
        assertEquals(2, produtoDAO.buscarPorId(a.getId()).get().getEstoque()); // nada foi baixado
    }

    @Test
    void validacoesDoPedido() {
        Cliente cliente = clienteEm("BA");
        Produto a = produto("Camiseta A", "100.00", 10);

        assertThrows(RecursoNaoEncontradoException.class, () -> service.criar(pedido(999L, "PIX", a.getId(), 1)));
        assertThrows(RecursoNaoEncontradoException.class, () -> service.criar(pedido(cliente.getId(), "PIX", 999L, 1)));
        assertThrows(NegocioException.class, () -> service.criar(pedido(cliente.getId(), "PIX")));
        assertThrows(NegocioException.class, () -> service.criar(pedido(cliente.getId(), "PIX", a.getId(), 0)));
        assertThrows(NegocioException.class, () -> service.criar(pedido(cliente.getId(), "FIADO", a.getId(), 1)));
        assertThrows(NegocioException.class, () -> service.criar(pedido(null, "PIX", a.getId(), 1)));
        assertThrows(NegocioException.class, () -> service.criar(null));
    }

    @Test
    void statusSegueOFluxoPermitido() {
        Cliente cliente = clienteEm("BA");
        Produto a = produto("Camiseta A", "100.00", 10);
        Long id = service.criar(pedido(cliente.getId(), "PIX", a.getId(), 1)).getId();

        StatusRequestDTO pago = new StatusRequestDTO();
        pago.setStatus("pago");
        assertEquals("PAGO", service.atualizarStatus(id, pago).getStatus());

        StatusRequestDTO entregue = new StatusRequestDTO();
        entregue.setStatus("ENTREGUE"); // PAGO -> ENTREGUE pula o envio
        assertThrows(NegocioException.class, () -> service.atualizarStatus(id, entregue));
    }

    @Test
    void cancelarDevolveOEstoqueEBloqueiaNovasMudancas() {
        Cliente cliente = clienteEm("BA");
        Produto a = produto("Camiseta A", "100.00", 10);
        Long id = service.criar(pedido(cliente.getId(), "PIX", a.getId(), 4)).getId();
        assertEquals(6, produtoDAO.buscarPorId(a.getId()).get().getEstoque());

        StatusRequestDTO cancelar = new StatusRequestDTO();
        cancelar.setStatus("CANCELADO");
        assertEquals("CANCELADO", service.atualizarStatus(id, cancelar).getStatus());
        assertEquals(10, produtoDAO.buscarPorId(a.getId()).get().getEstoque());

        StatusRequestDTO pago = new StatusRequestDTO();
        pago.setStatus("PAGO");
        assertThrows(NegocioException.class, () -> service.atualizarStatus(id, pago));
    }

    @Test
    void listaPedidosDoClienteEExigeClienteExistente() {
        Cliente cliente = clienteEm("BA");
        Produto a = produto("Camiseta A", "100.00", 10);
        service.criar(pedido(cliente.getId(), "PIX", a.getId(), 1));
        service.criar(pedido(cliente.getId(), "PIX", a.getId(), 1));

        assertEquals(2, service.listarPorCliente(cliente.getId()).size());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.listarPorCliente(999L));
    }

    @Test
    void buscarPedidoInexistenteGera404() {
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscar(999L));
    }
}
