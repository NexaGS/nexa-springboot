package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.dto.request.RegistroRequest;
import br.com.gs.springboot.gs_springboot.model.Usuario;

public interface UsuarioService {
    Usuario registrar(RegistroRequest request);
    boolean existeEmail(String email);
}
