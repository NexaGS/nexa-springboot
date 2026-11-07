package br.com.gs.springboot.gs_springboot.dto.response;

import br.com.gs.springboot.gs_springboot.model.TipoUnidade;

public record UnidadeSaudeResponse(
        Long id,
        String nomeUnidade,
        String cep,
        String logradouro,
        String numero,
        String bairro,
        Double tempoEspera,
        TipoUnidade tipoUnidade
) {
}
