# Aula 85 — Pesquisa de Autores

## Objetivo

Listar autores com filtros opcionais (`nome`, `nacionalidade`) e devolver DTO, sem expor a entidade JPA.

## O que foi feito

1. `GET /autores` — pesquisa com query params opcionais.
2. `AutorService.pesquisa` escolhe o query method:
   - nome + nacionalidade
   - só nome
   - só nacionalidade
   - `findAll` se nenhum filtro
3. Controller mapeia `List<Autor>` → `List<AutorDTO>` com `stream()`.
4. Injeção via `@RequiredArgsConstructor` (Lombok).
5. `GET /autores/{id}` passou a usar `service.obterDetalhes(id)` (DTO no service).

## CRUD e regras que já estavam no IntelliJ e entram neste sync

- `PUT /autores/{id}` — atualiza (`204` ou `404`)
- `DELETE /autores/{id}` — remove (`204` ou `404`); idempotente
- Bean Validation no `AutorDTO` (`@NotBlank`, `@Size`, `@NotNull`, `@Past`)
- `GlobalExceptionHandler`:
  - `400` validação
  - `409` autor duplicado (mesmo nome + data + nacionalidade)
  - `400` exclusão de autor com livro
- Testes: `AutorControllerTest` e `AutorServiceTest`

## Pontos que quero lembrar

- Controller fino: pesquisa no service, mapeamento para DTO na borda HTTP.
- `obterDetalhes` no service evita o controller conhecer a entidade no GET por id.
- `@RequiredArgsConstructor` gera o construtor do `final AutorService`.
- Papéis Gerente/Operador ainda não entram — falta Security.

## Pendências do `requisitos.txt`

- [x] Não cadastrar autor com mesmo nome + data de nascimento + nacionalidade
- [x] Não excluir autor que tenha livro
- [ ] Restringir escrita ao Gerente e leitura ao Operador
