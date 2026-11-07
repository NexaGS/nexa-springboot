package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.model.Localizacao;

public interface LocalizacaoService {
    Localizacao findById(Long id);
}
