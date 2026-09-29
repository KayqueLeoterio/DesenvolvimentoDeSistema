package org.example.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.example.exception.AlunoInvalidoException;
import org.example.exception.AlunoNaoEncontradoException;
import org.example.exception.ProfessorInvalidoException;
import org.example.exception.ProfessorNaoEncontradoException;
import org.example.exception.ErroRespostaAlunoDTO;
import org.example.exception.ErroRespostaProfessorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class TratarExecaoController {

    // 404 - Aluno
    @ExceptionHandler(AlunoNaoEncontradoException.class)
    public ResponseEntity<ErroRespostaAlunoDTO> tratarNaoEncontrado(AlunoNaoEncontradoException ex) {
        ErroRespostaAlunoDTO erro = new ErroRespostaAlunoDTO(HttpStatus.NOT_FOUND.value(), "Não encontrado", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    // 404 - Professor
    @ExceptionHandler(ProfessorNaoEncontradoException.class)
    public ResponseEntity<ErroRespostaProfessorDTO> tratarProfessorNaoEncontrado(ProfessorNaoEncontradoException ex) {
        ErroRespostaProfessorDTO erro = new ErroRespostaProfessorDTO(HttpStatus.NOT_FOUND.value(), "Não encontrado", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    // 400 - Aluno
    @ExceptionHandler(AlunoInvalidoException.class)
    public ResponseEntity<ErroRespostaAlunoDTO> tratarRequisicaoInvalida(AlunoInvalidoException ex) {
        ErroRespostaAlunoDTO erro = new ErroRespostaAlunoDTO(HttpStatus.BAD_REQUEST.value(), "Requisição inválida", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // 400 - Professor (Ex: SIAPE duplicado)
    @ExceptionHandler(ProfessorInvalidoException.class)
    public ResponseEntity<ErroRespostaProfessorDTO> tratarProfessorInvalido(ProfessorInvalidoException ex) {
        ErroRespostaProfessorDTO erro = new ErroRespostaProfessorDTO(HttpStatus.BAD_REQUEST.value(), "Requisição inválida", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // 400 - Campos inválidos (Bean Validation de Aluno e Professor)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroRespostaAlunoDTO> tratarCamposInvalidos(MethodArgumentNotValidException ex) {
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .toList();
        ErroRespostaAlunoDTO erro = new ErroRespostaAlunoDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Dados inválidos",
                "Um ou mais campos não passaram na validação",
                detalhes
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // 400 - JSON malformado ou com tipo incompatível
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroRespostaAlunoDTO> tratarJsonInvalido(HttpMessageNotReadableException ex) {
        String mensagem = ex.getCause() instanceof InvalidFormatException
                ? "Um dos campos enviados está em um formato inválido"
                : "O corpo da requisição não é um JSON válido";
        ErroRespostaAlunoDTO erro = new ErroRespostaAlunoDTO(HttpStatus.BAD_REQUEST.value(), "JSON inválido", mensagem);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }
}
