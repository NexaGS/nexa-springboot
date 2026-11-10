package br.com.gs.springboot.gs_springboot.mapper;

import br.com.gs.springboot.gs_springboot.dto.response.AtendimentoResponse;
import br.com.gs.springboot.gs_springboot.model.Atendimento;

public final class AtendimentoMapper {

    private static final Double MINUTOS_PACIENTE = 20.0;

    private AtendimentoMapper() {
    }

    public static AtendimentoResponse toResponse(Atendimento atendimento){
        return toResponse(atendimento, 0);
    }

    public static AtendimentoResponse toResponse(Atendimento atendimento, int posicaoFila){
        String nomePaciente = atendimento.getPaciente() != null ? atendimento.getPaciente().getNome() : null;
        String nomeColaborador = atendimento.getColaborador() != null ? atendimento.getColaborador().getNome() : "sem colaborador no momento";
        Double tempoEsperaMinutos = posicaoFila * MINUTOS_PACIENTE;

        return new AtendimentoResponse(
                atendimento.getId(),
                nomePaciente,
                nomeColaborador,
                atendimento.getStatus(),
                atendimento.getDataHoraChegada(),
                atendimento.getDataHoraFim(),
                atendimento.getDataHoraChamada(),
                atendimento.getSenha(),
                atendimento.getServico(),
                posicaoFila,
                tempoEsperaMinutos
        );
    }

}
