package br.com.gs.springboot.gs_springboot.dto.response;

import br.com.gs.springboot.gs_springboot.model.StatusAtendimento;
import br.com.gs.springboot.gs_springboot.model.TipoServico;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(
        name = "AtendimentoResponse",
        description = "Dados detalhados do atendimento, incluindo identificador da senha, serviço, posição em tempo real na fila e tempo estimado de espera."
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
                description = "Nome do colaborador responsável pelo atendimento (preenchido após o encerramento ou chamada).",
                example = "Dra. Ana Paula Souza",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String nomeColaborador,

        @Schema(
                description = "Status atual do atendimento (`AGUARDANDO` na fila, `CHAMADO` pelo colaborador ou `FINALIZADO`).",
                example = "AGUARDANDO",
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
                description = "Data e hora do encerramento do atendimento (nulo enquanto não estiver `FINALIZADO`).",
                example = "2026-11-08T14:52:10",
                type = "string",
                format = "date-time",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        LocalDateTime horaFim,

        @Schema(
                description = "Data e hora em que o paciente foi chamado para atendimento (nulo enquanto `AGUARDANDO`).",
                example = "2026-11-08T14:35:00",
                type = "string",
                format = "date-time",
                nullable = true,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        LocalDateTime horaChamada,

        @Schema(
                description = "Senha sequencial formatada emitida para o paciente (ex.: CLI-001, VAC-002, TRI-003).",
                example = "CLI-001",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String senha,

        @Schema(
                description = "Serviço solicitado no atendimento (especialidade médica ou triagem).",
                example = "CLINICO_GERAL",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        TipoServico servico,

        @Schema(
                description = "Posição do paciente na fila em tempo real (0 quando já CHAMADO ou em atendimento).",
                example = "1",
                minimum = "0",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Integer posicaoFila,

        @Schema(
                description = "Tempo estimado de espera em minutos para ser atendido (calculado: posicaoFila * 20 min).",
                example = "20.0",
                minimum = "0",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Double tempoEsperaMinutos
) {
}
