# Huari Backend

**API REST para transparência pública, análise de dados políticos e educação cidadã.**

O Huari é um projeto acadêmico que busca facilitar o acesso à informação política por meio da organização de dados públicos, indicadores e recursos de educação cidadã.

Este repositório contém o backend da plataforma, responsável pelas regras de negócio, persistência de dados e disponibilização de recursos por meio de uma API.

## Objetivos do projeto

- **Transparência pública:** facilitar o acesso e a compreensão de informações políticas.
- **Análise de dados:** criar uma base para cruzamento de informações e geração de indicadores.
- **Educação cidadã:** apoiar o aprendizado sobre política, instituições públicas e participação democrática.
- **Acessibilidade da informação:** tornar dados complexos mais compreensíveis para diferentes públicos.

## Tecnologias utilizadas

| Tecnologia | Finalidade |
|---|---|
| Java 25 | Linguagem de programação |
| Spring Boot 4.1.0 | Estrutura da aplicação backend |
| Spring Web MVC | Desenvolvimento de endpoints HTTP |
| Spring Data JPA | Persistência e acesso a dados |
| MySQL | Banco de dados relacional |
| Spring Security | Recursos de segurança |
| Bean Validation | Validação de dados |
| JWT | Suporte à autenticação baseada em tokens |
| Springdoc OpenAPI | Documentação da API |
| Maven | Gerenciamento de dependências e build |
| Lombok | Redução de código repetitivo |

## Arquitetura

O projeto utiliza uma organização em camadas para separar responsabilidades e facilitar a manutenção.

- **Entities:** representam as entidades do domínio e seu mapeamento para o banco de dados.
- **DTOs:** definem os dados utilizados na comunicação entre a API e seus consumidores.
- **Repositories:** abstraem as operações de persistência.
- **Services:** concentram as regras de negócio.
- **Controllers:** recebem as requisições HTTP e disponibilizam os recursos da API.

Essa separação ajuda a manter o código organizado, testável e preparado para evoluções futuras.

## Pré-requisitos

Antes de executar o projeto, instale:

