package br.com.lojaroupas.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Gera o hash da senha com PBKDF2 (ja vem no Java, sem biblioteca extra).
 * A senha NUNCA e guardada em texto puro e o hash NUNCA sai da API (o DTO de
 * resposta do cliente nao tem esse campo).
 * Formato guardado: iteracoes:salt:hash (Base64).
 */
public final class SenhaUtil {

    private static final int ITERACOES = 65_536;
    private static final int TAMANHO_HASH_BITS = 256;
    private static final int TAMANHO_SALT_BYTES = 16;

    private SenhaUtil() {
    }

    public static String hash(String senha) {
        byte[] salt = new byte[TAMANHO_SALT_BYTES];
        new SecureRandom().nextBytes(salt);
        byte[] hash = pbkdf2(senha.toCharArray(), salt, ITERACOES);
        return ITERACOES + ":" + Base64.getEncoder().encodeToString(salt)
                + ":" + Base64.getEncoder().encodeToString(hash);
    }

    /** Compara uma senha digitada com o hash guardado (pronto para um futuro login). */
    public static boolean confere(String senha, String guardado) {
        String[] partes = guardado.split(":");
        if (partes.length != 3) {
            return false;
        }
        int iteracoes = Integer.parseInt(partes[0]);
        byte[] salt = Base64.getDecoder().decode(partes[1]);
        byte[] esperado = Base64.getDecoder().decode(partes[2]);
        byte[] calculado = pbkdf2(senha.toCharArray(), salt, iteracoes);
        return java.security.MessageDigest.isEqual(esperado, calculado);
    }

    private static byte[] pbkdf2(char[] senha, byte[] salt, int iteracoes) {
        try {
            PBEKeySpec spec = new PBEKeySpec(senha, salt, iteracoes, TAMANHO_HASH_BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Não foi possível gerar o hash da senha", e);
        }
    }
}
