package de.chrgroth.generictypesystem.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {

    // TODO external class
    public static class Error {
        private final String path;
        private final String message;

        public Error(String path, String message) {
            this.path = path;
            this.message = message;
        }

        public String getPath() {
            return path;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return "Error [path=" + path + ", message=" + message + "]";
        }
    }

    private final Object item;
    private final List<Error> errors;

    public ValidationResult(Object item) {
        this.item = item;
        errors = new ArrayList<>();
    }

    public void error(String path, String error) {
        errors.add(new Error(path, error));
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public Object getItem() {
        return item;
    }

    public List<Error> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "GenericItemValidationResult [item=" + item + ", errors=" + errors + "]";
    }
}
