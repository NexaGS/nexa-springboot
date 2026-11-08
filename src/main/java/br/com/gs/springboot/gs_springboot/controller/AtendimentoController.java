package br.com.gs.springboot.gs_springboot.controller;

import br.com.gs.springboot.gs_springboot.dto.errors.ErrorResponse;
import br.com.gs.springboot.gs_springboot.dto.errors.ErrorValidation;
import br.com.gs.springboot.gs_springboot.dto.request.AtendimentoAtualizacaoRequest;
import br.com.gs.springboot.gs_springboot.dto.request.AtendimentoCriacaoRequest;
import br.com.gs.springboot.gs_springboot.dto.response.AtendimentoResponse;
import br.com.gs.springboot.gs_springboot.dto.response.PageResponse;
import br.com.gs.springboot.gs_springboot.service.AtendimentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/atendimentos")
@Tag(
        name = "Atendimento",
        description = "Gestão do fluxo de fila: abertura de senha no totem/app (check-in), listagem por unidade e encerramento pelo colaborador."
)
public class AtendimentoController {
    private final AtendimentoService atendimentoService;

    public AtendimentoController(AtendimentoService atendimentoService) {
        this.atendimentoService = atendimentoService;
    }

    @Operation(
            summary = "Buscar atendimento por ID (com HATEOAS)",
            description = """
                    Retorna os detalhes de um atendimento específico pelo seu identificador único (`id`), enriquecido com links hipermídia **HATEOAS** (`_links`).

                    ### Navegação Hipermídia (HATEOAS)
                    - `self`: Link para o próprio atendimento (`GET /api/atendimentos/{id}`).
                    - `atendimentos-unidade`: Link para a fila de atendimentos da unidade de saúde.
                    - `encerrar-atendimento`: Exibido quando o atendimento encontra-se `ABERTO`, indicando a transição de estado permitida (`PUT /api/atendimentos/{id}/encerramento`).
                    """,
            tags = {"Atendimentos"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Atendimento encontrado com sucesso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AtendimentoResponse.class),
                            examples = @ExampleObject(
                                    name = "Atendimento em Aberto (HATEOAS)",
                                    value = """
                                            {
                                              "id": 42,
                                              "nomePaciente": "João da Silva Santos",
                                              "nomeColaborador": "Dra. Ana Paula Souza",
                                              "status": "ABERTO",
                                              "horaChegada": "2026-11-08T14:15:30",
                                              "horaFim": null,
                                              "_links": {
                                                "self": {
                                                  "href": "http://localhost:8080/api/atendimentos/42"
                                                },
                                                "encerrar-atendimento": {
                                                  "href": "http://localhost:8080/api/atendimentos/42/encerramento"
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
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "ID Inválido",
                                    value = """
                                            {
                                              "mensagem": "Parâmetro: (id) inválido!",
                                              "erro": "Bad Request",
                                              "status": 400,
                                              "timestamp": "2026-11-08T18:30:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Atendimento não encontrado para o `id` informado.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Atendimento Não Encontrado",
                                    value = """
                                            {
                                              "mensagem": "Atendimento de id (999) não encontrado!",
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
                    description = "Erro interno inesperado no servidor.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @SecurityRequirements
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<AtendimentoResponse>> buscarAtendimento(
            @Parameter(
                    description = "Identificador único do atendimento.",
                    example = "42",
                    required = true,
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long id) {
        return ResponseEntity.ok(atendimentoService.buscarAtendimento(id));
    }

    @Operation(
            summary = "Listar atendimentos abertos de uma unidade de saúde (paginado)",
            description = """
                    Retorna uma página de atendimentos com status `ABERTO` vinculados a uma unidade de saúde específica (`unidadeId`).

                    ### Cache e Performance
                    - Resultados cacheados no cache `atendimentos-unidade` via `@Cacheable`.
                    - Requer autenticação via **Bearer JWT**.
                    """,
            tags = {"Atendimentos"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Página de atendimentos da unidade retornada com sucesso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponse.class),
                            examples = @ExampleObject(
                                    name = "Fila da Unidade",
                                    value = """
                                            {
                                              "conteudo": [
                                                {
                                                  "id": 42,
                                                  "nomePaciente": "João da Silva Santos",
                                                  "nomeColaborador": "Dra. Ana Paula Souza",
                                                  "status": "ABERTO",
                                                  "horaChegada": "2026-11-08T14:15:30",
                                                  "horaFim": null
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
                    description = "Parâmetros de paginação fora dos limites permitidos (`pagina >= 0`, `1 <= tamanho <= 100`).",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorValidation.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token JWT ausente, inválido ou expirado.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado no servidor.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @Cacheable("atendimentos-unidade")
    @GetMapping("/unidades/{unidadeId}")
    public ResponseEntity<PageResponse<AtendimentoResponse>> buscarAtendimentosPorUnidades(
            @Parameter(
                    description = "Identificador único da unidade de saúde.",
                    example = "1",
                    required = true,
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long unidadeId,

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
            @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(100) Integer tamanho) {

        return ResponseEntity.ok(PageResponse.from(atendimentoService.listarAtendimentosPorUnidade(unidadeId, pagina, tamanho)));
    }

    @Operation(
            summary = "Iniciar atendimento (Check-in no Totem / App)",
            description = """
                    Registra a entrada do cidadão na fila de atendimento da unidade de saúde informando o **CPF** e o **ID da unidade**.

                    ### Efeitos Colaterais e Regras de Negócio
                    1. Localiza o cadastro do paciente pelo `cpf` e a unidade pelo `idUnidade`.
                    2. Cria um novo registro de atendimento com status inicial `ABERTO` e `horaChegada` atual.
                    3. **Recalcula automaticamente o `tempoEspera`** da unidade de saúde proporcionalmente ao tamanho da fila aberta (`filaAberta * 20 min`).
                    4. **Invalida o cache** `unidades` (`@CacheEvict`) para que novas buscas de proximidade reflitam imediatamente o novo tempo de espera.
                    """,
            tags = {"Atendimentos"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Check-in realizado e atendimento iniciado com sucesso.",
                    headers = @Header(
                            name = "Location",
                            description = "URI do atendimento iniciado (`api/atendimentos/checkIn/{id}`).",
                            schema = @Schema(type = "string", example = "api/atendimentos/checkIn/42")
                    ),
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AtendimentoResponse.class),
                            examples = @ExampleObject(
                                    name = "Atendimento Iniciado",
                                    value = """
                                            {
                                              "id": 42,
                                              "nomePaciente": "João da Silva Santos",
                                              "nomeColaborador": null,
                                              "status": "ABERTO",
                                              "horaChegada": "2026-11-08T14:15:30",
                                              "horaFim": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "CPF ou ID da unidade ausentes ou inválidos.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorValidation.class),
                            examples = @ExampleObject(
                                    name = "CPF Ausente",
                                    value = """
                                            {
                                              "erro": "Bad Request",
                                              "status": 400,
                                              "campos": [
                                                {
                                                  "campo": "cpf",
                                                  "mensagem": "Obrigatório inserir o CPF!"
                                                }
                                              ],
                                              "timestamp": "2026-11-08T18:30:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Unidade de saúde ou paciente com o CPF informado não encontrado na base.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Paciente ou Unidade Não Encontrada",
                                    value = """
                                            {
                                              "mensagem": "Paciente de id (0) não encontrado!",
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
                    description = "Erro interno inesperado no servidor.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @SecurityRequirements
    @CacheEvict(value = "unidades", allEntries = true)
    @PostMapping("/checkIn")
    public ResponseEntity<AtendimentoResponse> iniciarAtendimento(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do totem/aplicativo contendo o ID da unidade de saúde e o CPF do cidadão.",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AtendimentoCriacaoRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Check-in Totem UBS Paulista",
                                            summary = "Cidadão retirando senha pelo CPF na Unidade 1",
                                            value = """
                                                    {
                                                      "idUnidade": 1,
                                                      "cpf": "12345678901"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Check-in App Mobile UPA",
                                            summary = "Cidadão iniciando atendimento na Unidade 2",
                                            value = """
                                                    {
                                                      "idUnidade": 2,
                                                      "cpf": "98765432100"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            @RequestBody @Valid AtendimentoCriacaoRequest request) {
        AtendimentoResponse atendimento = atendimentoService.iniciarAtendimento(request);
        return ResponseEntity.created(URI.create("api/atendimentos/checkIn/" + atendimento.id())).body(atendimento);
    }

    @Operation(
            summary = "Encerrar atendimento (Colaborador autenticado)",
            description = """
                    Finaliza um atendimento em aberto quando o colaborador libera o paciente após a consulta ou triagem.

                    ### Regras de Negócio e Segurança
                    - **Acesso Restrito:** Exige token JWT com perfil `ROLE_COLABORADOR`.
                    - **Transição de Estado:** Apenas atendimentos com status `ABERTO` podem ser encerrados. Caso o atendimento já esteja `ENCERRADO`, retorna `422 Unprocessable Content` (`InvalidStatusTransitionException`).
                    - **Atualização de Fila e Cache:** Registra `horaFim = LocalDateTime.now()`, recalcula o `tempoEspera` da unidade e invalida o cache `unidades` (`@CacheEvict`).
                    """,
            tags = {"Atendimentos"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Atendimento encerrado com sucesso e fila da unidade atualizada.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AtendimentoResponse.class),
                            examples = @ExampleObject(
                                    name = "Atendimento Encerrado",
                                    value = """
                                            {
                                              "id": 42,
                                              "nomePaciente": "João da Silva Santos",
                                              "nomeColaborador": "Dra. Ana Paula Souza",
                                              "status": "ENCERRADO",
                                              "horaChegada": "2026-11-08T14:15:30",
                                              "horaFim": "2026-11-08T14:52:10"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado. Token Bearer JWT ausente ou inválido.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado. O usuário autenticado não possui a role `COLABORADOR`.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Atendimento não encontrado para o `id` informado.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Atendimento Não Encontrado",
                                    value = """
                                            {
                                              "mensagem": "Atendimento de id (999) não encontrado!",
                                              "erro": "Not Found",
                                              "status": 404,
                                              "timestamp": "2026-11-08T18:30:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Transição de status inválida: o atendimento já se encontra `ENCERRADO`.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Atendimento Já Encerrado",
                                    value = """
                                            {
                                              "mensagem": "Atendimento já consta encerrado!",
                                              "erro": "Unprocessable Content",
                                              "status": 422,
                                              "timestamp": "2026-11-08T18:30:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado no servidor.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @CacheEvict(value = "unidades", allEntries = true)
    @PutMapping("/{id}/encerramento")
    public ResponseEntity<AtendimentoResponse> encerrarAtendimento(
            @Parameter(
                    description = "Identificador único do atendimento a ser encerrado.",
                    example = "42",
                    required = true,
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objeto JSON de confirmação para encerramento do atendimento.",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AtendimentoAtualizacaoRequest.class),
                            examples = @ExampleObject(
                                    name = "Confirmar Encerramento",
                                    summary = "Payload padrão de encerramento pelo colaborador",
                                    value = "{}"
                            )
                    )
            )
            @RequestBody @Valid AtendimentoAtualizacaoRequest request) {
        return ResponseEntity.ok(atendimentoService.encerrarAtendimento(id, request));
    }
}
