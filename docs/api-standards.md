# Padrões e Contratos da API REST — Huari (`hu-service`)

Este documento estabelece as convenções oficiais de design, nomenclatura, validação, tratamento de erros, paginação e documentação OpenAPI/Swagger da API REST do **Huari** (`hu-service`), construída em **Java 25 + Spring Boot 4.1.0**.

---

## 1. Convenções de Nomenclatura de URIs e Parâmetros (`kebab-case`)

### 1.1. Estrutura de Rotas (URIs)
- **Padrão obrigatório:** Todas as rotas e segmentos compostos de URI devem utilizar **`kebab-case`** (letras minúsculas separadas por hífen).
  - ✅ `/transparencia/projetos-lei`
  - ✅ `/transparencia/tribunal-contas/contas-irregulares`
  - ✅ `/progresso-aulas`
  - ❌ `/projetosLei`, `/projetos_lei`, `/ProjetosLei`
- **Substantivos no plural para coleções:** Recursos colecionáveis devem ser nomeados por substantivos no plural, evitando verbos na URL.
  - ✅ `/cursos`, `/categorias`, `/autores`, `/modulos`, `/aulas`, `/matriculas`, `/usuarios`, `/usuarios/admins`
  - ✅ `/transparencia/parlamentares`, `/transparencia/projetos-lei`, `/transparencia/despesas`
  - ✅ `/transparencia/camara/deputados`, `/transparencia/camara/proposicoes`, `/transparencia/camara/sincronizacoes`
  - ✅ `/transparencia/tse/candidatos`, `/transparencia/tse/candidatos/{id}/bens`
  - ✅ `/transparencia/empresas/{cnpj}`, `/transparencia/despesas/{id}/fornecedor`
  - ✅ `/localidades/estados`, `/localidades/estados/{uf}/municipios`, `/localidades/ceps/{cep}`
  - ✅ `/ia/explicacoes-projetos-lei`
- **Sub-recursos singulares e aliases de contexto:**
  - `/usuarios/me` e `/usuarios/me/senha` representam o recurso único do usuário autenticado.
  - `/transparencia/tribunal-contas/resumo`, `/transparencia/despesas/resumo`, `/progresso-aulas/resumo` e `/transparencia/integracoes/status` representam agregados singulares de leitura.
  - `/auth/*` (`/auth/login`, `/auth/refresh`, `/auth/logout`, `/auth/forgot-password`, `/auth/reset-password`) segue convenção RPC amplamente adotada para operações de ciclo de vida de sessão JWT.
- **Versionamento (Diretriz de Evolução):** Atualmente a API opera sem prefixo de versão na raiz (`/`). Novas gerações incompatíveis de contrato deverão introduzir versionamento explícito via URI (`/api/v1/...`, `/api/v2/...`).

### 1.2. Query Parameters e Headers Customizados
- **Query params compostos:** Devem seguir rigorosamente **`kebab-case`** (`@RequestParam(name = "...")`), mantendo variáveis Java internas em `camelCase`.
  - ✅ `?curso-id=1`, `?modulo-id=2`, `?usuario-id=5`, `?parlamentar-id=10`, `?sigla-tipo=PL`
  - ❌ `?courseId=1`, `?cursoId=1`, `?parliamentarianId=10`, `?siglaTipo=PL`
- **Headers HTTP:**
  - Padrões oficiais: `Authorization: Bearer <jwt>`, `Content-Type: application/json`, `Location`.
  - Headers customizados (quando introduzidos) devem usar `X-` ou prefixo de domínio em `kebab-case` (ex.: `X-Request-Id`, `X-Correlation-Id`).

