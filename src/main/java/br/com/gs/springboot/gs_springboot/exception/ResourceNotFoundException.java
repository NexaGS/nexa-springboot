package br.com.gs.springboot.gs_springboot.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String recurso, Long id) {
        super(recurso + " de id (" + id + ") não encontrado!");
    }
}
