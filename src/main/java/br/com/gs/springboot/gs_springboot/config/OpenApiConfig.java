package br.com.gs.springboot.gs_springboot.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@OpenAPIDefinition
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Token JWT obtido nos endpoints `POST /api/auth/login` ou `POST /api/auth/registro`. Informe o token no campo de autorização."
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Nexa (CitizenTech) — API de Gestão Inteligente de Filas e Atendimentos no SUS")
                        .version("v1.0.0")
                        .description("""
                                **API RESTful da solução Nexa (Global Solution FIAP)** voltada à gestão inteligente de filas e direcionamento de cidadãos às Unidades de Saúde Públicas (SUS/CNES).

                                ### Arquitetura e Recursos Principais
                                - **Jornada do Cidadão & Consentimento LGPD:** Criação de conta com aceite explícito de uso de geolocalização (`POST /api/auth/registro`) e login unificado para app mobile e portal web (`POST /api/auth/login`).
                                - **Geolocalização & Proximidade:** Consulta integrada com *ViaCEP* e *Google Geocoding API*, aplicando cálculo espacial com a **Fórmula de Haversine** e filtro por *Bounding Box* sobre dados em **3ª Forma Normal (3FN)**.
                                - **Motor de Fila FIFO & Senha Sequencial:** Check-in com seleção de serviço (`CLINICO_GERAL`, `TRIAGEM`, `VACINACAO`, `PEDIATRIA`, `ODONTOLOGIA`), emissão automática de senhas formatadas (ex.: `CLI-001`, `VAC-002`), chamada automática imediata em guichês livres e estimativa dinâmica de posição e tempo de espera.
                                - **Encerramento com Promoção Automática:** Colaboradores autenticados finalizam atendimentos, associando seu perfil e promovendo automaticamente o próximo paciente `AGUARDANDO` para `CHAMADO`, com recálculo da fila e invalidação preventiva de cache (`@CacheEvict`).
                                - **Filtros Dinâmicos (Query by Example):** Catálogo de unidades de saúde filtrável de forma flexível por nome e tipo de estabelecimento.
                                - **Hipermídia (HATEOAS):** Respostas enriquecidas com links dinâmicos (`_links`), guiando o cliente nas transições de estado permitidas.
                                - **Segurança Stateless:** Tokens **JWT (HS256)** com autorização baseada em roles (`ROLE_COLABORADOR` e `ROLE_USUARIO`) e senhas com hash **BCrypt**.
                                """)
                        .contact(new Contact()
                                .name("Equipe Nexa — Global Solution FIAP")
                                .url("https://github.com/NexaGS"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("Visualização interativa Redoc integrada (/docs.html)")
                        .url("/docs.html"))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor de Desenvolvimento Local"),
                        new Server().url("/").description("Servidor Atual (Relativo)")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new io.swagger.v3.oas.models.security.SecurityScheme()
                                .name("bearerAuth")
                                .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Insira o token JWT retornado por `POST /api/auth/login` ou `POST /api/auth/registro`.")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .tags(List.of(
                        new Tag()
                                .name("Autenticação")
                                .description("Criação de conta de usuário com consentimento LGPD e autenticação stateless (JWT Bearer) para usuários e colaboradores."),
                        new Tag()
                                .name("Unidades de Saúde")
                                .description("Catálogo de estabelecimentos do SUS (CNES), filtros dinâmicos com Query by Example, consulta individual com HATEOAS e busca geoespacial por proximidade e menor fila."),
                        new Tag()
                                .name("Atendimentos")
                                .description("Gestão do fluxo de fila: check-in com serviço e emissão de senha sequencial, chamada automática FIFO, consulta de posição e encerramento autenticado."),
                        new Tag()
                                .name("Colaboradores")
                                .description("Cadastro e gerenciamento de profissionais de saúde vinculados às unidades de atendimento (acesso restrito a colaboradores).")
                ));

        // Extensão oficial Redocly (x-tagGroups) para navegação hierárquica na barra lateral do Redocly
        openAPI.addExtension("x-tagGroups", List.of(
                Map.of(
                        "name", "1. Segurança & Acesso",
                        "tags", List.of("Autenticação", "Colaboradores")
                ),
                Map.of(
                        "name", "2. Jornada do Cidadão & Triagem",
                        "tags", List.of("Unidades de Saúde", "Atendimentos")
                )
        ));

        return openAPI;
    }
}
