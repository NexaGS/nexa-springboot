package br.com.gs.springboot.gs_springboot.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GoogleMapsApiClient {
    private static final Logger log = LoggerFactory.getLogger(GoogleMapsApiClient.class);

    private final RestClient restClient;
    private final String apiKey;

    public GoogleMapsApiClient(
            @Qualifier("googleMapsRestClient") RestClient restClient,
            @Value("${}") String apiKey
    ) {
        this.restClient = restClient;
        this.apiKey = apiKey;
    }
}
