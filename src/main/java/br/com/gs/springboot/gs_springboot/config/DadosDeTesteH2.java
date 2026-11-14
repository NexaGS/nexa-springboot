package br.com.gs.springboot.gs_springboot.config;

import br.com.gs.springboot.gs_springboot.model.Cidadao;
import br.com.gs.springboot.gs_springboot.model.Estado;
import br.com.gs.springboot.gs_springboot.model.Localizacao;
import br.com.gs.springboot.gs_springboot.model.Municipio;
import br.com.gs.springboot.gs_springboot.model.TipoUnidade;
import br.com.gs.springboot.gs_springboot.model.UnidadeSaude;
import br.com.gs.springboot.gs_springboot.repository.CidadaoRepository;
import br.com.gs.springboot.gs_springboot.repository.EstadoRepository;
import br.com.gs.springboot.gs_springboot.repository.MunicipioRepository;
import br.com.gs.springboot.gs_springboot.repository.UnidadeSaudeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Profile("h2")
public class DadosDeTesteH2 {

    private static final Logger log = LoggerFactory.getLogger(DadosDeTesteH2.class);

    private static final String NOME_DE_TESTE = "Renan";
    private static final String EMAIL_DE_TESTE = "renan@nexa.com";
    private static final String SENHA_DE_TESTE = "Nexa@2026";

    private record UnidadeDeTeste(
            String nome,
            TipoUnidade tipo,
            String logradouro,
            String numero,
            String bairro,
            String cep,
            String latitude,
            String longitude,
            double tempoEspera
    ) {
    }

    private static final List<UnidadeDeTeste> UNIDADES = List.of(
            new UnidadeDeTeste("Unidade A - São Mateus", TipoUnidade.CENTRO_DE_SAUDE_UNIDADE_BASICA,
                    "Rua Doutor Carlos Júlio Spera", "100", "Cidade São Mateus", "03963090",
                    "-23.6115000", "-46.4785000", 90.0),
            new UnidadeDeTeste("UBS Abobrinha - Maria Cursi", TipoUnidade.CENTRO_DE_SAUDE_UNIDADE_BASICA,
                    "Avenida Maria Cursi", "500", "Cidade São Mateus", "03962000",
                    "-23.6023800", "-46.4783400", 45.0),
            new UnidadeDeTeste("UPA Jabuticaba - São Mateus", TipoUnidade.PRONTO_ATENDIMENTO,
                    "Rua André de Almeida", "300", "Cidade São Mateus", "03950000",
                    "-23.5982200", "-46.4718600", 15.0),
            new UnidadeDeTeste("Hospital Goiabada - Mateo Bei", TipoUnidade.HOSPITAL_GERAL,
                    "Avenida Mateo Bei", "800", "Cidade São Mateus", "03949000",
                    "-23.5895000", "-46.4899500", 20.0),
            new UnidadeDeTeste("UBS Tatuapé", TipoUnidade.CENTRO_DE_SAUDE_UNIDADE_BASICA,
                    "Rua Tuiuti", "700", "Tatuapé", "03081015",
                    "-23.5333200", "-46.5755900", 25.0),
            new UnidadeDeTeste("UPA Tiradentes - Luz", TipoUnidade.PRONTO_ATENDIMENTO,
                    "Avenida Tiradentes", "500", "Luz", "01102000",
                    "-23.5282700", "-46.6310700", 60.0),
            new UnidadeDeTeste("Hospital Santo Amaro", TipoUnidade.HOSPITAL_GERAL,
                    "Avenida Adolfo Pinheiro", "1000", "Santo Amaro", "04733971",
                    "-23.6451800", "-46.7008900", 40.0),
            new UnidadeDeTeste("UBS Vila Romana", TipoUnidade.CENTRO_DE_SAUDE_UNIDADE_BASICA,
                    "Rua Catão", "300", "Vila Romana", "05049000",
                    "-23.5284200", "-46.7007600", 10.0)
    );

    private record Cidade(String codigoUf, String sigla, String nomeEstado, String codigoIbge, String nome) {
    }

    private record UnidadeEmOutroEstado(Cidade cidade, UnidadeDeTeste unidade) {
    }

