package org.example.client;

import org.example.exception.ServicoIndisponivelException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Consulta o servico de Turmas (porta 8082): GET /turmas?professorId={id}. */
@Component
public class TurmasClient {

    private final RestClient restClient;

    public TurmasClient(@Qualifier("turmasRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Devolve as turmas do professor. Aceita tanto uma lista simples quanto uma
     * pagina do Spring Data (objeto com o campo "content").
     */
    public List<Object> listarTurmasDoProfessor(Long professorId) {
        try {
            Object corpo = restClient.get()
                    .uri("/turmas?professorId={id}&size=100", professorId)
                    .retrieve()
                    .body(Object.class);
            return extrairLista(corpo);
        } catch (ResourceAccessException e) {
            throw new ServicoIndisponivelException(
                    "O serviço de Turmas está fora do ar ou não respondeu a tempo.");
        } catch (RestClientException e) {
            throw new ServicoIndisponivelException("Falha ao consultar o serviço de Turmas.");
        }
    }

    @SuppressWarnings("unchecked")
    private List<Object> extrairLista(Object corpo) {
        if (corpo instanceof List<?> lista) {
            return new ArrayList<>(lista);
        }
        if (corpo instanceof Map<?, ?> mapa && mapa.get("content") instanceof List<?> conteudo) {
            return new ArrayList<>(conteudo);
        }
        throw new ServicoIndisponivelException("Resposta inesperada do serviço de Turmas.");
    }
}
