package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.dto.request.RegistroRequest;
import br.com.gs.springboot.gs_springboot.exception.EmailRegistradoException;
import br.com.gs.springboot.gs_springboot.model.Usuario;
import br.com.gs.springboot.gs_springboot.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario registrar(RegistroRequest request) {
        if (existeEmail(request.email())) {
            throw new EmailRegistradoException(request.email());
        }

        if (!request.senha().equals(request.confirmarSenha())) {
            throw new IllegalArgumentException("Senha e confirmação de senha não conferem");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setAceitouLocalizacaoEm(LocalDateTime.now());

        return repository.save(usuario);
    }

    @Override
    public boolean existeEmail(String email) {
        return repository.existsByEmail(email);
    }
}
