package com.house.hunter.advice;

import com.house.hunter.exception.InvalidRefreshTokenException;
import com.house.hunter.exception.UserAlreadyExistsException;
import com.house.hunter.model.dto.error.ErrorDto;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.web.bind.annotation.ControllerAdvice
public final class ControllerAdvice {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDto> handleValidationException(ConstraintViolationException ex) {
        List<String> errorMessages = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        final ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST.value(), "Validation failed", errorMessages);
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .collect(Collectors.toList());
        final ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST.value(), "Validation failed", errorMessages);
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorDto> handleValidationException(UserAlreadyExistsException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.CONFLICT.value(), "User already exists", List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorDto> handleValidationException(InvalidRefreshTokenException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDto> handleValidationException(BadCredentialsException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.UNAUTHORIZED.value(), "Invalid username or password", List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorDto> handleValidationException(ExpiredJwtException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST.value(), "JWT is expired", List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ErrorDto> handleValidationException(InternalAuthenticationServiceException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }


}
