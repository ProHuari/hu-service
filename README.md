# Huari Backend

**API REST para plataforma de cursos, educação cidadã e assistência com Inteligência Artificial.**

O Huari é uma plataforma educacional e de capacitação cidadã que busca facilitar o acesso ao conhecimento e ao aprendizado guiado por meio de cursos estruturados, trilhas em módulos e aulas, acompanhamento de progresso e tira-dúvidas com IA generativa (Google Gemini).

Este repositório contém o backend da aplicação, desenvolvido com Java e Spring Boot, responsável pelas regras de negócio, controle de acesso baseado em papéis (RBAC com JWT), persistência de dados e integração com IA.

---

## Objetivos do Projeto

- **Educação Cidadã & Capacitação:** estruturar cursos, módulos e aulas multimídia de forma acessível e organizada.
- **Acompanhamento de Aprendizado:** controle individual de matrículas e progresso de conclusão de aulas por aluno.
- **Assistência com Inteligência Artificial:** tira-dúvidas inteligente integrado ao Gemini para auxiliar estudantes durante as aulas.
- **Segurança e Transparência:** arquitetura segura com autenticação stateless via JWT e controle refinado de permissões para alunos e administradores.

---

## Tecnologias Utilizadas

| Tecnologia | Finalidade |
|---|---|
| **Java 25** | Linguagem de programação principal |
| **Spring Boot 3.4.x / 4.x** | Framework base para a aplicação backend |
| **Spring Web MVC** | Desenvolvimento de endpoints RESTful |
| **Spring Data JPA & Hibernate** | Mapeamento objeto-relacional e persistência de dados |
| **MySQL** | Banco de dados relacional principal |
| **H2 Database** | Banco de dados em memória para testes automatizados |
| **Spring Security** | Autenticação stateless, autorização RBAC e filtros de segurança |
| **JJWT (io.jsonwebtoken)** | Criação, assinatura (HMAC-SHA) e validação de tokens JWT |
| **Bean Validation (Hibernate Validator)** | Validação de entradas e payloads HTTP |
| **Springdoc OpenAPI (Swagger UI)** | Documentação interativa e especificação da API |
| **RestClient & Google Gemini API** | Integração HTTP resiliente com modelos de IA Generativa |
| **Lombok** | Produtividade e redução de código boilerplate |
| **Maven** | Gerenciamento de ciclo de vida e dependências do projeto |

---

## Arquitetura do Sistema

O projeto adota uma arquitetura em camadas bem delimitada e padronizada em inglês, com mensagens de feedback ao usuário final localizadas em português:

```
src/main/java/com/huariservice/huariia/
├── config/         # Configurações de segurança, CORS, Swagger, clientes HTTP e inicializadores
├── controllers/    # Controladores REST expondo os endpoints HTTP
├── DTOs/           # Data Transfer Objects (Records imutáveis para requests e responses)
├── entities/       # Entidades JPA de domínio e seus mapeamentos de banco de dados
│   └── enums/      # Enumerações de domínio (Role, EnrollmentStatus, LessonStatus)
├── exceptions/     # Exceções customizadas e GlobalExceptionHandler unificado
├── repositories/   # Interfaces Spring Data JPA para acesso aos dados
├── security/       # Filtros JWT, modelo de usuário autenticado e emissão de tokens
└── services/       # Regras de negócio, casos de uso e integração com IA
```

---

## Pré-requisitos

Para executar o projeto localmente, certifique-se de possuir:

