package br.com.gs.springboot.gs_springboot.mapper;

import br.com.gs.springboot.gs_springboot.dto.response.AtendimentoResponse;
import br.com.gs.springboot.gs_springboot.model.Atendimento;

public final class AtendimentoMapper {

    private AtendimentoMapper() {
    }

    public static AtendimentoResponse toResponse(Atendimento atendimento){
        String nomePaciente = atendimento.getPaciente() != null ? atendimento.getPaciente().getNome() : null;
        String nomeColaborador = atendimento.getColaborador() != null ? atendimento.getColaborador().getNome() : "sem colaborador no momento";

        return new AtendimentoResponse(
                atendimento.getId(),
                nomePaciente,
                nomeColaborador,
                atendimento.getStatus(),
                atendimento.getDataHoraChegada(),
                atendimento.getDataHoraFim()
        );
    }
}
