package io.github.cursodsousa.libraryapi.service;

import io.github.cursodsousa.libraryapi.controller.dto.AutorDTO;
import io.github.cursodsousa.libraryapi.exceptions.OperacaoNaoPermitidaException;
import io.github.cursodsousa.libraryapi.exceptions.RegistroDuplicadoException;
import io.github.cursodsousa.libraryapi.model.Autor;
import io.github.cursodsousa.libraryapi.repository.AutorRepository;
import io.github.cursodsousa.libraryapi.repository.LivroRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AutorService {

    private final AutorRepository repository;
    private final LivroRepository livroRepository;

    public AutorService(AutorRepository repository, LivroRepository livroRepository){
        this.repository = repository;
        this.livroRepository = livroRepository;
    }

    public Autor salvar(Autor autor){
        if (existeAutorCadastrado(autor)) {
            throw new RegistroDuplicadoException("Autor já cadastrado.");
        }
        return repository.save(autor);
    }

    public Optional<Autor> obterPorId(UUID id){
        return repository.findById(id);
    }

    public void atualizar(Autor autor){
        if (existeAutorCadastrado(autor)) {
            throw new RegistroDuplicadoException("Autor já cadastrado.");
        }
        repository.save(autor);
    }

    public List<Autor> pesquisa(String nome, String nacionalidade){
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

    public void deletar(Autor autor){
        if (possuiLivro(autor)) {
            throw new OperacaoNaoPermitidaException("Não é permitido excluir um Autor que possui livros.");
        }
        repository.delete(autor);
    }

    public boolean possuiLivro(Autor autor) {
        if (autor.getLivros() != null && !autor.getLivros().isEmpty()) {
            return true;
        }
        return livroRepository.existsByAutor(autor);
    }

    public Optional<AutorDTO> obterDetalhes(UUID id){
        return repository.findById(id).map(autor ->
                new AutorDTO(
                        autor.getId(),
                        autor.getNome(),
                        autor.getDataNascimento(),
                        autor.getNacionalidade()
                )
        );
    }

    private boolean existeAutorCadastrado(Autor autor) {
        Optional<Autor> autorEncontrado = repository.findByNomeAndDataNascimentoAndNacionalidade(
                autor.getNome(), autor.getDataNascimento(), autor.getNacionalidade());
        if (autor.getId() != null) {
            return autorEncontrado.isPresent() && !autor.getId().equals(autorEncontrado.get().getId());
        }
        return autorEncontrado.isPresent();
    }
}
