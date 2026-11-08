package br.com.gs.springboot.gs_springboot.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String BEARER = "Bearer ";
    public static final String HEADER_AUTORIZACAO = "Authorization";

    public final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HEADER_AUTORIZACAO);

        if(header != null && header.startsWith(BEARER) && SecurityContextHolder.getContext().getAuthentication() == null)
        {
            String token = header.substring(BEARER.length());

            try{
                String email = jwtService.extrairUsername(token);
                if(jwtService.tokenEhValido(token, usuario)){
                    UsernamePasswordAuthenticationToken autenticacao = new UsernamePasswordAuthenticationToken(usuario, null, usuario.get());
                    SecurityContextHolder.getContext().setAuthentication(autenticacao);
                }

            } catch (JwtException | IllegalArgumentException){
                SecurityContextHolder.clearContext();
            }

        }

        filterChain.doFilter(request, response);
    }
}
