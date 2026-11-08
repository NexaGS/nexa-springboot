package br.com.gs.springboot.gs_springboot.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ViaCepApiClient {
    private final RestClient restClient;

    public ViaCepApiClient(RestClient restClient) {
        this.restClient = restClient;
    }
}
