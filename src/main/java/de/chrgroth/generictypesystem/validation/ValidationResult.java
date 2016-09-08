package de.chrgroth.generictypesystem.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all validation violations computed by {@link ValidationService}.
 *
 * @author Christian Groth
 */
public class ValidationResult<T> {

    private final T item;
    private final List<ValidationError> errors;

    /**
     * Creates a new empty validation result for the given object.
     *
     * @param item
     *            object this result is created for
     */
    public ValidationResult(T item) {
        this.item = item;
        errors = new ArrayList<>();
    }

    /**
     * Adds the given error for given path.
     *
     * @param path
     *            message path in object
     * @param messageKey
     *            message key
     * @param messageParameters
     *            message parameters
     */
    public void error(String path, ValidationMessageKey messageKey, Object... messageParameters) {
        errors.add(new ValidationError(path, messageKey, messageParameters));
    }

    /**
     * The result is valid if no errors are contained.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    /**
     * Returns the reference to the validated item.
     *
     * @return validated item
     */
    public T getItem() {
        return item;
    }

    /**
     * Returns all errors.
     *
     * @return
     */
    public List<ValidationError> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "GenericItemValidationResult [item=" + item + ", errors=" + errors + "]";
    }
}
