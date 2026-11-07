package br.com.gs.springboot.gs_springboot.exception;

public class EmailRegistradoException extends RuntimeException {
    public EmailRegistradoException(String email) {
        super("Já existe um colaborador cadastrado com o email " + email);
    }
}
