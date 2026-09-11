package io.github.cursodsousa.libraryapi.controller.common;

import io.github.cursodsousa.libraryapi.controller.dto.ErroCampo;
import io.github.cursodsousa.libraryapi.controller.dto.ErroResposta;
import io.github.cursodsousa.libraryapi.exceptions.OperacaoNaoPermitidaException;
import io.github.cursodsousa.libraryapi.exceptions.RegistroDuplicadoException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResposta handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getFieldErrors();
        List<ErroCampo> listaErros = fieldErrors.stream()
                .map(fe -> new ErroCampo(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return new ErroResposta(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação.",
                listaErros);
    }

    @ExceptionHandler(RegistroDuplicadoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErroResposta handleRegistroDuplicadoException(RegistroDuplicadoException e) {
        return ErroResposta.conflito(e.getMessage());
    }

    @ExceptionHandler(OperacaoNaoPermitidaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResposta handleOperacaoNaoPermitidaException(OperacaoNaoPermitidaException e) {
        return ErroResposta.respostaPadrao(e.getMessage());
    }
}
