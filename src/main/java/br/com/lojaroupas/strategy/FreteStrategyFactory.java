package br.com.lojaroupas.strategy;

import br.com.lojaroupas.infra.Config;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Escolhe qual FreteStrategy usar. A UF vem do endereco do cliente, que por sua
 * vez foi obtido na API externa ViaCEP: o dado externo participa da regra de negocio.
 */
public final class FreteStrategyFactory {

    public static final BigDecimal LIMITE_FRETE_GRATIS = new BigDecimal("300.00");

    private static final Set<String> NORDESTE = Set.of("AL", "BA", "CE", "MA", "PB", "PE", "PI", "RN", "SE");

    private FreteStrategyFactory() {
    }

    public static FreteStrategy para(String uf, BigDecimal valorPedido) {
        if (valorPedido.compareTo(LIMITE_FRETE_GRATIS) >= 0) {
            return new FreteGratisStrategy();
        }
        if (uf == null || uf.isBlank()) {
            return new FreteNacionalStrategy();
        }
        String ufNormalizada = uf.trim().toUpperCase();
        if (ufNormalizada.equals(Config.ufLoja())) {
            return new FreteLocalStrategy();
        }
        if (NORDESTE.contains(ufNormalizada)) {
            return new FreteRegionalStrategy();
        }
        return new FreteNacionalStrategy();
    }
}
