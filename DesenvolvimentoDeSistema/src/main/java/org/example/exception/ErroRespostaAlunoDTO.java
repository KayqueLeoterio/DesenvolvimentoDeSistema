package org.example.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErroRespostaAlunoDTO(
        LocalDateTime momento,
        int status,
        String erro,
        String mensagem,
        List<String> detalhes
) {

    public ErroRespostaAlunoDTO(int status, String erro, String mensagem, List<String> detalhes) {
        this(LocalDateTime.now(), status, erro, mensagem, detalhes);
    }

    public ErroRespostaAlunoDTO(int status, String erro, String mensagem) {
        this(LocalDateTime.now(), status, erro, mensagem, List.of());
    }

}
