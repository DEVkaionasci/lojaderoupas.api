package br.com.lojaroupas.exception;

public class NaoAutorizadoException extends AppException {

    public NaoAutorizadoException(String mensagem) {
        super(401, mensagem);
    }
}
