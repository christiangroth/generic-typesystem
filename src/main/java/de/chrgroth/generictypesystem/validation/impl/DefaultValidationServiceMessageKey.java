package de.chrgroth.generictypesystem.validation.impl;

import de.chrgroth.generictypesystem.validation.ValidationMessageKey;

/**
 * Represents an enumeration of all validation messages used by {@link DefaultValidationService}.
 *
 * @author Christian Groth
 */
public enum DefaultValidationServiceMessageKey implements ValidationMessageKey {

    GENERAL_TYPE_NOT_PROVIDED,
    GENERAL_STRUCTURE_NOT_PROVIDED,
    GENERAL_ITEM_NOT_PROVIDED,

    TYPE_NAME_MANDATORY,
    TYPE_GROUP_MANDATORY,
    TYPE_PAGE_SIZE_NEGATIVE,
    TYPE_AMBIGIOUS_ATTRIBUTE_ID,

    TYPE_ATTRIBUTE_ID_MANDATORY,
    TYPE_ATTRIBUTE_NAME_MANDATORY,
    TYPE_ATTRIBUTE_NAME_CONTAINS_DOT,
    TYPE_ATTRIBUTE_TYPE_MANDATORY,
    TYPE_ATTRIBUTE_UNIQUE_BUT_NOT_MANDATORY,

    TYPE_ATTRIBUTE_NOT_MIN_CAPABLE,
    TYPE_ATTRIBUTE_NOT_MAX_CAPABLE,
    TYPE_ATTRIBUTE_MIN_GREATER_MAX,
    TYPE_ATTRIBUTE_NOT_STEP_CAPABLE,
    TYPE_ATTRIBUTE_STEP_NEGATIVE,
    TYPE_ATTRIBUTE_NOT_PATTERN_CAPABLE,
    TYPE_ATTRIBUTE_NOT_VALUE_PROPOSAL_CAPABLE,

    TYPE_ATTRIBUTE_NOT_UNIT_CAPABLE,
    TYPE_ATTRIBUTE_EXACTLY_ONE_BASE_UNIT_MANDATORY,
    TYPE_ATTRIBUTE_UNIT_NAME_MANDATORY,
    TYPE_ATTRIBUTE_UNIT_AMBIGIOUS_NAME,
    TYPE_ATTRIBUTE_UNIT_AMBIGIOUS_FACTOR,

    TYPE_ATTRIBUTE_VALUE_TYPE_NOT_ALLOWED,
    TYPE_ATTRIBUTE_STRUCTURE_NOT_ALLOWED,

    TYPE_ATTRIBUTE_LIST_VALUE_TYPE_MANDATORY,
    TYPE_ATTRIBUTE_LIST_NESTED_LISTS_NOT_ALLOWED,
    TYPE_ATTRIBUTE_LIST_STRUCTURE_MANDATORY,
    TYPE_ATTRIBUTE_LIST_STRUCTURE_NOT_ALLOWED,

    TYPE_ATTRIBUTE_STRUCTURE_VALUE_TYPE_NOT_ALLOWED,
    TYPE_ATTRIBUTE_STRUCTURE_STRUCTURE_MANDATORY,

    ITEM_TYPE_MANDATORY,
    ITEM_TYPE_DOES_NOT_MATCH,
    ITEM_TYPE_INVALID,

    ITEM_VALUE_UNIT_BASED,
    ITEM_VALUE_NOT_UNIT_BASED,
    ITEM_VALUE_UNIT_INVALID,

    ITEM_VALUE_MANDATORY,
    ITEM_VALUE_TYPE_INVALID,
    ITEM_LIST_VALUE_TYPE_INVALID,

    ITEM_VALUE_MIN_UNDERCUT,
    ITEM_VALUE_MAX_EXCEEDED,
    ITEM_VALUE_PATTERN_VIOLATED,

    ITEM_ATTRIBUTE_UNDEFINED,

    DUMMY;
}
