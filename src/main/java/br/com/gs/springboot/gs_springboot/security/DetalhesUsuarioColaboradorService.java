package br.com.gs.springboot.gs_springboot.security;

import br.com.gs.springboot.gs_springboot.model.Colaborador;
import br.com.gs.springboot.gs_springboot.repository.ColaboradorRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DetalhesUsuarioColaboradorService implements UserDetailsService {
    private final ColaboradorRepository repository;

    public DetalhesUsuarioColaboradorService(ColaboradorRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Colaborador colaborador = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Colaborador não encontrado: " + email));

        return User.builder()
                .username(colaborador.getEmail())
                .password(colaborador.getSenha())
                .roles("COLABORADOR")
                .build();

    }
}
