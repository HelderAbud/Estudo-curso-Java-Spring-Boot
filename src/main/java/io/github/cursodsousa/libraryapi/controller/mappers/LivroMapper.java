package io.github.cursodsousa.libraryapi.controller.mappers;

import io.github.cursodsousa.libraryapi.controller.dto.CadastroLivroDTO;
import io.github.cursodsousa.libraryapi.controller.dto.ResultadoPesquisaLivroDTO;
import io.github.cursodsousa.libraryapi.model.Autor;
import io.github.cursodsousa.libraryapi.model.Livro;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface LivroMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "autor", source = "idAutor")
    Livro toEntity(CadastroLivroDTO dto);

    ResultadoPesquisaLivroDTO toResultadoDTO(Livro livro);

    default Autor toAutor(UUID id) {
        if (id == null) {
            return null;
        }
        Autor autor = new Autor();
        autor.setId(id);
        return autor;
    }
}
