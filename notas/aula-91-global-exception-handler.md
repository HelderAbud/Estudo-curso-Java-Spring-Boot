# Aula 91 — GlobalExceptionHandler

## Objetivo

Tirar `try/catch` do `AutorController`. A exception estoura e o `@RestControllerAdvice` responde.

## O que foi feito

1. `POST /autores` e `DELETE /autores/{id}` voltaram limpos (`ResponseEntity<Void>`).
2. `RegistroDuplicadoException` → `409` no `GlobalExceptionHandler`.
3. `OperacaoNaoPermitidaException` → `400` no `GlobalExceptionHandler`.
4. `AutorValidator.validar` antes de `save` (cadastro e atualização).
5. `possuiLivro` = `livroRepository.existsByAutor(autor)`.
6. Sem `CascadeType.ALL` em `Autor.livros`.
7. `@RequiredArgsConstructor` no `AutorService` (injeta repository, validator e livroRepository).

## Pontos que quero lembrar

- Controller fino: chama o service e monta `ResponseEntity`. Não trata regra de negócio.
- Aula 90 mostrou `try/catch` só para ver o erro; a 91 remove.
- Sem cascade no `@OneToMany`, apagar autor não apaga livros; `possuiLivro` barra antes.

## Como testar

- POST autor novo → `201`
- POST autor igual → `409`
- DELETE autor sem livro → `204`
- DELETE autor com livro → `400`

## Pendências do `requisitos.txt`

- [x] Não cadastrar autor duplicado
- [x] Não excluir autor que tenha livro
- [ ] Restringir escrita ao Gerente e leitura ao Operador
