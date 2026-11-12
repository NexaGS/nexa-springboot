package br.com.gs.springboot.gs_springboot.dto.response;

import br.com.gs.springboot.gs_springboot.model.UnidadeSaude;

import java.time.LocalDateTime;
import java.util.List;

public record CidadaoResponse(
        Long id,
        String nome,
        String email,
        LocalDateTime dataHoraAceiteLocalizacao,
        List<String> unidadesFavoritas
) {
}
