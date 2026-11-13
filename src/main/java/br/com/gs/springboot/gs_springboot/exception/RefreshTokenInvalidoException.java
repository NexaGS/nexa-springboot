package br.com.gs.springboot.gs_springboot.exception;

public class RefreshTokenInvalidoException extends RuntimeException {
    public RefreshTokenInvalidoException() {
        super("Sessão expirada. Entre novamente.");
    }
}