### 1.3. Corpos JSON (Request/Response DTOs) e Convenções de Código
- **Payloads JSON:** Propriedades de DTOs seguem `camelCase` (padrão Jackson/JavaScript no `hu-web`), modelados como `record`s imutáveis no pacote `com.huariservice.huariia.dto`.
- **Nunca expor entidades JPA diretamente:** `spring.jpa.open-in-view=false` está habilitado; todos os controllers recebem e devolvem DTOs.
- **Arquivos de configuração, recursos e documentação:** Devem utilizar `kebab-case` (ex.: `application-dev.properties`, `application-prod.properties`, `application-test.properties`, `docker-compose.yml`, `docs/api-standards.md`), preservando:
  - Convenções obrigatórias da linguagem Java (`PascalCase` para classes/interfaces/records/enums e `camelCase` para métodos/variáveis).
  - Convenção obrigatória do **Flyway** para scripts de migração (`V<versao>__<descricao_em_snake_case>.sql` em `src/main/resources/db/migration/`), para preservar integridade de checksums em bancos já migrados.
  - Convenção padrão de repositórios Git (`README.md`, `Dockerfile`, `pom.xml`).

---

## 2. Métodos HTTP e Códigos de Status

| Método HTTP | Semântica | Status de Sucesso | Observações |
|---|---|---|---|
| `GET` | Leitura de coleção ou recurso único | `200 OK` | Seguro e idempotente; coleções persistidas retornam `Page<T>` |
| `POST` | Criação de recurso ou disparo de processamento | `201 Created` (criação) ou `200 OK` (consulta/ação/sync) | Em `201 Created`, retornar header `Location` com a URI do novo recurso |
| `PUT` | Substituição completa de recurso existente | `200 OK` | Idempotente; exige payload completo validado |
| `PATCH` | Atualização parcial específica (ex.: status, senha) | `200 OK` ou `204 No Content` | Ex.: `PATCH /matriculas/{id}/status`, `PATCH /usuarios/me/senha` |
| `DELETE` | Remoção lógica (*soft-delete*) | `204 No Content` | Corpo vazio; registros usam `deleted_at` |

### Códigos de Erro Padronizados

| Status | Cenário | Origem no Projeto |
|---|---|---|
| `400 Bad Request` | Erro de validação Bean Validation, JSON malformado, tipo ou parâmetro obrigatório ausente | `GlobalExceptionHandler` (`MethodArgumentNotValidException`, `ConstraintViolationException`, `HttpMessageNotReadableException`, `MethodArgumentTypeMismatchException`, `MissingServletRequestParameterException`) |
| `401 Unauthorized` | Token JWT ausente, inválido, expirado ou credenciais incorretas | `SecurityConfig` (`authenticationEntryPoint`) / `InvalidCredentialsException` |
| `403 Forbidden` | Usuário autenticado sem permissão (`ROLE_ADMIN` requerida) | `SecurityConfig` (`accessDeniedHandler`) |
| `404 Not Found` | Recurso inexistente ou rota não mapeada | `ResourceNotFoundException`, `NoResourceFoundException` |
| `405 Method Not Allowed` | Verbo HTTP não suportado na rota | `HttpRequestMethodNotSupportedException` |
| `409 Conflict` | Violação de unicidade (e-mail, título de curso, código de PL, matrícula duplicada) | `ConflictException`, `DataIntegrityViolationException` |
| `422 Unprocessable Entity` | Violação de regra de negócio (ex.: token de recuperação expirado, aluno sem matrícula no curso) | `BusinessRuleException` |
| `429 Too Many Requests` | Excesso de tentativas de login falhas (*brute-force protection*) ou limite de requisições em `/ia/**` | `TooManyRequestsException` / `AiRateLimitFilter` |
| `500 Internal Server Error` | Falha inesperada não tratada (sem vazar *stack trace* ao cliente) | `GlobalExceptionHandler.handleGenericException` |
| `502 Bad Gateway` / `503 Service Unavailable` | Falha ou indisponibilidade de provedor externo de IA | `ResponseStatusException` |

---

## 3. Validação e Contrato Único de Erro

### 3.1. Validação de Entrada (Bean Validation)
- Todos os endpoints que recebem `@RequestBody` utilizam `@Valid` nos controllers em conjunto com anotações `jakarta.validation.constraints.*` (`@NotBlank`, `@NotNull`, `@Email`, `@Size`, `@Positive`, `@URL`, etc.) nos DTOs de requisição.
- Regras de domínio adicionais (unicidade, existência de chaves estrangeiras, posse do recurso) são validadas na camada `services`.

