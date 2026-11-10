package br.com.gs.springboot.gs_springboot.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "StatusAtendimento",
        description = "Estado atual do atendimento no ciclo de vida da fila da unidade de saúde (`AGUARDANDO` enquanto aguarda/em atendimento; `FINALIZADO` após finalização pelo colaborador).",
        enumAsRef = true
)
public enum StatusAtendimento {
    AGUARDANDO,
    CHAMADO,
    FINALIZADO;


}
