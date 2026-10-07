package io.github.cursodsousa.libraryapi.controller;

import io.github.cursodsousa.libraryapi.controller.dto.CadastroLivroDTO;
import io.github.cursodsousa.libraryapi.controller.dto.ResultadoPesquisaLivroDTO;
import io.github.cursodsousa.libraryapi.controller.mappers.LivroMapper;
import io.github.cursodsousa.libraryapi.model.Autor;
import io.github.cursodsousa.libraryapi.model.GeneroLivro;
import io.github.cursodsousa.libraryapi.model.Livro;
import io.github.cursodsousa.libraryapi.service.AutorService;
import io.github.cursodsousa.libraryapi.service.LivroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/livros")
@RequiredArgsConstructor
public class LivroController implements GenericController {

    private final LivroService service;
    private final AutorService autorService;
    private final LivroMapper mapper;

    @PostMapping
    public ResponseEntity<Void> salvar(@RequestBody @Valid CadastroLivroDTO dto) {
        Optional<Autor> autorOptional = autorService.obterPorId(dto.idAutor());
        if (autorOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Livro livro = mapper.toEntity(dto);
        livro.setAutor(autorOptional.get());
        service.salvar(livro);

        URI location = gerarHeaderLocation(livro.getId());

        return ResponseEntity.created(location).build();
    }

    @GetMapping("{id}")
    public ResponseEntity<ResultadoPesquisaLivroDTO> obterDetalhes(@PathVariable("id") String id) {
        var idLivro = UUID.fromString(id);
        return service.obterPorId(idLivro)
                .map(livro -> ResponseEntity.ok(mapper.toResultadoDTO(livro)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<ResultadoPesquisaLivroDTO>> pesquisa(
            @RequestParam(value = "isbn", required = false) String isbn,
            @RequestParam(value = "titulo", required = false) String titulo,
            @RequestParam(value = "nome-autor", required = false) String nomeAutor,
            @RequestParam(value = "genero", required = false) GeneroLivro genero,
            @RequestParam(value = "ano-publicacao", required = false) Integer anoPublicacao,
            @PageableDefault(size = 10) Pageable page) {
        Page<Livro> pagina = service.pesquisa(isbn, titulo, nomeAutor, genero, anoPublicacao, page);
        return ResponseEntity.ok(pagina.map(mapper::toResultadoDTO));
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> atualizar(
            @PathVariable("id") String id,
            @RequestBody @Valid CadastroLivroDTO dto) {
        var idLivro = UUID.fromString(id);
        Optional<Livro> livroOptional = service.obterPorId(idLivro);
        if (livroOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Autor> autorOptional = autorService.obterPorId(dto.idAutor());
        if (autorOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Livro livro = livroOptional.get();
        livro.setIsbn(dto.isbn());
        livro.setTitulo(dto.titulo());
        livro.setDataPublicacao(dto.dataPublicacao());
        livro.setGenero(dto.genero());
        livro.setPreco(dto.preco());
        livro.setAutor(autorOptional.get());
        service.atualizar(livro);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletar(@PathVariable("id") String id) {
        var idLivro = UUID.fromString(id);
        Optional<Livro> livroOptional = service.obterPorId(idLivro);
        if (livroOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.deletar(livroOptional.get());
        return ResponseEntity.noContent().build();
    }
}
