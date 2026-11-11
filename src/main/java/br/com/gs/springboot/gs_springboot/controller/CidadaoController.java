package br.com.gs.springboot.gs_springboot.controller;

import br.com.gs.springboot.gs_springboot.dto.response.CidadaoResponse;
import br.com.gs.springboot.gs_springboot.service.CidadaoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cidadaos")
@Tag(
        name = "Cidadão",
        description = "Endpoints referentes a Cidadão, entidade representada por quem acessa o aplicativo para monitorar as filas."
)
public class CidadaoController {
    private final CidadaoService cidadaoService;

    public CidadaoController(CidadaoService cidadaoService) {
        this.cidadaoService = cidadaoService;
    }

    @PutMapping("{id}/adicionarUnidade")
    public CidadaoResponse adicionarUnidadeComoFavorita(Long id){
        return null;
    }

    @PutMapping("{id}/removerUnidade")
    public CidadaoResponse removerUnidadeComoFavorita(Long id){
        return null;
    }
}
