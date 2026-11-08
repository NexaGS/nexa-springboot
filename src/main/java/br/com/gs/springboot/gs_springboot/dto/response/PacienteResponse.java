package br.com.gs.springboot.gs_springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "PacienteResponse",
        description = "Dados resumidos de identificação do paciente."
)
public record PacienteResponse(
        @Schema(
                description = "Identificador único do paciente cadastrado.",
                example = "15",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long id
) {
}
