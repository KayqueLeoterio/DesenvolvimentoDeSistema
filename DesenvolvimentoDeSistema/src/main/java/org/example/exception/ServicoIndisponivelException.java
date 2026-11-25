package org.example.exception;

/** Outro servico fora do ar, com erro ou que estourou o timeout (HTTP 503). */
public class ServicoIndisponivelException extends RuntimeException {
    public ServicoIndisponivelException(String mensagem) {
        super(mensagem);
    }
}
