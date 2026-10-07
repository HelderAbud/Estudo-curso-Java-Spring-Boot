package io.github.cursodsousa.libraryapi.controller;

import io.github.cursodsousa.libraryapi.controller.common.GlobalExceptionHandler;
import io.github.cursodsousa.libraryapi.controller.dto.CadastroLivroDTO;
import io.github.cursodsousa.libraryapi.controller.mappers.LivroMapperImpl;
import io.github.cursodsousa.libraryapi.exceptions.CampoInvalidoException;
import io.github.cursodsousa.libraryapi.exceptions.RegistroDuplicadoException;
import io.github.cursodsousa.libraryapi.model.Autor;
import io.github.cursodsousa.libraryapi.model.GeneroLivro;
import io.github.cursodsousa.libraryapi.model.Livro;
import io.github.cursodsousa.libraryapi.service.AutorService;
import io.github.cursodsousa.libraryapi.service.LivroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LivroControllerTest {

    MockMvc mockMvc;
    ObjectMapper mapper;

    @Mock
    LivroService service;

    @Mock
    AutorService autorService;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(mapper);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new LivroController(service, autorService, new LivroMapperImpl()))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setValidator(validator)
                .setMessageConverters(converter)
                .build();
    }

    @Test
    void devePesquisarLivrosPaginadoComFiltrosOpcionais() throws Exception {
        when(service.pesquisa(
                isNull(), isNull(), eq("Tolkien"), eq(GeneroLivro.FANTASIA), isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(livro()), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/livros")
                        .param("genero", "FANTASIA")
                        .param("nome-autor", "Tolkien")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].titulo").value("O Senhor dos Anéis"))
                .andExpect(jsonPath("$.content[0].genero").value("FANTASIA"))
                .andExpect(jsonPath("$.content[0].autor.nome").value("J.R.R. Tolkien"));
    }

    @Test
    void deveCadastrarLivro() throws Exception {
        UUID idAutor = UUID.fromString("2449f4e4-ee1a-4a71-8aa3-e9d46306fe8a");
        when(autorService.obterPorId(idAutor)).thenReturn(Optional.of(livro().getAutor()));
        when(service.salvar(any(Livro.class))).thenAnswer(invocation -> {
            Livro livro = invocation.getArgument(0);
            livro.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
            return livro;
        });

        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto(idAutor))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        "http://localhost/livros/11111111-1111-1111-1111-111111111111"));
    }

    @Test
    void deveRetornar400QuandoCamposObrigatoriosFaltarem() throws Exception {
        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar409QuandoIsbnDuplicado() throws Exception {
        UUID idAutor = UUID.fromString("2449f4e4-ee1a-4a71-8aa3-e9d46306fe8a");
        when(autorService.obterPorId(idAutor)).thenReturn(Optional.of(livro().getAutor()));
        when(service.salvar(any(Livro.class)))
                .thenThrow(new RegistroDuplicadoException("ISBN Duplicado"));

        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto(idAutor))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensagem").value("ISBN Duplicado"))
                .andExpect(jsonPath("$.erros").isEmpty());
    }

    @Test
    void deveRetornar422QuandoPrecoObrigatorioAPartirDe2020() throws Exception {
        UUID idAutor = UUID.fromString("2449f4e4-ee1a-4a71-8aa3-e9d46306fe8a");
        when(autorService.obterPorId(idAutor)).thenReturn(Optional.of(livro().getAutor()));
        when(service.salvar(any(Livro.class)))
                .thenThrow(new CampoInvalidoException(
                        "preco", "Para livros a partir de 2020, preço é obrigatório"));

        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto(idAutor))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.mensagem").value("Erro de Validação"))
                .andExpect(jsonPath("$.erros[0].campo").value("preco"));
    }

    @Test
    void deveRetornar409QuandoAtualizarParaIsbnDeOutroLivro() throws Exception {
        UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID idAutor = UUID.fromString("2449f4e4-ee1a-4a71-8aa3-e9d46306fe8a");
        when(service.obterPorId(id)).thenReturn(Optional.of(livro()));
        when(autorService.obterPorId(idAutor)).thenReturn(Optional.of(livro().getAutor()));
        doThrow(new RegistroDuplicadoException("ISBN Duplicado"))
                .when(service).atualizar(any(Livro.class));

        mockMvc.perform(put("/livros/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto(idAutor))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value("ISBN Duplicado"));
    }

    @Test
    void deveRetornarLivroQuandoIdExistir() throws Exception {
        UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
        when(service.obterPorId(id)).thenReturn(Optional.of(livro()));

        mockMvc.perform(get("/livros/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("O Senhor dos Anéis"));
    }

    @Test
    void deveRetornar404QuandoIdNaoExistir() throws Exception {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        when(service.obterPorId(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/livros/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAtualizarLivro() throws Exception {
        UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID idAutor = UUID.fromString("2449f4e4-ee1a-4a71-8aa3-e9d46306fe8a");
        when(service.obterPorId(id)).thenReturn(Optional.of(livro()));
        when(autorService.obterPorId(idAutor)).thenReturn(Optional.of(livro().getAutor()));

        mockMvc.perform(put("/livros/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto(idAutor))))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveExcluirLivro() throws Exception {
        UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
        when(service.obterPorId(id)).thenReturn(Optional.of(livro()));

        mockMvc.perform(delete("/livros/{id}", id))
                .andExpect(status().isNoContent());
    }

    private CadastroLivroDTO dto(UUID idAutor) {
        return new CadastroLivroDTO(
                "978-0-00",
                "O Senhor dos Anéis",
                LocalDate.of(1954, 7, 29),
                GeneroLivro.FANTASIA,
                BigDecimal.valueOf(99.90),
                idAutor);
    }

    private Livro livro() {
        Autor autor = new Autor();
        autor.setId(UUID.fromString("2449f4e4-ee1a-4a71-8aa3-e9d46306fe8a"));
        autor.setNome("J.R.R. Tolkien");
        autor.setDataNascimento(LocalDate.of(1892, 1, 3));
        autor.setNacionalidade("Britânica");

        Livro livro = new Livro();
        livro.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        livro.setIsbn("978-0-00");
        livro.setTitulo("O Senhor dos Anéis");
        livro.setDataPublicacao(LocalDate.of(1954, 7, 29));
        livro.setGenero(GeneroLivro.FANTASIA);
        livro.setPreco(BigDecimal.valueOf(99.90));
        livro.setAutor(autor);
        return livro;
    }
}
