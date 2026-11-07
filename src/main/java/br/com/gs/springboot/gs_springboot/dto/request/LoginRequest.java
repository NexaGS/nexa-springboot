package br.com.gs.springboot.gs_springboot.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Modelo de login, contendo email e senha.")
//Estou pensando que inicialmente so teria login o colaborador que encerraria o atendimento para acessar o endpoint de encerramento
public record LoginRequest(
        @NotBlank(message = "Email é obrigatório")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        String senha

) {
}
