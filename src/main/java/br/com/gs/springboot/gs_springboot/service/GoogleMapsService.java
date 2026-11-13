package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.dto.response.CoordenadasResponse;
import br.com.gs.springboot.gs_springboot.dto.response.LocalizacaoResponse;

import java.math.BigDecimal;

public interface GoogleMapsService {
    CoordenadasResponse resolverCoordenadas(BigDecimal latitude, BigDecimal longitude, String endereco);

    LocalizacaoResponse resolverEndereco(BigDecimal latitude, BigDecimal longitude);
}
