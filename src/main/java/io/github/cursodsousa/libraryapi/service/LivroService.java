package io.github.cursodsousa.libraryapi.service;

import io.github.cursodsousa.libraryapi.model.GeneroLivro;
import io.github.cursodsousa.libraryapi.model.Livro;
import io.github.cursodsousa.libraryapi.repository.LivroRepository;
import io.github.cursodsousa.libraryapi.validator.LivroValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;

import static io.github.cursodsousa.libraryapi.repository.specs.LivroSpecs.anoPublicacaoEqual;
import static io.github.cursodsousa.libraryapi.repository.specs.LivroSpecs.generoEqual;
import static io.github.cursodsousa.libraryapi.repository.specs.LivroSpecs.isbnEqual;
import static io.github.cursodsousa.libraryapi.repository.specs.LivroSpecs.nomeAutorLike;
import static io.github.cursodsousa.libraryapi.repository.specs.LivroSpecs.tituloLike;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository repository;
    private final LivroValidator validator;

    @Transactional
    public Livro salvar(Livro livro) {
        validator.validar(livro);
        return repository.save(livro);
    }

    public Optional<Livro> obterPorId(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public void atualizar(Livro livro) {
        validator.validar(livro);
        repository.save(livro);
    }

    public void deletar(Livro livro) {
        repository.delete(livro);
    }

    public Page<Livro> pesquisa(
            String isbn,
            String titulo,
            String nomeAutor,
            GeneroLivro genero,
            Integer anoPublicacao,
            Pageable pageRequest) {

        Specification<Livro> specs = Specification.where((root, query, cb) -> cb.conjunction());

        if (StringUtils.hasText(isbn)) {
            specs = specs.and(isbnEqual(isbn));
        }
        if (StringUtils.hasText(titulo)) {
            specs = specs.and(tituloLike(titulo));
        }
        if (genero != null) {
            specs = specs.and(generoEqual(genero));
        }
        if (StringUtils.hasText(nomeAutor)) {
            specs = specs.and(nomeAutorLike(nomeAutor));
        }
        if (anoPublicacao != null) {
            specs = specs.and(anoPublicacaoEqual(anoPublicacao));
        }

        return repository.findAll(specs, pageRequest);
    }
}
