# AGENTS.md — Estudo Curso Spring Boot

Caderno de estudo da **libraryapi**. Código-fonte de trabalho no IntelliJ; este repo recebe o progresso do dia.

## Gatilho

Quando o usuário escrever **Atualizar estudo**, **Dia do curso** ou **Sincronizar libraryapi**:

1. Fonte: `C:\Users\Pessoal\IdeaProjects\curso-spring-boot-especialista\libraryapi`
2. Destino: este repositório (não alterar o `origin` do clone do curso).
3. Copiar só a `libraryapi` (excluir `target/`, `.idea/`, `*.iml`).
4. Atualizar `DIARIO.md` e `notas/aula-XX-*.md` com a aula do dia.
5. Ajustar a tabela de status do `README.md`.
6. Abrir branch `feat/aula-XX-...` a partir de `main`.
7. Commit / push / PR **somente** se o usuário pedir.

Não republicar o monorepo inteiro do curso (`pedidos-api`, zips, outras branches do instrutor).

## Comandos

| Objetivo | Comando |
|----------|---------|
| Compilar | `./mvnw -DskipTests compile` |
| Subir API | `./mvnw spring-boot:run` |
| Testes | `./mvnw test` (precisa do Postgres em `localhost:5432`) |

## HITL

- Commit, push e PR só com pedido explícito.
- Não commitar `.env`, senhas reais, `target/` ou `.idea/`.
- Autor dos commits: Helder Abud (sem `Co-authored-by: Cursor`).
