package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.exception.RefreshTokenInvalidoException;
import br.com.gs.springboot.gs_springboot.model.RefreshToken;
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
    private final Duration validade;

    public RefreshTokenServiceImpl(
            RefreshTokenRepository repository,
            @Value("${app.jwt.refresh-expiration-days}") long dias) {
        this.repository = repository;
        this.validade = Duration.ofDays(dias);
    }

    @Override
    @Transactional
    public RefreshEmitido emitir(String email) {
        repository.apagarExpiradosDe(email, Instant.now());
        return criar(email, UUID.randomUUID().toString());
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
        return criar(atual.getEmail(), atual.getFamilia());
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

    private RefreshEmitido criar(String email, String familia) {
        byte[] bytes = new byte[TAMANHO_EM_BYTES];
        aleatorio.nextBytes(bytes);
        String tokenBruto = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        RefreshToken token = new RefreshToken();
        token.setTokenHash(hash(tokenBruto));
        token.setEmail(email);
        token.setFamilia(familia);
        token.setExpiraEm(Instant.now().plus(validade));
        repository.save(token);

        return new RefreshEmitido(email, tokenBruto, token.getExpiraEm());
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
