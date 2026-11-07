package br.com.gs.springboot.gs_springboot.mapper;

import br.com.gs.springboot.gs_springboot.dto.response.ColaboradorResponse;
import br.com.gs.springboot.gs_springboot.model.Colaborador;

public final class ColaboradorMapper {

    private ColaboradorMapper() {
    }

    public static ColaboradorResponse toResponse(Colaborador colaborador) {
        String nomeUnidade = colaborador.getUnidade() != null ? colaborador.getUnidade().getNome(): null;

        return new ColaboradorResponse(
                colaborador.getId(),
                colaborador.getNome(),
                colaborador.getCargo(),
                nomeUnidade
        );
    }
}
