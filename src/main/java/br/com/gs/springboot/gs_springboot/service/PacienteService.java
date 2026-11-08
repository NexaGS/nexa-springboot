package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.model.Paciente;

import java.util.Optional;

public interface PacienteService {
    Paciente findById(Long id);
    Optional<Paciente> buscarPorCpf(String cpf);
}
