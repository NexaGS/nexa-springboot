package br.com.gs.springboot.gs_springboot.security;

import br.com.gs.springboot.gs_springboot.model.Role;
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

    public JwtService(@Value("${app.jwt.secret}") String secretBase64, @Value("${app.jwt.expiration-ms}") long expiracaoMs) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretBase64));
        this.expiracaoMs = expiracaoMs;
    }

    public String geraToken(String email, String nome, Role role){
        Date dataAtual = new Date();
        return Jwts.builder()
                .subject(email)
                .claim("nome", nome)
                .claim("role", role.name())
                .issuedAt(dataAtual)
                .expiration(new Date(dataAtual.getTime() + expiracaoMs))
                .signWith(secretKey)
                .compact();
    }

    public long getExpiracaoSegundos(){
        return this.expiracaoMs/1000;
    }

    public String extrairUsername(String token){
        return parsearToken(token).getSubject();
    }

    public String extrairRole(String token){
        return parsearToken(token).get("role", String.class);
    }

    public boolean tokenEhValido(String token){
        Claims claims = parsearToken(token);
        return claims.getExpiration().after(new Date());
    }

    public Claims parsearToken(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build().parseSignedClaims(token).getPayload();
    }
}
