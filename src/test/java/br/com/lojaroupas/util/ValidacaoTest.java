package br.com.lojaroupas.util;

import br.com.lojaroupas.exception.NegocioException;
import br.com.lojaroupas.model.Categoria;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidacaoTest {

    @Test
    void cepAceitaMascaraEExigeOitoDigitos() {
        assertEquals("40020000", Validacao.normalizarCep("40020-000"));
        assertThrows(NegocioException.class, () -> Validacao.normalizarCep("123"));
        assertThrows(NegocioException.class, () -> Validacao.normalizarCep(null));
    }

    @Test
    void emailValidoEPadronizadoEmMinusculas() {
        assertEquals("ana@loja.com", Validacao.exigirEmail("  Ana@Loja.com "));
        assertThrows(NegocioException.class, () -> Validacao.exigirEmail("ana-sem-arroba"));
    }

    @Test
    void textoObrigatorioNaoPodeSerVazioNemGrande() {
        assertThrows(NegocioException.class, () -> Validacao.exigirTexto("   ", "nome", 10));
        assertThrows(NegocioException.class, () -> Validacao.exigirTexto("12345678901", "nome", 10));
        assertEquals("ok", Validacao.exigirTexto(" ok ", "nome", 10));
    }

    @Test
    void enumAceitaMinusculasEExplicaOsValoresValidos() {
        assertEquals(Categoria.CALCA, Validacao.converterEnum(Categoria.class, "calca", "categoria"));
        NegocioException erro = assertThrows(NegocioException.class,
                () -> Validacao.converterEnum(Categoria.class, "chapeu", "categoria"));
        assertEquals(true, erro.getMessage().contains("CAMISETA"));
    }
}
