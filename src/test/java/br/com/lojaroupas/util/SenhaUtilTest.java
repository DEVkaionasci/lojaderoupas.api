package br.com.lojaroupas.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SenhaUtilTest {

    @Test
    void hashNuncaEIgualASenhaEMudaACadaChamada() {
        String h1 = SenhaUtil.hash("segredo123");
        String h2 = SenhaUtil.hash("segredo123");
        assertNotEquals("segredo123", h1);
        assertNotEquals(h1, h2); // salt aleatorio
        assertFalse(h1.contains("segredo123"));
    }

    @Test
    void confereSenhaCorretaERejeitaIncorreta() {
        String hash = SenhaUtil.hash("segredo123");
        assertTrue(SenhaUtil.confere("segredo123", hash));
        assertFalse(SenhaUtil.confere("outra-senha", hash));
    }
}
