package br.com.gs.springboot.gs_springboot.dto.request;

public record ColaboradorRequest(
        //Algum outro identificador como cpf ou email
        String nome,
        String cargo,
        Long idUnidadeSaude
) {
}
