package br.com.gs.springboot.gs_springboot.dto.response;

public record UnidadeSaudeResponse(
        Long id,
        String nomeUnidade,
        String cep,
        String logradouro,
        String numero,
        String bairro,
        Double tempoMedioEspera
) {
}
