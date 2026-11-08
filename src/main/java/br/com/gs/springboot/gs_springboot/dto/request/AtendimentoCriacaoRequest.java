package br.com.gs.springboot.gs_springboot.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(
        name = "AtendimentoCriacaoRequest",
        description = "Payload para abertura de atendimento (check-in) quando o cidadão retira uma senha no totem ou aplicativo da unidade de saúde."
)
public record AtendimentoCriacaoRequest(
        //Pensar na possibilidade da unidade ser automática ou "fixa", não sei, penso que o totem estaria configurado com o id da unidade.
        @Schema(
                description = "Identificador único da unidade de saúde onde o atendimento está sendo iniciado.",
                example = "1",
                minimum = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Preencher o ID da unidade (transparente ao cidadão que pega senha)")
        Long idUnidade,

        @Schema(
                description = "CPF do paciente (somente números ou formatado, até 15 caracteres).",
                example = "12345678901",
                maxLength = 15,
                pattern = "^\\d{11}$|^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Obrigatório inserir o CPF!")
        @Size(max = 15)
        String cpf
) {
}
