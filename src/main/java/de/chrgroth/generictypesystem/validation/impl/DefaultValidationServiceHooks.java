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
 * Defines hooks to be used to enhance {@link DefaultValidationService}.
 *
 * @author Christian Groth
 */
public interface DefaultValidationServiceHooks {

    /**
     * Called to validate the given units.
     *
     * @param result
     *            the result to be updated
     * @param units
     *            the units to be validated
     */
    void unitsValidation(ValidationResult<GenericUnits> result, GenericUnits units);

    /**
     * Called to validate the given unit for given units.
     *
     * @param result
     *            the result to be updated
     * @param units
     *            units to unit belongs to
     * @param unit
     *            the unit to be validated
     */
    void unitsUnitValidation(ValidationResult<GenericUnits> result, GenericUnits units, GenericUnit unit);

    /**
     * Called to validate the given type.
     *
     * @param result
     *            the result to be updated
     * @param type
     *            the type to be validated
     */
    void typeValidation(ValidationResult<GenericType> result, GenericType type);

    /**
     * Called to validate the given structure with its attributes. This method is called for a type itself and for all nested structures contained in any
     * attribute, i.e. for all attributes returning true for {@link GenericAttribute#isStructure()}.
     *
     * @param result
     *            the result to be updated
     * @param structure
     *            the structure to be validated
     * @param path
     *            the path the given structure is located at
     */
    void structureValidation(ValidationResult<GenericType> result, GenericStructure structure, String path);

    /**
     * Called to validate an arbitrary type attribute. If you would like to validate special cases for list, structure or all other attributes see
     * {@link #typeListAttributeValidation(ValidationResult, GenericAttribute, String)},
     * {@link #typeStructureAttributeValidation(ValidationResult, GenericAttribute, String)} and
     * {@link #typeSimpleAttributeValidation(ValidationResult, GenericAttribute, String)}.
     *
     * @param result
     *            the result to be updated
     * @param attribute
     *            the attribute to be validated
     * @param path
     *            the path the given attribute is located at
     */
    void typeAttributeValidation(ValidationResult<GenericType> result, GenericAttribute attribute, String path);

    /**
     * Called to validate a type attribute returning true for {@link GenericAttribute#isList()}.
     *
     * @param result
     *            the result to be updated
     * @param attribute
     *            the attribute to be validated
     * @param path
     *            the path the given attribute is located at
     */
    void typeListAttributeValidation(ValidationResult<GenericType> result, GenericAttribute attribute, String path);

    /**
     * Called to validate a type attribute returning true for {@link GenericAttribute#isStructure()} and false for {@link GenericAttribute#isList()}.
     *
     * @param result
     *            the result to be updated
     * @param attribute
     *            the attribute to be validated
     * @param path
     *            the path the given attribute is located at
     */
    void typeStructureAttributeValidation(ValidationResult<GenericType> result, GenericAttribute attribute, String path);

    /**
     * Called to validate a type attribute returning false for {@link GenericAttribute#isStructure()} and false for {@link GenericAttribute#isList()}.
     *
     * @param result
     *            the result to be updated
     * @param attribute
     *            the attribute to be validated
     * @param path
     *            the path the given attribute is located at
     */
    void typeSimpleAttributeValidation(ValidationResult<GenericType> result, GenericAttribute attribute, String path);

    /**
     * Called to validate the given item. During item validation this method is called once for root item.
     *
     * @param result
     *            the result to be updated
     * @param type
     *            type belonging to item
     * @param item
     *            item to be validated
     */
    void itemValidation(ValidationResult<GenericItem> result, GenericType type, GenericItem item);

    /**
     * Called to validate the current level of this item, belonging to given structure. During item validation this method is called for root item and every
     * nested item.
     *
     * @param result
     *            the result to be updated
     * @param structure
     *            structure belonging to item
     * @param item
     *            item to be validated
     * @param path
     *            the path the given item is located at
     */
    void itemLevelValidation(ValidationResult<GenericItem> result, GenericStructure structure, GenericItem item, String path);

    /**
     * Called to validate the given attribute definition against given item. There are specific callbacks for attribute values you may also use.
     *
     * @param result
     *            the result to be updated
     * @param structure
     *            structure belonging to item
     * @param attribute
     *            attribute to be validated
     * @param item
     *            item to be validated
     * @param path
     *            the path the given item is located at
     */
    void itemAttributeValidation(ValidationResult<GenericItem> result, GenericStructure structure, GenericAttribute attribute, GenericItem item, String path);

    /**
     * Called to validate the given list value for given attribute and item.
     *
     * @param result
     *            the result to be updated
     * @param structure
     *            structure belonging to item
     * @param attribute
     *            attribute definition the value is based on
     * @param item
     *            item the value belongs to
     * @param value
     *            value to be validated
     * @param path
     *            the path the given item is located at
     */
    void itemListAttributeValueValidation(ValidationResult<GenericItem> result, GenericStructure structure, GenericAttribute attribute, GenericItem item, Collection<?> value,
            String path);

    /**
     * Called to validate the given value for given attribute and item.
     *
     * @param result
     *            the result to be updated
     * @param structure
     *            structure belonging to item
     * @param attribute
     *            attribute definition the value is based on
     * @param item
     *            item the value belongs to
     * @param value
     *            value to be validated
     * @param path
     *            the path the given item is located at
     */
    void itemSimpleAttributeValueValidation(ValidationResult<GenericItem> result, GenericStructure structure, GenericAttribute attribute, GenericItem item, Object value,
            String path);
}
