package com.example.graph.validate;

import com.example.graph.web.problem.ProblemFieldError;
import java.util.Collections;
import java.util.List;

public class ValidationException extends RuntimeException {
    private final List<ProblemFieldError> errors;

    public ValidationException(String message) {
        this(message, List.of());
    }

    public ValidationException(String message, List<ProblemFieldError> errors) {
        super(message);
        this.errors = errors == null ? List.of() : Collections.unmodifiableList(errors);
    }

    public List<ProblemFieldError> getErrors() {
        return errors;
    }
}
