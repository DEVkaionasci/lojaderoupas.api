package br.com.lojaroupas.exception;

/** Erro inesperado de banco de dados. Vira HTTP 500. */
public class PersistenciaException extends AppException {

    public PersistenciaException(String mensagem, Throwable causa) {
        super(500, mensagem, causa);
    }
}
