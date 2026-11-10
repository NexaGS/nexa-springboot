package br.com.gs.springboot.gs_springboot.security;

import br.com.gs.springboot.gs_springboot.model.Colaborador;
import br.com.gs.springboot.gs_springboot.model.Usuario;
import br.com.gs.springboot.gs_springboot.repository.ColaboradorRepository;
import br.com.gs.springboot.gs_springboot.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DetalhesUsuarioService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;
    private final ColaboradorRepository colaboradorRepository;

    public DetalhesUsuarioService(UsuarioRepository usuarioRepository, ColaboradorRepository colaboradorRepository) {
        this.usuarioRepository = usuarioRepository;
        this.colaboradorRepository = colaboradorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario != null) {
            return User.builder()
                    .username(usuario.getEmail())
                    .password(usuario.getSenha())
                    .roles("USUARIO")
                    .build();
        }

        Colaborador colaborador = colaboradorRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        return User.builder()
                .username(colaborador.getEmail())
                .password(colaborador.getSenha())
                .roles("COLABORADOR")
                .build();
    }
}
