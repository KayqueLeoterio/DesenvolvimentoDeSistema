package org.example.exception;

/** Operacao recusada por regra de integridade entre servicos (HTTP 409). */
public class ConflitoException extends RuntimeException {
    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
