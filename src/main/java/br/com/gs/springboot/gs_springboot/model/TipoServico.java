package br.com.gs.springboot.gs_springboot.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(
        name = "TipoServico",
        description = "Especialidade ou serviço do atendimento na unidade de saúde, utilizado para o roteamento da fila e emissão da senha sequencial (ex.: `CLINICO_GERAL` gera senhas `CLI-001`, `VACINACAO` gera `VAC-001`).",
        enumAsRef = true
)
public enum TipoServico {
    CLINICO_GERAL("CLI"),
    TRIAGEM("TRI"),
    VACINACAO("VAC"),
    PEDIATRIA("PED"),
    ODONTOLOGIA("ODO");

    private final String prefixo;
}
