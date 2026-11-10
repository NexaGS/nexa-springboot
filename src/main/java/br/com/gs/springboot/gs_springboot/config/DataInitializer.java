package br.com.gs.springboot.gs_springboot.config;

import br.com.gs.springboot.gs_springboot.model.*;
import br.com.gs.springboot.gs_springboot.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Value("${app.admin.email:admin@nexa.com}")
    private String adminEmail;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Bean
    public CommandLineRunner carregarDadosIniciais(
            EstadoRepository estadoRepository,
            MunicipioRepository municipioRepository,
            UnidadeSaudeRepository unidadeRepository,
            PacienteRepository pacienteRepository,
            ColaboradorRepository colaboradorRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (unidadeRepository.count() == 0) {
                Estado sp = new Estado();
                sp.setCodigoUf("35");
                sp.setSigla("SP");
                sp.setNome("São Paulo");
                sp = estadoRepository.save(sp);

                Municipio saoPaulo = new Municipio();
                saoPaulo.setCodigoMunicipioIbge("3550308");
                saoPaulo.setNome("São Paulo");
                saoPaulo.setEstado(sp);
                saoPaulo = municipioRepository.save(saoPaulo);

                Localizacao loc1 = new Localizacao();
                loc1.setCep("01311000");
                loc1.setLogradouro("Avenida Paulista");
                loc1.setNumero("1100");
                loc1.setBairro("Bela Vista");
                loc1.setMunicipio(saoPaulo);
                loc1.setLatitude(new BigDecimal("-23.5648865"));
                loc1.setLongitude(new BigDecimal("-46.6529992"));

                UnidadeSaude ubsPaulista = new UnidadeSaude();
                ubsPaulista.setNome("UBS Bela Vista - Paulista");
                ubsPaulista.setLocal(loc1);
                ubsPaulista.setTempoEspera(0.0);
                ubsPaulista.setTipoUnidade(TipoUnidade.CENTRO_DE_SAUDE_UNIDADE_BASICA);
                ubsPaulista = unidadeRepository.save(ubsPaulista);

                Localizacao loc2 = new Localizacao();
                loc2.setCep("04018000");
                loc2.setLogradouro("Rua Domingos de Morais");
                loc2.setNumero("1940");
                loc2.setBairro("Vila Mariana");
                loc2.setMunicipio(saoPaulo);
                loc2.setLatitude(new BigDecimal("-23.5880000"));
                loc2.setLongitude(new BigDecimal("-46.6340000"));

                UnidadeSaude upaVilaMariana = new UnidadeSaude();
                upaVilaMariana.setNome("UPA Vila Mariana");
                upaVilaMariana.setLocal(loc2);
                upaVilaMariana.setTempoEspera(0.0);
                upaVilaMariana.setTipoUnidade(TipoUnidade.PRONTO_ATENDIMENTO);
                unidadeRepository.save(upaVilaMariana);

                if (colaboradorRepository.findByEmail(adminEmail).isEmpty()) {
                    Colaborador admin = new Colaborador();
                    admin.setNome("Dra. Ana Paula Souza");
                    admin.setCargo("Enfermeira Chefe");
                    admin.setEmail(adminEmail);
                    admin.setSenha(passwordEncoder.encode(adminPassword));
                    admin.setUnidade(ubsPaulista);
                    colaboradorRepository.save(admin);
                }
            }

            if (pacienteRepository.count() == 0) {
                criarPaciente(pacienteRepository, "12345678901", "João da Silva Santos");
                criarPaciente(pacienteRepository, "98765432100", "Maria Oliveira Souza");
                criarPaciente(pacienteRepository, "11122233344", "Carlos Eduardo Lima");
            }
        };
    }

    private void criarPaciente(PacienteRepository repo, String cpf, String nome) {
        Paciente p = new Paciente();
        p.setCpf(cpf);
        p.setNome(nome);
        repo.save(p);
    }
}