    private static final List<UnidadeEmOutroEstado> UNIDADES_EM_OUTROS_ESTADOS = List.of(
            new UnidadeEmOutroEstado(
                    new Cidade("33", "RJ", "Rio de Janeiro", "3304557", "Rio de Janeiro"),
                    new UnidadeDeTeste("UPA Carioca - Centro", TipoUnidade.PRONTO_ATENDIMENTO,
                            "Avenida Rio Branco", "181", "Centro", "20040918",
                            "-22.9026000", "-43.1796100", 35.0)),
            new UnidadeEmOutroEstado(
                    new Cidade("31", "MG", "Minas Gerais", "3106200", "Belo Horizonte"),
                    new UnidadeDeTeste("UBS Mineira - Centro", TipoUnidade.CENTRO_DE_SAUDE_UNIDADE_BASICA,
                            "Avenida Afonso Pena", "1901", "Centro", "30130925",
                            "-19.9209600", "-43.9371500", 50.0)),
            new UnidadeEmOutroEstado(
                    new Cidade("43", "RS", "Rio Grande do Sul", "4314902", "Porto Alegre"),
                    new UnidadeDeTeste("Hospital Gaúcho - Centro Histórico", TipoUnidade.HOSPITAL_GERAL,
                            "Largo dos Medeiros", "100", "Centro Histórico", "90020190",
                            "-30.0267800", "-51.2289800", 70.0)),
            new UnidadeEmOutroEstado(
                    new Cidade("29", "BA", "Bahia", "2927408", "Salvador"),
                    new UnidadeDeTeste("UBS Baiana - Vitória", TipoUnidade.CENTRO_DE_SAUDE_UNIDADE_BASICA,
                            "Avenida Sete de Setembro", "2400", "Vitória", "40080003",
                            "-13.0030000", "-38.5324900", 25.0))
    );

    private final UnidadeSaudeRepository unidadeRepository;
    private final EstadoRepository estadoRepository;
    private final MunicipioRepository municipioRepository;
    private final CidadaoRepository cidadaoRepository;
    private final PasswordEncoder passwordEncoder;

    public DadosDeTesteH2(
            UnidadeSaudeRepository unidadeRepository,
            EstadoRepository estadoRepository,
            MunicipioRepository municipioRepository,
            CidadaoRepository cidadaoRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.unidadeRepository = unidadeRepository;
        this.estadoRepository = estadoRepository;
        this.municipioRepository = municipioRepository;
        this.cidadaoRepository = cidadaoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void carregar(Municipio saoPaulo) {
        for (UnidadeDeTeste dados : UNIDADES) {
            salvarUnidade(saoPaulo, dados);
        }
        for (UnidadeEmOutroEstado outra : UNIDADES_EM_OUTROS_ESTADOS) {
            salvarUnidade(criarMunicipio(outra.cidade()), outra.unidade());
        }
        criarUsuarioDeTeste();

        log.info("Dados de teste do H2: {} unidades e o usuário {}",
                UNIDADES.size() + UNIDADES_EM_OUTROS_ESTADOS.size(), EMAIL_DE_TESTE);
    }

    private Municipio criarMunicipio(Cidade cidade) {
        Estado estado = new Estado();
        estado.setCodigoUf(cidade.codigoUf());
        estado.setSigla(cidade.sigla());
        estado.setNome(cidade.nomeEstado());
        estado = estadoRepository.save(estado);

        Municipio municipio = new Municipio();
        municipio.setCodigoMunicipioIbge(cidade.codigoIbge());
        municipio.setNome(cidade.nome());
        municipio.setEstado(estado);
        return municipioRepository.save(municipio);
    }

    private void salvarUnidade(Municipio municipio, UnidadeDeTeste dados) {
        Localizacao local = new Localizacao();
        local.setCep(dados.cep());
        local.setLogradouro(dados.logradouro());
        local.setNumero(dados.numero());
        local.setBairro(dados.bairro());
        local.setMunicipio(municipio);
        local.setLatitude(new BigDecimal(dados.latitude()));
        local.setLongitude(new BigDecimal(dados.longitude()));

        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setNome(dados.nome());
        unidade.setLocal(local);
        unidade.setTempoEspera(dados.tempoEspera());
        unidade.setTipoUnidade(dados.tipo());
        unidadeRepository.save(unidade);
    }

    private void criarUsuarioDeTeste() {
        if (cidadaoRepository.existsByEmail(EMAIL_DE_TESTE)) {
            return;
        }
        Cidadao cidadao = new Cidadao();
        cidadao.setNome(NOME_DE_TESTE);
        cidadao.setEmail(EMAIL_DE_TESTE);
        cidadao.setSenha(passwordEncoder.encode(SENHA_DE_TESTE));
        cidadao.setDataHoraAceiteLocalizacao(LocalDateTime.now());
        cidadaoRepository.save(cidadao);
    }
}
