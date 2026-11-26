package br.com.lojaroupas.exception;

public class ConflitoException extends AppException {

    public ConflitoException(String mensagem) {
        super(409, mensagem);
    }
}
