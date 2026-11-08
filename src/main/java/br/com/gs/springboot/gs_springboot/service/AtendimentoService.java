package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.dto.request.AtendimentoAtualizacaoRequest;
import br.com.gs.springboot.gs_springboot.dto.request.AtendimentoCriacaoRequest;
import br.com.gs.springboot.gs_springboot.dto.response.AtendimentoResponse;
import br.com.gs.springboot.gs_springboot.model.Atendimento;
import org.springframework.hateoas.EntityModel;

public interface AtendimentoService {
    Atendimento findById(Long id);
    EntityModel<AtendimentoResponse> buscarAtendimento(Long id);
    AtendimentoResponse iniciarAtendimento(AtendimentoCriacaoRequest request);
    AtendimentoResponse encerrarAtendimento(Long id, AtendimentoAtualizacaoRequest request);
}
