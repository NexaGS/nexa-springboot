package br.com.gs.springboot.gs_springboot.mapper;

import br.com.gs.springboot.gs_springboot.dto.response.LocalizacaoResponse;
import br.com.gs.springboot.gs_springboot.model.Localizacao;

public final class LocalizacaoMapper {
    private LocalizacaoMapper(){

    }

    public static LocalizacaoResponse toResponse(Localizacao localizacao){
        return new LocalizacaoResponse(
                localizacao.getId(),
                localizacao.getCep(),
                localizacao.getLogradouro(),
                localizacao.getNumero(),
                localizacao.getBairro(),
                localizacao.getLatitude().toString(),
                localizacao.getLongitude().toString()
        );
    }
}
