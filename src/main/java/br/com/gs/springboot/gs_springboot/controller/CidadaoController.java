package br.com.gs.springboot.gs_springboot.controller;

import br.com.gs.springboot.gs_springboot.dto.response.CidadaoResponse;
import br.com.gs.springboot.gs_springboot.dto.response.PageResponse;
import br.com.gs.springboot.gs_springboot.service.CidadaoService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public PageResponse<CidadaoResponse> listarCidadaos(
            @Parameter(
                    description = "Índice da página desejada (inicia em `0`).",
                    example = "0",
                    schema = @Schema(type = "integer", defaultValue = "0", minimum = "0")
            )
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer pagina,

            @Parameter(
                    description = "Quantidade de registros por página (mínimo `1`, máximo `100`).",
                    example = "10",
                    schema = @Schema(type = "integer", defaultValue = "10", minimum = "1", maximum = "100")
            )
            @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(100) Integer tamanho
    ){
        return PageResponse.from(cidadaoService.listarCidadaos(pagina, tamanho));
    }

    @PutMapping("{id}/adicionarUnidade/{unidadeId}")
    public ResponseEntity<CidadaoResponse> adicionarUnidadeComoFavorita(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long unidadeId){
        return ResponseEntity.ok(cidadaoService.adicionarUnidadeLista(id, unidadeId));
    }

    @DeleteMapping("{id}/removerUnidade/{unidadeId}")
    public ResponseEntity<CidadaoResponse> removerUnidadeComoFavorita(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long unidadeId){
        return ResponseEntity.ok(cidadaoService.removerUnidadeLista(id, unidadeId));
    }
}
