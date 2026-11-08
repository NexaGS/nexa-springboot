package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.exception.ResourceNotFoundException;
import br.com.gs.springboot.gs_springboot.model.Localizacao;
import br.com.gs.springboot.gs_springboot.repository.LocalizacaoRepository;
import org.springframework.stereotype.Service;

@Service
public class LocalizacaoServiceImpl implements LocalizacaoService{
    private final LocalizacaoRepository repository;

    public LocalizacaoServiceImpl(LocalizacaoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Localizacao findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Localização", id));
    }

}
