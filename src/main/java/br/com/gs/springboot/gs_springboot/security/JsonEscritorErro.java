package br.com.gs.springboot.gs_springboot.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.time.LocalDateTime;

public final class JsonEscritorErro {
    private JsonEscritorErro() {
    }

    public static void escreve(HttpServletResponse response, HttpStatus status, String mensagem)throws IOException{
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("""
                {"timestamp":"%s","status":%d,"error":"%s","message":"%s"}"""
                .formatted(LocalDateTime.now(), status.value(), status.getReasonPhrase(), mensagem));
    }
}
