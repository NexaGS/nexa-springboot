package br.com.gs.springboot.gs_springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "AuthResponse",
        description = "Payload de resposta contendo o token JWT emitido após autenticação bem-sucedida."
)
public record AuthResponse(
        @Schema(
                description = "Token JWT assinado (HS256) para envio no header `Authorization: Bearer <token>`.",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBuZXhhLmNvbSIsInJvbGUiOiJDT0xBQk9SQURPUiIsImlhdCI6MTc2MjYzMDAwMCwiZXhwIjoxNzYyNjMzNjAwfQ.signature",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String token,

        @Schema(
                description = "Tipo do esquema de autenticação HTTP.",
                example = "Bearer",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String tokenType,

        @Schema(
                description = "Tempo de validade do token em segundos.",
                example = "900",
                minimum = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        long expiresInSeconds,

        @Schema(
                description = "Refresh token opaco e de uso único. O app o guarda no cofre do aparelho e o troca por um novo par de tokens em `POST /api/auth/refresh`.",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String refreshToken,

        @Schema(
                description = "Validade do refresh token em segundos.",
                example = "2592000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        long refreshExpiresInSeconds
) {
}
