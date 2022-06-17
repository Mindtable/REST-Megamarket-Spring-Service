package ru.itmo.yandex.backend.part2.spring.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ValidationException;

@ControllerAdvice
public class MegaMarketExceptionHandler extends ResponseEntityExceptionHandler {


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        return new ResponseEntity<>(
                new Error(400, "Validation failed"),
                headers,
                status
        );
    }

    @ExceptionHandler(NoSuchShopUnitException.class)
    public ResponseEntity<?> handleNoSuchShopUnitException(
            NoSuchShopUnitException e,
            WebRequest request) {

        return new ResponseEntity<>(
                new Error(404, "Item not found"),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, ValidationException.class})
    public ResponseEntity<?> handleValidationFailure(
            RuntimeException e,
            WebRequest request
    ) {
        return new ResponseEntity<>(
                new Error(400, "Validation failed"),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(UndefinedBehaviorException.class)
    public ResponseEntity<?> handleUndefinedBehaviour(
            UndefinedBehaviorException e,
            WebRequest request
    ) {
        return new ResponseEntity<>(
                new Error(502, e.getMessage()),
                HttpStatus.BAD_GATEWAY
        );
    }
}
