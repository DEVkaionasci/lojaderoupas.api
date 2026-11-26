package br.com.lojaroupas.exception;

public class MetodoNaoPermitidoException extends AppException {

    public MetodoNaoPermitidoException(String mensagem) {
        super(405, mensagem);
    }
}
