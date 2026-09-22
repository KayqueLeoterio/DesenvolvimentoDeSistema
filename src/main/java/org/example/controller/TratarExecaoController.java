package org.example.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.example.exception.AlunoInvalidoException;
import org.example.exception.ErroRespostaDTO;
import org.example.exception.AlunoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;


@RestControllerAdvice
public class TratarExecaoController {
    // 404
    @ExceptionHandler(AlunoNaoEncontradoException.class)
    public ResponseEntity<ErroRespostaDTO> tratarNaoEncontrado(AlunoNaoEncontradoException ex) {
        ErroRespostaDTO erro = new ErroRespostaDTO(HttpStatus.NOT_FOUND.value(), "Não encontrado", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }
    // 400
    @ExceptionHandler(AlunoInvalidoException.class)
    public ResponseEntity<ErroRespostaDTO> tratarRequisicaoInvalida(AlunoInvalidoException ex) {
        ErroRespostaDTO erro = new ErroRespostaDTO(HttpStatus.BAD_REQUEST.value(), "Requisição inválida", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }




}
