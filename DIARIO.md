# Diário do curso

Registro curto do que entrou no repositório a cada dia. Notas da aula ficam em `notas/`.

## 2026-10-06 — Aula 103

- `GenericController.gerarHeaderLocation` centraliza o header `Location` dos POSTs.
- `AutorController` e `LivroController` implementam a interface.
- CRUD de Livro: `POST/GET/PUT/DELETE /livros` + pesquisa paginada com `LivroSpecs`.
- MapStruct (`LivroMapper`), `LivroValidator` e `CampoInvalidoException` → `422` no `GlobalExceptionHandler`.
- Pesquisa de autores passa a `Example` (`pesquisaByExample`).

Notas: [`notas/aula-103-generic-controller-livros.md`](notas/aula-103-generic-controller-livros.md)

## 2026-09-17 — Aula 91

- Tira `try/catch` do `AutorController`; exceptions sobem para o `GlobalExceptionHandler`.
- `AutorValidator` valida duplicidade; `salvar`/`atualizar` chamam `validator.validar`.
- `possuiLivro` usa `livroRepository.existsByAutor`.
- Remove `CascadeType.ALL` de `Autor.livros` para a exclusão não apagar livros em cascata.
- `@RequiredArgsConstructor` no `AutorService`.

Notas: [`notas/aula-91-global-exception-handler.md`](notas/aula-91-global-exception-handler.md)

## 2026-09-11 — Aula 85

- `GET /autores` com filtros opcionais `nome` e `nacionalidade`.
- `GET /autores/{id}` usa `AutorService.obterDetalhes` (DTO no service).
- CRUD completo de Autor: `PUT` e `DELETE`, validação e `GlobalExceptionHandler`.
- Regras: autor duplicado (`409`) e exclusão bloqueada se houver livro (`400`).
- Testes unitários de controller e service.

Notas: [`notas/aula-85-pesquisa-autores.md`](notas/aula-85-pesquisa-autores.md)

## 2026-09-03 — Aula 58

- Módulo `libraryapi` versionado neste repo (JPA + auditoria + Hikari).
- API de Autor: `POST /autores` e `GET /autores/{id}`.
- Camadas: `AutorController` → `AutorService` → `AutorRepository`.
- DTO record `AutorDTO` com mapeamento para entidade.
- Requisitos de negócio anotados em `requisitos.txt` (roles, duplicidade e exclusão ainda pendentes).

Notas: [`notas/aula-58-api-autor.md`](notas/aula-58-api-autor.md)
