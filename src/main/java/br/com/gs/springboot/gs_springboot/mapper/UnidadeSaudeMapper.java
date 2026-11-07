package br.com.gs.springboot.gs_springboot.mapper;

import br.com.gs.springboot.gs_springboot.dto.response.UnidadeSaudeResponse;
import br.com.gs.springboot.gs_springboot.model.UnidadeSaude;

public final class UnidadeSaudeMapper {

    private UnidadeSaudeMapper() {
    }

    public static UnidadeSaudeResponse toResponse(UnidadeSaude unidade){
        String cep = unidade.getLocal() != null ? unidade.getLocal().getCep() : null;
        String logradouro = unidade.getLocal() != null ? unidade.getLocal().getLogradouro() : null;
        String numero = unidade.getLocal() != null ? unidade.getLocal().getNumero() : null;
        String bairro = unidade.getLocal() != null ? unidade.getLocal().getBairro() : null;

        return new UnidadeSaudeResponse(
                unidade.getId(),
                unidade.getNome(),
                cep,
                logradouro,
                numero,
                bairro,
                unidade.getTempoMedioEspera()
        );
    }
}
