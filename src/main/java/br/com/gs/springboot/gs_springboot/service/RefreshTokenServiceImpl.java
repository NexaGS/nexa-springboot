package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.exception.RefreshTokenInvalidoException;
import br.com.gs.springboot.gs_springboot.model.Cidadao;
import br.com.gs.springboot.gs_springboot.model.Colaborador;
import br.com.gs.springboot.gs_springboot.model.RefreshToken;
import br.com.gs.springboot.gs_springboot.repository.CidadaoRepository;
import br.com.gs.springboot.gs_springboot.repository.ColaboradorRepository;
import br.com.gs.springboot.gs_springboot.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private static final int TAMANHO_EM_BYTES = 32;

    private final SecureRandom aleatorio = new SecureRandom();
    private final RefreshTokenRepository repository;
    private final CidadaoRepository cidadaoRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final Duration validade;

    public RefreshTokenServiceImpl(
            RefreshTokenRepository repository,
            CidadaoRepository cidadaoRepository,
            ColaboradorRepository colaboradorRepository,
            @Value("${app.jwt.refresh-expiration-days}") long dias) {
        this.repository = repository;
        this.cidadaoRepository = cidadaoRepository;
        this.colaboradorRepository = colaboradorRepository;
        this.validade = Duration.ofDays(dias);
    }

    @Override
    @Transactional
    public RefreshEmitido emitir(String email) {
        Instant agora = Instant.now();
        Cidadao cidadao = cidadaoRepository.findByEmail(email).orElse(null);
        if (cidadao != null) {
            repository.apagarExpiradosDe(cidadao, agora);
            return criar(cidadao, null, UUID.randomUUID().toString());
        }
        Colaborador colaborador = colaboradorRepository.findByEmail(email)
                .orElseThrow(RefreshTokenInvalidoException::new);
        repository.apagarExpiradosDe(colaborador, agora);
        return criar(null, colaborador, UUID.randomUUID().toString());
    }

    @Override
    @Transactional(noRollbackFor = RefreshTokenInvalidoException.class)
    public RefreshEmitido rotacionar(String refreshToken) {
        RefreshToken atual = repository.findByTokenHash(hash(refreshToken))
                .orElseThrow(RefreshTokenInvalidoException::new);

        if (atual.isRevogado() || atual.getExpiraEm().isBefore(Instant.now())) {
            throw new RefreshTokenInvalidoException();
        }
        if (atual.isUsado()) {
            repository.revogarFamilia(atual.getFamilia());
            throw new RefreshTokenInvalidoException();
        }

        atual.setUsado(true);
        repository.save(atual);
        return criar(atual.getCidadao(), atual.getColaborador(), atual.getFamilia());
    }

    @Override
    @Transactional
    public void revogar(String refreshToken) {
        repository.findByTokenHash(hash(refreshToken))
                .ifPresent(token -> repository.revogarFamilia(token.getFamilia()));
    }

    @Override
    public long getValidadeEmSegundos() {
        return validade.toSeconds();
    }

    private RefreshEmitido criar(Cidadao cidadao, Colaborador colaborador, String familia) {
        byte[] bytes = new byte[TAMANHO_EM_BYTES];
        aleatorio.nextBytes(bytes);
        String tokenBruto = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        RefreshToken token = new RefreshToken();
        token.setTokenHash(hash(tokenBruto));
        token.setCidadao(cidadao);
        token.setColaborador(colaborador);
        token.setFamilia(familia);
        token.setExpiraEm(Instant.now().plus(validade));
        repository.save(token);

        return new RefreshEmitido(token.emailDoDono(), tokenBruto, token.getExpiraEm());
    }

    private static String hash(String tokenBruto) {
        try {
            byte[] resumo = MessageDigest.getInstance("SHA-256")
                    .digest(tokenBruto.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(resumo);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
