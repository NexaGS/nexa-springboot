package br.com.gs.springboot.gs_springboot.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Entrada de informações para poder encerrar atendimento - colaborador encerrando o atendimento.")
public record AtendimentoAtualizacaoRequest() {
}
