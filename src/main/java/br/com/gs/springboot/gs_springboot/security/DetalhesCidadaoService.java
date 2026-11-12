package br.com.gs.springboot.gs_springboot.security;

import br.com.gs.springboot.gs_springboot.model.Colaborador;
import br.com.gs.springboot.gs_springboot.model.Cidadao;
import br.com.gs.springboot.gs_springboot.repository.ColaboradorRepository;
import br.com.gs.springboot.gs_springboot.repository.CidadaoRepository;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DetalhesCidadaoService implements UserDetailsService {
    private final CidadaoRepository cidadaoRepository;
    private final ColaboradorRepository colaboradorRepository;

    public DetalhesCidadaoService(CidadaoRepository cidadaoRepository, ColaboradorRepository colaboradorRepository) {
        this.cidadaoRepository = cidadaoRepository;
        this.colaboradorRepository = colaboradorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Cidadao cidadao = cidadaoRepository.findByEmail(email).orElse(null);
        if (cidadao != null) {
            return new UsuarioAutenticado(
                    cidadao.getEmail(),
                    cidadao.getSenha(),
                    cidadao.getNome(),
                    AuthorityUtils.createAuthorityList("ROLE_USUARIO"));
        }

        Colaborador colaborador = colaboradorRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        return new UsuarioAutenticado(
                colaborador.getEmail(),
                colaborador.getSenha(),
                colaborador.getNome(),
                AuthorityUtils.createAuthorityList("ROLE_COLABORADOR"));
    }
}
