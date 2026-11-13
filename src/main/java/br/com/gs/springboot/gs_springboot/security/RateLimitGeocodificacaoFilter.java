package br.com.gs.springboot.gs_springboot.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


 // Limita por IP as chamadas ao endpoint público de geocodificação reversa
@Component
public class RateLimitGeocodificacaoFilter extends OncePerRequestFilter {
    private static final String CAMINHO = "/api/localizacoes/reversa";
    private static final long JANELA_MS = 60_000;
    private static final int MAX_IPS_RASTREADOS = 10_000;

    private record Janela(long inicio, int contagem) {}

    private final ConcurrentHashMap<String, Janela> janelas = new ConcurrentHashMap<>();
    private final int limitePorMinuto;

    public RateLimitGeocodificacaoFilter(@Value("${app.rate-limit.geocodificacao-por-minuto:20}") int limitePorMinuto) {
        this.limitePorMinuto = limitePorMinuto;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().equals(request.getContextPath() + CAMINHO);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        long agora = System.currentTimeMillis();
        if (janelas.size() > MAX_IPS_RASTREADOS) {
            janelas.values().removeIf(j -> agora - j.inicio() >= JANELA_MS);
        }
        Janela atual = janelas.compute(request.getRemoteAddr(), (ip, j) ->
                (j == null || agora - j.inicio() >= JANELA_MS) ? new Janela(agora, 1) : new Janela(j.inicio(), j.contagem() + 1));

        if (atual.contagem() > limitePorMinuto) {
            long restanteSeg = Math.max(1, (JANELA_MS - (agora - atual.inicio())) / 1000);
            response.setHeader("Retry-After", String.valueOf(restanteSeg));
            JsonEscritorErro.escreve(response, HttpStatus.TOO_MANY_REQUESTS,
                    "Muitas requisições de localização. Tente novamente em instantes.");
            return;
        }
        chain.doFilter(request, response);
    }
}
