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
    // 404 - id não encontrado
    @ExceptionHandler(AlunoNaoEncontradoException.class)
    public ResponseEntity<ErroRespostaDTO> tratarNaoEncontrado(AlunoNaoEncontradoException ex) {
        ErroRespostaDTO erro = new ErroRespostaDTO(HttpStatus.NOT_FOUND.value(), "Não encontrado", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }
    // 400 - regra de negócio violada (ex: matrícula duplicada)
    @ExceptionHandler(AlunoInvalidoException.class)
    public ResponseEntity<ErroRespostaDTO> tratarRequisicaoInvalida(AlunoInvalidoException ex) {
        ErroRespostaDTO erro = new ErroRespostaDTO(HttpStatus.BAD_REQUEST.value(), "Requisição inválida", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }
    // 400 - campos inválidos (Bean Validation: @NotBlank, @Email, @Past, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroRespostaDTO> tratarCamposInvalidos(MethodArgumentNotValidException ex) {
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .toList();
        ErroRespostaDTO erro = new ErroRespostaDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Dados inválidos",
                "Um ou mais campos não passaram na validação",
                detalhes
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }
    // 400 - JSON malformado ou com tipo incompatível (ex: data em formato errado)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroRespostaDTO> tratarJsonInvalido(HttpMessageNotReadableException ex) {
        String mensagem = ex.getCause() instanceof InvalidFormatException
                ? "Um dos campos enviados está em um formato inválido"
                : "O corpo da requisição não é um JSON válido";
        ErroRespostaDTO erro = new ErroRespostaDTO(HttpStatus.BAD_REQUEST.value(), "JSON inválido", mensagem);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

}
