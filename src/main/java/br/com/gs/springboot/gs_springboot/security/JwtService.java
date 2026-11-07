package br.com.gs.springboot.gs_springboot.security;

import br.com.gs.springboot.gs_springboot.model.Colaborador;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey secretKey;
    private final long expiracaoMs;

    public JwtService(String secretBase64, @Value("3600000") long expiracaoMs) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretBase64));
        this.expiracaoMs = expiracaoMs;
    }

    public String geraToken(Colaborador colaborador){
        Date dataAtual = new Date();
        return Jwts.builder()
                .subject(colaborador.getEmail())
//                .claim()
                .issuedAt(dataAtual)
                .expiration(new Date(dataAtual.getTime() + expiracaoMs))
                .signWith(secretKey)
                .compact();
    }

    public long getExpiracaoMs(){
        return this.expiracaoMs/1000;
    }

    public boolean tokenEhValido(String token){
        Claims claims = parsearToken(token);
    }

    public Claims parsearToken(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build().parseSignedClaims(token).getPayload();
    }
}
