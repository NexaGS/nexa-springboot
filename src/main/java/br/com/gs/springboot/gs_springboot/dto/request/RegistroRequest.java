package br.com.gs.springboot.gs_springboot.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        name = "RegistroRequest",
        description = "Payload contendo os dados para registro de credenciais de acesso no sistema."
)
public record RegistroRequest(
        @Schema(
                description = "Nome completo do usuário.",
                example = "Carlos Eduardo Mendes",
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        String nome,

        @Schema(
                description = "Endereço de e-mail válido do usuário.",
                example = "carlos.mendes@nexa.com",
                format = "email",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @Schema(
                description = "Senha de acesso (deve conter entre 8 e 72 caracteres).",
                example = "SenhaForte@2026",
                format = "password",
                minLength = 8,
                maxLength = 72,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Senha é obrigatório")
        @Size(min = 8, max = 72, message = "Senha deve ter entre 8 e 72 caracteres")
        String senha
) {
}
