# Aula 58 — API REST de Autor

## Objetivo

Expor o cadastro e a consulta de autores pela API, sem misturar JSON com a entidade JPA.

## O que foi feito

1. `AutorDTO` (record) com `id`, `nome`, `dataNascimento`, `nacionalidade` e `mapearParaAutor()`.
2. `AutorService.salvar` persiste via `AutorRepository`.
3. `AutorService.obterDetalhes` devolve DTO (não a entidade).
4. `POST /autores` responde `201 Created` com `Location: /autores/{uuid}`.
5. `GET /autores/{id}` responde `200` com o DTO ou `404` se o UUID não existir.

## Pontos que quero lembrar

- Controller fino: recebe DTO, chama service, monta `ResponseEntity`.
- `ServletUriComponentsBuilder.fromCurrentRequest()` monta a URI do recurso criado.
- `@EnableJpaAuditing` na `Application` liga `dataCadastro` / `dataAtualizacao` do `Autor`.
- Papéis (Gerente cadastra, Operador só consulta) ainda não entram — falta Security.

## Pendências do `requisitos.txt`

- [ ] Não cadastrar autor com mesmo nome + data de nascimento + nacionalidade
- [ ] Não excluir autor que tenha livro
- [ ] Restringir escrita ao Gerente e leitura ao Operador
