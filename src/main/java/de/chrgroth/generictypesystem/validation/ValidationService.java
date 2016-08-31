package de.chrgroth.generictypesystem.validation;

import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;

public interface ValidationService {

    ValidationResult validate(GenericType type);

    ValidationResult validate(GenericType type, GenericItem item);
}
