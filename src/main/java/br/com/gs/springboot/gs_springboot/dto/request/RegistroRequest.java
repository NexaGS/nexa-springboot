package br.com.gs.springboot.gs_springboot.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        name = "RegistroRequest",
        description = "Payload para criar conta de usuário comum (nome, email, senha e aceite de localização)."
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
                description = "Endereço de e-mail válido do usuário. Também é o login.",
                example = "usuario@exemplo.com",
                format = "email",
                maxLength = 150,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        @Size(max = 150, message = "Email deve ter no máximo 150 caracteres")
        String email,

        @Schema(
                description = "Senha de acesso (deve conter entre 8 e 72 caracteres).",
                example = "SenhaForte@2026",
                format = "password",
                minLength = 8,
                maxLength = 72,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 8, max = 72, message = "Senha deve ter entre 8 e 72 caracteres")
        String senha,

        @Schema(
                description = "Confirmação da senha (deve ser igual à senha).",
                example = "SenhaForte@2026",
                format = "password",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Confirmação de senha é obrigatória")
        String confirmarSenha,

        @Schema(
                description = "Aceite do uso da localização. Precisa ser `true` para criar a conta.",
                example = "true",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @AssertTrue(message = "É preciso aceitar o uso da localização para criar a conta")
        boolean aceitouLocalizacao
) {
}
