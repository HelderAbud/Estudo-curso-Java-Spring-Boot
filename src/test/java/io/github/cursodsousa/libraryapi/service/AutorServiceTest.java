package io.github.cursodsousa.libraryapi.service;

import io.github.cursodsousa.libraryapi.exceptions.OperacaoNaoPermitidaException;
import io.github.cursodsousa.libraryapi.exceptions.RegistroDuplicadoException;
import io.github.cursodsousa.libraryapi.model.Autor;
import io.github.cursodsousa.libraryapi.model.Livro;
import io.github.cursodsousa.libraryapi.repository.AutorRepository;
import io.github.cursodsousa.libraryapi.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutorServiceTest {

    @Mock
    AutorRepository repository;

    @Mock
    LivroRepository livroRepository;

    AutorService service;

    @BeforeEach
    void setUp() {
        service = new AutorService(repository, livroRepository);
    }

    @Test
    void deveRecusarAutorComMesmoNomeDataENacionalidade() {
        Autor autor = autorSemId();
        when(repository.findByNomeAndDataNascimentoAndNacionalidade(
                "José", LocalDate.of(1951, 1, 31), "Brasileira"))
                .thenReturn(Optional.of(new Autor()));

        assertThrows(RegistroDuplicadoException.class, () -> service.salvar(autor));
        verify(repository, never()).save(any());
    }

    @Test
    void deveRecusarExclusaoQuandoAutorPossuiLivro() {
        Autor autor = autorSemId();
        autor.setId(UUID.fromString("2449f4e4-ee1a-4a71-8aa3-e9d46306fe8a"));
        autor.setLivros(List.of(new Livro()));

        assertThrows(OperacaoNaoPermitidaException.class, () -> service.deletar(autor));
        verify(repository, never()).delete(any());
    }

    private Autor autorSemId() {
        Autor autor = new Autor();
        autor.setNome("José");
        autor.setDataNascimento(LocalDate.of(1951, 1, 31));
        autor.setNacionalidade("Brasileira");
        return autor;
    }
}
