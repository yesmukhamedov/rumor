package com.example.graph.controller;

import com.example.graph.validate.ValidationException;
import com.example.graph.web.problem.ProblemDetails;
import com.example.graph.web.problem.ProblemFieldError;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PublicApiExceptionHandler {
    private static final MediaType PROBLEM_MEDIA_TYPE = MediaType.valueOf("application/problem+json");

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ProblemDetails> handleValidation(ValidationException ex, HttpServletRequest request) {
        ProblemDetails body = new ProblemDetails(
            "https://example.org/problems/validation",
            "Validation error",
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            request.getRequestURI(),
            ex.getErrors().isEmpty() ? null : ex.getErrors()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(PROBLEM_MEDIA_TYPE).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetails> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                       HttpServletRequest request) {
        List<ProblemFieldError> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(this::toProblemFieldError)
            .toList();
        ProblemDetails body = new ProblemDetails(
            "https://example.org/problems/validation",
            "Validation error",
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed.",
            request.getRequestURI(),
            errors.isEmpty() ? null : errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(PROBLEM_MEDIA_TYPE).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetails> handleConflict(DataIntegrityViolationException ex,
                                                         HttpServletRequest request) {
        ProblemDetails body = new ProblemDetails(
            "https://example.org/problems/conflict",
            "Conflict",
            HttpStatus.CONFLICT.value(),
            "Duplicate or conflicting data.",
            request.getRequestURI(),
            null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).contentType(PROBLEM_MEDIA_TYPE).body(body);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetails> handleRuntime(RuntimeException ex, HttpServletRequest request) {
        ProblemDetails body = new ProblemDetails(
            "https://example.org/problems/internal",
            "Internal Server Error",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred.",
            request.getRequestURI(),
            null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(PROBLEM_MEDIA_TYPE).body(body);
    }

    private ProblemFieldError toProblemFieldError(FieldError error) {
        String field = error.getField();
        String message = error.getDefaultMessage() == null ? "Invalid value." : error.getDefaultMessage();
        return new ProblemFieldError(field, message);
    }
}
