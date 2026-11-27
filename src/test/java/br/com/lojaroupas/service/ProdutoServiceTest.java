package br.com.lojaroupas.service;

import br.com.lojaroupas.dto.ProdutoRequestDTO;
import br.com.lojaroupas.dto.ProdutoResponseDTO;
import br.com.lojaroupas.exception.NegocioException;
import br.com.lojaroupas.exception.RecursoNaoEncontradoException;
import br.com.lojaroupas.fake.FakeProdutoDAO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProdutoServiceTest {

    private final ProdutoService service = new ProdutoService(new FakeProdutoDAO());

    private ProdutoRequestDTO dto(String nome, String categoria, String preco, Integer estoque) {
        ProdutoRequestDTO dto = new ProdutoRequestDTO();
        dto.setNome(nome);
        dto.setCategoria(categoria);
        dto.setTamanho("M");
        dto.setCor("Azul");
        dto.setPreco(preco == null ? null : new BigDecimal(preco));
        dto.setEstoque(estoque);
        return dto;
    }

    @Test
    void criaProdutoEPadronizaPrecoComDuasCasas() {
        ProdutoResponseDTO r = service.criar(dto("Camiseta", "camiseta", "49.9", 10));

        assertEquals(1L, r.getId());
        assertEquals("CAMISETA", r.getCategoria());
        assertEquals("49.90", r.getPreco().toPlainString());
    }

    @Test
    void rejeitaDadosInvalidos() {
        assertThrows(NegocioException.class, () -> service.criar(null));
        assertThrows(NegocioException.class, () -> service.criar(dto("", "CAMISETA", "10", 1)));
        assertThrows(NegocioException.class, () -> service.criar(dto("Camiseta", "CHAPEU", "10", 1)));
        assertThrows(NegocioException.class, () -> service.criar(dto("Camiseta", "CAMISETA", "0", 1)));
        assertThrows(NegocioException.class, () -> service.criar(dto("Camiseta", "CAMISETA", "-5", 1)));
        assertThrows(NegocioException.class, () -> service.criar(dto("Camiseta", "CAMISETA", null, 1)));
        assertThrows(NegocioException.class, () -> service.criar(dto("Camiseta", "CAMISETA", "10", -1)));
        assertThrows(NegocioException.class, () -> service.criar(dto("Camiseta", "CAMISETA", "10", null)));
    }

    @Test
    void filtraPorCategoria() {
        service.criar(dto("Camiseta", "CAMISETA", "50", 5));
        service.criar(dto("Calça", "CALCA", "150", 5));
        service.criar(dto("Camiseta 2", "CAMISETA", "60", 5));

        assertEquals(3, service.listar(null).size());
        assertEquals(2, service.listar("camiseta").size());
        assertEquals(1, service.listar("CALCA").size());
        assertThrows(NegocioException.class, () -> service.listar("INEXISTENTE"));
    }

    @Test
    void atualizaERemove() {
        service.criar(dto("Camiseta", "CAMISETA", "50", 5));

        ProdutoResponseDTO r = service.atualizar(1L, dto("Camiseta Premium", "CAMISETA", "80", 3));
        assertEquals("Camiseta Premium", service.buscar(1L).getNome());
        assertEquals("80.00", r.getPreco().toPlainString());

        service.remover(1L);
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscar(1L));
    }

    @Test
    void atualizarOuRemoverInexistenteGera404() {
        assertThrows(RecursoNaoEncontradoException.class, () -> service.atualizar(9L, dto("X", "CAMISETA", "10", 1)));
        assertThrows(RecursoNaoEncontradoException.class, () -> service.remover(9L));
    }
}
