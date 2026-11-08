package br.com.gs.springboot.gs_springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "ColaboradorResponse",
        description = "Dados do colaborador (profissional de saúde) cadastrado na unidade."
)
public record ColaboradorResponse(
        @Schema(
                description = "Identificador único do colaborador.",
                example = "10",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long id,

        @Schema(
                description = "Nome completo do colaborador.",
                example = "Dra. Ana Paula Souza",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String nome,

        @Schema(
                description = "Cargo ou função exercida na unidade de saúde.",
                example = "Enfermeira Triagem",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String cargo,

        @Schema(
                description = "E-mail institucional do colaborador.",
                example = "ana.souza@nexa.saude.gov.br",
                format = "email",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String email,

        @Schema(
                description = "Nome da unidade de saúde onde o colaborador está lotado.",
                example = "UBS Bela Vista - Paulista",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String nomeUnidadeSaude
) {
}
