package br.com.gs.springboot.gs_springboot.client;

import br.com.gs.springboot.gs_springboot.dto.response.CoordenadasResponse;
import br.com.gs.springboot.gs_springboot.dto.response.LocalizacaoResponse;
import br.com.gs.springboot.gs_springboot.exception.GoogleMapsException;
import br.com.gs.springboot.gs_springboot.exception.ServicoIndisponivelException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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


    public LocalizacaoResponse buscarEnderecoPorCoordenadas(BigDecimal latitude, BigDecimal longitude){
        if(apiKey == null || apiKey.isBlank()){
            throw new GoogleMapsException("A chave da API do Google Maps (GOOGLE_MAPS_API_KEY) não está configurada.");
        }
        try{
            GoogleReverseGeocodeResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/maps/api/geocode/json")
                            .queryParam("latlng", latitude.toPlainString() + "," + longitude.toPlainString())
                            .queryParam("language", "pt-BR")
                            .queryParam("key", apiKey)
                            .build())
                    .retrieve()
                    .body(GoogleReverseGeocodeResponse.class);
            return converterEndereco(response, latitude, longitude);
        } catch (RestClientException ex){
            log.error("Falha ao se comunicar com a API do Google Maps (geocodificação reversa): {}", ex.getClass().getSimpleName());
            throw new ServicoIndisponivelException();
        }
    }

    static LocalizacaoResponse converterEndereco(GoogleReverseGeocodeResponse response, BigDecimal latitude, BigDecimal longitude){
        if(response == null){
            throw new ServicoIndisponivelException();
        }
        if("ZERO_RESULTS".equals(response.status())){
            throw new GoogleMapsException("Não foi possível identificar um endereço para as coordenadas informadas.");
        }
        // o Google responde HTTP 200 mesmo em falha (REQUEST_DENIED, OVER_QUERY_LIMIT...): é erro do serviço, não do usuário.
        if(!"OK".equals(response.status()) || response.results() == null || response.results().isEmpty()){
            log.error("Geocodificação reversa retornou status inesperado do Google: {}", response.status());
            throw new ServicoIndisponivelException();
        }

        List<AddressComponent> componentes = response.results().stream()
                .filter(resultado -> resultado.addressComponents() != null)
                .flatMap(resultado -> resultado.addressComponents().stream())
                .toList();

        String cep = nomeDe(buscarComponente(componentes, "postal_code"));
        cep = cep == null ? null : cep.replaceAll("\\D", "");
        if(cep != null && cep.length() != 8){
            cep = null;
        }

        AddressComponent estado = buscarComponente(componentes, "administrative_area_level_1");

        return new LocalizacaoResponse(
                null,
                cep,
                nomeDe(buscarComponente(componentes, "route")),
                nomeDe(buscarComponente(componentes, "street_number")),
                nomeDe(buscarComponente(componentes, "sublocality_level_1", "sublocality", "neighborhood")),
                nomeDe(buscarComponente(componentes, "administrative_area_level_2", "locality")),
                estado == null ? null : estado.shortName(),
                nomeDe(estado),
                latitude,
                longitude
        );
    }

    /**
     * Busca um componente (lista retornado pelo Google Reverse Geocoding) de endereço específico na resposta do Google, tentando os tipos na ordem de preferência.
     */
    private static AddressComponent buscarComponente(List<AddressComponent> componentes, String... tipos){
        for(String tipo : tipos){
            for(AddressComponent componente : componentes){
                if(componente.types() != null && componente.types().contains(tipo)){
                    return componente;
                }
            }
        }
        return null;
    }

    private static String nomeDe(AddressComponent componente){
        return componente == null ? null : componente.longName();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GoogleReverseGeocodeResponse(String status, List<ReverseGeocodeResult> results) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ReverseGeocodeResult(@JsonProperty("address_components") List<AddressComponent> addressComponents) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AddressComponent(
            @JsonProperty("long_name") String longName,
            @JsonProperty("short_name") String shortName,
            List<String> types) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GoogleGeocodeResponse(String status, List<GeocodeResult> results) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GeocodeResult(Geometry geometry) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Geometry(Location location) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Location(BigDecimal lat, BigDecimal lng) {}
}
