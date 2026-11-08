package br.com.gs.springboot.gs_springboot.mapper;

import br.com.gs.springboot.gs_springboot.dto.response.LocalizacaoResponse;
import br.com.gs.springboot.gs_springboot.model.Localizacao;

public final class LocalizacaoMapper {
    private LocalizacaoMapper(){

    }

    public static LocalizacaoResponse toResponse(Localizacao localizacao){
        String nomeMunicipio = localizacao.getMunicipio() != null ? localizacao.getMunicipio().getNome() : "sem município";

        String uf = (localizacao.getMunicipio() != null && localizacao.getMunicipio().getEstado() != null) ?
                localizacao.getMunicipio().getEstado().getCodigoUf() : null;

        String nomeEstado = (localizacao.getMunicipio() != null && localizacao.getMunicipio().getEstado() != null) ?
                localizacao.getMunicipio().getEstado().getNome() : null;

        return new LocalizacaoResponse(
                localizacao.getId(),
                localizacao.getCep(),
                localizacao.getLogradouro(),
                localizacao.getNumero(),
                localizacao.getBairro(),
                nomeMunicipio,
                uf,
                nomeEstado,
                localizacao.getLatitude(),
                localizacao.getLongitude()
        );
    }
}
