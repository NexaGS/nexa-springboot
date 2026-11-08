package br.com.gs.springboot.gs_springboot.dto.errors;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(
        name = "ErrorResponse",
        description = "Estrutura padronizada de resposta para erros de regra de negócio, recursos não encontrados (404), conflitos (409), transições inválidas (422) e falhas internas (500)."
)
public record ErrorResponse(

        @Schema(
                description = "Mensagem amigável e detalhada descrevendo a causa do erro.",
                example = "Unidade de id (999) não encontrado!",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String mensagem,

        @Schema(
                description = "Descrição textual padrão do status HTTP retornado.",
                example = "Not Found",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String erro,

        @Schema(
                description = "Código numérico do status HTTP da resposta.",
                example = "404",
                minimum = "100",
                maximum = "599",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        int status,

        @Schema(
                description = "Data e hora exatas em que a exceção foi capturada pelo GlobalExceptionHandler (ISO-8601).",
                example = "2026-11-08T18:30:00",
                type = "string",
                format = "date-time",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        LocalDateTime timestamp
) {}
