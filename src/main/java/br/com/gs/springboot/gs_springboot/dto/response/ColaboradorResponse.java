package br.com.gs.springboot.gs_springboot.dto.response;

public record ColaboradorResponse(
        Long id,
        String nome,
        String cargo,
        String email,
        String nomeUnidadeSaude
) {
}
