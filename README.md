# 🏥 Nexa (CitizenTech) — Gestão Inteligente de Filas e Atendimentos no SUS

API RESTful desenvolvida em **Java 21** e **Spring Boot** para a **Global Solution FIAP**, projetada para otimizar o direcionamento de cidadãos e a gestão de filas de atendimento nas Unidades de Saúde Públicas (SUS/CNES).

A plataforma combina **geolocalização de alta precisão** (*ViaCEP* + *Google Geocoding API* + cálculo espacial via **Fórmula de Haversine** com *Bounding Box* sobre banco relacional em **3FN**), **estimativa dinâmica de tempo de espera em fila**, **navegação hipermídia (HATEOAS)**, **cacheamento inteligente (`@Cacheable` / `@CacheEvict`)**, **observabilidade (Actuator)** e **autenticação stateless via Bearer JWT**.

---

## 👨‍💻 Integrantes do Grupo

| Nome completo | RM |
|---|---|
| Paulo Cavalcante Caroba | RM566667 |
| Renan da Silva Paulino | RM566610 |
| Sergio Henrique da Silva Santos | RM567254 |

---

## 🛠️ Stack Tecnológica & Pré-requisitos

### Tecnologias Principais
- **Linguagem:** Java 21 (LTS)
- **Framework:** Spring Boot `4.1.2` (`spring-boot-starter-webmvc`)
- **Persistência & ORM:** Spring Data JPA / Hibernate (Modelagem em **3ª Forma Normal - 3FN** compatível com base IBGE e CNES/SUS)
- **Bancos de Dados:** H2 Database (em memória para execução imediata/dev) e Oracle Database (`ojdbc11`)
- **Segurança:** Spring Security 6 + Autenticação Stateless **JWT (`jjwt 0.13.0`)** + Hash de senhas **BCrypt**
- **Hipermídia:** Spring HATEOAS (`EntityModel`, `RepresentationModelAssembler`)
- **Performance:** Spring Cache (`@EnableCaching`, `@Cacheable`, `@CacheEvict`)
- **Validação & Erros:** Jakarta Bean Validation (`@Valid`, `@CepValido` customizado) + `@RestControllerAdvice` (`GlobalExceptionHandler`)
- **Integrações HTTP:** Spring `RestClient` nativo (ViaCEP API & Google Maps Geocoding API)
- **Observabilidade:** Spring Boot Actuator (`/actuator/health`, `/actuator/info`, `/actuator/metrics`)
- **Documentação da API:** Springdoc OpenAPI 3 (`springdoc-openapi-starter-webmvc-ui:3.2.0`) + **Swagger UI** + **Redoc / Redocly** (`x-tagGroups`)

### Pré-requisitos para Execução
- **JDK 21** instalado e configurado no `PATH` (`JAVA_HOME`)
- **Maven 3.9+** (opcional — o projeto inclui o wrapper `./mvnw` / `mvnw.cmd`)
- **Node.js 18+** (opcional — apenas caso deseje rodar o CLI externo do `@redocly/cli`)

---

## 🚀 Como Executar Localmente

### 1. Clonar o repositório
```bash
git clone <URL_DO_REPOSITORIO>
cd gs-springboot
```

### 2. Configurar Variáveis de Ambiente (Opcional para perfil H2 padrão)
Por padrão, a aplicação sobe imediatamente utilizando o banco em memória **H2** sem exigir configurações externas. Caso deseje conectar ao **Oracle** ou utilizar a chave real da **Google Geocoding API**, crie um arquivo `.env` na raiz (baseado no `.env.exemplo`):

```properties
# Segurança JWT
JWT_SECRET=cHJvamVjdG1ndC1kZXYtc2VjcmV0LWtleS0zMmJ5dGU
ADMIN_EMAIL=admin@nexa.com
ADMIN_PASSWORD=admin123

# Integração Google Maps Geocoding
GOOGLE_API_KEY=sua_chave_google_maps_aqui

# Banco de Dados Oracle (quando perfil 'oracle' estiver ativo)
DB_URL=jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL
DB_USUARIO=rmXXXXXX
DB_SENHA=sua_senha
```

### 3. Compilar e Executar a Aplicação

**No Windows (PowerShell / CMD):**
```powershell
.\mvnw.cmd clean compile
.\mvnw.cmd spring-boot:run
```

**No Linux / macOS:**
```bash
./mvnw clean compile
./mvnw spring-boot:run
```

**Empacotar e executar o JAR:**
```bash
./mvnw clean package -DskipTests
java -jar target/gs-springboot-0.0.1-SNAPSHOT.jar
```

A API estará disponível em: **`http://localhost:8080`**

---

## 📚 Acesso à Documentação (OpenAPI 3, Swagger UI & Redocly)

Com a aplicação em execução na porta `8080`, acesse qualquer um dos canais abaixo:

