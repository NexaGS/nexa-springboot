package br.com.gs.springboot.gs_springboot.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoServico {
    CLINICO_GERAL("CLI"),
    TRIAGEM("TRI"),
    VACINACAO("VAC"),
    PEDIATRIA("PED"),
    ODONTOLOGIA("ODO");

    private final String prefixo;
}
