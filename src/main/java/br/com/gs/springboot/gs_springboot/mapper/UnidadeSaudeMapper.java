package br.com.gs.springboot.gs_springboot.mapper;

import br.com.gs.springboot.gs_springboot.dto.response.UnidadeSaudeResponse;
import br.com.gs.springboot.gs_springboot.model.Municipio;
import br.com.gs.springboot.gs_springboot.model.UnidadeSaude;

public final class UnidadeSaudeMapper {

    private UnidadeSaudeMapper() {
    }

    public static UnidadeSaudeResponse toResponse(UnidadeSaude unidade){
        return toResponse(unidade, null);
    }

    public static UnidadeSaudeResponse toResponse(UnidadeSaude unidade, Double distanciaKm){
        String cep = unidade.getLocal() != null ? unidade.getLocal().getCep() : null;
        String logradouro = unidade.getLocal() != null ? unidade.getLocal().getLogradouro() : null;
        String numero = unidade.getLocal() != null ? unidade.getLocal().getNumero() : null;
        String bairro = unidade.getLocal() != null ? unidade.getLocal().getBairro() : null;

        Municipio municipio = unidade.getLocal() != null ? unidade.getLocal().getMunicipio() : null;
        String nomeMunicipio = municipio != null ? municipio.getNome() : null;
        String uf = municipio != null && municipio.getEstado() != null ? municipio.getEstado().getSigla() : null;

        return new UnidadeSaudeResponse(
                unidade.getId(),
                unidade.getNome(),
                cep,
                logradouro,
                numero,
                bairro,
                unidade.getTempoEspera(),
                unidade.getTipoUnidade(),
                nomeMunicipio,
                uf,
                distanciaKm == null ? null : Math.round(distanciaKm * 10) / 10.0
        );
    }
}
