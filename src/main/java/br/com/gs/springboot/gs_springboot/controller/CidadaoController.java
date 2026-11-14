package br.com.gs.springboot.gs_springboot.controller;

import br.com.gs.springboot.gs_springboot.assembler.CidadaoModelAssembler;
import br.com.gs.springboot.gs_springboot.dto.response.CidadaoResponse;
import br.com.gs.springboot.gs_springboot.dto.response.PageResponse;
import br.com.gs.springboot.gs_springboot.dto.response.UnidadeSaudeResponse;
import br.com.gs.springboot.gs_springboot.service.CidadaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cidadaos")
@Tag(
        name = "Cidadão",
        description = "Endpoints referentes a Cidadão, entidade representada por quem acessa o aplicativo para monitorar as filas."
)
public class CidadaoController {
    private final CidadaoService cidadaoService;
    private final CidadaoModelAssembler cidadaoModelAssembler;

    public CidadaoController(CidadaoService cidadaoService, CidadaoModelAssembler cidadaoModelAssembler) {
        this.cidadaoService = cidadaoService;
        this.cidadaoModelAssembler = cidadaoModelAssembler;
    }

    @GetMapping("/me")
    public ResponseEntity<EntityModel<CidadaoResponse>> obterPerfil() {
        return ResponseEntity.ok(cidadaoModelAssembler.toModel(cidadaoService.obterPerfilLogado()));
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

    @PutMapping("usuario/adicionarUnidade/{unidadeId}")
    public ResponseEntity<EntityModel<CidadaoResponse>> adicionarUnidadeComoFavorita(
            @PathVariable @Positive Long unidadeId){
        return ResponseEntity.ok(cidadaoModelAssembler.toModel(cidadaoService.adicionarUnidadeLista(unidadeId)));
    }

    @DeleteMapping("usuario/removerUnidade/{unidadeId}")
    public ResponseEntity<EntityModel<CidadaoResponse>> removerUnidadeComoFavorita(
            @PathVariable @Positive Long unidadeId){
        return ResponseEntity.ok(cidadaoModelAssembler.toModel(cidadaoService.removerUnidadeLista(unidadeId)));
    }

    @Operation(
            summary = "Listar as unidades favoritas do cidadão logado",
            description = "Devolve as unidades favoritas completas (espera atual e endereço), em ordem alfabética. "
                    + "Se `latitude` e `longitude` forem informadas, cada unidade traz `distanciaKm` até esse ponto."
    )
    @GetMapping("usuario/favoritas")
    public List<UnidadeSaudeResponse> listarFavoritas(
            @RequestParam(required = false) BigDecimal latitude,
            @RequestParam(required = false) BigDecimal longitude){
        return cidadaoService.listarFavoritas(latitude, longitude);
    }
}
