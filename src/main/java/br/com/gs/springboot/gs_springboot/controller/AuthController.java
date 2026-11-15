package br.com.gs.springboot.gs_springboot.controller;

import br.com.gs.springboot.gs_springboot.dto.errors.ErrorResponse;
import br.com.gs.springboot.gs_springboot.dto.errors.ErrorValidation;
import br.com.gs.springboot.gs_springboot.dto.request.LoginRequest;
import br.com.gs.springboot.gs_springboot.dto.request.RegistroRequest;
import br.com.gs.springboot.gs_springboot.dto.response.AuthResponse;
import br.com.gs.springboot.gs_springboot.model.Role;
import br.com.gs.springboot.gs_springboot.model.Cidadao;
import br.com.gs.springboot.gs_springboot.security.JwtService;
import br.com.gs.springboot.gs_springboot.security.UsuarioAutenticado;
import br.com.gs.springboot.gs_springboot.service.CidadaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(
        name = "Autenticação",
        description = "Autenticação stateless de usuários comuns e colaboradores com emissão de tokens **JWT (Bearer)**."
)
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CidadaoService cidadaoService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, CidadaoService cidadaoService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.cidadaoService = cidadaoService;
    }

    @Operation(
            summary = "Autenticar colaborador e gerar token JWT",
            description = """
                    Realiza a autenticação stateless de um colaborador previamente cadastrado por meio de **e-mail** e **senha**.

                    ### Fluxo de Autenticação
                    1. Valida o formato dos campos de entrada (`email` e `senha`).
                    2. Verifica as credenciais junto ao `AuthenticationManager` utilizando hash **BCrypt**.
                    3. Emite um token **JWT (HS256)** contendo o `subject` (e-mail) e a claim `role = COLABORADOR`.
                    4. O token retornado deve ser enviado no cabeçalho `Authorization: Bearer <token>` nas rotas protegidas (ex.: encerramento de atendimento).
                    """,
            tags = {"Autenticação"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Autenticação realizada com sucesso. Retorna o token JWT e seu tempo de expiração.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    name = "Token Emitido",
                                    summary = "Resposta de login bem-sucedido",
                                    value = """
                                            {
                                              "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBuZXhhLmNvbSIsInJvbGUiOiJDT0xBQk9SQURPUiIsImlhdCI6MTc2MjYzMDAwMCwiZXhwIjoxNzYyNjMzNjAwfQ.signature",
                                              "tokenType": "Bearer",
                                              "expiresInSeconds": 28800
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados de requisição inválidos ou campos obrigatórios ausentes.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorValidation.class),
                            examples = @ExampleObject(
                                    name = "Campos Obrigatórios Ausentes",
                                    value = """
                                            {
                                              "erro": "Bad Request",
                                              "status": 400,
                                              "campos": [
                                                {
                                                  "campo": "email",
                                                  "mensagem": "Email é obrigatório"
                                                },
                                                {
                                                  "campo": "senha",
                                                  "mensagem": "Senha é obrigatória"
                                                }
                                              ],
                                              "timestamp": "2026-11-08T18:30:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciais inválidas (e-mail não encontrado ou senha incorreta).",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Credenciais Inválidas",
                                    value = """
                                            {
                                              "mensagem": "Credenciais inválidas ou acesso não autorizado.",
                                              "erro": "Unauthorized",
                                              "status": 401,
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
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciais de acesso do colaborador (e-mail e senha).",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Login Colaborador Padrão",
                                            summary = "Credenciais de administrador/colaborador padrão",
                                            value = """
                                                    {
                                                      "email": "admin@nexa.com",
                                                      "senha": "admin123"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Login Enfermeira Triagem",
                                            summary = "Credenciais de profissional de unidade de saúde",
                                            value = """
                                                    {
                                                      "email": "ana.souza@nexa.saude.gov.br",
                                                      "senha": "Senha@2026"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            @RequestBody @Valid LoginRequest request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha()));

        // O mesmo login atende o app (USUARIO) e o sistema web (COLABORADOR):
        // o perfil vem da conta autenticada, não de um valor fixo.
        Role role = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_COLABORADOR")) ? Role.COLABORADOR : Role.USUARIO;
        // authentication.getName() devolve o e-mail (o username); o nome de verdade vem do UsuarioAutenticado.
        UsuarioAutenticado autenticado = (UsuarioAutenticado) authentication.getPrincipal();
        String token = jwtService.geraToken(request.email(), autenticado.getNome(), role);

        return ResponseEntity.ok(new AuthResponse(token, "Bearer", jwtService.getExpiracaoSegundos()));
    }

    @Operation(
            summary = "Criar conta de usuário comum",
            description = """
                    Cria uma nova conta de usuário comum com **nome completo**, **e-mail**, **senha** e **aceite de localização**.

                    ### Regras de Negócio
                    - O **e-mail** é único no sistema (`409 Conflict` se já existir).
                    - A **senha** (8 a 72 caracteres) é guardada apenas como hash **BCrypt**.
                    - O **confirmarSenha** deve ser idêntico à **senha**.
                    - O **aceitouLocalizacao** precisa ser `true` (consenso LGPD).
                    - A data e hora do aceite fica registrada como prova de consentimento.
                    - A resposta já traz um token JWT: o app entra logado sem uma segunda chamada.
                    """,
            tags = {"Autenticação"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Conta criada. Retorna o token JWT.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    name = "Conta Criada",
                                    value = """
                                            {
                                              "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c3VhcmlvQGV4ZW1wbG8uY29tIiwicm9sZSI6IlVTVUFSSU8iLCJpYXQiOjE3NjI2MzAwMDAsImV4cCI6MTc2MjYzMzYwMH0.signature",
                                              "tokenType": "Bearer",
                                              "expiresInSeconds": 28800
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos (email inválido ou senha curta).",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorValidation.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "E-mail já cadastrado.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "E-mail Já Registrado",
                                    value = """
                                            {
                                              "mensagem": "Email já cadastrado",
                                              "erro": "Conflict",
                                              "status": 409,
                                              "timestamp": "2026-11-09T12:00:00"
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
    @PostMapping("/registro")
    public ResponseEntity<AuthResponse> registrarUsuario(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados para criar a conta (nome, email, senha, confirmação e aceite de localização).",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RegistroRequest.class),
                            examples = @ExampleObject(
                                    name = "Nova conta",
                                    value = """
                                            {
                                              "nome": "Ana Souza",
                                              "email": "ana@exemplo.com",
                                              "senha": "SenhaForte@2026",
                                              "confirmarSenha": "SenhaForte@2026",
                                              "aceitouLocalizacao": true
                                            }
                                            """
                            )
                    )
            )
            @RequestBody @Valid RegistroRequest request) {
        Cidadao cidadao = cidadaoService.registrar(request);
        String token = jwtService.geraToken(cidadao.getEmail(), cidadao.getNome(), Role.USUARIO);
        return ResponseEntity.status(201)
                .body(new AuthResponse(token, "Bearer", jwtService.getExpiracaoSegundos()));
    }
}
