package br.com.gs.springboot.gs_springboot.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@OpenAPIDefinition(
        info = @Info(
                title = "Nexa - Solução para atendimentos no Sistema Unificado de Súde",
                version = "v1",
                description = "API RESTful do solução da Global Solution proposta em otimizar os atendimentos nas unidades de saúde públicas"
        ),
        servers = {
                @Server(url = "/", description = "Servidor atual")
        }
)
public class OpenApiConfig {
}
