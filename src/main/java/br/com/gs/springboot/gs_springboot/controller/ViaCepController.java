package br.com.gs.springboot.gs_springboot.controller;

import br.com.gs.springboot.gs_springboot.client.ViaCepClient;
import br.com.gs.springboot.gs_springboot.dto.response.ViaCepResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/viacep")
@Tag(
        name = "ViaCep - (Interno)",
        description = "Para validar endereços, solicitando requisição com CEP."
)
public class ViaCepController {
    private final ViaCepClient viaCepClient;

    public ViaCepController(ViaCepClient viaCepClient) {
        this.viaCepClient = viaCepClient;
    }

    @PostMapping
    public ResponseEntity<ViaCepResponse> buscarEndereco(String cep){
        return ResponseEntity.ok(viaCepClient.buscarEndereco(cep));
    }
}
