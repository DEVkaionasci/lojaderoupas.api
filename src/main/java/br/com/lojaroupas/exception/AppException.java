package br.com.lojaroupas.exception;

/**
 * Excecao base da aplicacao. Carrega o status HTTP que o Front Controller
 * deve devolver ao cliente quando ela for lancada em qualquer camada.
 */
public class AppException extends RuntimeException {

    private final int status;

    public AppException(int status, String mensagem) {
        super(mensagem);
        this.status = status;
    }

    public AppException(int status, String mensagem, Throwable causa) {
        super(mensagem, causa);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
