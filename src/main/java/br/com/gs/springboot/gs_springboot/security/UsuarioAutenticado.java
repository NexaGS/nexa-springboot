package br.com.gs.springboot.gs_springboot.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class UsuarioAutenticado extends User {
    private String nome;

    public UsuarioAutenticado(
            String email, String senha, String nome,
            Collection<? extends GrantedAuthority> perfis
            ) {
        super(email, senha, perfis);
        this.nome =nome;
    }
}
