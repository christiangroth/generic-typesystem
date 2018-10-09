package de.chrgroth.generictypesystem.validation.impl;

import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.model.GenericUnits;
import de.chrgroth.generictypesystem.validation.ValidationResult;
import de.chrgroth.generictypesystem.validation.ValidationService;

/**
 * A default implementation to bypass all validations.
 *
 * @author Christian Groth
 */
public class NoValidationService implements ValidationService {

    @Override
    public ValidationResult<GenericUnits> validate(GenericUnits units) {
        return new ValidationResult<GenericUnits>(units);
    }

    @Override
    public ValidationResult<GenericType> validate(GenericType type) {
        return new ValidationResult<GenericType>(type);
    }

    @Override
    public ValidationResult<GenericItem> validate(GenericType type, GenericItem item) {
        return new ValidationResult<GenericItem>(item);
    }
}
