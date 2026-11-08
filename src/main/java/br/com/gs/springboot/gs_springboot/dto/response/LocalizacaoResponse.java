package br.com.gs.springboot.gs_springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "DTO contendo as informações de localização com coordenadas geográficas")
public record LocalizacaoResponse(
        @Schema(description = "ID da localização", example = "1")
        Long id,

        @Schema(description = "CEP (8 dígitos)", example = "01311000")
        String cep,

        @Schema(description = "Logradouro", example = "Avenida Paulista")
        String logradouro,

        @Schema(description = "Número", example = "1100")
        String numero,

        @Schema(description = "Bairro", example = "Bela Vista")
        String bairro,

        @Schema(description = "Município", example = "São Paulo")
        String municipio,

        @Schema(description = "Sigla da UF", example = "SP")
        String uf,

        @Schema(description = "Estado", example = "São Paulo")
        String estado,

        @Schema(description = "Latitude em graus decimais", example = "-23.5648865")
        BigDecimal latitude,

        @Schema(description = "Longitude em graus decimais", example = "-46.6529992")
        BigDecimal longitude
) {
}
