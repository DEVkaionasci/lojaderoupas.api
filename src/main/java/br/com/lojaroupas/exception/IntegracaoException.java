package br.com.lojaroupas.exception;

/** Falha ao falar com uma API externa (ex.: ViaCEP). Vira HTTP 502. */
public class IntegracaoException extends AppException {

    public IntegracaoException(String mensagem) {
        super(502, mensagem);
    }

    public IntegracaoException(String mensagem, Throwable causa) {
        super(502, mensagem, causa);
    }
}
