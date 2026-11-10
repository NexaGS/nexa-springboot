package br.com.gs.springboot.gs_springboot.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "StatusAtendimento",
        description = "Estado do atendimento no ciclo de vida da fila: `AGUARDANDO` (na fila aguardando chamada), `CHAMADO` (chamado para atendimento pelo colaborador) ou `FINALIZADO` (atendimento concluído).",
        enumAsRef = true
)
public enum StatusAtendimento {
    AGUARDANDO,
    CHAMADO,
    FINALIZADO;
}
