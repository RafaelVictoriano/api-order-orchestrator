package com.br.apiorderorchestrator.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus statusCode;

    private final String message;

}
