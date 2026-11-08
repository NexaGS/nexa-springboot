package br.com.gs.springboot.gs_springboot.exception;

public class GoogleMapsException extends RuntimeException {
    public GoogleMapsException(String message) {
        super(message);
    }
}
