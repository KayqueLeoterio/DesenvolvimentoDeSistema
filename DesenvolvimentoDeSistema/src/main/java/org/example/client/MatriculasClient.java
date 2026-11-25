package org.example.client;

import org.example.exception.ServicoIndisponivelException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

/** Consulta o servico de Matriculas (porta 8083): GET /matriculas?alunoId={id}&situacao=ativa. */
@Component
public class MatriculasClient {

    private final RestClient restClient;

    public MatriculasClient(@Qualifier("matriculasRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public boolean alunoPossuiMatriculaAtiva(Long alunoId) {
        try {
            Object corpo = restClient.get()
                    .uri("/matriculas?alunoId={id}&situacao=ativa&size=1", alunoId)
                    .retrieve()
                    .body(Object.class);
            return temAlgumaMatricula(corpo);
        } catch (ResourceAccessException e) {
            throw new ServicoIndisponivelException(
                    "O serviço de Matrículas está fora do ar ou não respondeu a tempo.");
        } catch (RestClientException e) {
            throw new ServicoIndisponivelException("Falha ao consultar o serviço de Matrículas.");
        }
    }

    /**
     * Aceita uma lista simples ou uma página do Spring Data (objeto com "content").
     * Olhar só o "content" funciona nos dois formatos de serialização de página.
     */
    private boolean temAlgumaMatricula(Object corpo) {
        if (corpo instanceof List<?> lista) {
            return !lista.isEmpty();
        }
        if (corpo instanceof Map<?, ?> mapa && mapa.get("content") instanceof List<?> conteudo) {
            return !conteudo.isEmpty();
        }
        throw new ServicoIndisponivelException("Resposta inesperada do serviço de Matrículas.");
    }
}