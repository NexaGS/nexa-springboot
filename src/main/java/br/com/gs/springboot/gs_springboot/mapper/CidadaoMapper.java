package br.com.gs.springboot.gs_springboot.mapper;

import br.com.gs.springboot.gs_springboot.dto.response.CidadaoResponse;
import br.com.gs.springboot.gs_springboot.model.Cidadao;
import br.com.gs.springboot.gs_springboot.model.UnidadeSaude;

import java.util.List;

public final class CidadaoMapper {
    public CidadaoMapper() {
    }

    public static CidadaoResponse toResponse(Cidadao cidadao){
        List<String> nomesUnidades = cidadao.getUnidadesFavoritas()
                .stream()
                .map(UnidadeSaude::getNome)
                .toList();

        return new CidadaoResponse(
                cidadao.getId(),
                cidadao.getNome(),
                cidadao.getEmail(),
                cidadao.getDataHoraAceiteLocalizacao(),
                nomesUnidades
        );
    }
}
