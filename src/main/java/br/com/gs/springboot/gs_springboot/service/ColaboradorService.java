package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.dto.request.ColaboradorRequest;
import br.com.gs.springboot.gs_springboot.dto.response.ColaboradorResponse;
import br.com.gs.springboot.gs_springboot.model.Colaborador;

import java.util.List;

public interface ColaboradorService {
    Colaborador findById(Long id);
//    List<ColaboradorResponse> listarColaboradoresPorUnidade(Long idUnidade);
    ColaboradorResponse criar(ColaboradorRequest request);
}