### 3.2. Contrato Único de Resposta de Erro (`ErrorResponse`)
Todas as falhas — tanto as tratadas pelo `@RestControllerAdvice` (`GlobalExceptionHandler`) quanto os bloqueios de segurança (`401`/`403` no `SecurityConfig`) — retornam o mesmo envelope JSON (`ErrorResponse`):

```json
{
  "timestamp": "2026-11-23T09:15:00.123456",
  "status": 400,
  "error": "Validation error",
  "message": "One or more fields are invalid",
  "details": [
    "title: Title is required",
    "videoUrl: Invalid video URL"
  ]
}
```

- `timestamp` *(ISO-8601)*: Data/hora do servidor no momento do erro.
- `status` *(int)*: Código HTTP numérico.
- `error` *(string)*: Categoria legível do erro.
- `message` *(string)*: Descrição clara para o consumidor da API.
- `details` *(array de strings, opcional)*: Presente apenas em erros de validação (`@JsonInclude(NON_NULL)`), listando cada campo inválido no formato `"campo: mensagem"`.

---

## 4. Paginação, Filtragem e Ordenação

### 4.1. Configuração Global (`application.properties`)
```properties
spring.data.web.pageable.default-page-size=20
spring.data.web.pageable.max-page-size=100
spring.data.web.pageable.page-serialization-mode=via-dto
```
- O modo `via-dto` garante contrato JSON estável e enxuto para o front-end (`hu-web`), encapsulando metadados em `page` em vez de serializar campos internos da classe `PageImpl`.

### 4.2. Parâmetros de Paginação e Ordenação (`Pageable` / `Sort`)
Todos os endpoints de listagem de recursos persistidos em banco aceitam `Pageable` anotado com `@ParameterObject` (Springdoc):
- `page`: Índice da página (base `0`, padrão `0`).
- `size`: Quantidade de itens por página (padrão `20`, máximo `100`).
- `sort`: Campo e direção no formato `campo,asc` ou `campo,desc` (pode ser repetido).
  - Exemplo: `GET /cursos?page=0&size=10&sort=title,asc`
  - Exemplo: `GET /transparencia/despesas?parlamentar-id=1&page=0&size=20&sort=expenseDate,desc`

### 4.3. Filtragem
Filtros específicos de domínio são passados como query params em `kebab-case`:
- `GET /modulos?curso-id=1`
- `GET /aulas?modulo-id=3`
- `GET /matriculas?usuario-id=2`
- `GET /progresso-aulas?curso-id=1&usuario-id=2`
- `GET /progresso-aulas/resumo?curso-id=1`
- `GET /historico-ia?usuario-id=2`
- `GET /transparencia/parlamentares?chamber=CHAMBER_OF_DEPUTIES`
- `GET /transparencia/projetos-lei?q=inteligencia`
- `GET /transparencia/despesas?parlamentar-id=1`
- `GET /transparencia/despesas/{id}`
- `GET /transparencia/despesas/resumo`
- `GET /transparencia/camara/proposicoes?sigla-tipo=PL&ano=2025&ementa=educacao`
- `GET /transparencia/tse/candidatos?nome=tabata&uf=SP&ano=2022&cargo=deputado`
- `GET /transparencia/tse/candidatos/{id}/bens?ano=2022&uf=SP`
- `GET /transparencia/empresas/{cnpj}`
- `GET /transparencia/despesas/{id}/fornecedor?cnpj=02012862000160`

> **Nota sobre integrações em tempo real:** Endpoints proxy de APIs governamentais externas (`/transparencia/camara/*`, `/transparencia/senado/*`, `/transparencia/tribunal-contas/*`, `/transparencia/tse/*`, `/transparencia/empresas/*`, `/localidades/*`) retornam `List<DTO>` ou DTOs enriquecidos com filtros próprios refletindo as capacidades das fontes externas (Câmara, Senado, TCU, TSE, BrasilAPI / Receita Federal, IBGE).

---

## 5. Documentação Interativa — OpenAPI 3 / Swagger (`springdoc-openapi`)

