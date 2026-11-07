package br.com.gs.springboot.gs_springboot.mapper;

import br.com.gs.springboot.gs_springboot.dto.response.AtendimentoResponse;
import br.com.gs.springboot.gs_springboot.model.Atendimento;

public final class AtendimentoMapper {

    private AtendimentoMapper() {
    }

    public static AtendimentoResponse toResponse(Atendimento atendimento){
        return new AtendimentoResponse(
                atendimento.getId(),
                atendimento.getPaciente().getNome(),
                atendimento.getColaborador().getNome(),
                atendimento.getStatus(),
                atendimento.getDataHoraChegada(),
                atendimento.getDataHoraFim()
        );
    }
}
