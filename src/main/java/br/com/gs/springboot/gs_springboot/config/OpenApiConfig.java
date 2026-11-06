package br.com.gs.springboot.gs_springboot.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@SecurityScheme(name = "basicAuth", type = SecuritySchemeType.HTTP, scheme = "basic")
@OpenAPIDefinition(
        info = @Info(
                title = "Global Solution - Cityzen Tech",
                version = "v1",
                description = "API RESTful do solução da Global Solution proposta pela FIAP, para atender"
        ),
        servers = {
                @Server(url = "/", description = "Servidor atual")
        }
)
public class OpenApiConfig {
}
