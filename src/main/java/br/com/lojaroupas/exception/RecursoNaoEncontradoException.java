package br.com.lojaroupas.exception;

public class RecursoNaoEncontradoException extends AppException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(404, mensagem);
    }
}
