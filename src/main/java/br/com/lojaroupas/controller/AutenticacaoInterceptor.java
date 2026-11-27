package br.com.lojaroupas.controller;

import br.com.lojaroupas.exception.NaoAutorizadoException;
import jakarta.servlet.http.HttpServletRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Pre-processamento transversal de AUTENTICACAO, executado pelo Front Controller
 * antes de despachar para o Command. Rotas marcadas como protegidas exigem o
 * header "X-API-Key" com a chave do administrador (variavel de ambiente API_KEY).
 *
 * Simplificacao didatica: nao ha login de cliente. Em producao usariamos
 * JWT/sessao e ligariamos o pedido ao usuario autenticado.
 */
public class AutenticacaoInterceptor {

    public static final String HEADER = "X-API-Key";

    private final byte[] chaveEsperada;

    public AutenticacaoInterceptor(String chave) {
        this.chaveEsperada = chave.getBytes(StandardCharsets.UTF_8);
    }

    public void validar(HttpServletRequest request) {
        String recebida = request.getHeader(HEADER);
        // isEqual compara em tempo constante (evita ataques de timing)
        if (recebida == null || !MessageDigest.isEqual(chaveEsperada, recebida.getBytes(StandardCharsets.UTF_8))) {
            throw new NaoAutorizadoException("Acesso negado: informe o header " + HEADER + " com a chave válida");
        }
    }
}
