package br.com.gs.springboot.gs_springboot.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Entrada de informações de um novo colaborador que será registrado.")
public record ColaboradorRequest(
        //Algum outro identificador como cpf ou email

        @NotBlank(message = "Nome do colaborador deve ser inserido!")
        @Size(max = 150)
        String nome,

        @NotBlank(message = "Cargo do colaborador deve ser inserido!")
        @Size(max = 50)
        String cargo,

        @NotBlank(message = "Cargo do colaborador deve ser inserido!")
        @Size(max = 150)
        String email,

        @NotNull(message = "Unidade de atuação do colaborador deve ser inserida!")
        Long idUnidadeSaude
) {
}
