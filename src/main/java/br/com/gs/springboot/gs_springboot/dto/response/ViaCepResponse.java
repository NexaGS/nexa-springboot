package br.com.gs.springboot.gs_springboot.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "DTO contendo o retorno da busca de CEP na Api do ViaCep")
public record ViaCepResponse(
        String cep,
        String logradouro,
        String complemento,
        String bairro,

        @JsonProperty("localidade")
        String municipio,

        String uf,
        String estado,

        @JsonProperty("erro")
        String erro
) {
}
