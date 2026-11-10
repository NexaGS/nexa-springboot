package br.com.gs.springboot.gs_springboot.security;

import br.com.gs.springboot.gs_springboot.model.Colaborador;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
    private SecurityUtils() {
    }

    public static String obterEmailColaborador(){
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        if(autenticacao == null || !(autenticacao.getPrincipal() instanceof String email)){
            throw new IllegalArgumentException("Nenhum colaborador autenticado.");
        }
        return email;
    }
}
