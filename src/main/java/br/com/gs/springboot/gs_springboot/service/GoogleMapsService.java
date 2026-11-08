package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.dto.response.CoordenadasResponse;

import java.math.BigDecimal;

public interface GoogleMapsService {
    CoordenadasResponse resolverCoordenadas(BigDecimal latitude, BigDecimal longitude, String endereco);
}
