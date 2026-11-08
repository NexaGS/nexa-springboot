package br.com.gs.springboot.gs_springboot.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "AtendimentoAtualizacaoRequest",
        description = "Payload para encerramento de um atendimento em aberto pelo colaborador da unidade de saúde."
)
public record AtendimentoAtualizacaoRequest() {
}
