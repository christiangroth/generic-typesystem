package de.chrgroth.generictypesystem.validation;

import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;

public class NullValidationService implements ValidationService {

    @Override
    public ValidationResult<GenericType> validate(GenericType type) {
        return new ValidationResult<GenericType>(type);
    }

    @Override
    public ValidationResult<GenericItem> validate(GenericType type, GenericItem item) {
        return new ValidationResult<GenericItem>(item);
    }
}
