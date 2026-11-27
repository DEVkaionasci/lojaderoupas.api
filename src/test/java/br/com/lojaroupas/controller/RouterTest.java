package br.com.lojaroupas.controller;

import br.com.lojaroupas.command.Command;
import br.com.lojaroupas.exception.MetodoNaoPermitidoException;
import br.com.lojaroupas.exception.RecursoNaoEncontradoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RouterTest {

    private final Command listar = (req, res) -> { };
    private final Command buscar = (req, res) -> { };
    private final Command criar = (req, res) -> { };

    private Router router() {
        return new Router()
                .adicionar("GET", "/produtos", false, listar)
                .adicionar("GET", "/produtos/(\\d+)", false, buscar)
                .adicionar("POST", "/produtos", true, criar);
    }

    @Test
    void encontraRotaSemParametro() {
        Router.Resolucao r = router().resolver("/produtos", "GET");
        assertSame(listar, r.getRota().getCommand());
        assertNull(r.getParametro());
    }

    @Test
    void extraiOIdDaUrl() {
        Router.Resolucao r = router().resolver("/produtos/42", "GET");
        assertSame(buscar, r.getRota().getCommand());
        assertEquals("42", r.getParametro());
    }

    @Test
    void ignoraBarraFinalEMetodoEmMinusculas() {
        assertSame(listar, router().resolver("/produtos/", "get").getRota().getCommand());
    }

    @Test
    void marcaRotasProtegidas() {
        assertTrue(router().resolver("/produtos", "POST").getRota().isProtegida());
        assertFalse(router().resolver("/produtos", "GET").getRota().isProtegida());
    }

    @Test
    void caminhoInexistenteGera404() {
        assertThrows(RecursoNaoEncontradoException.class, () -> router().resolver("/camisas", "GET"));
        assertThrows(RecursoNaoEncontradoException.class, () -> router().resolver("/produtos/abc", "GET"));
    }

    @Test
    void metodoNaoSuportadoGera405() {
        assertThrows(MetodoNaoPermitidoException.class, () -> router().resolver("/produtos", "DELETE"));
    }
}
