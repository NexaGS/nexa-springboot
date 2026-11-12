# 🏥 Nexa (CitizenTech) — Gestão Inteligente de Filas e Atendimentos no SUS
## Resumo Executivo da Arquitetura, Regras de Negócio e Fluxos do MVP

> **Global Solution FIAP 2026 — Java Advanced & Smart Cities**  
> **Integrantes:**  
> - Paulo Cavalcante Caroba (`RM566667`)  
> - Renan da Silva Paulino (`RM566610`)  
> - Sergio Henrique da Silva Santos (`RM567254`)  

---

## 📌 1. Visão Geral da Solução (Cidade Tech & SUS)

O **Nexa** é uma solução de **Smart Healthcare** projetada para sanar uma das maiores dores da atenção básica no Sistema Único de Saúde (SUS): o **desbalanceamento da demanda entre unidades públicas de saúde**. 

Enquanto uma Unidade Básica de Saúde (UBS) enfrenta superlotação, outra unidade a poucos quilômetros pode estar com guichês ociosos. O Nexa conecta o cidadão e as equipes de saúde por meio de:
1. **Roteamento Geoespacial Inteligente:** Calcula a distância real do cidadão até as unidades mais próximas (**Fórmula de Haversine** com *Bounding Box*), priorizando proximidade física e menor tempo de espera.
2. **Motor de Filas FIFO Dinâmico:** Emissão de senhas padronizadas por especialidade médica, chamada imediata em guichês vagos e **promoção automática** do próximo paciente quando um atendimento é encerrado.
3. **Conformidade com a LGPD:** Registro com consentimento explícito e auditável para uso de geolocalização.
4. **Prontidão para Front-End Multiplataforma:** API RESTful stateless em Spring Boot com CORS habilitado, paginação padronizada (`PageResponse`) e hipermídia (**Spring HATEOAS**).

---

## 🔄 2. Desenhos de Fluxo Explicativos

### 2.1. Fluxo Geral da Jornada do Usuário (Ponta a Ponta)

```mermaid
flowchart TD
    subgraph "1. Jornada do Cidadão (App Mobile / Web)"
        A["Cidadão acessa o App<br/>(com consentimento LGPD)"] --> B["Consulta Unidades Próximas<br/>(GET /api/unidades/proximas)"]
        B --> C{"Compara Unidades"}
        C -->|Vê distância e tempo de espera| D["Escolhe Unidade Ideal<br/>(Ex: menor fila)"]
        D --> E["Salva como Favorita<br/>(PUT /api/cidadaos/usuario/adicionarUnidade/{id})"]
    end

    subgraph "2. Chegada à Unidade (Totem / Recepção)"
        E --> F["Chegada na Unidade<br/>(POST /api/atendimentos/checkIn)"]
        F --> G{"Guichê livre para o serviço?"}
        G -->|Sim| H["Status: CHAMADO<br/>(Senha: CLI-001)"]
        G -->|Não| I["Status: AGUARDANDO<br/>(Senha: CLI-002 / Posição: 1)"]
    end

    subgraph "3. Painel / Telão da Sala de Espera"
        H --> J["Exibição no Telão<br/>(GET /api/atendimentos/unidades/{id})"]
        I --> J
    end

    subgraph "4. Atendimento Médico (Guichê)"
        H --> K["Médico autenticado inicia consulta<br/>(ROLE_COLABORADOR)"]
        K --> L["Finaliza Atendimento<br/>(PUT /api/atendimentos/{id}/encerramento)"]
        L --> M["Status do Atual: FINALIZADO<br/>Hora Fim Registrada"]
        M --> N["PROMOÇÃO AUTOMÁTICA:<br/>Próximo AGUARDANDO passa para CHAMADO"]
        N --> O["Recalcula tempoEspera da Unidade<br/>e Invalida Caches"]
        O -.-> J
    end
```

---

### 2.2. Fluxo Detalhado do Motor de Filas FIFO & Promoção Automática

