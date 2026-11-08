package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.client.GoogleMapsClient;
import br.com.gs.springboot.gs_springboot.client.ViaCepClient;
import br.com.gs.springboot.gs_springboot.dto.response.CoordenadasResponse;
import br.com.gs.springboot.gs_springboot.dto.response.ViaCepResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class GoogleMapsServiceImpl implements GoogleMapsService{
    private final GoogleMapsClient googleMapsClient;
    private final ViaCepClient viaCepClient;

    public GoogleMapsServiceImpl(GoogleMapsClient googleMapsClient, ViaCepClient viaCepClient) {
        this.googleMapsClient = googleMapsClient;
        this.viaCepClient = viaCepClient;
    }

    public CoordenadasResponse resolverCoordenadas(BigDecimal latitude, BigDecimal longitude, String endereco){

        if(latitude != null && longitude != null){
            return new CoordenadasResponse(latitude, longitude);
        }

        if(endereco == null || endereco.isBlank()){
            throw new IllegalArgumentException("Informe um endereço, ou CEP para buscar unidades próximas.");
        }

        String entradaEndereco = endereco.trim();

        String REGEX_CEP = "^\\d{5}-?\\d{3}$";

        if(entradaEndereco.matches(REGEX_CEP)){
            String cep = entradaEndereco.replaceAll("\\D", "");
            ViaCepResponse viaCepResponse = viaCepClient.buscarEndereco(cep);

            String enderecoCompleto = viaCepResponse.logradouro() + ", " + viaCepResponse.bairro() + ", " + viaCepResponse.municipio();

            return googleMapsClient.buscarCoordenadasEndereco(enderecoCompleto);
        }

        return googleMapsClient.buscarCoordenadasEndereco(entradaEndereco);
    }
}
