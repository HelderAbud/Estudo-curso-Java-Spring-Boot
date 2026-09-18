package io.github.cursodsousa.libraryapi.service;

import io.github.cursodsousa.libraryapi.controller.dto.AutorDTO;
import io.github.cursodsousa.libraryapi.exceptions.OperacaoNaoPermitidaException;
import io.github.cursodsousa.libraryapi.model.Autor;
import io.github.cursodsousa.libraryapi.repository.AutorRepository;
import io.github.cursodsousa.libraryapi.repository.LivroRepository;
import io.github.cursodsousa.libraryapi.validator.AutorValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AutorService {

    private final AutorRepository repository;
    private final AutorValidator validator;
    private final LivroRepository livroRepository;

    public Autor salvar(Autor autor) {
        validator.validar(autor);
        return repository.save(autor);
    }

    public Optional<Autor> obterPorId(UUID id) {
        return repository.findById(id);
    }

    public void atualizar(Autor autor) {
        validator.validar(autor);
        repository.save(autor);
    }

    public List<Autor> pesquisa(String nome, String nacionalidade) {
        if (nome != null && nacionalidade != null) {
            return repository.findByNomeAndNacionalidade(nome, nacionalidade);
        }
        if (nome != null) {
            return repository.findByNome(nome);
        }
        if (nacionalidade != null) {
            return repository.findByNacionalidade(nacionalidade);
        }
        return repository.findAll();
    }

    public void deletar(Autor autor) {
        if (possuiLivro(autor)) {
            throw new OperacaoNaoPermitidaException("Não é permitido excluir um Autor que possui livros.");
        }
        repository.delete(autor);
    }

    public boolean possuiLivro(Autor autor) {
        return livroRepository.existsByAutor(autor);
    }

    public Optional<AutorDTO> obterDetalhes(UUID id) {
        return repository.findById(id).map(autor ->
                new AutorDTO(
                        autor.getId(),
                        autor.getNome(),
                        autor.getDataNascimento(),
                        autor.getNacionalidade()
                )
        );
    }

}
