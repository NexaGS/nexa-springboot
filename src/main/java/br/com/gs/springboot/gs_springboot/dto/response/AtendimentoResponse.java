package br.com.gs.springboot.gs_springboot.dto.response;

import br.com.gs.springboot.gs_springboot.model.StatusAtendimento;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(
        name = "AtendimentoResponse",
        description = "Dados detalhados de um atendimento registrado em uma unidade de saúde."
)
public record AtendimentoResponse(
        @Schema(
                description = "Identificador único do atendimento.",
                example = "42",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long id,

        @Schema(
                description = "Nome completo do paciente atendido.",
                example = "João da Silva Santos",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String nomePaciente,

        @Schema(
                description = "Nome do colaborador responsável pelo atendimento (pode ser nulo enquanto aguarda na fila).",
                example = "Dra. Ana Paula Souza",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String nomeColaborador,

        @Schema(
                description = "Status atual do atendimento na fila da unidade (`ABERTO` ou `ENCERRADO`).",
                example = "ABERTO",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        StatusAtendimento status,

        @Schema(
                description = "Data e hora do check-in (retirada de senha pelo paciente).",
                example = "2026-11-08T14:15:30",
                type = "string",
                format = "date-time",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        LocalDateTime horaChegada,

        @Schema(
                description = "Data e hora do encerramento do atendimento (nulo enquanto o status for `ABERTO`).",
                example = "2026-11-08T14:52:10",
                type = "string",
                format = "date-time",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        LocalDateTime horaFim
) {
}