- [Java JDK 25](https://jdk.java.net/25/)
- [MySQL](https://dev.mysql.com/downloads/)
- [Git](https://git-scm.com/)
- Um terminal ou IDE de sua preferência.

## Como executar localmente

### 1. Clone o repositório

```bash
git clone https://github.com/ProHuari/hu-service.git
cd hu-service
```

### 2. Configure o banco de dados

Crie um banco de dados MySQL para a aplicação:

```sql
CREATE DATABASE huari;
```

Configure a conexão no arquivo `src/main/resources/application.properties`, utilizando as propriedades exigidas pela aplicação.

Exemplo de configuração para desenvolvimento:

```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/huari}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD}
```

Defina as credenciais de acordo com seu ambiente. Não publique senhas, tokens ou outras informações confidenciais no repositório.

> A configuração acima é um exemplo de conexão. Ajuste-a às configurações existentes do projeto e confirme como o esquema do banco de dados é criado antes de executar a aplicação.

### 3. Execute o backend

No Linux ou macOS:

```bash
./mvnw spring-boot:run
```

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

A aplicação será iniciada conforme a configuração de porta definida no projeto. Se nenhuma porta diferente estiver configurada, o Spring Boot utiliza a porta `8080`.

## Documentação da API

O projeto inclui a dependência Springdoc OpenAPI para documentação dos endpoints.

Após iniciar a aplicação, verifique:

- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **Especificação OpenAPI:** http://localhost:8080/v3/api-docs

Esses endereços dependem da configuração da aplicação e da disponibilidade dos respectivos endpoints.

A documentação permite consultar os recursos expostos, os parâmetros das requisições e os formatos das respostas.

## Testes

Para executar os testes automatizados com Maven:

```bash
./mvnw test
```

No Windows:

```powershell
.\mvnw.cmd test
```

Os testes devem ser utilizados para verificar as regras de negócio, a validação dos dados e o comportamento dos endpoints.

*Ferramenta utilizada para controle de qualidade das api: swagger 

### Métodos e Contratos da API (REST)

*AUTENTICAÇÃO (`/auth`)*

POST `/auth/login` — Autentica as credenciais do usuário e devolve tokens JWT (acesso e refresh)
Request:
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```
Response (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsIn...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsIn...",
  "type": "Bearer",
  "userId": 1,
  "role": "STUDENT"
}
```

POST `/auth/refresh` — Emite um novo par de tokens a partir de um Refresh Token válido
Request:
```json
{
  "refreshToken": "string"
}
```

POST `/auth/logout` — Revoga o token JWT atual do usuário autenticado (Header `Authorization: Bearer <token>`)

POST `/auth/forgot-password` — Solicita recuperação de senha
Request:
```json
{
  "email": "user@example.com"
}
```
Response (200 OK):
```json
{
  "message": "Password reset token generated successfully (valid for 15 minutes).",
  "resetToken": "token-retornado-apenas-em-dev"
}
```

POST `/auth/reset-password` — Redefine a senha com o token temporário
Request:
```json
{
  "token": "string",
  "newPassword": "newPassword123"
}
```

*USUÁRIOS (`/usuarios`)*

POST `/usuarios` — Cadastra um novo usuário aluno (perfil público `STUDENT`)
Request:
```json
{
  "name": "Maria Santos",
  "email": "maria@example.com",
  "password": "password123"
}
```
Response (201 Created):
```json
{
  "id": 1,
  "name": "Maria Santos",
  "email": "maria@example.com",
  "role": "STUDENT"
}
```

POST `/usuarios/admin` — Cadastra um novo administrador (somente ADMIN)
Request:
```json
{
  "name": "Admin Huari",
  "email": "admin@huari.com",
  "password": "adminPassword123"
}
```

GET `/usuarios/me` — Retorna o perfil do usuário autenticado (200 OK)
PUT `/usuarios/me` — Atualiza nome e e-mail do usuário autenticado
Request:
```json
{
  "name": "Maria Silva Santos",
  "email": "maria.silva@example.com"
}
```

PATCH `/usuarios/me/senha` — Altera a senha do usuário autenticado
Request:
```json
{
  "currentPassword": "password123",
  "newPassword": "newPassword456"
}
```

GET `/usuarios` — Lista usuários com paginação (somente ADMIN)
GET `/usuarios/{id}` — Busca usuário por ID (somente ADMIN)
PUT `/usuarios/{id}` — Atualiza dados e reseta senha de um usuário (somente ADMIN)
DELETE `/usuarios/{id}` — Remove logicamente (soft-delete) um usuário (somente ADMIN)

*CURSOS (`/cursos`) [Leitura Pública]*

GET `/cursos` — Lista cursos com paginação (Público / Landing Page)
GET `/cursos/{id}` — Busca curso por ID (Público)
POST `/cursos` — Cria curso (somente ADMIN)
Request:
```json
{
  "title": "Introdução ao Processo Legislativo",
  "description": "Aprenda como leis são criadas no Brasil.",
  "videoUrl": "https://www.youtube.com/watch?v=exemplo",
  "categoryId": 1,
  "authorId": 1
}
```
PUT `/cursos/{id}` — Atualiza curso existente (somente ADMIN)
DELETE `/cursos/{id}` — Exclui logicamente (soft-delete) um curso (somente ADMIN)

*CATEGORIAS (`/categorias`) [Leitura Pública]*

GET `/categorias` — Lista categorias com paginação
GET `/categorias/{id}` — Busca categoria por ID
POST `/categorias` — Cria categoria (somente ADMIN)
Request:
```json
{
  "name": "Ciência Política",
  "description": "Estudos sobre instituições, sistemas de governo e democracia."
}
```
PUT `/categorias/{id}` — Atualiza categoria (somente ADMIN)
DELETE `/categorias/{id}` — Exclui logicamente uma categoria (somente ADMIN)

*AUTORES (`/autores`) [Leitura Pública]*

GET `/autores` — Lista autores/criadores de conteúdo com paginação
GET `/autores/{id}` — Busca autor por ID
POST `/autores` — Cadastra novo autor (somente ADMIN)
Request:
```json
{
  "channelName": "Canal Cidadania Aberta",
  "channelUrl": "https://youtube.com/@cidadaniaberta"
}
```
PUT `/autores/{id}` — Atualiza autor (somente ADMIN)
DELETE `/autores/{id}` — Exclui logicamente um autor (somente ADMIN)

*MÓDULOS (`/modulos`) [Leitura Pública]*

GET `/modulos` — Lista módulos com paginação (filtro opcional: `?courseId=1`)
GET `/modulos/{id}` — Busca módulo por ID
POST `/modulos` — Cria módulo (somente ADMIN)
Request:
```json
{
  "title": "Módulo 1: O Papel do Congresso Nacional",
  "description": "Câmara dos Deputados e Senado Federal.",
  "position": 1,
  "courseId": 1
}
```
PUT `/modulos/{id}` — Atualiza módulo (somente ADMIN)
DELETE `/modulos/{id}` — Exclui logicamente um módulo (somente ADMIN)

*AULAS (`/aulas`)*

GET `/aulas` — Lista aulas com paginação (filtro opcional: `?moduleId=1`)
GET `/aulas/{id}` — Busca aula por ID
POST `/aulas` — Cria aula (somente ADMIN)
Request:
```json
{
  "title": "Aula 1: Como tramita uma PEC",
  "description": "Entenda as votações em dois turnos.",
  "videoUrl": "https://youtube.com/watch?v=aula1",
  "position": 1,
  "durationInMinutes": 15,
  "moduleId": 1
}
```
PUT `/aulas/{id}` — Atualiza aula (somente ADMIN)
DELETE `/aulas/{id}` — Exclui logicamente uma aula (somente ADMIN)

*MATRÍCULAS (`/matriculas`)*

GET `/matriculas` — Lista matrículas com paginação (aluno vê as suas; admin vê todas ou filtra `?userId=1`)
POST `/matriculas` — Inscreve o usuário autenticado em um curso
Request:
```json
{
  "courseId": 1
}
```
Response (201 Created):
```json
{
  "id": 1,
  "enrollmentDate": "2026-11-07",
  "status": "ACTIVE",
  "userId": 1,
  "courseId": 1
}
```
GET `/matriculas/{id}` — Busca matrícula por ID
PATCH `/matriculas/{id}/status` — Atualiza status da matrícula (`ACTIVE`, `COMPLETED`, `CANCELLED`) (somente ADMIN)
DELETE `/matriculas/{id}` — Exclui logicamente uma matrícula (somente ADMIN)

*PROGRESSO DAS AULAS (`/progresso-aulas`)*

GET `/progresso-aulas` — Lista progresso com paginação (filtros opcionais: `?courseId=1`, `?userId=1`)
POST `/progresso-aulas` — **Upsert** de progresso do aluno autenticado (cria ou atualiza automaticamente)
Request:
```json
{
  "status": "COMPLETED",
  "lessonId": 1
}
```
Response (201 Created):
```json
{
  "id": 1,
  "status": "COMPLETED",
  "completedAt": "2026-11-07T14:00:00",
  "userId": 1,
  "lessonId": 1
}
```
GET `/progresso-aulas/{id}` — Busca registro de progresso por ID
PUT `/progresso-aulas/{id}` — Atualiza status de progresso
DELETE `/progresso-aulas/{id}` — Remove registro de progresso

*HISTÓRICO DA IA (`/historico-ia`)*

POST `/historico-ia` — Envia pergunta ao assistente cívico (com RAG legislativo e resposta gravada)
Request:
```json
{
  "question": "O que é o projeto de lei PL 2338/2023 de Inteligência Artificial?"
}
```
Response (201 Created):
```json
{
  "id": 1,
  "question": "O que é o projeto de lei PL 2338/2023 de Inteligência Artificial?",
  "answer": "O PL 2338/2023 estabelece o marco legal para o desenvolvimento e uso ético da IA no Brasil...",
  "queriedAt": "2026-11-07T14:00:00",
  "userId": 1
}
```
POST `/historico-ia/stream` — Envia pergunta com resposta transmitida em tempo real via Server-Sent Events (SSE)
GET `/historico-ia` — Lista histórico do usuário autenticado (admin pode filtrar `?userId=1`)
GET `/historico-ia/{id}` — Busca interação por ID
DELETE `/historico-ia/{id}` — Remove interação do histórico

*TRANSPARÊNCIA PÚBLICA (`/transparencia`) [Leitura Pública]*

- **Parlamentares (`/transparencia/parlamentares`)**:
  - `GET /transparencia/parlamentares` — Lista parlamentares (`?chamber=CHAMBER_OF_DEPUTIES` ou `FEDERAL_SENATE`)
  - `GET /transparencia/parlamentares/{id}` — Detalha parlamentar
  - `POST /transparencia/parlamentares` — Cadastra parlamentar (somente ADMIN)
  - `PUT /transparencia/parlamentares/{id}` — Atualiza dados do parlamentar (somente ADMIN)
  - `DELETE /transparencia/parlamentares/{id}` — Remove parlamentar (somente ADMIN)

- **Projetos de Lei (`/transparencia/projetos-lei`)**:
  - `GET /transparencia/projetos-lei` — Lista PLs (filtro por texto: `?q=inteligência`)
  - `GET /transparencia/projetos-lei/{id}` — Detalha PL
  - `POST /transparencia/projetos-lei` — Cadastra PL (somente ADMIN)
  - `PUT /transparencia/projetos-lei/{id}` — Atualiza PL (somente ADMIN)
  - `DELETE /transparencia/projetos-lei/{id}` — Remove PL (somente ADMIN)

- **Despesas Parlamentares CEAP (`/transparencia/despesas`)**:
  - `GET /transparencia/despesas` — Lista despesas parlamentares (`?parliamentarianId=1`)
  - `POST /transparencia/despesas` — Registra despesa (somente ADMIN)
  - `PUT /transparencia/despesas/{id}` — Atualiza despesa (somente ADMIN)
  - `DELETE /transparencia/despesas/{id}` — Remove despesa (somente ADMIN)

*INTEGRAÇÃO COM DADOS ABERTOS DO TRIBUNAL DE CONTAS (`/transparencia/tribunal-contas`) [Público]*

- `GET /transparencia/tribunal-contas/resumo?q=` — Visão consolidada de processos, contas irregulares e status da API do Tribunal de Contas.
- `GET /transparencia/tribunal-contas/processos?subject=&limit=` — Lista processos de fiscalização e auditoria em andamento no TCU/TCE.
- `GET /transparencia/tribunal-contas/contas-irregulares?name=` — Consulta a lista oficial de responsáveis com contas julgadas irregulares.
Exemplo de resposta consolidada:
```json
{
  "provider": "Tribunal de Contas da União (TCU) / TCE",
  "status": "ONLINE",
  "message": "Dados obtidos com sucesso do Tribunal de Contas.",
  "totalItems": 12,
  "queriedAt": "2026-11-07T14:00:00",
  "processes": [
    {
      "numeroProcesso": "TC 012.345/2026-0",
      "relator": "Ministro Relator",
      "situacao": "ABERTO",
      "assunto": "Auditoria de conformidade em compras de TI",
      "orgao": "Ministério da Gestão e Inovação",
      "ano": 2026
    }
  ],
  "irregularAccounts": [],
  "sourceUrl": "https://dadosabertos.tcu.gov.br/api"
}
```


## Segurança

O projeto inclui componentes do Spring Security e dependências para trabalhar com JWT.

As regras de autorização, os fluxos de autenticação e a proteção dos endpoints devem ser verificados na implementação antes de serem considerados concluídos.

Boas práticas recomendadas:

- Não versionar credenciais ou tokens.
- Utilizar variáveis de ambiente para configurações sensíveis.
- Validar os dados recebidos pela API.
- Proteger endpoints conforme suas permissões.
- Evitar expor informações confidenciais em respostas e logs.

## Roadmap

As próximas etapas podem incluir:

- [ ] Consolidar os recursos de consulta e gerenciamento de dados.
- [ ] Documentar os endpoints e seus exemplos de uso.
- [ ] Integrar fontes oficiais de dados públicos.
- [ ] Desenvolver recursos de análise e cruzamento de informações.
- [ ] Expandir os recursos de educação cidadã.
- [ ] Ampliar a cobertura de testes automatizados.
- [ ] Documentar o processo de implantação.

*O roadmap representa possibilidades de evolução e não necessariamente funcionalidades já implementadas.*

## Contribuição

Contribuições são bem-vindas.

1. Faça um fork do repositório.
2. Crie uma branch para sua alteração.
3. Implemente e teste as modificações.
4. Envie um pull request com uma descrição objetiva das mudanças.

Mantenha o código consistente com a arquitetura existente e evite incluir credenciais ou dados sensíveis nos commits.

## Contexto acadêmico

O Huari é desenvolvido com finalidade acadêmica, buscando explorar o uso de tecnologia para ampliar a transparência pública, facilitar a análise de informações e incentivar a educação política.

As análises devem priorizar fontes verificáveis, critérios transparentes e apresentação imparcial dos dados.

## Licença

Consulte as informações de licença do repositório. Caso ainda não exista uma licença definida, estabeleça uma antes de autorizar formalmente o uso, a distribuição ou a modificação do código por terceiros.

---

**Huari — tecnologia e informação a serviço da transparência pública.**
