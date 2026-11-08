package br.com.gs.springboot.gs_springboot.controller;

import br.com.gs.springboot.gs_springboot.dto.request.AtendimentoAtualizacaoRequest;
import br.com.gs.springboot.gs_springboot.dto.request.AtendimentoCriacaoRequest;
import br.com.gs.springboot.gs_springboot.dto.response.AtendimentoResponse;
import br.com.gs.springboot.gs_springboot.dto.response.PageResponse;
import br.com.gs.springboot.gs_springboot.mapper.AtendimentoMapper;
import br.com.gs.springboot.gs_springboot.model.Atendimento;
import br.com.gs.springboot.gs_springboot.service.AtendimentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.apache.coyote.Response;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/atendimentos")
@Tag(
        name = "Atendimento",
        description = "Endpoints de registro dos atendimentos nas unidades de saúde públicas."
)
public class AtendimentoController {
    private final AtendimentoService atendimentoService;

    public AtendimentoController(AtendimentoService atendimentoService) {
        this.atendimentoService = atendimentoService;
    }

    @Operation(
            summary = "Buscar atendimento único",
            description = "Retorna um atendimento da base, correspondente ao id inserido"
    )
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<AtendimentoResponse>> buscarAtendimento(@PathVariable Long id) {
        return ResponseEntity.ok(atendimentoService.buscarAtendimento(id));
    }

    @Operation(
            summary = "Buscar atendimentos de uma única unidade de saúde",
            description = "Retorna atendimentos da unidade da base, correspondente ao id inserido."
    )
    @Cacheable("atendimentos-unidade")
    @GetMapping("/unidades/{unidadeId}")
    public ResponseEntity<PageResponse<AtendimentoResponse>> buscarAtendimentosPorUnidades(
            @PathVariable Long unidadeId,

            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer pagina,

            @Parameter(description = "Quantidade de registros por página", example = "10")
            @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(100) Integer tamanho) {

        return ResponseEntity.ok(PageResponse.from(atendimentoService.listarAtendimentosPorUnidade(unidadeId, pagina, tamanho)));
    }

    @Operation(
            summary = "Iniciar atendimento",
            description = "Simula o início do atendimento, quando a pessoa pega a senha no totem"
    )
    @CacheEvict(value = "unidades", allEntries = true)
    @PostMapping("/checkIn")
    public ResponseEntity<AtendimentoResponse> iniciarAtendimento(@RequestBody @Valid AtendimentoCriacaoRequest request) {
        AtendimentoResponse atendimento = atendimentoService.iniciarAtendimento(request);
        return ResponseEntity.created(URI.create("api/atendimentos/checkIn/" + atendimento.id())).body(atendimento);
    }

    @Operation(
            summary = "Encerrar atendimento",
            description = "Simula o encerramento do atendimento, quando o colaborador libera o paciente"
    )
    @CacheEvict(value = "unidades", allEntries = true)
    @PutMapping("/{id}/encerramento")
    public ResponseEntity<AtendimentoResponse> encerrarAtendimento(@PathVariable Long id, @RequestBody @Valid AtendimentoAtualizacaoRequest request) {
        return ResponseEntity.ok(atendimentoService.encerrarAtendimento(id, request));
    }
}
