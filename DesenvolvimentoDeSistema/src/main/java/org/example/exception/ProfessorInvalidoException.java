package org.example.exception;

public class ProfessorInvalidoException extends RuntimeException {
    public ProfessorInvalidoException(String mensagem) {
        super(mensagem);
    }
}
