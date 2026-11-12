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
import jakarta.validation.constraints.Positive;
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
        name = "Atendimentos",
        description = "Gestão do fluxo de fila: abertura de senha no totem/app (check-in), chamada automática FIFO, listagem da fila ativa e encerramento com promoção."
)
public class AtendimentoController {
    private final AtendimentoService atendimentoService;

    public AtendimentoController(AtendimentoService atendimentoService) {
        this.atendimentoService = atendimentoService;
    }

    @Operation(
            summary = "Buscar atendimento por ID (com HATEOAS)",
            description = """
                    Retorna os detalhes completos de um atendimento pelo seu identificador único (`id`), incluindo senha emitida, posição dinâmica na fila, serviço e links hipermídia **HATEOAS** (`_links`).

                    ### Navegação Hipermídia (HATEOAS)
                    - `self`: Link direto para a consulta deste atendimento (`GET /api/atendimentos/{id}`).
                    - `encerrar`: Link exibido dinamicamente enquanto o atendimento não estiver `FINALIZADO`, orientando o cliente para a ação de conclusão (`PUT /api/atendimentos/{id}/encerramento`).
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
                                    name = "Atendimento Aguardando na Fila (HATEOAS)",
                                    value = """
                                            {
                                              "id": 42,
                                              "nomePaciente": "João da Silva Santos",
                                              "nomeColaborador": null,
                                              "status": "AGUARDANDO",
                                              "horaChegada": "2026-11-09T14:15:30",
                                              "horaFim": null,
                                              "horaChamada": null,
                                              "senha": "CLI-002",
                                              "servico": "CLINICO_GERAL",
                                              "posicaoFila": 1,
                                              "tempoEsperaMinutos": 20.0,
                                              "_links": {
                                                "self": {
                                                  "href": "http://localhost:8080/api/atendimentos/42"
                                                },
                                                "encerrar": {
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
                                              "timestamp": "2026-11-09T18:30:00"
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
                                              "timestamp": "2026-11-09T18:30:00"
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
            summary = "Listar fila ativa de atendimentos de uma unidade (paginado)",
            description = """
                    Retorna uma página contendo os atendimentos ativos da unidade de saúde informada (`unidadeId`), filtrando apenas os status `CHAMADO` e `AGUARDANDO`, ordenados cronologicamente por hora de chegada (**FIFO**).

                    ### Indicadores de Fila
                    - Cada item retorna a senha emitida (`senha`), o serviço (`servico`), a posição dinâmica na fila (`posicaoFila`) e a estimativa individual de espera (`tempoEsperaMinutos`).
                    - **Cache:** Resultados mantidos no cache `atendimentos-unidade` e invalidados automaticamente a cada check-in ou encerramento.
                    """,
            tags = {"Atendimentos"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Página da fila ativa da unidade retornada com sucesso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponse.class),
                            examples = @ExampleObject(
                                    name = "Fila da Unidade",
                                    value = """
                                            {
                                              "conteudo": [
                                                {
                                                  "id": 1,
                                                  "nomePaciente": "Carlos Eduardo Silva",
                                                  "nomeColaborador": null,
                                                  "status": "CHAMADO",
                                                  "horaChegada": "2026-11-09T14:00:00",
                                                  "horaFim": null,
                                                  "horaChamada": "2026-11-09T14:00:05",
                                                  "senha": "CLI-001",
                                                  "servico": "CLINICO_GERAL",
                                                  "posicaoFila": 0,
                                                  "tempoEsperaMinutos": 0.0
                                                },
                                                {
                                                  "id": 2,
                                                  "nomePaciente": "Maria Oliveira Santos",
                                                  "nomeColaborador": null,
                                                  "status": "AGUARDANDO",
                                                  "horaChegada": "2026-11-09T14:10:00",
                                                  "horaFim": null,
                                                  "horaChamada": null,
                                                  "senha": "CLI-002",
                                                  "servico": "CLINICO_GERAL",
                                                  "posicaoFila": 1,
                                                  "tempoEsperaMinutos": 20.0
                                                }
                                              ],
                                              "pagina": 0,
                                              "tamanho": 10,
                                              "totalElementos": 2,
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
                    description = "Token Bearer JWT ausente, inválido ou expirado.",
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
            summary = "Iniciar atendimento (Check-in no Totem / App com Emissão de Senha)",
            description = """
                    Registra o check-in do cidadão em uma unidade de saúde, vinculando-o a um **serviço** e emitindo automaticamente uma **senha sequencial formatada**.

                    ### Motor de Fila FIFO e Regras de Negócio
                    1. **Higienização e Busca:** O CPF informado tem pontuações removidas e é localizado no cadastro de pacientes.
                    2. **Geração de Senha Sequencial:** Gera uma senha baseada no prefixo do serviço e na contagem de atendimentos da unidade (ex.: `CLI-001`, `VAC-002`).
                    3. **Chamada Automática Imediata:** Se não houver nenhum atendimento com status `CHAMADO` para aquela mesma unidade e serviço, o paciente é promovido imediatamente para `CHAMADO` (`posicaoFila = 0`, `tempoEsperaMinutos = 0.0`). Caso contrário, entra como `AGUARDANDO`.
                    4. **Invalidação de Cache:** Dispara `@CacheEvict` nos caches `unidades` e `atendimentos-unidade` para sincronizar os painéis.
                    """,
            tags = {"Atendimentos"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Check-in realizado com sucesso e senha emitida.",
                    headers = @Header(
                            name = "Location",
                            description = "URI do atendimento iniciado (`api/atendimentos/checkIn/{id}`).",
                            schema = @Schema(type = "string", example = "api/atendimentos/checkIn/42")
                    ),
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AtendimentoResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Paciente Chamado Imediatamente (Guichê Livre)",
                                            summary = "Fila vazia: promovido a CHAMADO na hora",
                                            value = """
                                                    {
                                                      "id": 42,
                                                      "nomePaciente": "João da Silva Santos",
                                                      "nomeColaborador": null,
                                                      "status": "CHAMADO",
                                                      "horaChegada": "2026-11-09T14:15:30",
                                                      "horaFim": null,
                                                      "horaChamada": "2026-11-09T14:15:30",
                                                      "senha": "CLI-001",
                                                      "servico": "CLINICO_GERAL",
                                                      "posicaoFila": 0,
                                                      "tempoEsperaMinutos": 0.0
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Paciente Aguardando na Fila",
                                            summary = "Já existe alguém em atendimento: entra como AGUARDANDO",
                                            value = """
                                                    {
                                                      "id": 43,
                                                      "nomePaciente": "Maria Oliveira Santos",
                                                      "nomeColaborador": null,
                                                      "status": "AGUARDANDO",
                                                      "horaChegada": "2026-11-09T14:20:10",
                                                      "horaFim": null,
                                                      "horaChamada": null,
                                                      "senha": "CLI-002",
                                                      "servico": "CLINICO_GERAL",
                                                      "posicaoFila": 1,
                                                      "tempoEsperaMinutos": 20.0
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Campos obrigatórios ausentes (`idUnidade`, `cpf` ou `servico`).",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorValidation.class),
                            examples = @ExampleObject(
                                    name = "Serviço Não Informado",
                                    value = """
                                            {
                                              "erro": "Bad Request",
                                              "status": 400,
                                              "campos": [
                                                {
                                                  "campo": "servico",
                                                  "mensagem": "Obrigatório informar o serviço!"
                                                }
                                              ],
                                              "timestamp": "2026-11-09T18:30:00"
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
                                              "timestamp": "2026-11-09T18:30:00"
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
    @CacheEvict(value = {"unidades", "atendimentos-unidade"}, allEntries = true)
    @PostMapping("/checkIn")
    public ResponseEntity<AtendimentoResponse> iniciarAtendimento(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do check-in com ID da unidade, CPF do paciente e serviço solicitado.",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AtendimentoCriacaoRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Check-in Clínico Geral",
                                            summary = "Retirada de senha para Consulta com Clínico Geral",
                                            value = """
                                                    {
                                                      "idUnidade": 1,
                                                      "cpf": "12345678901",
                                                      "servico": "CLINICO_GERAL"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Check-in Vacinação",
                                            summary = "Retirada de senha para Vacinação na UBS",
                                            value = """
                                                    {
                                                      "idUnidade": 1,
                                                      "cpf": "98765432100",
                                                      "servico": "VACINACAO"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Check-in Triagem / Enfermagem",
                                            summary = "Retirada de senha para Triagem",
                                            value = """
                                                    {
                                                      "idUnidade": 2,
                                                      "cpf": "11122233344",
                                                      "servico": "TRIAGEM"
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
            summary = "Encerrar atendimento com Promoção Automática do Próximo (Colaborador autenticado)",
            description = """
                    Finaliza o atendimento em andamento pelo colaborador autenticado, registrando `horaFim`, associando o colaborador logado e promovendo o próximo paciente da fila.

                    ### Regras de Negócio e Segurança
                    - **Acesso Restrito:** Exige Bearer JWT com perfil `ROLE_COLABORADOR`.
                    - **Associação Automática do Profissional:** O sistema extrai o e-mail do colaborador logado a partir do token JWT e o vincula ao atendimento.
                    - **Transição de Estado:** Altera o status para `FINALIZADO`. Caso o atendimento já esteja encerrado, retorna `422 Unprocessable Content` (`InvalidStatusTransitionException`).
                    - **Promoção Automática do Próximo da Fila:** O primeiro paciente `AGUARDANDO` na mesma unidade e serviço é promovido automaticamente para `CHAMADO` e tem sua `horaChamada` registrada.
                    - **Recálculo e Invalidação de Cache:** Recalcula a fila da unidade e limpa os caches `unidades` e `atendimentos-unidade` via `@CacheEvict`.
                    """,
            tags = {"Atendimentos"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Atendimento finalizado com sucesso, colaborador associado e próximo da fila chamado.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AtendimentoResponse.class),
                            examples = @ExampleObject(
                                    name = "Atendimento Finalizado",
                                    value = """
                                            {
                                              "id": 42,
                                              "nomePaciente": "João da Silva Santos",
                                              "nomeColaborador": "Dra. Ana Paula Souza",
                                              "status": "FINALIZADO",
                                              "horaChegada": "2026-11-09T14:15:30",
                                              "horaFim": "2026-11-09T14:52:10",
                                              "horaChamada": "2026-11-09T14:15:30",
                                              "senha": "CLI-001",
                                              "servico": "CLINICO_GERAL",
                                              "posicaoFila": 0,
                                              "tempoEsperaMinutos": 0.0
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado. Token Bearer JWT ausente, inválido ou expirado.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado. Apenas usuários com perfil `ROLE_COLABORADOR` podem encerrar atendimentos.",
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
                                              "timestamp": "2026-11-09T18:30:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Transição de status inválida: o atendimento já se encontra `FINALIZADO`.",
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
                                              "timestamp": "2026-11-09T18:30:00"
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
    @CacheEvict(value = {"unidades", "atendimentos-unidade"}, allEntries = true)
    @PutMapping("/{id}/encerramento")
    public ResponseEntity<AtendimentoResponse> encerrarAtendimento(
            @Parameter(
                    description = "Identificador único do atendimento a ser encerrado.",
                    example = "42",
                    required = true,
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable @Positive Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Payload de confirmação do encerramento pelo colaborador.",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AtendimentoAtualizacaoRequest.class),
                            examples = @ExampleObject(
                                    name = "Confirmar Encerramento",
                                    summary = "Payload de confirmação",
                                    value = "{}"
                            )
                    )
            )
            @RequestBody @Valid AtendimentoAtualizacaoRequest request) {
        return ResponseEntity.ok(atendimentoService.encerrarAtendimento(id, request));
    }
}
