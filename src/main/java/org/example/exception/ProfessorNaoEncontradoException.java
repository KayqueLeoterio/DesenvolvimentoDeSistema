package org.example.exception;

public class ProfessorNaoEncontradoException extends RuntimeException {
    public ProfessorNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
