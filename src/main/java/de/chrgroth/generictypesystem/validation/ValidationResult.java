package de.chrgroth.generictypesystem.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {

    private final Object item;
    private final List<ValidationError> errors;

    public ValidationResult(Object item) {
        this.item = item;
        errors = new ArrayList<>();
    }

    public void error(String path, String error) {
        errors.add(new ValidationError(path, error));
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public Object getItem() {
        return item;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "GenericItemValidationResult [item=" + item + ", errors=" + errors + "]";
    }
}
