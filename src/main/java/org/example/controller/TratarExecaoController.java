package org.example.controller;

import org.example.exception.AlunoInvalidoException;
import org.example.exception.ErroRespostaAlunoDTO;
import org.example.exception.AlunoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class TratarExecaoController {
    // 404
    @ExceptionHandler(AlunoNaoEncontradoException.class)
    public ResponseEntity<ErroRespostaAlunoDTO> tratarNaoEncontrado(AlunoNaoEncontradoException ex) {
        ErroRespostaAlunoDTO erro = new ErroRespostaAlunoDTO(HttpStatus.NOT_FOUND.value(), "Não encontrado", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }
    // 400
    @ExceptionHandler(AlunoInvalidoException.class)
    public ResponseEntity<ErroRespostaAlunoDTO> tratarRequisicaoInvalida(AlunoInvalidoException ex) {
        ErroRespostaAlunoDTO erro = new ErroRespostaAlunoDTO(HttpStatus.BAD_REQUEST.value(), "Requisição inválida", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }




}