- [Java JDK 25](https://jdk.java.net/25/) instalado e configurado no `JAVA_HOME`.
- [MySQL 8+](https://dev.mysql.com/downloads/) em execução (ou container Docker).
- [Git](https://git-scm.com/) para versionamento.
- Chave de API do Google Gemini (opcional, caso queira testar a IA localmente).

---

## Como Executar Localmente

### 1. Clone o repositório

```bash
git clone https://github.com/ProHuari/hu-service.git
cd hu-service
```

### 2. Configure as variáveis de ambiente

Você pode configurar suas variáveis no arquivo `src/main/resources/application.properties` ou definir variáveis de ambiente no sistema:

```properties
# Banco de Dados
DB_URL=jdbc:mysql://localhost:3306/huari
DB_USERNAME=root
DB_PASSWORD=sua_senha_aqui

# JWT
JWT_SECRET=sua-chave-secreta-com-pelo-menos-32-caracteres-para-hmac-sha

# Administrador Inicial (Opcional - criado no primeiro boot)
ADMIN_NOME=Administrador
ADMIN_EMAIL=admin@huari.com
ADMIN_SENHA=senhaAdminForte123

# Integração com IA (Google Gemini)
IA_ENABLED=false
IA_API_KEY=sua_chave_gemini_aqui
IA_MODEL=gemini-3.5-flash-lite
```

### 3. Crie o banco de dados

No MySQL:

```sql
CREATE DATABASE huari CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 4. Execute a aplicação

No Windows (PowerShell):

```powershell
.\mvnw.cmd spring-boot:run
```

No Linux / macOS:

```bash
./mvnw spring-boot:run
```

A API estará disponível por padrão em: `http://localhost:8080`.

---

## Documentação Interativa da API (Swagger)

Com a aplicação em execução, acesse a documentação interativa para visualizar esquemas, exemplos de requisição e testar os endpoints:

- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

> Para testar rotas protegidas no Swagger UI, faça login em `/auth/login`, copie o token JWT gerado e clique no botão **Authorize** no topo da página informando `Bearer <seu-token>`.

---

## Visão Geral dos Endpoints da API

Todas as rotas foram padronizadas em inglês:

### 1. Autenticação (`/auth`)

| Método | Endpoint | Descrição | Acesso |
|---|---|---|---|
| `POST` | `/auth/login` | Autentica usuário e retorna token JWT | Público |

**Exemplo de Payload de Login:**
```json
{
  "email": "aluno@huari.com",
  "password": "senhaSegura123"
}
```

---

### 2. Usuários (`/users`)

| Método | Endpoint | Descrição | Acesso |
|---|---|---|---|
| `POST` | `/users` | Cadastro público de novo aluno (`STUDENT`) | Público |
| `POST` | `/users/admin` | Cadastro de novo administrador | `ADMIN` |
| `GET` | `/users/me` | Obtém o perfil do usuário logado | Autenticado |
| `PUT` | `/users/me` | Atualiza dados do perfil (nome e e-mail) | Autenticado |
| `PATCH` | `/users/me/password` | Altera a senha (exige a senha atual) | Autenticado |
| `GET` | `/users` | Lista todos os usuários cadastrados | `ADMIN` |
| `GET` | `/users/{id}` | Busca usuário por ID | `ADMIN` |
| `PUT` | `/users/{id}` | Atualiza usuário e redefine senha | `ADMIN` |
| `DELETE` | `/users/{id}` | Exclui usuário | `ADMIN` |

---

### 3. Autores (`/authors`)

| Método | Endpoint | Descrição | Acesso |
|---|---|---|---|
| `GET` | `/authors` | Lista todos os autores | Autenticado |
| `GET` | `/authors/{id}` | Busca autor por ID | Autenticado |
| `POST` | `/authors` | Cadastra novo autor | `ADMIN` |
| `PUT` | `/authors/{id}` | Atualiza autor | `ADMIN` |
| `DELETE` | `/authors/{id}` | Exclui autor | `ADMIN` |

---

### 4. Categorias (`/categories`)

| Método | Endpoint | Descrição | Acesso |
|---|---|---|---|
| `GET` | `/categories` | Lista todas as categorias | Autenticado |
| `GET` | `/categories/{id}` | Busca categoria por ID | Autenticado |
| `POST` | `/categories` | Cadastra nova categoria | `ADMIN` |
| `PUT` | `/categories/{id}` | Atualiza categoria | `ADMIN` |
| `DELETE` | `/categories/{id}` | Exclui categoria | `ADMIN` |

---

### 5. Cursos (`/courses`)

| Método | Endpoint | Descrição | Acesso |
|---|---|---|---|
| `GET` | `/courses` | Lista todos os cursos publicados | Autenticado |
| `GET` | `/courses/{id}` | Busca curso por ID | Autenticado |
| `POST` | `/courses` | Publica um novo curso | `ADMIN` |
| `PUT` | `/courses/{id}` | Atualiza curso existente | `ADMIN` |
| `DELETE` | `/courses/{id}` | Exclui curso | `ADMIN` |

---

### 6. Módulos (`/modules`)

| Método | Endpoint | Descrição | Acesso |
|---|---|---|---|
| `GET` | `/modules` | Lista módulos (permite filtro `?courseId=1`) | Autenticado |
| `GET` | `/modules/{id}` | Busca módulo por ID | Autenticado |
| `POST` | `/modules` | Cria novo módulo no curso | `ADMIN` |
| `PUT` | `/modules/{id}` | Atualiza módulo | `ADMIN` |
| `DELETE` | `/modules/{id}` | Exclui módulo | `ADMIN` |

---

### 7. Aulas (`/lessons`)

| Método | Endpoint | Descrição | Acesso |
|---|---|---|---|
| `GET` | `/lessons` | Lista aulas (permite filtro `?moduleId=1`) | Autenticado |
| `GET` | `/lessons/{id}` | Busca aula por ID | Autenticado |
| `POST` | `/lessons` | Cadastra nova aula no módulo | `ADMIN` |
| `PUT` | `/lessons/{id}` | Atualiza aula existente | `ADMIN` |
| `DELETE` | `/lessons/{id}` | Exclui aula | `ADMIN` |

---

### 8. Matrículas (`/enrollments`)

| Método | Endpoint | Descrição | Acesso |
|---|---|---|---|
| `POST` | `/enrollments` | Matricula o usuário logado em um curso | Autenticado |
| `GET` | `/enrollments` | Lista matrículas do aluno (ou todas para `ADMIN`) | Autenticado |
| `GET` | `/enrollments/{id}` | Busca detalhes da matrícula por ID | Autenticado |
| `PATCH` | `/enrollments/{id}/status` | Altera status da matrícula (`ACTIVE`, `COMPLETED`, `CANCELLED`) | `ADMIN` |
| `DELETE` | `/enrollments/{id}` | Remove matrícula | `ADMIN` |

---

### 9. Progresso de Aulas (`/lesson-progresses`)

| Método | Endpoint | Descrição | Acesso |
|---|---|---|---|
| `POST` | `/lesson-progresses` | Registra início ou conclusão de aula pelo aluno logado | Autenticado |
| `GET` | `/lesson-progresses` | Lista progressos do aluno logado (ou todos para `ADMIN`) | Autenticado |
| `GET` | `/lesson-progresses/{id}` | Detalhes do progresso por ID | Autenticado |
| `PUT` | `/lesson-progresses/{id}` | Atualiza status (`NOT_STARTED`, `IN_PROGRESS`, `COMPLETED`) | Autenticado |
| `DELETE` | `/lesson-progresses/{id}` | Remove registro de progresso | Autenticado |

---

### 10. Consulta e Histórico de IA (`/ai-history`)

| Método | Endpoint | Descrição | Acesso |
|---|---|---|---|
| `POST` | `/ai-history` | Envia pergunta à IA Gemini e registra resposta no histórico | Autenticado |
| `GET` | `/ai-history` | Lista histórico de perguntas do aluno (ou todos para `ADMIN`) | Autenticado |
| `GET` | `/ai-history/{id}` | Busca pergunta/resposta por ID | Autenticado |
| `DELETE` | `/ai-history/{id}` | Exclui registro do histórico | Autenticado |

---

## Executando os Testes

Para rodar os testes unitários e de integração com cobertura de perfis e rotas:

```powershell
.\mvnw.cmd test
```

Os testes cobrem:
- Emissão e validação de JWTs (`TokenServiceTest`).
- Fluxo de login e credenciais inválidas (`AuthServiceTest`).
- Criação de usuários, checagem de e-mail duplicado e regras de troca de senha (`UserServiceTest`).
- Proteção de rotas e status HTTP 401 para requisições não autorizadas (`SecurityConfigTest`).
- Inicialização do contexto condicional da IA (`IaAtivadaContextTest`).

---

## Contribuição

1. Faça um Fork do repositório.
2. Crie uma branch para sua funcionalidade (`git checkout -b feature/minha-feature`).
3. Commit suas alterações seguindo o padrão Conventional Commits (`git commit -m 'feat: adiciona nova funcionalidade'`).
4. Envie para o branch remoto (`git push origin feature/minha-feature`).
5. Abra um Pull Request detalhado.

---

## Licença

Este projeto está licenciado sob os termos da licença especificada no repositório.
