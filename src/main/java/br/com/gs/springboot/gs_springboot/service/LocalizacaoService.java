package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.dto.response.LocalizacaoResponse;
import br.com.gs.springboot.gs_springboot.model.Localizacao;

import java.math.BigDecimal;

public interface LocalizacaoService {
    Localizacao findById(Long id);

    LocalizacaoResponse buscarPorCoordenadas(BigDecimal latitude, BigDecimal longitude);
}
