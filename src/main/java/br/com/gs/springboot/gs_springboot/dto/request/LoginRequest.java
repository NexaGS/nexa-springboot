package br.com.gs.springboot.gs_springboot.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(
        name = "LoginRequest",
        description = "Credenciais de autenticação do colaborador para emissão do token JWT."
)
//Estou pensando que inicialmente so teria login o colaborador que encerraria o atendimento para acessar o endpoint de encerramento
public record LoginRequest(
        @Schema(
                description = "E-mail cadastrado do colaborador.",
                example = "admin@nexa.com",
                format = "email",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Email é obrigatório")
        String email,

        @Schema(
                description = "Senha de acesso do colaborador em texto plano.",
                example = "admin123",
                format = "password",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Senha é obrigatória")
        String senha

) {
}
