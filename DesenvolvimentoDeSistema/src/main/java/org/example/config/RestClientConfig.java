package org.example.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    @Qualifier("turmasRestClient")
    public RestClient turmasRestClient(
            @Value("${servicos.turmas.url}") String url,
            @Value("${servicos.http.connect-timeout:2000}") long connectTimeout,
            @Value("${servicos.http.read-timeout:3000}") long readTimeout) {
        return criar(url, connectTimeout, readTimeout);
    }

    @Bean
    @Qualifier("matriculasRestClient")
    public RestClient matriculasRestClient(
            @Value("${servicos.matriculas.url}") String url,
            @Value("${servicos.http.connect-timeout:2000}") long connectTimeout,
            @Value("${servicos.http.read-timeout:3000}") long readTimeout) {
        return criar(url, connectTimeout, readTimeout);
    }

    private RestClient criar(String baseUrl, long connectTimeout, long readTimeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(connectTimeout));
        factory.setReadTimeout(Duration.ofMillis(readTimeout));
        return RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }
}
