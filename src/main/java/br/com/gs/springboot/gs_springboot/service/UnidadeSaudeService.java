package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.dto.response.UnidadeSaudeResponse;
import br.com.gs.springboot.gs_springboot.model.UnidadeSaude;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;

import java.math.BigDecimal;

public interface UnidadeSaudeService {
    UnidadeSaude findById(Long id);
    Page<UnidadeSaude> listarTodas(Integer pagina, Integer tamanho);
    EntityModel<UnidadeSaudeResponse> listarUnidade(Long id);
    Page<UnidadeSaude> listarUnidadesProximidade(BigDecimal latitude,
                                                 BigDecimal longitude,
                                                 String endereco,
                                                 Integer pagina,
                                                 Integer tamanho);
}