```mermaid
sequenceDiagram
    autonumber
    actor Paciente1 as Paciente 1 (Totem)
    actor Paciente2 as Paciente 2 (Totem)
    participant API as AtendimentoService
    participant DB as Banco de Dados
    actor Medico as Médico (Mesa)

    Paciente1->>API: POST /api/atendimentos/checkIn (Serviço: CLINICO_GERAL)
    API->>DB: Verifica atendimentos CHAMADO no serviço
    Note over API,DB: Guichê livre!
    API->>DB: Salva Atendimento 1 (Status: CHAMADO, Senha: CLI-001)
    API-->>Paciente1: 201 Created (Senha CLI-001, Status: CHAMADO, Posição: 0)

    Paciente2->>API: POST /api/atendimentos/checkIn (Serviço: CLINICO_GERAL)
    API->>DB: Verifica atendimentos CHAMADO no serviço
    Note over API,DB: Guichê ocupado pelo Paciente 1!
    API->>DB: Salva Atendimento 2 (Status: AGUARDANDO, Senha: CLI-002)
    API-->>Paciente2: 201 Created (Senha CLI-002, Status: AGUARDANDO, Posição: 1, Espera: 20 min)

    Note over Medico,API: Médico encerra o atendimento do Paciente 1
    Medico->>API: PUT /api/atendimentos/1/encerramento (Bearer JWT)
    API->>DB: Atualiza Atendimento 1 para FINALIZADO
    API->>DB: Busca primeiro AGUARDANDO (FIFO: ordem de chegada)
    Note over API,DB: Encontra Paciente 2!
    API->>DB: Promove Atendimento 2 para CHAMADO (horaChamada = agora)
    API->>DB: Recalcula tempoEspera da Unidade
    API-->>Medico: 200 OK (Atendimento 1 Encerrado com sucesso)

    Note over Paciente2,API: Paciente 2 consulta seu status no telão ou app
    Paciente2->>API: GET /api/atendimentos/2
    API-->>Paciente2: 200 OK (Status: CHAMADO, Posição: 0, Espera: 0 min)
```

---

### 2.3. Fluxo do Algoritmo de Busca Geoespacial (Haversine + Desempate por Fila)

```mermaid
flowchart TD
    A["Entrada do Usuário:<br/>(Latitude/Longitude OU Endereço/CEP)"] --> B{"Enviou coordenadas GPS?"}
    B -->|Não| C["Consulta ViaCEP / Google Geocoding<br/>e converte endereço para Lat/Lon"]
    B -->|Sim| D["Coordenadas Base:<br/>(latitudeUsuario, longitudeUsuario)"]
    C --> D

    D --> E["Cria Bounding Box Espacial:<br/>latMin, latMax, lonMin, lonMax (delta = ±0.20°)"]
    E --> F["Executa JPQL no UnidadeSaudeRepository<br/>filtrando apenas unidades dentro da caixa"]
    
    F --> G["Aplica Fórmula de Haversine (R = 6371 km):<br/>6371 * acos(cos(latU) * cos(latL) * cos(lonL - lonU) + sin(latU) * sin(latL))"]
    
    G --> H["Ordenação Composta:<br/>1º Mais próximo (distância ASC)<br/>2º Menor fila (u.tempoEspera ASC)"]
    
    H --> I["Retorna lista paginada com endereço completo e tempoEspera"]
```

---

### 2.4. Fluxo de Segurança, Autenticação e CORS

```mermaid
flowchart LR
    subgraph "Clientes"
        W["Front-end Web / Mobile<br/>(localhost:3000, 5173, etc.)"]
    end

    subgraph "Spring Security 6 Filter Chain"
        CORS["CorsConfigurationSource<br/>(Libera OriginPatterns, Methods e Headers)"]
        JWT["JwtAuthenticationFilter<br/>(Valida Bearer Token no cabeçalho)"]
        AUTH["AuthorizeHttpRequests"]
    end

    subgraph "Recursos da Aplicação"
        PUB["Rotas Públicas:<br/>- POST /api/auth/login<br/>- POST /api/auth/registro<br/>- POST /api/atendimentos/checkIn<br/>- GET /api/unidades/**"]
        USR["Rotas ROLE_USUARIO:<br/>- GET /api/cidadaos/me<br/>- PUT /api/cidadaos/usuario/adicionarUnidade<br/>- DELETE /api/cidadaos/usuario/removerUnidade"]
        COL["Rotas ROLE_COLABORADOR:<br/>- POST /api/colaboradores<br/>- PUT /api/atendimentos/{id}/encerramento<br/>- GET /api/atendimentos/unidades/{id}"]
    end

    W -->|1. Requisição HTTP| CORS
    CORS -->|2. Preflight Aprovado| JWT
    JWT -->|3. Popula SecurityContext| AUTH
    AUTH --> PUB
    AUTH --> USR
    AUTH --> COL
```

