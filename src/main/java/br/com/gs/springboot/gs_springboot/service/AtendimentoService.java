package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.dto.request.AtendimentoAtualizacaoRequest;
import br.com.gs.springboot.gs_springboot.dto.request.AtendimentoCriacaoRequest;
import br.com.gs.springboot.gs_springboot.dto.response.AtendimentoResponse;
import br.com.gs.springboot.gs_springboot.model.Atendimento;
import br.com.gs.springboot.gs_springboot.model.UnidadeSaude;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;

public interface AtendimentoService {
    Atendimento findById(Long id);
    Page<AtendimentoResponse> listarAtendimentosPorUnidade(Long unidadeId, Integer pagina, Integer tamanho);
    EntityModel<AtendimentoResponse> buscarAtendimento(Long id);
    AtendimentoResponse iniciarAtendimento(AtendimentoCriacaoRequest request);
    AtendimentoResponse encerrarAtendimento(Long id, AtendimentoAtualizacaoRequest request);
}
