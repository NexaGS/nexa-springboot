package br.com.gs.springboot.gs_springboot.client;

import br.com.gs.springboot.gs_springboot.dto.response.CoordenadasResponse;
import br.com.gs.springboot.gs_springboot.exception.GoogleMapsException;
import br.com.gs.springboot.gs_springboot.exception.ServicoIndisponivelException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.List;

@Component
public class GoogleMapsClient {
    private static final Logger log = LoggerFactory.getLogger(GoogleMapsClient.class);

    private final RestClient restClient;
    private final String apiKey;

    public GoogleMapsClient(
            @Qualifier("googleMapsRestClient") RestClient restClient,
            @Value("${integracao.google-maps.api-key:${GOOGLE_API_KEY:}}") String apiKey
    ) {
        this.restClient = restClient;
        this.apiKey = apiKey;
    }

    public CoordenadasResponse buscarCoordenadasEndereco(String endereco){
        if(apiKey == null || apiKey.isBlank()){
            throw new GoogleMapsException("A chave da API do Google Maps (GOOGLE_MAPS_API_KEY) não está configurada.");
        }

        try{
            GoogleGeocodeResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/maps/api/geocode/json")
                            .queryParam("address", endereco)
                            .queryParam("region", "br")
                            .queryParam("key", apiKey)
                            .build())
                    .retrieve()
                    .body(GoogleGeocodeResponse.class);

            if(response == null || !"OK".equals(response.status()) || response.results() == null || response.results().isEmpty())  {
                throw new IllegalArgumentException("Não foi possível localizar o endereço informado: " + endereco);
            }

            Location local = response.results().getFirst().geometry().location();
            return new CoordenadasResponse(local.lat, local.lng);

        } catch (RestClientException ex){
            log.error("Falha so se comunicar com a API do Google Maps para o endereço: {}", endereco, ex);
            throw new ServicoIndisponivelException();
        }
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GoogleGeocodeResponse(String status, List<GeocodeResult> results) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GeocodeResult(Geometry geometry) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Geometry(Location location) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Location(BigDecimal lat, BigDecimal lng) {}
}
