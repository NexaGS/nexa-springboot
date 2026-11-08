package br.com.gs.springboot.gs_springboot.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(
        name = "TipoUnidade",
        description = "Classificação oficial do estabelecimento de saúde segundo a tabela do CNES/SUS.",
        enumAsRef = true
)
public enum TipoUnidade {
    POSTO_DE_SAUDE(1),
    CENTRO_DE_SAUDE_UNIDADE_BASICA(2),
    POLICLINICA(4),
    HOSPITAL_GERAL(5),
    HOSPITAL_ESPECIALIZADO(7),
    UNIDADE_MISTA(15),
    PRONTO_SOCORRO_GERAL(20),
    PRONTO_SOCORRO_ESPECIALIZADO(21),
    CONSULTORIO_ISOLADO(22),
    UNIDADE_MOVEL_FLUVIAL(32),
    CLINICA_CENTRO_DE_ESPECIALIDADE(36),
    UNIDADE_DE_APOIO_DIAGNOSE_E_TERAPIA(39),
    UNIDADE_MOVEL_TERRESTRE(40),
    UNIDADE_MOVEL_PRE_HOSPITALAR(42),
    UNIDADE_DE_VIGILANCIA_EM_SAUDE(50),
    COOPERATIVA_DE_TRABALHADORES_NA_SAUDE(60),
    CENTRO_DE_PARTO_NORMAL(61),
    HOSPITAL_DIA_ISOLADO(62),
    CENTRAL_DE_REGULACAO_DE_SERVICOS_DE_SAUDE(64),
    LACEN(67),
    CENTRAL_DE_GESTAO_EM_SAUDE(68),
    CENTRO_HEMOTERAPICO(69),
    CAPS(70),
    NASF(71),
    SAUDE_INDIGENA(72),
    PRONTO_ATENDIMENTO(73),
    TELESSAUDE(75),
    CENTRAL_DE_REGULACAO_MEDICA_DAS_URGENCIAS(76),
    HOME_CARE(77),
    UNIDADE_RESIDENCIAL(78),
    OFICINA_ORTOPEDICA(79),
    POLO_DE_PREVENCAO(83),
    CENTRO_DE_IMUNIZACAO(85);

    private final Integer codigo;
}
