package io.github.cursodsousa.libraryapi.validator;

import io.github.cursodsousa.libraryapi.exceptions.CampoInvalidoException;
import io.github.cursodsousa.libraryapi.exceptions.RegistroDuplicadoException;
import io.github.cursodsousa.libraryapi.model.Livro;
import io.github.cursodsousa.libraryapi.repository.LivroRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class LivroValidator {

    private static final int ANO_EXIGENCIA_PRECO = 2020;

    private final LivroRepository repository;

    public LivroValidator(LivroRepository repository) {
        this.repository = repository;
    }

    public void validar(Livro livro) {
        if (existeLivroComIsbn(livro)) {
            throw new RegistroDuplicadoException("ISBN Duplicado");
        }
        if (isDataPublicacaoFutura(livro)) {
            throw new CampoInvalidoException("dataPublicacao", "Data não pode ser futura");
        }
        if (isPrecoObrigatorioNulo(livro)) {
            throw new CampoInvalidoException(
                    "preco", "Para livros a partir de 2020, preço é obrigatório");
        }
    }

    private boolean existeLivroComIsbn(Livro livro) {
        List<Livro> livrosEncontrados = repository.findByIsbn(livro.getIsbn());
        if (livrosEncontrados.isEmpty()) {
            return false;
        }
        if (livro.getId() == null) {
            return true;
        }
        return livrosEncontrados.stream()
                .anyMatch(cadastrado -> !cadastrado.getId().equals(livro.getId()));
    }

    private boolean isDataPublicacaoFutura(Livro livro) {
        return livro.getDataPublicacao() != null
                && livro.getDataPublicacao().isAfter(LocalDate.now());
    }

    private boolean isPrecoObrigatorioNulo(Livro livro) {
        return livro.getDataPublicacao() != null
                && livro.getDataPublicacao().getYear() >= ANO_EXIGENCIA_PRECO
                && livro.getPreco() == null;
    }
}
