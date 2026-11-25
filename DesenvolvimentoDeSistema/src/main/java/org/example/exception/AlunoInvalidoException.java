package org.example.exception;

public class AlunoInvalidoException extends RuntimeException {

    public AlunoInvalidoException(String mensagem) {
        super(mensagem);
    }

}
