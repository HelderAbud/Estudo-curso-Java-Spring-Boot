package io.github.cursodsousa.libraryapi.service;

import io.github.cursodsousa.libraryapi.exceptions.CampoInvalidoException;
import io.github.cursodsousa.libraryapi.exceptions.RegistroDuplicadoException;
import io.github.cursodsousa.libraryapi.model.Autor;
import io.github.cursodsousa.libraryapi.model.GeneroLivro;
import io.github.cursodsousa.libraryapi.model.Livro;
import io.github.cursodsousa.libraryapi.repository.LivroRepository;
import io.github.cursodsousa.libraryapi.validator.LivroValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    LivroRepository repository;

    LivroService service;

    @BeforeEach
    void setUp() {
        service = new LivroService(repository, new LivroValidator(repository));
    }

    @Test
    void deveRecusarIsbnDuplicadoAoSalvar() {
        Livro livro = livroFantasia();
        when(repository.findByIsbn("978-0-00")).thenReturn(List.of(livroExistente()));

        RegistroDuplicadoException ex = assertThrows(
                RegistroDuplicadoException.class, () -> service.salvar(livro));

        assertEquals("ISBN Duplicado", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void deveRecusarDataPublicacaoFutura() {
        Livro livro = livroFantasia();
        livro.setDataPublicacao(LocalDate.now().plusDays(1));

        CampoInvalidoException ex = assertThrows(
                CampoInvalidoException.class, () -> service.salvar(livro));

        assertEquals("dataPublicacao", ex.getCampo());
        verify(repository, never()).save(any());
    }

    @Test
    void deveRecusarLivroAPartirDe2020SemPreco() {
        Livro livro = livroFantasia();
        livro.setDataPublicacao(LocalDate.of(2020, 1, 1));
        livro.setPreco(null);

        CampoInvalidoException ex = assertThrows(
                CampoInvalidoException.class, () -> service.salvar(livro));

        assertEquals("preco", ex.getCampo());
        verify(repository, never()).save(any());
    }

    @Test
    void devePermitirAtualizarComOProprioIsbn() {
        Livro livro = livroExistente();
        when(repository.findByIsbn("978-0-00")).thenReturn(List.of(livroExistente()));

        service.atualizar(livro);

        verify(repository).save(livro);
    }

    @Test
    void deveRecusarAtualizacaoComIsbnDeOutroLivro() {
        Livro livro = livroExistente();
        livro.setIsbn("999-9-99");
        Livro outro = livroExistente();
        outro.setId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        outro.setIsbn("999-9-99");
        when(repository.findByIsbn("999-9-99")).thenReturn(List.of(outro));

        assertThrows(RegistroDuplicadoException.class, () -> service.atualizar(livro));
        verify(repository, never()).save(any());
    }

    @Test
    void devePesquisarPorSpecificationComFiltros() {
        Livro livro = livroFantasia();
        Pageable page = PageRequest.of(0, 10);
        when(repository.findAll(any(Specification.class), eq(page)))
                .thenReturn(new PageImpl<>(List.of(livro), page, 1));

        Page<Livro> resultado = service.pesquisa(null, null, null, null, 1954, page);

        assertEquals(1, resultado.getContent().size());
        verify(repository).findAll(any(Specification.class), eq(page));
    }

    private Livro livroFantasia() {
        Autor autor = new Autor();
        autor.setNome("J.R.R. Tolkien");

        Livro livro = new Livro();
        livro.setTitulo("O Senhor dos Anéis");
        livro.setIsbn("978-0-00");
        livro.setGenero(GeneroLivro.FANTASIA);
        livro.setDataPublicacao(LocalDate.of(1954, 7, 29));
        livro.setAutor(autor);
        return livro;
    }

    private Livro livroExistente() {
        Livro existente = livroFantasia();
        existente.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        return existente;
    }
}
