package br.com.lojaroupas.exception;

public class NegocioException extends AppException {

    public NegocioException(String mensagem) {
        super(400, mensagem);
    }
}
