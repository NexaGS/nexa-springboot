package br.com.gs.springboot.gs_springboot.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "Role",
        description = "Perfil de acesso do usuário autenticado no sistema.",
        enumAsRef = true
)
public enum Role {
//    CIDADAO,
    COLABORADOR
}
