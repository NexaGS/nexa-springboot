package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.exception.ResourceNotFoundException;
import br.com.gs.springboot.gs_springboot.model.Paciente;
import br.com.gs.springboot.gs_springboot.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PacienteServiceImpl implements PacienteService {
    private final PacienteRepository repository;

    public PacienteServiceImpl(PacienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public Paciente findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Paciente", id));
    }

    //Para inicio de atendimento simulando uma base de registros do SUS
    @Override
    public Optional<Paciente> buscarPorCpf(String cpf){
        return repository.findByCpf(cpf);
    }
}
