package br.com.gs.springboot.gs_springboot.dto.response;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresInSeconds
        //duvida sobre email e role. - em nossa aplicacao role de quem é colaborador pode encerrar atendimento. outras coisas é pro cidadao iniciar atendimento e verificar unidade mais vazia
//        String email,
//        Role role
) {
}
