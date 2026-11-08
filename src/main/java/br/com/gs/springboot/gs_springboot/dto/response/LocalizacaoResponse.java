package br.com.gs.springboot.gs_springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(
        name = "LocalizacaoResponse",
        description = "DTO contendo as informações de localização normalizadas (3FN) e coordenadas geográficas."
)
public record LocalizacaoResponse(
        @Schema(
                description = "Identificador único da localização.",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long id,

        @Schema(
                description = "CEP sem formatação (8 dígitos numéricos).",
                example = "01311000",
                minLength = 8,
                maxLength = 8,
                pattern = "^\\d{8}$",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String cep,

        @Schema(
                description = "Nome do logradouro (rua, avenida, praça).",
                example = "Avenida Paulista",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String logradouro,

        @Schema(
                description = "Número do imóvel no logradouro (ou S/N).",
                example = "1100",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String numero,

        @Schema(
                description = "Bairro ou distrito da localização.",
                example = "Bela Vista",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String bairro,

        @Schema(
                description = "Nome oficial do município segundo o IBGE.",
                example = "São Paulo",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String municipio,

        @Schema(
                description = "Sigla da Unidade Federativa (UF).",
                example = "SP",
                minLength = 2,
                maxLength = 2,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String uf,

        @Schema(
                description = "Nome completo do Estado.",
                example = "São Paulo",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String estado,

        @Schema(
                description = "Latitude em graus decimais (precisão de 7 casas decimais).",
                example = "-23.5648865",
                minimum = "-90",
                maximum = "90",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        BigDecimal latitude,

        @Schema(
                description = "Longitude em graus decimais (precisão de 7 casas decimais).",
                example = "-46.6529992",
                minimum = "-180",
                maximum = "180",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        BigDecimal longitude
) {
}
