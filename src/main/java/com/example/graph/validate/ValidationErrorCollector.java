package com.example.graph.validate;

import com.example.graph.web.problem.ProblemFieldError;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationErrorCollector {
    private final List<ProblemFieldError> errors = new ArrayList<>();

    public void add(String field, String message) {
        errors.add(new ProblemFieldError(field, message));
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<ProblemFieldError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public void throwIfAny() {
        if (hasErrors()) {
            throw new ValidationException("Validation error", errors);
        }
    }
}
