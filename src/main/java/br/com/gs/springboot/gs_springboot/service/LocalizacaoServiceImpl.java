package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.dto.response.LocalizacaoResponse;
import br.com.gs.springboot.gs_springboot.exception.ResourceNotFoundException;
import br.com.gs.springboot.gs_springboot.model.Localizacao;
import br.com.gs.springboot.gs_springboot.repository.LocalizacaoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class LocalizacaoServiceImpl implements LocalizacaoService{
    private final LocalizacaoRepository repository;
    private final GoogleMapsService googleMapsService;

    public LocalizacaoServiceImpl(LocalizacaoRepository repository, GoogleMapsService googleMapsService) {
        this.repository = repository;
        this.googleMapsService = googleMapsService;
    }

    @Override
    public Localizacao findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Localização", id));
    }

    @Override
    public LocalizacaoResponse buscarPorCoordenadas(BigDecimal latitude, BigDecimal longitude) {
        return googleMapsService.resolverEndereco(latitude, longitude);
    }

}
