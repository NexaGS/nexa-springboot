package br.com.gs.springboot.gs_springboot.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "LocalizacaoRequest",
        description = "Payload para busca ou geocodificação de endereço a partir de um CEP ou logradouro textual."
)
public record LocalizacaoRequest(
        @Schema(
                description = "CEP (8 dígitos, com ou sem hífen) ou endereço textual completo para geocodificação.",
                example = "01311-000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String entradaEndereco
) {
}
