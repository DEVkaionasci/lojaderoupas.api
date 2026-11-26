package br.com.lojaroupas.infra;

/**
 * Configuracoes da aplicacao. Cada valor pode ser trocado por variavel de
 * ambiente; se ela nao existir, usamos o padrao de desenvolvimento.
 */
public final class Config {

    private Config() {
    }

    public static int porta() {
        return Integer.parseInt(env("PORT", "8080"));
    }

    public static String dbUrl() {
        return env("DB_URL", "jdbc:postgresql://localhost:5432/loja_roupas");
    }

    public static String dbUser() {
        return env("DB_USER", "postgres");
    }

    public static String dbPassword() {
        return env("DB_PASSWORD", "postgres");
    }

    /** Chave exigida no header X-API-Key para rotas administrativas. */
    public static String apiKey() {
        return env("API_KEY", "loja-admin-123");
    }

    /** UF onde fica a loja (usada para definir o frete local). */
    public static String ufLoja() {
        return env("LOJA_UF", "BA");
    }

    private static String env(String nome, String padrao) {
        String valor = System.getenv(nome);
        return (valor == null || valor.isBlank()) ? padrao : valor;
    }
}