---

## 📋 3. Matriz de Endpoints do MVP

| Módulo | Método | Endpoint | Acesso | Descrição Funcional |
| :--- | :---: | :--- | :---: | :--- |
| **Autenticação** | `POST` | `/api/auth/registro` | 🔓 Público | Cria conta com consentimento LGPD e emite JWT. |
| **Autenticação** | `POST` | `/api/auth/login` | 🔓 Público | Autentica `USUARIO` ou `COLABORADOR` e emite JWT. |
| **Cidadão** | `GET` | `/api/cidadaos/me` | 🔒 Usuário | Retorna os dados do próprio cidadão logado com HATEOAS. |
| **Cidadão** | `PUT` | `/api/cidadaos/usuario/adicionarUnidade/{unidadeId}` | 🔒 Usuário | Adiciona unidade de saúde à lista de favoritas do usuário. |
| **Cidadão** | `DELETE` | `/api/cidadaos/usuario/removerUnidade/{unidadeId}` | 🔒 Usuário | Remove unidade de saúde das favoritas do usuário. |
| **Unidades** | `GET` | `/api/unidades` | 🔓 Público | Catálogo paginado com cache e filtro por Query by Example. |
| **Unidades** | `GET` | `/api/unidades/{id}` | 🔓 Público | Detalhes de uma unidade específica com HATEOAS. |
| **Unidades** | `GET` | `/api/unidades/proximas` | 🔓 Público | Busca geoespacial ordenada por proximidade e menor fila. |
| **Atendimentos** | `POST` | `/api/atendimentos/checkIn` | 🔓 Público | Check-in por CPF e especialidade; emite senha sequencial. |
| **Atendimentos** | `GET` | `/api/atendimentos/{id}` | 🔓 Público | Consulta status, posição na fila e tempo estimado. |
| **Atendimentos** | `GET` | `/api/atendimentos/unidades/{unidadeId}` | 🔒 Autenticado | Telão da unidade com fila ativa ordenada por chegada (FIFO). |
| **Atendimentos** | `PUT` | `/api/atendimentos/{id}/encerramento` | 🔒 Colaborador | Finaliza consulta e promove o próximo da fila para CHAMADO. |

---

## 🎯 4. Roteiro Prático de Demonstração (Para a Apresentação na FIAP)

Para uma apresentação ágil e impactante (5 a 8 minutos):

1. **Apresentar a Inteligência de Proximidade (Swagger ou Front):**
   - Executar `GET /api/unidades/proximas?latitude=-23.563&longitude=-46.654`.
   - Mostrar que as unidades retornam ordenadas pela distância e exibem o campo `tempoEspera`.
2. **Demonstrar a Criação de Conta do Cidadão:**
   - Executar `POST /api/auth/registro` demonstrando o aceite explícito de geolocalização (`aceitouLocalizacao = true`).
   - Consultar `GET /api/cidadaos/me` enviando o token gerado.
3. **Simular a Fila em Tempo Real (O Ponto Alto do Pitch):**
   - **Check-in 1:** Fazer check-in do Carlos (`POST /api/atendimentos/checkIn`) na UBS 1. Retorna senha `CLI-001` com status `CHAMADO` (guichê vago).
   - **Check-in 2:** Fazer check-in da Maria na mesma UBS. Retorna senha `CLI-002` com status `AGUARDANDO`, `posicaoFila: 1` e `tempoEsperaMinutos: 20.0`.
   - **Login do Médico:** Fazer login com `admin@nexa.com` e copiar o token `ROLE_COLABORADOR`.
   - **Encerramento:** Encerrar o atendimento 1 (`PUT /api/atendimentos/1/encerramento`).
   - **Promoção Automática:** Consultar o atendimento 2 (`GET /api/atendimentos/2`). Mostrar que a Maria foi **automaticamente promovida para `CHAMADO`** (`posicaoFila: 0`).
4. **Encerramento Técnico:**
   - Mostrar a documentação viva em `/swagger-ui/index.html` e `/docs.html` (Redocly) e o `/actuator/health`.
