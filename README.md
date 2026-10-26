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

*Ferramenta utilizada para controle de qualidade das api: postman

métodos atuais 

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
