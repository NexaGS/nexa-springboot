package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.dto.request.RegistroRequest;
import br.com.gs.springboot.gs_springboot.dto.response.CidadaoResponse;
import br.com.gs.springboot.gs_springboot.dto.response.UnidadeSaudeResponse;
import br.com.gs.springboot.gs_springboot.exception.EmailRegistradoException;
import br.com.gs.springboot.gs_springboot.exception.ResourceNotFoundException;
import br.com.gs.springboot.gs_springboot.mapper.CidadaoMapper;
import br.com.gs.springboot.gs_springboot.mapper.UnidadeSaudeMapper;
import br.com.gs.springboot.gs_springboot.model.Localizacao;
import br.com.gs.springboot.gs_springboot.model.Cidadao;
import br.com.gs.springboot.gs_springboot.model.UnidadeSaude;
import br.com.gs.springboot.gs_springboot.repository.CidadaoRepository;
import br.com.gs.springboot.gs_springboot.security.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class CidadaoServiceImpl implements CidadaoService {
    private final CidadaoRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UnidadeSaudeService unidadeSaudeService;

    public CidadaoServiceImpl(CidadaoRepository repository, PasswordEncoder passwordEncoder, UnidadeSaudeService unidadeSaudeService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.unidadeSaudeService = unidadeSaudeService;
    }

    @Override
    public Cidadao findById(Long id){
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cidadão", id));
    }

    private Cidadao obterCidadaoLogado() {
        String email = SecurityUtils.obterEmailUsuarioLogado();
        return repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cidadão não encontrado para o e-mail: " + email));
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
        cidadao.setDataHoraAceiteLocalizacao(LocalDateTime.now());

        return repository.save(cidadao);
    }

    @Override
    public boolean existeEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Page<CidadaoResponse> listarCidadaos(Integer pagina, Integer tamanho) {
        return repository.findAll(Pageable.ofSize(tamanho).withPage(pagina)).map(CidadaoMapper::toResponse);
    }

    @Override
    @Transactional
    public CidadaoResponse obterPerfilLogado() {
        Cidadao cidadao = obterCidadaoLogado();
        return CidadaoMapper.toResponse(cidadao);
    }

    @Override
    @Transactional
    public CidadaoResponse adicionarUnidadeLista(Long unidadeId) {
        Cidadao cidadao = obterCidadaoLogado();
        UnidadeSaude unidade = unidadeSaudeService.findById(unidadeId);

        cidadao.adicionarUnidadeFavorita(unidade);

        return CidadaoMapper.toResponse(cidadao);
    }

    @Override
    @Transactional
    public CidadaoResponse removerUnidadeLista(Long unidadeId) {
        Cidadao cidadao = obterCidadaoLogado();
        UnidadeSaude unidade = unidadeSaudeService.findById(unidadeId);

        if(!cidadao.getUnidadesFavoritas().contains(unidade)){
            throw new ResourceNotFoundException("Unidade Saúde", unidadeId);
        }

        cidadao.removerUnidadeFavorita(unidade);

        return CidadaoMapper.toResponse(cidadao);
    }

    @Override
    @Transactional
    public List<UnidadeSaudeResponse> listarFavoritas(BigDecimal latitude, BigDecimal longitude) {
        boolean comPosicao = latitude != null && longitude != null;

        return obterCidadaoLogado().getUnidadesFavoritas().stream()
                .sorted(Comparator.comparing(UnidadeSaude::getNome, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(UnidadeSaude::getId))
                .map(unidade -> UnidadeSaudeMapper.toResponse(
                        unidade, comPosicao ? distanciaKm(latitude, longitude, unidade.getLocal()) : null))
                .toList();
    }

    private static Double distanciaKm(BigDecimal latitude, BigDecimal longitude, Localizacao local) {
        if (local == null || local.getLatitude() == null || local.getLongitude() == null) {
            return null;
        }
        double lat1 = Math.toRadians(latitude.doubleValue());
        double lat2 = Math.toRadians(local.getLatitude().doubleValue());
        double dLat = lat2 - lat1;
        double dLon = Math.toRadians(local.getLongitude().doubleValue() - longitude.doubleValue());
        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dLon / 2), 2);
        return 6371.0 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
