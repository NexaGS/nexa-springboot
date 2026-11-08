package br.com.gs.springboot.gs_springboot.controller;

import br.com.gs.springboot.gs_springboot.assembler.UnidadeSaudeModelAssembler;
import br.com.gs.springboot.gs_springboot.dto.errors.ErrorResponse;
import br.com.gs.springboot.gs_springboot.dto.response.PageResponse;
import br.com.gs.springboot.gs_springboot.dto.response.UnidadeSaudeResponse;
import br.com.gs.springboot.gs_springboot.mapper.UnidadeSaudeMapper;
import br.com.gs.springboot.gs_springboot.model.UnidadeSaude;
import br.com.gs.springboot.gs_springboot.service.UnidadeSaudeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/unidades")
@Tag(
        name = "UnidadeSaúde",
        description = "Endpoints das unidades de saúde."
)
public class UnidadeSaudeController {
    private final UnidadeSaudeService unidadeSaudeService;
    private final UnidadeSaudeModelAssembler assembler;

    public UnidadeSaudeController(UnidadeSaudeService unidadeSaudeService, UnidadeSaudeModelAssembler assembler) {
        this.unidadeSaudeService = unidadeSaudeService;
        this.assembler = assembler;
    }

    @Operation(
            summary = "Lista todas as unidades de saúde com paginação",
            description = "Retorna uma lista paginada das unidades de saúde registradas."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Página de locais retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @Cacheable("unidades")
    @GetMapping
    public PageResponse<UnidadeSaudeResponse> listarUnidades(
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer pagina,

            @Parameter(description = "Quantidade de registros por página", example = "10")
            @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(100) Integer tamanho
    ){
        return PageResponse.from(unidadeSaudeService.listarTodas(pagina, tamanho).map(UnidadeSaudeMapper::toResponse));
    }

    @Cacheable(value = "unidades", key = "#id")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UnidadeSaudeResponse>> listarUnidade(@PathVariable Long id){
        return ResponseEntity.ok(unidadeSaudeService.listarUnidade(id));
    }


    @GetMapping("/proximas")
    public PageResponse<UnidadeSaudeResponse> listarUnidadesProximas(
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer pagina,

            @Parameter(description = "Quantidade de registros por página", example = "10")
            @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(100) Integer tamanho){

        return PageResponse.from(unidadeSaudeService.listarUnidadesProximidade(pagina, tamanho).map(UnidadeSaudeMapper::toResponse));
    }

}
