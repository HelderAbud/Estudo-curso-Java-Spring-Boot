package io.github.cursodsousa.libraryapi.controller;

import io.github.cursodsousa.libraryapi.controller.common.GlobalExceptionHandler;
import io.github.cursodsousa.libraryapi.controller.dto.AutorDTO;
import io.github.cursodsousa.libraryapi.exceptions.OperacaoNaoPermitidaException;
import io.github.cursodsousa.libraryapi.exceptions.RegistroDuplicadoException;
import io.github.cursodsousa.libraryapi.model.Autor;
import io.github.cursodsousa.libraryapi.service.AutorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AutorControllerTest {

    MockMvc mockMvc;
    ObjectMapper mapper;

    @Mock
    AutorService service;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(mapper);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new AutorController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .setMessageConverters(converter)
                .build();
    }

    @Test
    void deveRetornarAutorQuandoIdExistir() throws Exception {
        UUID id = UUID.fromString("2449f4e4-ee1a-4a71-8aa3-e9d46306fe8a");
        when(service.obterDetalhes(id)).thenReturn(Optional.of(autorDTO(id)));

        mockMvc.perform(get("/autores/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value("José"))
                .andExpect(jsonPath("$.dataNascimento").value("1951-01-31"))
                .andExpect(jsonPath("$.nacionalidade").value("Brasileira"));
    }

    @Test
    void deveRetornar404QuandoIdNaoExistir() throws Exception {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        when(service.obterDetalhes(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/autores/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void devePesquisarAutoresPorNome() throws Exception {
        UUID id = UUID.fromString("2449f4e4-ee1a-4a71-8aa3-e9d46306fe8a");
        when(service.pesquisa("José", null)).thenReturn(List.of(autor(id)));

        mockMvc.perform(get("/autores").param("nome", "José"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("José"));
    }

    @Test
    void deveAtualizarAutor() throws Exception {
        UUID id = UUID.fromString("2449f4e4-ee1a-4a71-8aa3-e9d46306fe8a");
        when(service.obterPorId(id)).thenReturn(Optional.of(autor(id)));

        mockMvc.perform(put("/autores/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto())))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar409QuandoAutorDuplicado() throws Exception {
        doThrow(new RegistroDuplicadoException("Autor já cadastrado."))
                .when(service).salvar(any());

        mockMvc.perform(post("/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto())))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRetornar400QuandoExcluirAutorComLivro() throws Exception {
        UUID id = UUID.fromString("2449f4e4-ee1a-4a71-8aa3-e9d46306fe8a");
        Autor autor = autor(id);
        when(service.obterPorId(id)).thenReturn(Optional.of(autor));
        doThrow(new OperacaoNaoPermitidaException("Não é permitido excluir um Autor que possui livros."))
                .when(service).deletar(autor);

        mockMvc.perform(delete("/autores/{id}", id))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400QuandoCamposObrigatoriosFaltarem() throws Exception {
        mockMvc.perform(post("/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    private Autor autor(UUID id) {
        Autor autor = new Autor();
        autor.setId(id);
        autor.setNome("José");
        autor.setDataNascimento(LocalDate.of(1951, 1, 31));
        autor.setNacionalidade("Brasileira");
        return autor;
    }

    private AutorDTO autorDTO(UUID id) {
        return new AutorDTO(id, "José", LocalDate.of(1951, 1, 31), "Brasileira");
    }

    private record DtoBody(String nome, LocalDate dataNascimento, String nacionalidade) {}

    private DtoBody dto() {
        return new DtoBody("José", LocalDate.of(1951, 1, 31), "Brasileira");
    }
}
