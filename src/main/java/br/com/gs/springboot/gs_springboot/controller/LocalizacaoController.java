package br.com.gs.springboot.gs_springboot.controller;

import br.com.gs.springboot.gs_springboot.service.LocalizacaoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/localizacoes")
//@Tag(
//        name = "Localização",
//        description = "Gerenciamento de endereços normalizados (3FN) e coordenadas geográficas das unidades de saúde."
//)
public class LocalizacaoController {
    private final LocalizacaoService localizacaoService;

    public LocalizacaoController(LocalizacaoService localizacaoService) {
        this.localizacaoService = localizacaoService;
    }
}
