# Diário do curso

Registro curto do que entrou no repositório a cada dia. Notas da aula ficam em `notas/`.

## 2026-09-03 — Aula 58

- Módulo `libraryapi` versionado neste repo (JPA + auditoria + Hikari).
- API de Autor: `POST /autores` e `GET /autores/{id}`.
- Camadas: `AutorController` → `AutorService` → `AutorRepository`.
- DTO record `AutorDTO` com mapeamento para entidade.
- Requisitos de negócio anotados em `requisitos.txt` (roles, duplicidade e exclusão ainda pendentes).

Notas: [`notas/aula-58-api-autor.md`](notas/aula-58-api-autor.md)
