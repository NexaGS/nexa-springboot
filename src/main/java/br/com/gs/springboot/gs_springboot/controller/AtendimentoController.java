package br.com.gs.springboot.gs_springboot.controller;

import br.com.gs.springboot.gs_springboot.dto.request.AtendimentoAtualizacaoRequest;
import br.com.gs.springboot.gs_springboot.dto.request.AtendimentoCriacaoRequest;
import br.com.gs.springboot.gs_springboot.dto.response.AtendimentoResponse;
import br.com.gs.springboot.gs_springboot.dto.response.PageResponse;
import br.com.gs.springboot.gs_springboot.mapper.AtendimentoMapper;
import br.com.gs.springboot.gs_springboot.model.Atendimento;
import br.com.gs.springboot.gs_springboot.service.AtendimentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
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
    public AtendimentoResponse buscarAtendimento(@PathVariable Long id){
        Atendimento atendimento = atendimentoService.findById(id);
        return AtendimentoMapper.toResponse(atendimento);
    }

//    @Cacheable("atendimentos-unidade")
//    @GetMapping("/unidades/{unidadeId}")
//    public Page<AtendimentoResponse> buscarAtendimentosPorUnidades(@PathVariable Long unidadeId){
//        return PageResponse.from();
//    }

    //Enquanto só há esse endpoint de listagem por unidades
    @CacheEvict(value = "atendimentos-unidade", allEntries = true)
    @PostMapping("/checkIn")
    public ResponseEntity<AtendimentoResponse> iniciarAtendimento(@RequestBody @Valid AtendimentoCriacaoRequest request){
        AtendimentoResponse atendimento = atendimentoService.iniciarAtendimento(request);
        return ResponseEntity.created(URI.create("api/atendimentos/checkIn/" + atendimento.id())).body(atendimento);
    }

    //Enquanto só há esse endpoint de listagem por unidades
    @CacheEvict(value = "atendimentos-unidade", allEntries = true)
    @PutMapping("/{id}/encerramento")
    public ResponseEntity<AtendimentoResponse> encerrarAtendimento(@PathVariable Long id, @RequestBody @Valid AtendimentoAtualizacaoRequest request){
        return ResponseEntity.ok(atendimentoService.encerrarAtendimento(id, request));
    }
}
