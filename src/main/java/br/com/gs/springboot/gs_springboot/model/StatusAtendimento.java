package br.com.gs.springboot.gs_springboot.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "StatusAtendimento",
        description = "Estado atual do atendimento no ciclo de vida da fila da unidade de saúde (`ABERTO` enquanto aguarda/em atendimento; `ENCERRADO` após finalização pelo colaborador).",
        enumAsRef = true
)
public enum StatusAtendimento {
    ABERTO,
    //EM ANDAMENTO - NA SALA OU CHAMADO - AINDA NAO RESPONDEU, ANTES DE PASSAR PRO PROXIMO - em discussão
    ENCERRADO
}