### 5.1. Escolha e Justificativa Técnica
O projeto adota **`org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.1`**, configurado em `SwaggerConfig.java`:
1. **Compatibilidade nativa com Spring Boot 4 / Java 25:** Integração direta com Spring Web MVC, Jakarta Validation (`@Valid`, `@NotBlank`, `@Size`), Java `record`s e `Pageable` (`@ParameterObject`).
2. **Geração *code-first* sem divergência:** A especificação OpenAPI 3 (`/v3/api-docs`) é derivada automaticamente das anotações `@Tag`, `@Operation`, `@ApiResponses` e dos DTOs reais, reduzindo desatualização entre código e documentação.
3. **Autenticação JWT integrada na UI:** O esquema `bearerAuth` (`HTTP Bearer / JWT`) permite testar endpoints protegidos diretamente no Swagger UI.
4. **Segurança em produção:** Em `application-prod.properties`, os endpoints `/v3/api-docs` e `/swagger-ui/**` vêm desabilitados por padrão (`SWAGGER_ENABLED=false`), podendo ser habilitados explicitamente apenas em ambientes controlados.

### 5.2. URLs de Acesso (Ambiente Local / Dev)
- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
- **Especificação OpenAPI (JSON):** `http://localhost:8080/v3/api-docs`

---

## 6. Exemplos Práticos Reais da API

### 6.1. `200 OK` — Listagem Paginada de Cursos (`GET /cursos?page=0&size=2&sort=title,asc`)

**Requisição:**
```http
GET /cursos?page=0&size=2&sort=title,asc HTTP/1.1
Host: localhost:8080
Accept: application/json
```

**Resposta (`200 OK`):**
```json
{
  "content": [
    {
      "id": 1,
      "title": "Introdução ao Processo Legislativo",
      "description": "Aprenda como leis são criadas e votadas no Congresso Nacional.",
      "videoUrl": "https://www.youtube.com/watch?v=exemplo1",
      "category": {
        "id": 1,
        "name": "Ciência Política",
        "description": "Estudos sobre instituições e democracia."
      },
      "author": {
        "id": 1,
        "channelName": "Canal Cidadania Aberta",
        "channelUrl": "https://youtube.com/@cidadaniaberta"
      },
      "modules": []
    }
  ],
  "page": {
    "size": 2,
    "number": 0,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### 6.2. `201 Created` — Registro de Novo Usuário Aluno (`POST /usuarios`)

**Requisição:**
```http
POST /usuarios HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "name": "Maria Santos",
  "email": "maria@example.com",
  "password": "password123"
}
```

**Resposta (`201 Created`):**
```http
HTTP/1.1 201 Created
Location: http://localhost:8080/usuarios/1
Content-Type: application/json

{
  "id": 1,
  "name": "Maria Santos",
  "email": "maria@example.com",
  "role": "STUDENT"
}
```

### 6.3. `400 Bad Request` — Erro de Validação de Campos (`POST /cursos`)

**Requisição:**
```http
POST /cursos HTTP/1.1
Host: localhost:8080
Authorization: Bearer <admin-jwt>
Content-Type: application/json

{
  "title": "",
  "description": "Curso sem título e URL inválida",
  "videoUrl": "url-invalida",
  "categoryId": null,
  "authorId": 1
}
```

**Resposta (`400 Bad Request`):**
```json
{
  "timestamp": "2026-11-23T09:15:00.123456",
  "status": 400,
  "error": "Validation error",
  "message": "One or more fields are invalid",
  "details": [
    "title: Title is required",
    "videoUrl: Invalid video URL",
    "categoryId: Category is required"
  ]
}
```

### 6.4. `404 Not Found` — Recurso Não Encontrado (`GET /transparencia/parlamentares/99999`)

**Requisição:**
```http
GET /transparencia/parlamentares/99999 HTTP/1.1
Host: localhost:8080
Accept: application/json
```

**Resposta (`404 Not Found`):**
```json
{
  "timestamp": "2026-11-23T09:16:12.456789",
  "status": 404,
  "error": "Resource not found",
  "message": "Parliamentarian not found with id: 99999"
}
```

### 6.5. `500 Internal Server Error` — Erro Inesperado no Servidor

**Resposta (`500 Internal Server Error`):**
```json
{
  "timestamp": "2026-11-23T09:17:45.789012",
  "status": 500,
  "error": "Internal server error",
  "message": "An unexpected error occurred. Please try again later."
}
```
