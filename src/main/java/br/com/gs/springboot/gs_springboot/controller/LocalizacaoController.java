package br.com.gs.springboot.gs_springboot.controller;

import br.com.gs.springboot.gs_springboot.dto.errors.ErrorResponse;
import br.com.gs.springboot.gs_springboot.dto.response.LocalizacaoResponse;
import br.com.gs.springboot.gs_springboot.service.LocalizacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/localizacoes")
@Tag(
        name = "Localização",
        description = "Gerenciamento de endereços normalizados (3FN) e coordenadas geográficas das unidades de saúde."
)
public class LocalizacaoController {

    private final LocalizacaoService localizacaoService;

    public LocalizacaoController(LocalizacaoService localizacaoService) {
        this.localizacaoService = localizacaoService;
    }

    @Operation(
            summary = "Descobrir o endereço a partir de coordenadas (geocodificação reversa)",
            description = """
                    Recebe a posição do aparelho (`latitude` e `longitude`) e devolve o endereço correspondente: logradouro, bairro, município e UF.

                    - Usa a **Google Geocoding API**; a chave fica apenas no servidor.
                    - O endereço **não é gravado**: o campo `id` vem nulo.
                    - O que o Google não souber informar vem nulo (por exemplo `numero` ou `cep`).
                    - Rota pública: o visitante também usa.
                    """,
            tags = {"Localização"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Endereço encontrado para as coordenadas.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LocalizacaoResponse.class),
                            examples = @ExampleObject(
                                    name = "Endereço Encontrado",
                                    value = """
                                            {
                                              "id": null,
                                              "cep": "01311000",
                                              "logradouro": "Avenida Paulista",
                                              "numero": "1100",
                                              "bairro": "Bela Vista",
                                              "municipio": "São Paulo",
                                              "uf": "SP",
                                              "estado": "São Paulo",
                                              "latitude": -23.5648865,
                                              "longitude": -46.6529992
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Coordenadas ausentes ou fora do intervalo, chave do Google não configurada ou local não encontrado.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor ou falha de integração externa.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @SecurityRequirements
    @GetMapping("/reversa")
    public LocalizacaoResponse buscarPorCoordenadas(
            @Parameter(
                    description = "Latitude em graus decimais (WGS84).",
                    example = "-23.5648865",
                    schema = @Schema(type = "number", format = "double", minimum = "-90", maximum = "90")
            )
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,

            @Parameter(
                    description = "Longitude em graus decimais (WGS84).",
                    example = "-46.6529992",
                    schema = @Schema(type = "number", format = "double", minimum = "-180", maximum = "180")
            )
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude) {
        return localizacaoService.buscarPorCoordenadas(latitude, longitude);
    }
}
