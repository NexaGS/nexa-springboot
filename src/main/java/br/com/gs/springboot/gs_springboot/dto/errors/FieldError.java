package br.com.gs.springboot.gs_springboot.dto.errors;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "FieldError",
        description = "Representa uma violação de validação associada a um campo ou parâmetro específico da requisição."
)
public record FieldError(
        @Schema(
                description = "Nome do atributo ou parâmetro que falhou na validação.",
                example = "cpf",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String campo,

        @Schema(
                description = "Mensagem explicativa detalhando a regra de validação violada.",
                example = "Obrigatório inserir o CPF!",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String mensagem
) {}
