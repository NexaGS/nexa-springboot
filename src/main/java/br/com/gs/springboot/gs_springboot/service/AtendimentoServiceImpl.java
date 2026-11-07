package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.exception.ResourceNotFoundException;
import br.com.gs.springboot.gs_springboot.model.Atendimento;
import br.com.gs.springboot.gs_springboot.repository.AtendimentoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class AtendimentoServiceImpl implements AtendimentoService {
    private final AtendimentoRepository repository;

    public AtendimentoServiceImpl(AtendimentoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Atendimento findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Atendimento", id));
    }


}
