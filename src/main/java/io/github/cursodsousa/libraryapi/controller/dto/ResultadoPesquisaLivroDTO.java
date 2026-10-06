package io.github.cursodsousa.libraryapi.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ResultadoPesquisaLivroDTO(
        UUID id,
        String isbn,
        String titulo,
        LocalDate dataPublicacao,
        String genero,
        BigDecimal preco,
        AutorDTO autor
) {
}
