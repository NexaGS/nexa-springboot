package br.com.gs.springboot.gs_springboot.exception;

public class EmailRegistradoException extends RuntimeException {
    public EmailRegistradoException(String email) {
        super("Já existe uma conta cadastrada com o email " + email);
    }
}
