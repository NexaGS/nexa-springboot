package br.com.gs.springboot.gs_springboot.controller;

import br.com.gs.springboot.gs_springboot.dto.errors.ErrorResponse;
import br.com.gs.springboot.gs_springboot.dto.errors.ErrorValidation;
import br.com.gs.springboot.gs_springboot.dto.request.ColaboradorRequest;
import br.com.gs.springboot.gs_springboot.dto.response.ColaboradorResponse;
import br.com.gs.springboot.gs_springboot.service.ColaboradorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/colaboradores")
@Tag(
        name = "Colaboradores",
        description = "Cadastro e gerenciamento de profissionais de saúde vinculados às unidades de atendimento."
)
public class ColaboradorController {
    private final ColaboradorService colaboradorService;

    public ColaboradorController(ColaboradorService colaboradorService) {
        this.colaboradorService = colaboradorService;
    }

//    @GetMapping("/unidade/{idUnidade}")
//    List<ColaboradorResponse> listarColaboradoresDaUnidade(@PathVariable @Validated Long idUnidade){
//        return new ArrayList<>();
//    }

    @Operation(
            summary = "Cadastrar novo colaborador em uma unidade de saúde",
            description = """
                    Registra um novo profissional de saúde (**Colaborador**) com senha criptografada em **BCrypt** e o vincula a uma **Unidade de Saúde** existente.

                    ### Regras de Negócio e Segurança
                    - **Acesso Restrito:** Exige token JWT com perfil `ROLE_COLABORADOR`.
                    - A **senha** deve conter entre 6 e 100 caracteres e é armazenada com hash **BCrypt**.
                    - O **e-mail** informado deve ser único no sistema (`409 Conflict` se já cadastrado).
                    - A **Unidade de Saúde** (`idUnidadeSaude`) deve existir previamente na base (`404 Not Found`).
                    - Retorna status `201 Created` com o cabeçalho `Location` apontando para o recurso recém-criado.
                    """,
            tags = {"Colaboradores"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Colaborador cadastrado com sucesso.",
                    headers = @Header(
                            name = "Location",
                            description = "URI do colaborador recém-criado (`api/colaboradores/{id}`).",
                            schema = @Schema(type = "string", example = "api/colaboradores/10")
                    ),
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ColaboradorResponse.class),
                            examples = @ExampleObject(
                                    name = "Colaborador Criado",
                                    value = """
                                            {
                                              "id": 10,
                                              "nome": "Dra. Ana Paula Souza",
                                              "cargo": "Enfermeira Triagem",
                                              "email": "ana.souza@nexa.saude.gov.br",
                                              "nomeUnidadeSaude": "UBS Bela Vista - Paulista"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Falha de validação nos campos obrigatórios ou tamanho inválido da senha.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorValidation.class),
                            examples = @ExampleObject(
                                    name = "Erro de Validação",
                                    value = """
                                            {
                                              "erro": "Bad Request",
                                              "status": 400,
                                              "campos": [
                                                {
                                                  "campo": "senha",
                                                  "mensagem": "Senha do colaborador deve ser inserida!"
                                                }
                                              ],
                                              "timestamp": "2026-11-09T18:30:00"
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
                    description = "Acesso negado. Apenas usuários com perfil `ROLE_COLABORADOR` podem cadastrar outros colaboradores.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Unidade de saúde informada (`idUnidadeSaude`) não encontrada.",
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
                                              "timestamp": "2026-11-09T18:30:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflito: já existe um colaborador registrado com o e-mail informado.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "E-mail Já Registrado",
                                    value = """
                                            {
                                              "mensagem": "O e-mail (ana.souza@nexa.saude.gov.br) já está registrado!",
                                              "erro": "Conflict",
                                              "status": 409,
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
    @PreAuthorize("hasRole('COLABORADOR')")
    @PostMapping
    public ResponseEntity<ColaboradorResponse> criarColaborador(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados cadastrais do colaborador, incluindo senha para login e ID da unidade de saúde de lotação.",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ColaboradorRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Enfermeira de Triagem",
                                            summary = "Cadastro de enfermeira na Unidade 1",
                                            value = """
                                                    {
                                                      "nome": "Dra. Ana Paula Souza",
                                                      "cargo": "Enfermeira Triagem",
                                                      "email": "ana.souza@nexa.saude.gov.br",
                                                      "senha": "Senha@2026",
                                                      "idUnidadeSaude": 1
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Médico Clínico Geral",
                                            summary = "Cadastro de médico clínico em UPA/UBS",
                                            value = """
                                                    {
                                                      "nome": "Dr. Marcos Vinícius Oliveira",
                                                      "cargo": "Médico Clínico Geral",
                                                      "email": "marcos.oliveira@nexa.saude.gov.br",
                                                      "senha": "Senha@2026",
                                                      "idUnidadeSaude": 2
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            @RequestBody @Valid ColaboradorRequest request){
        ColaboradorResponse colaborador = colaboradorService.criar(request);
        return ResponseEntity.created(URI.create("api/colaboradores/" + colaborador.id())).body(colaborador);
    }
}
