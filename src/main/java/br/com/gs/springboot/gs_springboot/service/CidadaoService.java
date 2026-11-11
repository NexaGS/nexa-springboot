package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.dto.request.RegistroRequest;
import br.com.gs.springboot.gs_springboot.dto.response.CidadaoResponse;
import br.com.gs.springboot.gs_springboot.model.Cidadao;
import org.springframework.data.domain.Page;

public interface CidadaoService {
    Cidadao findById(Long id);
    Cidadao registrar(RegistroRequest request);
    boolean existeEmail(String email);
    Page<CidadaoResponse> listarCidadaos(Integer pagina, Integer tamanho);
    CidadaoResponse adicionarUnidadeLista(Long id, Long unidadeId);
    CidadaoResponse removerUnidadeLista(Long id, Long unidadeId);
}
