package br.com.gs.springboot.gs_springboot.dto.response;

import br.com.gs.springboot.gs_springboot.model.TipoUnidade;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "UnidadeSaudeResponse",
        description = "Dados de uma Unidade de Saúde do SUS, incluindo endereço e tempo estimado de espera na fila."
)
public record UnidadeSaudeResponse(
        @Schema(
                description = "Identificador único da unidade de saúde.",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long id,

        @Schema(
                description = "Nome oficial da unidade de saúde.",
                example = "UBS Bela Vista - Paulista",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String nomeUnidade,

        @Schema(
                description = "CEP da unidade de saúde (8 dígitos).",
                example = "01311000",
                minLength = 8,
                maxLength = 8,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String cep,

        @Schema(
                description = "Logradouro (rua/avenida) onde a unidade está localizada.",
                example = "Avenida Paulista",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String logradouro,

        @Schema(
                description = "Número do endereço da unidade.",
                example = "1100",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String numero,

        @Schema(
                description = "Bairro onde a unidade está situada.",
                example = "Bela Vista",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String bairro,

        @Schema(
                description = "Tempo estimado de espera atual na fila (em minutos), calculado com base nos atendimentos em aberto.",
                example = "40.0",
                minimum = "0",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Double tempoEspera,

        @Schema(
                description = "Classificação da unidade de saúde segundo os códigos do CNES/SUS.",
                example = "CENTRO_DE_SAUDE_UNIDADE_BASICA",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        TipoUnidade tipoUnidade,

        @Schema(
                description = "Município onde a unidade está localizada.",
                example = "São Paulo",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String municipio,

        @Schema(
                description = "Sigla do estado (UF) onde a unidade está localizada.",
                example = "SP",
                minLength = 2,
                maxLength = 2,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String uf,

        @Schema(
                description = "Distância em km (uma casa decimal) até o ponto informado. Só vem preenchida na busca por proximidade.",
                example = "2.4",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        Double distanciaKm
) {
}
