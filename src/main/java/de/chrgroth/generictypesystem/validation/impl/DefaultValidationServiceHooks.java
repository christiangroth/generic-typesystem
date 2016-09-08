package de.chrgroth.generictypesystem.validation.impl;

import java.util.Collection;

import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericStructure;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.validation.ValidationResult;

/**
 * Defines hooks to be used to enhance {@link DefaultValidationService}.
 *
 * @author Christian Groth
 */
public interface DefaultValidationServiceHooks {

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
     * Called to validate the given item.
     *
     * @param result
     *            the result to be updated
     * @param item
     *            item to be validated
     */
    void itemValidation(ValidationResult<GenericItem> result, GenericItem item);

    /**
     * Called to validate the given attribute definition against given item. There are specific callbacks for attribute values you may also use.
     *
     * @param result
     *            the result to be updated
     * @param item
     *            item to be validated
     * @param attribute
     *            attribute to be validated
     */
    void itemAttributeValidation(ValidationResult<GenericItem> result, GenericItem item, GenericAttribute attribute);

    /**
     * Called to validate the given list value for given attribute and item.
     *
     * @param result
     *            the result to be updated
     * @param item
     *            item the value belongs to
     * @param attribute
     *            attribute definition the value is based on
     * @param value
     *            value to be validated
     */
    void itemListAttributeValueValidation(ValidationResult<GenericItem> result, GenericItem item, GenericAttribute attribute, Collection<?> value);

    /**
     * Called to validate the given value for given attribute and item.
     *
     * @param result
     *            the result to be updated
     * @param item
     *            item the value belongs to
     * @param attribute
     *            attribute definition the value is based on
     * @param value
     *            value to be validated
     */
    void itemSimpleAttributeValueValidation(ValidationResult<GenericItem> result, GenericItem item, GenericAttribute attribute, Object value);
}
