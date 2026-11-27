package br.com.lojaroupas.util;

import br.com.lojaroupas.exception.NegocioException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.regex.Pattern;

/** Validacoes simples e reutilizaveis. Todas lancam NegocioException (HTTP 400). */
public final class Validacao {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private Validacao() {
    }

    /** Texto obrigatorio, sem espacos nas pontas e com tamanho maximo. Devolve o texto "limpo". */
    public static String exigirTexto(String valor, String campo, int tamanhoMaximo) {
        if (valor == null || valor.isBlank()) {
            throw new NegocioException("O campo '" + campo + "' é obrigatório");
        }
        String limpo = valor.trim();
        if (limpo.length() > tamanhoMaximo) {
            throw new NegocioException("O campo '" + campo + "' deve ter no máximo " + tamanhoMaximo + " caracteres");
        }
        return limpo;
    }

    /** Texto opcional: null/vazio vira null; se vier preenchido respeita o tamanho maximo. */
    public static String textoOpcional(String valor, String campo, int tamanhoMaximo) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return exigirTexto(valor, campo, tamanhoMaximo);
    }

    public static void exigirPrecoPositivo(BigDecimal valor, String campo) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegocioException("O campo '" + campo + "' deve ser maior que zero");
        }
    }

    public static void exigirNaoNegativo(Integer valor, String campo) {
        if (valor == null || valor < 0) {
            throw new NegocioException("O campo '" + campo + "' é obrigatório e não pode ser negativo");
        }
    }

    public static void exigirPositivo(Integer valor, String campo) {
        if (valor == null || valor <= 0) {
            throw new NegocioException("O campo '" + campo + "' deve ser maior que zero");
        }
    }

    public static String exigirEmail(String valor) {
        String email = exigirTexto(valor, "email", 150).toLowerCase();
        if (!EMAIL.matcher(email).matches()) {
            throw new NegocioException("E-mail inválido");
        }
        return email;
    }

    /** Remove tudo que nao for digito e exige exatamente 8 digitos. */
    public static String normalizarCep(String cep) {
        if (cep == null) {
            throw new NegocioException("O campo 'cep' é obrigatório");
        }
        String digitos = cep.replaceAll("\\D", "");
        if (digitos.length() != 8) {
            throw new NegocioException("CEP inválido: informe 8 dígitos");
        }
        return digitos;
    }

    /** Converte texto em enum aceitando maiusculas/minusculas e explicando os valores validos. */
    public static <E extends Enum<E>> E converterEnum(Class<E> tipo, String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new NegocioException("O campo '" + campo + "' é obrigatório");
        }
        try {
            return Enum.valueOf(tipo, valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NegocioException("Valor inválido para '" + campo + "': " + valor
                    + ". Valores aceitos: " + Arrays.toString(tipo.getEnumConstants()));
        }
    }
}
