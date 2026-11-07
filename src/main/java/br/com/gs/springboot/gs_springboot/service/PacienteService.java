package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.model.Paciente;

public interface PacienteService {
    Paciente findById(Long id);
}
