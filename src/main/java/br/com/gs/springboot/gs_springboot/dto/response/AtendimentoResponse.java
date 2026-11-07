package br.com.gs.springboot.gs_springboot.dto.response;

import br.com.gs.springboot.gs_springboot.model.StatusAtendimento;

import java.time.LocalDateTime;

public record AtendimentoResponse(
        Long id,
        String nomePaciente,
        String nomeColaborador,
        StatusAtendimento status,
        LocalDateTime horaChegada,
        LocalDateTime horaFim
) {
}
