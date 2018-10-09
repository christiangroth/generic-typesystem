package de.chrgroth.generictypesystem.validation;

import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.model.GenericUnits;

/**
 * Common validation service interface for instances of {@link GenericType} and {@link GenericItem}. A {@link GenericItem} can only be validated in combination
 * with its {@link GenericType}.
 *
 * @author Christian Groth
 */
public interface ValidationService {

    /**
     * Validates the given {@link GenericUnits} and returns the {@link ValidationResult}.
     *
     * @param units
     *            the units to be validated
     * @return validation results
     */
    ValidationResult<GenericUnits> validate(GenericUnits units);

    /**
     * Validates the given {@link GenericType} and returns the {@link ValidationResult}.
     *
     * @param type
     *            the type to be validated
     * @return validation results
     */
    ValidationResult<GenericType> validate(GenericType type);

    /**
     * Validates the given {@link GenericItem} with belonging {@link GenericType} and returns the {@link ValidationResult}. The item can only be validated if it
     * belongs to the given type and the type itself can be validated successfully.
     *
     * @param type
     *            the type belonging to given item
     * @param item
     *            the item to be validates
     * @return validation results
     */
    ValidationResult<GenericItem> validate(GenericType type, GenericItem item);
}
