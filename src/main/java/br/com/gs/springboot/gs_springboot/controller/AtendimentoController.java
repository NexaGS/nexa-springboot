package br.com.gs.springboot.gs_springboot.controller;

import br.com.gs.springboot.gs_springboot.dto.response.AtendimentoResponse;
import br.com.gs.springboot.gs_springboot.dto.response.PageResponse;
import br.com.gs.springboot.gs_springboot.mapper.AtendimentoMapper;
import br.com.gs.springboot.gs_springboot.model.Atendimento;
import br.com.gs.springboot.gs_springboot.service.AtendimentoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public AtendimentoResponse buscarAtendimento(@PathVariable Long id){
        Atendimento atendimento = atendimentoService.findById(id);
        return AtendimentoMapper.toResponse(atendimento);
    }

//    @GetMapping("/unidades/{unidadeId}")
//    public Page<AtendimentoResponse> buscarAtendimentosPorUnidades(@PathVariable Long unidadeId){
//        return PageResponse.from();
//    }



//    @PostMapping("/checkin")
//    public AtendimentoResponse iniciarAtendimento(){
//
//        return AtendimentoMapper.toResponse(atendimento);
//    }

//    @PutMapping("/{id}/encerrar")
//    public AtendimentoResponse encerrarAtendimento(Long id){
//        Atendimento atendimento = atendimentoService.findById(id);
//
//    }
}
