package br.com.gs.springboot.gs_springboot.client;

import br.com.gs.springboot.gs_springboot.client.GoogleMapsClient.AddressComponent;
import br.com.gs.springboot.gs_springboot.client.GoogleMapsClient.GoogleReverseGeocodeResponse;
import br.com.gs.springboot.gs_springboot.client.GoogleMapsClient.ReverseGeocodeResult;
import br.com.gs.springboot.gs_springboot.dto.response.LocalizacaoResponse;
import br.com.gs.springboot.gs_springboot.exception.GoogleMapsException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Testa só a leitura da resposta do Google: não chama a internet nem sobe o Spring. */
class GoogleMapsClientTest {

    private static final BigDecimal LATITUDE = new BigDecimal("-23.5648865");
    private static final BigDecimal LONGITUDE = new BigDecimal("-46.6529992");

    private static AddressComponent componente(String nome, String sigla, String... tipos) {
        return new AddressComponent(nome, sigla, List.of(tipos));
    }

    private static GoogleReverseGeocodeResponse resposta(String status, AddressComponent... componentes) {
        return new GoogleReverseGeocodeResponse(status, List.of(new ReverseGeocodeResult(List.of(componentes))));
    }

    @Test
    void montaOEnderecoNoContratoDeLocalizacaoResponse() {
        var response = resposta("OK",
                componente("1100", "1100", "street_number"),
                componente("Avenida Paulista", "Av. Paulista", "route"),
                componente("Bela Vista", "Bela Vista", "political", "sublocality", "sublocality_level_1"),
                componente("São Paulo", "São Paulo", "administrative_area_level_2", "political"),
                componente("São Paulo", "SP", "administrative_area_level_1", "political"),
                componente("01311-000", "01311-000", "postal_code"));

        LocalizacaoResponse local = GoogleMapsClient.converterEndereco(response, LATITUDE, LONGITUDE);

        assertNull(local.id());
        assertEquals("01311000", local.cep());
        assertEquals("Avenida Paulista", local.logradouro());
        assertEquals("1100", local.numero());
        assertEquals("Bela Vista", local.bairro());
        assertEquals("São Paulo", local.municipio());
        assertEquals("SP", local.uf());
        assertEquals("São Paulo", local.estado());
        assertEquals(LATITUDE, local.latitude());
        assertEquals(LONGITUDE, local.longitude());
    }

    @Test
    void usaOsTiposDeReservaParaBairroEMunicipio() {
        var response = resposta("OK",
                componente("Vila Nova", "Vila Nova", "neighborhood", "political"),
                componente("Cidadezinha", "Cidadezinha", "locality", "political"),
                componente("Minas Gerais", "MG", "administrative_area_level_1", "political"));

        LocalizacaoResponse local = GoogleMapsClient.converterEndereco(response, LATITUDE, LONGITUDE);

        assertEquals("Vila Nova", local.bairro());
        assertEquals("Cidadezinha", local.municipio());
        assertEquals("MG", local.uf());
    }

    @Test
    void camposQueOGoogleNaoTrazemVemNulos() {
        var response = resposta("OK",
                componente("São Paulo", "São Paulo", "administrative_area_level_2", "political"));

        LocalizacaoResponse local = GoogleMapsClient.converterEndereco(response, LATITUDE, LONGITUDE);

        assertEquals("São Paulo", local.municipio());
        assertNull(local.logradouro());
        assertNull(local.numero());
        assertNull(local.bairro());
        assertNull(local.cep());
        assertNull(local.uf());
        assertNull(local.estado());
    }

    @Test
    void cepComMenosDeOitoDigitosVemNulo() {
        var response = resposta("OK",
                componente("01311", "01311", "postal_code"),
                componente("São Paulo", "São Paulo", "administrative_area_level_2", "political"));

        assertNull(GoogleMapsClient.converterEndereco(response, LATITUDE, LONGITUDE).cep());
    }

    @Test
    void statusDiferenteDeOkViraGoogleMapsException() {
        assertThrows(GoogleMapsException.class,
                () -> GoogleMapsClient.converterEndereco(resposta("ZERO_RESULTS"), LATITUDE, LONGITUDE));
        assertThrows(GoogleMapsException.class,
                () -> GoogleMapsClient.converterEndereco(null, LATITUDE, LONGITUDE));
    }
}
