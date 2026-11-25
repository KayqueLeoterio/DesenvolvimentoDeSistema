package org.example.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.example.exception.AlunoInvalidoException;
import org.example.exception.AlunoNaoEncontradoException;
import org.example.exception.ProfessorInvalidoException;
import org.example.exception.ProfessorNaoEncontradoException;
import org.example.exception.ConflitoException;
import org.example.exception.ErroRespostaAlunoDTO;
import org.example.exception.ErroRespostaDTO;
import org.example.exception.ServicoIndisponivelException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
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

    // 409 - Regra de integridade entre serviços (remoção recusada)
    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErroRespostaDTO> tratarConflito(ConflitoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErroRespostaDTO(HttpStatus.CONFLICT.value(), "Conflito", ex.getMessage()));
    }

    // 503 - Outro serviço fora do ar, com erro ou sem resposta no tempo limite
    @ExceptionHandler(ServicoIndisponivelException.class)
    public ResponseEntity<ErroRespostaDTO> tratarServicoIndisponivel(ServicoIndisponivelException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErroRespostaDTO(HttpStatus.SERVICE_UNAVAILABLE.value(), "Serviço indisponível", ex.getMessage()));
    }

    // 400 - Parâmetro com tipo errado (ex.: /alunos/abc) ou campo de ordenação inexistente
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroRespostaDTO> tratarTipoParametro(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest()
                .body(new ErroRespostaDTO(HttpStatus.BAD_REQUEST.value(), "Requisição inválida", "Parâmetro inválido: " + ex.getName()));
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ErroRespostaDTO> tratarOrdenacaoInvalida(PropertyReferenceException ex) {
        return ResponseEntity.badRequest()
                .body(new ErroRespostaDTO(HttpStatus.BAD_REQUEST.value(), "Requisição inválida", "Campo de ordenação inválido: " + ex.getPropertyName()));
    }
}
