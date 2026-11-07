package br.com.gs.springboot.gs_springboot.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Entrada de informações para iniciar atendimento - cidadão pegando senha no totem da unidade de saúde.")
public record AtendimentoCriacaoRequest(
        //Pensar na possibilidade da unidade ser automática ou "fixa", não sei, penso que o totem estaria configurado com o id da unidade.

        @NotBlank(message = "Obrigatório inserir o CPF!")
        @Size(max = 15)
        String cpf
) {
}