| Canal de Documentação | URL / Comando | Descrição |
| :--- | :--- | :--- |
| **Swagger UI (Interativo)** | [`http://localhost:8080/swagger-ui/index.html`](http://localhost:8080/swagger-ui/index.html) | Interface interativa para testar os endpoints e autenticar com **Bearer JWT** no botão **Authorize**. |
| **Redoc Integrado (Web)** | [`http://localhost:8080/docs.html`](http://localhost:8080/docs.html) | Portal Redoc embutido na aplicação que consome `/v3/api-docs` em tempo real com grupos hierárquicos (`x-tagGroups`). |
| **OpenAPI 3 Spec (JSON)** | [`http://localhost:8080/v3/api-docs`](http://localhost:8080/v3/api-docs) | Especificação bruta no padrão OpenAPI 3.0/3.1 gerada pelo `springdoc-openapi`. |
| **OpenAPI 3 Spec (YAML)** | [`http://localhost:8080/v3/api-docs.yaml`](http://localhost:8080/v3/api-docs.yaml) | Especificação OpenAPI em formato YAML para exportação e lint. |

### Renderizando via Redocly CLI
Você também pode pré-visualizar, validar ou gerar o bundle estático da documentação utilizando o **Redocly CLI** diretamente a partir do endpoint JSON da aplicação em execução:

```bash
# Pré-visualizar a documentação no servidor local do Redocly (porta 8080 -> preview na 8081)
npx @redocly/cli preview-docs http://localhost:8080/v3/api-docs --port 8081

# Ou gerar um arquivo HTML estático standalone da documentação
npx @redocly/cli build-docs http://localhost:8080/v3/api-docs -o target/redoc-static.html
```

---

## 🗺️ Tabela de Endpoints da API

### 1. Segurança & Acesso (`Autenticação` / `Colaboradores`)

| Método | Rota | Autenticação | Resumo da Operação | Status HTTP |
| :---: | :--- | :---: | :--- | :--- |
| `POST` | `/api/auth/login` | 🔓 Público | Autentica um colaborador por e-mail e senha e emite um token **Bearer JWT**. | `200`, `400`, `401`, `500` |
| `POST` | `/api/colaboradores` | 🔓 Público | Cadastra um novo colaborador (profissional de saúde) vinculado a uma unidade. | `201`, `400`, `404`, `409`, `500` |

### 2. Jornada do Cidadão & Triagem (`Unidades de Saúde` / `Atendimentos`)

| Método | Rota | Autenticação | Resumo da Operação | Status HTTP |
| :---: | :--- | :---: | :--- | :--- |
| `GET` | `/api/unidades` | 🔓 Público | Lista todas as unidades de saúde com paginação e cache (`@Cacheable("unidades")`). | `200`, `400`, `500` |
| `GET` | `/api/unidades/{id}` | 🔓 Público | Busca uma unidade de saúde específica por ID com links hipermídia **HATEOAS**. | `200`, `400`, `404`, `500` |
| `GET` | `/api/unidades/proximas` | 🔓 Público | Lista unidades de saúde ordenadas por proximidade geográfica (**Haversine + Bounding Box**) e fila. | `200`, `400`, `404`, `500` |
| `POST` | `/api/atendimentos/checkIn` | 🔓 Público | Inicia atendimento do paciente (totem/app), recalcula `tempoEspera` da unidade e limpa o cache. | `201`, `400`, `404`, `500` |
| `GET` | `/api/atendimentos/{id}` | 🔓 Público | Busca detalhes de um atendimento por ID com links **HATEOAS** (`self`, `encerrar-atendimento`). | `200`, `400`, `404`, `500` |
| `GET` | `/api/atendimentos/unidades/{unidadeId}` | 🔒 `Bearer JWT` | Lista paginada dos atendimentos em aberto (`ABERTO`) de uma unidade específica. | `200`, `400`, `401`, `500` |
| `PUT` | `/api/atendimentos/{id}/encerramento` | 🔒 `ROLE_COLABORADOR` | Encerra um atendimento em aberto, registra `horaFim`, recalcula a fila da unidade e invalida o cache. | `200`, `401`, `403`, `404`, `422`, `500` |

### 3. Observabilidade & Monitoramento (`Actuator`)

| Método | Rota | Autenticação | Resumo da Operação |
| :---: | :--- | :---: | :--- |
| `GET` | `/actuator/health` | 🔓 Público | Verifica o status de saúde da aplicação, banco de dados (`db`) e espaço em disco. |
| `GET` | `/actuator/info` | 🔓 Público | Exibe metadados gerais da aplicação. |
| `GET` | `/actuator/metrics` | 🔓 Público | Lista métricas de performance da JVM, pool HikariCP e requisições HTTP (`http.server.requests`). |
