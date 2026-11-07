package br.com.gs.springboot.gs_springboot.controller;

import br.com.gs.springboot.gs_springboot.dto.request.ColaboradorRequest;
import br.com.gs.springboot.gs_springboot.dto.response.ColaboradorResponse;
import br.com.gs.springboot.gs_springboot.dto.response.PageResponse;
import br.com.gs.springboot.gs_springboot.model.Colaborador;
import br.com.gs.springboot.gs_springboot.service.ColaboradorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/colaboradores")
@Tag(
        name = "Colaborador",
        description = "Endpoints dos colaboradores que atuam nas unidades de saúde."
)
public class ColaboradorController {
    private final ColaboradorService colaboradorService;

    public ColaboradorController(ColaboradorService colaboradorService) {
        this.colaboradorService = colaboradorService;
    }

    @GetMapping("/unidade/{idUnidade}")
    List<ColaboradorResponse> listarColaboradoresDaUnidade(@PathVariable @Validated Long idUnidade){
        return new ArrayList<>();
    }

    @PostMapping
    public ResponseEntity<ColaboradorResponse> criarColaborador(@RequestBody @Valid ColaboradorRequest request){
        ColaboradorResponse colaborador = colaboradorService.criar(request);
        return ResponseEntity.created(URI.create("api/colaboradores/" + colaborador.id())).body(colaborador);
    }


}
