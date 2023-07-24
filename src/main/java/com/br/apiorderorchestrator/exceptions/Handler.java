package com.br.apiorderorchestrator.exceptions;


import com.br.apiorderorchestrator.dto.ResponseErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class Handler {

    @Autowired
    private MessageSource messageSource;


    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<ResponseErrorDTO> handlingException(MethodArgumentNotValidException exception) {
        final var fieldsError = exception.getBindingResult().getFieldErrors();

        return fieldsError.stream().map(fieldError -> {
            var message = this.messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
            log.error("Error {}", message);
            return ResponseErrorDTO.builder()
                    .code(BAD_REQUEST)
                    .message(fieldError.getField() + " " + message)
                    .build();
        }).collect(Collectors.toList());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ResponseErrorDTO> handlingResponseStatusException(ResponseStatusException exception) {
        log.error("Error {}", exception.getReason());
        return ResponseEntity
                .status(exception.getStatusCode())
                .body(ResponseErrorDTO.builder()
                        .code(exception.getStatusCode())
                        .message(exception.getMessage())
                        .build());
    }


    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseErrorDTO> handlingExceptionGlobal(BusinessException exception) {
        log.error("Error {}", exception.getMessage());
        return ResponseEntity.status(exception.getStatusCode())
                .body(ResponseErrorDTO.builder()
                .code(exception.getStatusCode())
                .message(exception.getMessage())
                .build());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseErrorDTO handlingExceptionGlobal(Exception exception) {
        log.error("Error {}", exception.getMessage());
        return ResponseErrorDTO.builder()
                .code(INTERNAL_SERVER_ERROR)
                .message(exception.getMessage())
                .build();
    }
}
