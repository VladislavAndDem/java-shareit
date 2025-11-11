package ru.practicum.shareit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice()
public class ErrorHandler {
    @ExceptionHandler({MethodArgumentNotValidException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse validationExceptionHandle(Exception e) {
        return new ExceptionResponse("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler({Throwable.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse internalServerExceptionHandle(Exception e) {
        return new ExceptionResponse("Ошибка сервера", e.getMessage());
    }

    @Getter
    @AllArgsConstructor
    public static class ExceptionResponse {
        String error;
        String description;
    }
}