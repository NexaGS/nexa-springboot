package br.com.gs.springboot.gs_springboot.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "RefreshRequest", description = "Refresh token recebido no login, no registro ou na última renovação.")
public record RefreshRequest(
        @Schema(description = "Refresh token opaco. Cada um só vale para uma renovação.", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "O refresh token é obrigatório")
        String refreshToken
) {
}
