package br.com.gs.springboot.gs_springboot.controller;

import br.com.gs.springboot.gs_springboot.dto.errors.ErrorResponse;
import br.com.gs.springboot.gs_springboot.dto.errors.ErrorValidation;
import br.com.gs.springboot.gs_springboot.dto.response.PageResponse;
import br.com.gs.springboot.gs_springboot.dto.response.UnidadeSaudeResponse;
import br.com.gs.springboot.gs_springboot.mapper.UnidadeSaudeMapper;
import br.com.gs.springboot.gs_springboot.model.TipoUnidade;
import br.com.gs.springboot.gs_springboot.service.UnidadeSaudeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/unidades")
@Tag(
        name = "Unidade de Saúde",
        description = "Catálogo de estabelecimentos do SUS (CNES), consulta individual com HATEOAS e busca geoespacial por proximidade e menor fila."
)
public class UnidadeSaudeController {
    private final UnidadeSaudeService unidadeSaudeService;

    public UnidadeSaudeController(UnidadeSaudeService unidadeSaudeService) {
        this.unidadeSaudeService = unidadeSaudeService;
    }

    @Operation(
            summary = "Listar todas as unidades de saúde (paginado, cacheado e com filtro dinâmico Query by Example)",
            description = """
                    Retorna uma lista paginada de todas as **Unidades de Saúde** registradas na base do SUS/CNES, incluindo endereço completo e tempo estimado de espera na fila (`tempoEspera`).

                    ### Filtros Dinâmicos (Query by Example)
                    - Permite filtrar opcionalmente por trecho do nome (`nome` — busca case-insensitive), classificação oficial (`tipoUnidade`) e/ou estado (`uf`, sigla de duas letras, ex.: `SP`).
                    - A lista vem em ordem alfabética pelo nome, para a paginação (`pagina` e `tamanho`) ser estável; o app usa isso na rolagem infinita da tela Buscar.
                    
                    ### Cache
                    - Os resultados são mantidos no cache `unidades` (`@Cacheable("unidades")`), reduzindo a latência e protegendo o banco relacional.
                    - O cache é invalidado automaticamente (`@CacheEvict`) a cada check-in ou encerramento de atendimento.
                    """,
            tags = {"Unidades de Saúde"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Página de unidades de saúde retornada com sucesso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponse.class),
                            examples = @ExampleObject(
                                    name = "Lista Paginada de Unidades",
                                    value = """
                                            {
                                              "conteudo": [
                                                {
                                                  "id": 1,
                                                  "nomeUnidade": "UBS Bela Vista - Paulista",
                                                  "cep": "01311000",
                                                  "logradouro": "Avenida Paulista",
                                                  "numero": "1100",
                                                  "bairro": "Bela Vista",
                                                  "tempoEspera": 40.0,
                                                  "tipoUnidade": "CENTRO_DE_SAUDE_UNIDADE_BASICA",
                                                  "municipio": "São Paulo",
                                                  "uf": "SP"
                                                }
                                              ],
                                              "pagina": 0,
                                              "tamanho": 10,
                                              "totalElementos": 1,
                                              "totalPaginas": 1,
                                              "ultimo": true
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parâmetros de paginação inválidos (`pagina < 0` ou `tamanho` fora do intervalo `1..100`).",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorValidation.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @SecurityRequirements
    @Cacheable("unidades")
    @GetMapping
    public PageResponse<UnidadeSaudeResponse> listarUnidades(
            @Parameter(
                    description = "Filtro opcional por nome da unidade (busca parcial sem distinção entre maiúsculas e minúsculas via Query by Example).",
                    example = "Paulista"
            )
            @RequestParam(required = false) String nome,

            @Parameter(
                    description = "Filtro opcional pelo tipo de unidade segundo a tabela CNES/SUS (ex.: CENTRO_DE_SAUDE_UNIDADE_BASICA, PRONTO_ATENDIMENTO, HOSPITAL_GERAL).",
                    example = "CENTRO_DE_SAUDE_UNIDADE_BASICA"
            )
            @RequestParam(required = false) TipoUnidade tipoUnidade,

            @Parameter(
                    description = "Filtro opcional por estado, pela sigla de duas letras (ex.: `SP`, `RJ`). Sem distinção entre maiúsculas e minúsculas.",
                    example = "SP",
                    schema = @Schema(type = "string", minLength = 2, maxLength = 2)
            )
            @RequestParam(required = false) @Pattern(regexp = "^[A-Za-z]{2}$", message = "UF deve ter duas letras") String uf,

            @Parameter(
                    description = "Índice da página desejada (inicia em `0`).",
                    example = "0",
                    schema = @Schema(type = "integer", defaultValue = "0", minimum = "0")
            )
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer pagina,

            @Parameter(
                    description = "Quantidade de registros por página (mínimo `1`, máximo `100`).",
                    example = "10",
                    schema = @Schema(type = "integer", defaultValue = "10", minimum = "1", maximum = "100")
            )
            @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(100) Integer tamanho
    ){
        return PageResponse.from(unidadeSaudeService.listarTodas(nome, tipoUnidade, uf, pagina, tamanho).map(UnidadeSaudeMapper::toResponse));
    }

    @Operation(
            summary = "Buscar unidade de saúde por ID (com HATEOAS)",
            description = """
                    Retorna os detalhes de uma **Unidade de Saúde** específica pelo seu `id`, enriquecida com links hipermídia **HATEOAS** (`self`, listagem geral e fila de atendimentos da unidade).

                    ### Cache
                    - Resultado individual cacheado por chave `#id` no cache `unidades`.
                    """,
            tags = {"Unidades de Saúde"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Unidade de saúde encontrada com sucesso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UnidadeSaudeResponse.class),
                            examples = @ExampleObject(
                                    name = "Unidade de Saúde com HATEOAS",
                                    value = """
                                            {
                                              "id": 1,
                                              "nomeUnidade": "UBS Bela Vista - Paulista",
                                              "cep": "01311000",
                                              "logradouro": "Avenida Paulista",
                                              "numero": "1100",
                                              "bairro": "Bela Vista",
                                              "tempoEspera": 40.0,
                                              "tipoUnidade": "CENTRO_DE_SAUDE_UNIDADE_BASICA",
                                              "_links": {
                                                "self": {
                                                  "href": "http://localhost:8080/api/unidades/1"
                                                },
                                                "unidades": {
                                                  "href": "http://localhost:8080/api/unidades?pagina=0&tamanho=10"
                                                }
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parâmetro `id` em formato inválido.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Unidade de saúde não encontrada para o `id` informado.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Unidade Não Encontrada",
                                    value = """
                                            {
                                              "mensagem": "Unidade de id (999) não encontrado!",
                                              "erro": "Not Found",
                                              "status": 404,
                                              "timestamp": "2026-11-08T18:30:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @SecurityRequirements
    @Cacheable(value = "unidades", key = "#id")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UnidadeSaudeResponse>> listarUnidade(
            @Parameter(
                    description = "Identificador único da unidade de saúde.",
                    example = "1",
                    required = true,
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long id){
        return ResponseEntity.ok(unidadeSaudeService.listarUnidade(id));
    }

    @Operation(
            summary = "Listar unidades de saúde por proximidade geográfica (Haversine + Bounding Box)",
            description = """
                    **Funcionalidade Core CitizenTech:** Ordena e retorna as unidades de saúde mais próximas do cidadão combinadas com o tempo estimado de espera na fila.

                    ### Estratégia Geoespacial
                    1. **Entrada via Coordenadas GPS (`latitude` e `longitude`)** ou **Endereço/CEP (`entradaEndereco`)** (enriquecido via *ViaCEP* + *Google Geocoding API*).
                    2. **Filtro de Bounding Box progressivo (±0,5°, ±2° e, por fim, sem limite):** Restringe a leitura no banco por `latitude` e `longitude` e só amplia a caixa quando ela não garante a página completa das unidades mais próximas.
                    3. **Fórmula de Haversine (JPQL):** Calcula a distância ortodrômica exata em quilômetros sobre as coordenadas persistidas, sem custo recorrente de API externa para cada linha do banco.
                    4. **Sem raio máximo:** Sempre devolve as unidades mais próximas, da mais perto para a mais longe (a espera só desempata), mesmo em cidades sem unidade por perto. Cada item traz `distanciaKm`. O app escolhe 3, 5 ou 10 unidades pelo parâmetro `tamanho`.
                    """,
            tags = {"Unidades de Saúde"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Página de unidades de saúde próximas ordenada por distância e tempo de espera.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponse.class),
                            examples = @ExampleObject(
                                    name = "Unidades Próximas Encontradas",
                                    value = """
                                            {
                                              "conteudo": [
                                                {
                                                  "id": 1,
                                                  "nomeUnidade": "UBS Bela Vista - Paulista",
                                                  "cep": "01311000",
                                                  "logradouro": "Avenida Paulista",
                                                  "numero": "1100",
                                                  "bairro": "Bela Vista",
                                                  "tempoEspera": 20.0,
                                                  "tipoUnidade": "CENTRO_DE_SAUDE_UNIDADE_BASICA"
                                                }
                                              ],
                                              "pagina": 0,
                                              "tamanho": 10,
                                              "totalElementos": 1,
                                              "totalPaginas": 1,
                                              "ultimo": true
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parâmetros geográficos ou de paginação inválidos.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "CEP informado em `entradaEndereco` não encontrado na base do ViaCEP.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "CEP Não Encontrado",
                                    value = """
                                            {
                                              "mensagem": "CEP (99999999) não encontrado!",
                                              "erro": "Not Found",
                                              "status": 404,
                                              "timestamp": "2026-11-08T18:30:00"
                                            }
                                            """
                            )
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
    @GetMapping("/proximas")
    public PageResponse<UnidadeSaudeResponse> listarUnidadesProximas(
            @Parameter(
                    description = "Latitude atual do cidadão em graus decimais (WGS84).",
                    example = "-23.5648865",
                    schema = @Schema(type = "number", format = "double", minimum = "-90", maximum = "90")
            )
            @RequestParam (required = false) BigDecimal latitude,

            @Parameter(
                    description = "Longitude atual do cidadão em graus decimais (WGS84).",
                    example = "-46.6529992",
                    schema = @Schema(type = "number", format = "double", minimum = "-180", maximum = "180")
            )
            @RequestParam (required = false) BigDecimal longitude,

            @Parameter(
                    description = "CEP (8 dígitos, ex.: `01311000`) ou endereço textual (ex.: `Avenida Paulista, 1100, São Paulo`) caso o GPS não seja informado.",
                    example = "01311000"
            )
            @RequestParam (required = false) String endereco,

            @Parameter(
                    description = "Índice da página desejada (inicia em `0`).",
                    example = "0",
                    schema = @Schema(type = "integer", defaultValue = "0", minimum = "0")
            )
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer pagina,

            @Parameter(
                    description = "Quantidade de registros por página (mínimo `1`, máximo `100`).",
                    example = "10",
                    schema = @Schema(type = "integer", defaultValue = "10", minimum = "1", maximum = "100")
            )
            @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(100) Integer tamanho){

        return PageResponse.from(unidadeSaudeService.listarUnidadesProximidade(
                latitude, longitude, endereco, pagina, tamanho)
                .map(item -> UnidadeSaudeMapper.toResponse(item.unidade(), item.distanciaKm())));
    }

}
