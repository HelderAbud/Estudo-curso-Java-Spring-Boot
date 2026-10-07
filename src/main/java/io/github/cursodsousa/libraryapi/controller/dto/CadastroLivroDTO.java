package io.github.cursodsousa.libraryapi.controller.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.cursodsousa.libraryapi.model.GeneroLivro;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CadastroLivroDTO(
        @NotBlank(message = "campo obrigatório")
        String isbn,
        @NotBlank(message = "campo obrigatório")
        String titulo,
        @NotNull(message = "campo obrigatório")
        LocalDate dataPublicacao,
        GeneroLivro genero,
        BigDecimal preco,
        @NotNull(message = "campo obrigatório")
        @JsonProperty("id_autor")
        @JsonAlias("idAutor")
        UUID idAutor
) {
}
