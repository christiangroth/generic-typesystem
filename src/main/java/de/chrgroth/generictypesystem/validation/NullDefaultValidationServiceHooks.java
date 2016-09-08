package de.chrgroth.generictypesystem.validation;

import java.util.Collection;

import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericStructure;
import de.chrgroth.generictypesystem.model.GenericType;

/**
 * Default and empty hooks implementation for {@link DefaultValidationService}.
 *
 * @author Christian Groth
 */
public class NullDefaultValidationServiceHooks implements DefaultValidationServiceHooks {

    @Override
    public void typeValidation(ValidationResult<GenericType> result, GenericType type) {

        // empty implementation
    }

    @Override
    public void structureValidation(ValidationResult<GenericType> result, GenericStructure structure, String path) {

        // empty implementation
    }

    @Override
    public void typeAttributeValidation(ValidationResult<GenericType> result, GenericAttribute attribute, String path) {

        // empty implementation
    }

    @Override
    public void typeListAttributeValidation(ValidationResult<GenericType> result, GenericAttribute attribute, String path) {

        // empty implementation
    }

    @Override
    public void typeStructureAttributeValidation(ValidationResult<GenericType> result, GenericAttribute attribute, String path) {

        // empty implementation
    }

    @Override
    public void typeSimpleAttributeValidation(ValidationResult<GenericType> result, GenericAttribute attribute, String path) {

        // empty implementation
    }

    @Override
    public void itemValidation(ValidationResult<GenericItem> result, GenericItem item) {

        // empty implementation
    }

    @Override
    public void itemAttributeValidation(ValidationResult<GenericItem> result, GenericItem item, GenericAttribute attribute) {

        // empty implementation
    }

    @Override
    public void itemListAttributeValueValidation(ValidationResult<GenericItem> result, GenericItem item, GenericAttribute attribute, Collection<?> value) {

        // empty implementation
    }

    @Override
    public void itemSimpleAttributeValueValidation(ValidationResult<GenericItem> result, GenericItem item, GenericAttribute attribute, Object value) {

        // empty implementation
    }
}
