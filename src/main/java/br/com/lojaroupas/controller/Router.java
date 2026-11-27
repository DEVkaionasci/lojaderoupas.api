package br.com.lojaroupas.controller;

import br.com.lojaroupas.command.Command;
import br.com.lojaroupas.exception.MetodoNaoPermitidoException;
import br.com.lojaroupas.exception.RecursoNaoEncontradoException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Descobre qual Command atende cada requisicao (metodo + caminho).
 * Devolve 404 se o caminho nao existe e 405 se existe mas nao para aquele metodo.
 */
public class Router {

    /** Resultado da busca: a rota encontrada e o trecho variavel da URL (ex.: "5" em /produtos/5). */
    public static class Resolucao {
        private final Rota rota;
        private final String parametro;

        public Resolucao(Rota rota, String parametro) {
            this.rota = rota;
            this.parametro = parametro;
        }

        public Rota getRota() {
            return rota;
        }

        public String getParametro() {
            return parametro;
        }
    }

    private final List<Rota> rotas = new ArrayList<>();

    public Router adicionar(String metodo, String regex, boolean protegida, Command command) {
        rotas.add(new Rota(metodo, regex, protegida, command));
        return this;
    }

    public Resolucao resolver(String caminho, String metodo) {
        String path = normalizar(caminho);
        boolean caminhoExiste = false;
        for (Rota rota : rotas) {
            Matcher matcher = rota.getPadrao().matcher(path);
            if (matcher.matches()) {
                caminhoExiste = true;
                if (rota.getMetodo().equalsIgnoreCase(metodo)) {
                    String parametro = matcher.groupCount() >= 1 ? matcher.group(1) : null;
                    return new Resolucao(rota, parametro);
                }
            }
        }
        if (caminhoExiste) {
            throw new MetodoNaoPermitidoException("Método " + metodo + " não permitido para " + path);
        }
        throw new RecursoNaoEncontradoException("Rota não encontrada: " + path);
    }

    /** pathInfo sera "/produtos" para a URL /api/produtos; remove a barra final, se houver. */
    private String normalizar(String caminho) {
        if (caminho == null || caminho.isEmpty()) {
            return "/";
        }
        if (caminho.length() > 1 && caminho.endsWith("/")) {
            return caminho.substring(0, caminho.length() - 1);
        }
        return caminho;
    }
}
