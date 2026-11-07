package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.dto.response.UnidadeSaudeResponse;
import br.com.gs.springboot.gs_springboot.exception.ResourceNotFoundException;
import br.com.gs.springboot.gs_springboot.mapper.UnidadeSaudeMapper;
import br.com.gs.springboot.gs_springboot.model.UnidadeSaude;
import br.com.gs.springboot.gs_springboot.repository.UnidadeSaudeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UnidadeSaudeServiceImpl implements UnidadeSaudeService {
    private final UnidadeSaudeRepository repository;

    public UnidadeSaudeServiceImpl(UnidadeSaudeRepository repository) {
        this.repository = repository;
    }

    @Override
    public UnidadeSaude findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Unidade", id));
    }

    @Override
    public Page<UnidadeSaude> listarTodas(Integer pagina, Integer tamanho) {
        return repository.findAll(Pageable.ofSize(tamanho).withPage(pagina));
    }
}
