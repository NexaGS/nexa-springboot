//package br.com.gs.springboot.gs_springboot.mapper;
//
//import br.com.gs.springboot.gs_springboot.dto.response.LocalizacaoResponse;
//import br.com.gs.springboot.gs_springboot.dto.response.PacienteResponse;
//import br.com.gs.springboot.gs_springboot.model.Localizacao;
//import br.com.gs.springboot.gs_springboot.model.Paciente;
//
//public final class PacienteMapper {
//
//    private PacienteMapper() {
//    }
//
//    public static PacienteResponse toResponse(Paciente paciente){
//        return new LocalizacaoResponse(
//                localizacao.getId(),
//                localizacao.getCep(),
//                localizacao.getLogradouro(),
//                localizacao.getNumero(),
//                localizacao.getBairro(),
//                localizacao.getLatitude().toString(),
//                localizacao.getLongitude().toString()
//        );
//    }
//}
