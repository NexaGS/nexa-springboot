package br.com.gs.springboot.gs_springboot.mapper;

import br.com.gs.springboot.gs_springboot.dto.response.PacienteResponse;
import br.com.gs.springboot.gs_springboot.model.Paciente;

import java.util.Optional;

public final class PacienteMapper {

    private PacienteMapper() {
    }

    public static PacienteResponse toResponse(Paciente paciente){
        return new PacienteResponse(
                paciente.getId()
        );
    }

    //Para atribuição de paciente no atendimento - nao sei se a melhor solução
    public static Paciente toModel(Optional<Paciente> paciente){
        return new Paciente();
    }
}
