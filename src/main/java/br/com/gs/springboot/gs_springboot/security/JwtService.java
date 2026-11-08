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

    public JwtService(@Value("${app.jwt.secret}") String secretBase64, @Value("3600000") long expiracaoMs) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretBase64));
        this.expiracaoMs = expiracaoMs;
    }

    public String geraToken(Colaborador colaborador){
        Date dataAtual = new Date();
        return Jwts.builder()
                .subject(colaborador.getEmail())
//                .claim("role") -- nao teria no nosso caso diferenca de role dentro dos colaboradores. quem teria acesso ao sistema seria role unica
                .issuedAt(dataAtual)
                .expiration(new Date(dataAtual.getTime() + expiracaoMs))
                .signWith(secretKey)
                .compact();
    }

    public long getExpiracaoMs(){
        return this.expiracaoMs/1000;
    }

    public String extrairUsername(String token){
        return parsearToken(token).getSubject();
    }

    public boolean tokenEhValido(String token){
        Claims claims = parsearToken(token);
        //claims.getSubject().equals() - retirei momentaneamente por conta de nao termos role para colaborador, precisa ajustar
        return claims.getExpiration().after(new Date());
    }

    public Claims parsearToken(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build().parseSignedClaims(token).getPayload();
    }
}
