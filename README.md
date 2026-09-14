# Huari Backend

O Huari Backend é o núcleo de processamento e inteligência da plataforma, responsável por centralizar e padronizar dados públicos e eleitorais dispersos para gerar análises isentas e acessíveis. A aplicação atende a três frentes principais:

* **Jornalistas:** Acesso a dados consolidados para investigações e reportagens baseadas em evidências.
* **Órgãos oficiais:** Transparência e facilidade na consulta de indicadores e métricas institucionais.
* **Sociedade civil:** Democratização da informação política, promovendo o controle social e a participação cidadã.

Além da inteligência de dados, o sistema gerencia a infraestrutura de cursos e trilhas educacionais voltadas para a formação política.

## Arquitetura e Tecnologias

O projeto foi desenvolvido com foco em boas práticas de engenharia de software, separação de responsabilidades e segurança:

* **Linguagem:** Java (versão 25)
* **Framework:** Spring Boot (versão 4.1.1)
* **Persistência de Dados:** Mapeamento objeto-relacional (ORM) estruturado em camadas.

### Padrão Arquitetural

* **Entities:** Representação do modelo de domínio e mapeamento das tabelas do banco de dados.
* **DTOs (Data Transfer Objects):** Blindagem do domínio e segurança no tráfego de dados entre as camadas da API.
* **Repositories:** Camada de abstração para persistência e comunicação com o banco de dados.
* **Services:** Centralização das regras de negócio e lógica operacional da aplicação.
* **Controllers:** Exposição dos endpoints REST para gerenciamento das operações CRUD do sistema.
