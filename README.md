# Estudo — Curso Java Spring Boot Especialista

Caderno público do progresso no curso. O código daqui é a **libraryapi** (módulo atual), sincronizado a partir do IntelliJ:

`C:\Users\Pessoal\IdeaProjects\curso-spring-boot-especialista\libraryapi`

Repositório: [HelderAbud/Estudo-curso-Java-Spring-Boot](https://github.com/HelderAbud/Estudo-curso-Java-Spring-Boot)

## Sobre o curso

Caderno de estudo do curso **Spring Boot Expert: JPA, REST, JWT, OAuth2 com Docker e AWS**.

Temas que vou percorrendo neste repo:

- Spring Boot e arquitetura do ecossistema Spring
- JPA / Spring Data
- APIs REST
- Spring Security (JWT, OAuth2, login social)
- Docker e deploy na AWS (RDS)
- Testes (JUnit/Mockito) e documentação (Swagger/OpenAPI)

## Status atual

| Campo | Valor |
|-------|--------|
| Módulo | `libraryapi` |
| Aula | **85** — Pesquisa de Autores |
| Java | 21 |
| Spring Boot | 3.3.2 |
| Banco | PostgreSQL 16 (`localhost:5432/library`) |

Diário das aulas: [`DIARIO.md`](DIARIO.md)

## O que já existe na API

- Entidades JPA `Autor` e `Livro`, auditoria (`@CreatedDate`, `@LastModifiedDate`)
- Pool Hikari em `DatabaseConfiguration`
- `POST /autores` — cadastra autor e devolve `201` com header `Location`
- `GET /autores/{id}` — consulta por UUID (`200` ou `404`)
- `GET /autores` — pesquisa opcional por `nome` e/ou `nacionalidade`
- `PUT /autores/{id}` — atualiza (`204` ou `404`)
- `DELETE /autores/{id}` — remove (`204` ou `404`); bloqueia se o autor tiver livro
- DTO `AutorDTO` (record) com Bean Validation e `mapearParaAutor()`
- `GlobalExceptionHandler`: validação `400`, duplicado `409`, operação não permitida `400`

Ainda não implementado (está em `requisitos.txt`): papéis Gerente/Operador.

## Como rodar

Postgres (Docker, se disponível):

``
docker run --name librarydb -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=library -p 5432:5432 -d postgres:16.3
``

API:

``
./mvnw spring-boot:run
``

Smoke test:

``http
POST http://localhost:8080/autores
Content-Type: application/json

{
  "nome": "Machado de Assis",
  "dataNascimento": "1839-06-21",
  "nacionalidade": "Brasileira"
}

GET http://localhost:8080/autores/{id}

GET http://localhost:8080/autores?nome=Machado de Assis
``

Mais comandos: `comandos-docker.txt`, `comandos-sql.txt`, `comandos-sql-seed.txt`.

## Rotina diária

No Cursor, depois da aula no IntelliJ:

``
Atualizar estudo
``

O agente compara a `libraryapi` local com este repositório, atualiza o diário e abre PR. Detalhe operacional: [`AGENTS.md`](AGENTS.md).
