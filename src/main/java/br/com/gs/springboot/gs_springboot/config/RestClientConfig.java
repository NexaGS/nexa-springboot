package br.com.gs.springboot.gs_springboot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class RestClientConfig {
    @Value("${integracao.http.connect-timeout-ms:3000}")
    private long timeoutConexaoMs;

    @Value("${integracao.http.read-timeout-ms:3000}")
    private long timeoutLeituraMs;

//    @Value("${API_BRASIL_CEP_BASE_URL:viacep.com.br/ws/{cep}/json/}") //FALTA {cep}/

    @Bean(name = "viaCepRestClient")
    public RestClient viaCepRestClient(
            @Value("${integracao.viacep.base-url:https://viacep.com.br/ws}") String baseUrl){
        return buildRestClient(baseUrl);
    }

    @Bean(name = "googleMapsRestClient")
    public RestClient googleMapsRestClient(
            @Value("${integracao.google-maps.base-url:https://maps.googleapis.com}") String baseUrl){
        return buildRestClient(baseUrl);
    }

    private RestClient buildRestClient(String baseUrl){
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(timeoutConexaoMs))
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofMillis(timeoutLeituraMs));

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
