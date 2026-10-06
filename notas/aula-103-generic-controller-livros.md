# Aula 103 — GenericController e API de livros

## Objetivo

Centralizar o `Location` dos POSTs e expor o CRUD de livros com as regras de negócio do curso.

## O que foi feito

1. `GenericController` com `gerarHeaderLocation(UUID)` (`ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")`).
2. `AutorController` e `LivroController` implementam a interface.
3. CRUD `/livros`: cadastro `201` + `Location`, detalhe, atualização, exclusão.
4. `GET /livros` paginado (`isbn`, `titulo`, `nome-autor`, `genero`, `ano-publicacao`).
5. `LivroSpecs` + `JpaSpecificationExecutor` (join no nome do autor; `year()` no ano).
6. MapStruct: `LivroMapper` (`idAutor` → `autor`).
7. `LivroValidator`: ISBN único (`409` "ISBN Duplicado"); data futura e preço obrigatório a partir de 2020 (`422`).
8. `CampoInvalidoException` no `GlobalExceptionHandler` → `422` com `erros[{ campo, erro }]`.
9. Pesquisa de autores por `Example` (`containing`, ignore case).

## Pontos que quero lembrar

- O builder do `Location` se repetia em todo POST; a interface default some com a duplicação.
- Bean Validation continua `400`. Regra de livro (ISBN / data / preço) é no validator, não no DTO.
- No update, o mesmo ISBN do próprio livro não é conflito; ISBN de outro livro é `409`.

## Como testar

- POST `/livros` válido → `201` e `Location` `/livros/{id}`
- POST ISBN repetido → `409` e mensagem `ISBN Duplicado`
- POST/PUT data futura ou livro 2020+ sem preço → `422`
- GET `/livros?nome-autor=&genero=` → página com conteúdo
- POST `/autores` → `201` ainda usa o `Location` da interface

## Pendências do `requisitos.txt`

- [x] Não cadastrar autor duplicado
- [x] Não excluir autor que tenha livro
- [ ] Restringir escrita ao Gerente e leitura ao Operador
