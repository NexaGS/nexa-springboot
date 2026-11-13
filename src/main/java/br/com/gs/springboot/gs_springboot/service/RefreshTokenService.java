package br.com.gs.springboot.gs_springboot.service;

import java.time.Instant;

public interface RefreshTokenService {
    RefreshEmitido emitir(String email);

    RefreshEmitido rotacionar(String refreshToken);

    void revogar(String refreshToken);

    long getValidadeEmSegundos();

    record RefreshEmitido(String email, String token, Instant expiraEm) {
    }
}
