package org.example.exception;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Formato padrão de corpo de erro devolvido pela API em JSON.
 * Usado por TratarExecaoController para todas as respostas de erro (400 e 404).
 */
public record ErroRespostaDTO(
        LocalDateTime momento,
        int status,
        String erro,
        String mensagem,
        List<String> detalhes
) {

    public ErroRespostaDTO(int status, String erro, String mensagem, List<String> detalhes) {
        this(LocalDateTime.now(), status, erro, mensagem, detalhes);
    }

    public ErroRespostaDTO(int status, String erro, String mensagem) {
        this(LocalDateTime.now(), status, erro, mensagem, List.of());
    }

}
