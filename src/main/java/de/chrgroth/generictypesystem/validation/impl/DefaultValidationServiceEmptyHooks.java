package de.chrgroth.generictypesystem.validation.impl;

import java.util.Collection;

import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericStructure;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.model.GenericUnit;
import de.chrgroth.generictypesystem.model.GenericUnits;
import de.chrgroth.generictypesystem.validation.ValidationResult;

/**
 * Default and empty hooks implementation for {@link DefaultValidationService}.
 *
 * @author Christian Groth
 */
public class DefaultValidationServiceEmptyHooks implements DefaultValidationServiceHooks {

    @Override
    public void unitsValidation(ValidationResult<GenericUnits> result, GenericUnits units) {

        // empty implementation
    }

    @Override
    public void unitsUnitValidation(ValidationResult<GenericUnits> result, GenericUnits units, GenericUnit unit) {

        // empty implementation
    }

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
    public void itemValidation(ValidationResult<GenericItem> result, GenericType type, GenericItem item) {

        // empty implementation
    }

    @Override
    public void itemLevelValidation(ValidationResult<GenericItem> result, GenericStructure structure, GenericItem item, String path) {

        // empty implementation
    }

    @Override
    public void itemAttributeValidation(ValidationResult<GenericItem> result, GenericStructure structure, GenericAttribute attribute, GenericItem item, String path) {

        // empty implementation
    }

    @Override
    public void itemListAttributeValueValidation(ValidationResult<GenericItem> result, GenericStructure structure, GenericAttribute attribute, GenericItem item,
            Collection<?> value, String path) {

        // empty implementation
    }

    @Override
    public void itemSimpleAttributeValueValidation(ValidationResult<GenericItem> result, GenericStructure structure, GenericAttribute attribute, GenericItem item, Object value,
            String path) {

        // empty implementation
    }
}
