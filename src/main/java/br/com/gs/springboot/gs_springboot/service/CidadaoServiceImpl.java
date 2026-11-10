package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.dto.request.RegistroRequest;
import br.com.gs.springboot.gs_springboot.exception.EmailRegistradoException;
import br.com.gs.springboot.gs_springboot.model.Cidadao;
import br.com.gs.springboot.gs_springboot.repository.CidadaoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CidadaoServiceImpl implements CidadaoService {
    private final CidadaoRepository repository;
    private final PasswordEncoder passwordEncoder;

    public CidadaoServiceImpl(CidadaoRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Cidadao registrar(RegistroRequest request) {
        if (existeEmail(request.email())) {
            throw new EmailRegistradoException(request.email());
        }

        if (!request.senha().equals(request.confirmarSenha())) {
            throw new IllegalArgumentException("Senha e confirmação de senha não conferem.");
        }

        Cidadao cidadao = new Cidadao();
        cidadao.setNome(request.nome());
        cidadao.setEmail(request.email());
        cidadao.setSenha(passwordEncoder.encode(request.senha()));
        cidadao.setAceitouLocalizacaoEm(LocalDateTime.now());

        return repository.save(cidadao);
    }

    @Override
    public boolean existeEmail(String email) {
        return repository.existsByEmail(email);
    }
}
