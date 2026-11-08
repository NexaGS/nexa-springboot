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
        description = "Token JWT obtido no endpoint `POST /api/auth/login`. Informe apenas o token no campo de autorização."
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Nexa (CitizenTech) — API de Gestão Inteligente de Filas e Atendimentos no SUS")
                        .version("v1.0.0")
                        .description("""
                                **API RESTful da solução Nexa (Global Solution FIAP)** voltada à otimização do fluxo de triagem, \
                                consulta de unidades de saúde próximas por geolocalização (Fórmula de Haversine + Bounding Box) \
                                e acompanhamento em tempo real do tempo estimado de espera nas filas do Sistema Único de Saúde (SUS).

                                ### Arquitetura e Recursos Principais
                                - **Geolocalização & Proximidade:** Consulta integrada com *ViaCEP* e *Google Geocoding API*, aplicando cálculo espacial Haversine sobre coordenadas persistidas em 3FN (IBGE/CNES).
                                - **Gestão de Filas e Tempo de Espera:** Check-in automatizado via totem/app e encerramento autenticado por colaboradores, recalculando dinamicamente o tempo estimado da unidade com invalidação automática de cache (`@CacheEvict`).
                                - **HATEOAS:** Respostas enriquecidas com hipermídia (`_links`) guiando o cliente pelas transições de estado permitidas.
                                - **Segurança Stateless:** Autenticação via **Bearer JWT (HS256)** e senhas criptografadas com **BCrypt**.
                                """)
                        .contact(new Contact()
                                .name("Equipe Nexa — Global Solution FIAP - Repositório GitHub")
                                .url("https://github.com/NexaGS"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description(" Visualização interativa Redoc integrada (/docs.html)")
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
                                .description("Insira o token JWT retornado por `POST /api/auth/login`.")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .tags(List.of(
                        new Tag()
                                .name("Autenticação")
                                .description("Autenticação stateless de colaboradores e emissão de tokens **JWT (Bearer)**."),
                        new Tag()
                                .name("Unidades de Saúde")
                                .description("Catálogo de estabelecimentos do SUS (CNES), consulta individual com HATEOAS e busca geoespacial por proximidade e menor fila."),
                        new Tag()
                                .name("Atendimentos")
                                .description("Gestão do fluxo de fila: abertura de senha no totem/app (check-in), listagem por unidade e encerramento pelo colaborador."),
                        new Tag()
                                .name("Colaboradores")
                                .description("Cadastro e gerenciamento de profissionais de saúde vinculados às unidades de atendimento."),
                        new Tag()
                                .name("Localizações")
                                .description("Gerenciamento de endereços normalizados (3FN) e coordenadas geográficas das unidades de saúde.")
                ));

        // Extensão oficial Redocly (x-tagGroups) para navegação hierárquica na barra lateral
        openAPI.addExtension("x-tagGroups", List.of(
                Map.of(
                        "name", "1. Segurança & Acesso",
                        "tags", List.of("Autenticação", "Colaboradores")
                ),
                Map.of(
                        "name", "2. Jornada do Cidadão & Triagem",
                        "tags", List.of("Unidades de Saúde", "Atendimentos")
                ),
                Map.of(
                        "name", "3. Infraestrutura Geográfica",
                        "tags", List.of("Localizações")
                )
        ));

        return openAPI;
    }
}
