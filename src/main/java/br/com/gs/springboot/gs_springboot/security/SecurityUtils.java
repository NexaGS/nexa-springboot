package br.com.gs.springboot.gs_springboot.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
    private SecurityUtils() {
    }

    public static String obterEmailUsuarioLogado(){
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        if(autenticacao == null || !(autenticacao.getPrincipal() instanceof String email)){
            throw new IllegalArgumentException("Nenhum usuário autenticado.");
        }
        return email;
    }

    public static String obterEmailColaborador(){
        return obterEmailUsuarioLogado();
    }
}
