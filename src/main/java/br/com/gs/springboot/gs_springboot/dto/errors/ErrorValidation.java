package br.com.gs.springboot.gs_springboot.dto.errors;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(
        name = "ErrorValidation",
        description = "Estrutura de resposta retornada quando uma ou mais validações de entrada (Jakarta Bean Validation) falham na requisição (HTTP 400)."
)
public record ErrorValidation(
        @Schema(
                description = "Descrição textual do status HTTP de erro de validação.",
                example = "Bad Request",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String erro,

        @Schema(
                description = "Código HTTP retornado para falha de validação.",
                example = "400",
                minimum = "400",
                maximum = "499",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        int status,

        @ArraySchema(
                schema = @Schema(implementation = FieldError.class),
                arraySchema = @Schema(
                        description = "Lista detalhada contendo todos os campos ou parâmetros que violaram regras de validação.",
                        requiredMode = Schema.RequiredMode.REQUIRED
                )
        )
        List<FieldError> campos,

        @Schema(
                description = "Data e hora em que a validação da requisição falhou (ISO-8601).",
                example = "2026-11-08T18:30:00",
                type = "string",
                format = "date-time",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        LocalDateTime timestamp
) {}
