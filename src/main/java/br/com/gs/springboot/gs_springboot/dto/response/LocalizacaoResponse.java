package br.com.gs.springboot.gs_springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO contendo as informações de localização")
public record LocalizacaoResponse(
        Long id,
        String cep,
        String logradouro,
        String numero,
        String bairro,
        String latitude,
        String longitude
) {
}
