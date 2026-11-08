package br.com.gs.springboot.gs_springboot.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(
        name = "ViaCepResponse",
        description = "DTO contendo o retorno da consulta de endereço por CEP na API externa ViaCEP."
)
public record ViaCepResponse(
        @Schema(
                description = "CEP retornado pelo ViaCEP.",
                example = "01311-000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String cep,

        @Schema(
                description = "Nome do logradouro.",
                example = "Avenida Paulista",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String logradouro,

        @Schema(
                description = "Complemento do trecho do logradouro.",
                example = "de 1047 a 1865 - lado ímpar",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String complemento,

        @Schema(
                description = "Bairro correspondente ao CEP.",
                example = "Bela Vista",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String bairro,

        @Schema(
                description = "Nome do município (mapeado a partir do campo `localidade` do ViaCEP).",
                example = "São Paulo",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @JsonProperty("localidade")
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
                description = "Nome por extenso do Estado.",
                example = "São Paulo",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String estado,

        @Schema(
                description = "Indicador de CEP inexistente retornado pelo ViaCEP (`\"true\"` quando não encontrado).",
                example = "false",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @JsonProperty("erro")
        String erro
) {
}
