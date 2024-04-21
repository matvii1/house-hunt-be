package com.house.hunter.advice;

import com.house.hunter.exception.UserAlreadyExistsException;
import com.house.hunter.model.dto.error.ValidationErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public final class UserControllerAdvice {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorDto> handleValidationException(ConstraintViolationException ex) {
        List<String> errorMessages = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        final ValidationErrorDto error = new ValidationErrorDto(HttpStatus.BAD_REQUEST.value(), "Validation failed", errorMessages);
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .collect(Collectors.toList());
        final ValidationErrorDto error = new ValidationErrorDto(HttpStatus.BAD_REQUEST.value(), "Validation failed", errorMessages);
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ValidationErrorDto> handleValidationException(UserAlreadyExistsException ex) {
        final ValidationErrorDto error = new ValidationErrorDto(HttpStatus.CONFLICT.value(), "User already exists", List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }
}
