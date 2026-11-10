package br.com.gs.springboot.gs_springboot.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(
        name = "ColaboradorRequest",
        description = "Payload contendo os dados para cadastro de um novo colaborador (profissional de saúde) vinculado a uma unidade."
)
public record ColaboradorRequest(
        //Algum outro identificador como cpf ou email

        @Schema(
                description = "Nome completo do colaborador.",
                example = "Dra. Ana Paula Souza",
                maxLength = 150,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Nome do colaborador deve ser inserido!")
        @Size(max = 150)
        String nome,

        @Schema(
                description = "Cargo ou função exercida pelo colaborador na unidade de saúde.",
                example = "Enfermeira Triagem",
                maxLength = 50,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Cargo do colaborador deve ser inserido!")
        @Size(max = 50)
        String cargo,

        @Schema(
                description = "Endereço de e-mail institucional do colaborador (utilizado como login).",
                example = "ana.souza@nexa.saude.gov.br",
                format = "email",
                maxLength = 150,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Cargo do colaborador deve ser inserido!")
        @Size(max = 150)
        String email,

        @Schema(
                description = "Senha de acesso do colaborador (será armazenada com hash BCrypt).",
                example = "Senha@2026",
                minLength = 6,
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Senha do colaborador deve ser inserida!")
        @Size(min = 6, max = 100)
        String senha,

        @Schema(
                description = "Identificador único da unidade de saúde onde o colaborador atua.",
                example = "1",
                minimum = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Unidade de atuação do colaborador deve ser inserida!")
        Long idUnidadeSaude
) {
}
