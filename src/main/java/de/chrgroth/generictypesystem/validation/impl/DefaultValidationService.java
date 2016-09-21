package de.chrgroth.generictypesystem.validation.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrgroth.generictypesystem.model.DefaultGenericAttributeType;
import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericAttributeUnit;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericStructure;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.model.UnitValue;
import de.chrgroth.generictypesystem.validation.ValidationResult;
import de.chrgroth.generictypesystem.validation.ValidationService;

/**
 * The default validation service for all type and item validations. You may pass an instance of {@link DefaultValidationServiceHooks} to customize or enhance
 * the validation logic rather than implementing a new validation service by yourself.
 *
 * @author Christian Groth
 */
public class DefaultValidationService implements ValidationService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultValidationService.class);

    private final DefaultValidationServiceHooks hooks;

    public DefaultValidationService(DefaultValidationServiceHooks hooks) {
        this.hooks = hooks != null ? hooks : new DefaultValidationServiceEmptyHooks();
    }

    @Override
    public ValidationResult<GenericType> validate(GenericType type) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("validating type: " + type);
        }

        // null guard
        ValidationResult<GenericType> result = new ValidationResult<>(type);
        if (type == null) {
            result.error("", DefaultValidationServiceMessageKey.GENERAL_TYPE_NOT_PROVIDED);
            return result;
        }

        // name mandatory
        if (StringUtils.isBlank(type.getName())) {
            result.error("name", DefaultValidationServiceMessageKey.TYPE_NAME_MANDATORY);
        }

        // group mandatory
        if (StringUtils.isBlank(type.getGroup())) {
            result.error("group", DefaultValidationServiceMessageKey.TYPE_GROUP_MANDATORY);
        }

        // paging valid
        if (type.getPageSize() != null && type.getPageSize() < 1) {
            result.error("pageSize", DefaultValidationServiceMessageKey.TYPE_PAGE_SIZE_INVALID);
        }

        // call type hook
        hooks.typeValidation(result, type);

        // validate structure
        validateStructure(result, type, "");

        // done
        return result;
    }

    private void validateStructure(ValidationResult<GenericType> result, GenericStructure structure, String path) {

        // validate structure attributes
        Set<GenericAttribute> allAttributes = structure.attributes();
        structure.getAttributes().forEach(a -> validateTypeAttribute(result, allAttributes, a, path));

        // validate attribute ids are unique
        Map<Long, Long> countByIds = allAttributes.stream().map(a -> a.getId()).filter(Objects::nonNull).collect(Collectors.groupingBy(a -> a, Collectors.counting()));
        countByIds.entrySet().stream().filter(e -> e.getValue() > 1).forEach(e -> {
            result.error(path, DefaultValidationServiceMessageKey.TYPE_AMBIGIOUS_ATTRIBUTE_ID, e.getKey().longValue());
        });

        // call structure hook
        hooks.structureValidation(result, structure, path);
    }

    private void validateTypeAttribute(ValidationResult<GenericType> result, Set<GenericAttribute> allAttributes, GenericAttribute a, String path) {

        // validate id
        if (a.getId() == null) {
            result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_ID_MANDATORY);
        }

        // validate name
        if (StringUtils.isBlank(a.getName())) {
            result.error(path, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_NAME_MANDATORY);
        } else if (a.getName().indexOf(".") >= 0) {
            result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_NAME_CONTAINS_DOT);
        }

        // type must be be set
        if (a.getType() == null) {
            result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_TYPE_MANDATORY);
            return;
        }

        // must be mandatory if unique
        if (a.isUnique() && !a.isMandatory()) {
            result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_UNIQUE_BUT_NOT_MANDATORY);
        }

        // check min and max values
        boolean minMaxAppliable = a.getType().isMinMaxCapable();
        if (!minMaxAppliable && a.getMin() != null) {
            result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_NOT_MIN_CAPABLE, a.getType().toString());
        }
        if (!minMaxAppliable && a.getMax() != null) {
            result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_NOT_MAX_CAPABLE, a.getType().toString());
        }
        if (minMaxAppliable && a.getMin() != null && a.getMax() != null && a.getMin() >= a.getMax()) {
            result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_MIN_GREATER_MAX);
        }

        // check step value
        boolean stepAppliable = a.getType().isStepCapable();
        if (stepAppliable && a.getStep() != null && a.getStep().doubleValue() <= 0.0) {
            result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_STEP_NEGATIVE);
        }
        if (!stepAppliable && a.getStep() != null) {
            result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_NOT_STEP_CAPABLE, a.getType().toString());
        }

        // check pattern value
        boolean patternAppliable = a.getType().isPatternCapable();
        if (!patternAppliable && StringUtils.isNotBlank(a.getPattern())) {
            result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_NOT_PATTERN_CAPABLE, a.getType().toString());
        }

        // validate valueProposalDependencies
        if (a.getValueProposalDependencies() != null && !a.getValueProposalDependencies().isEmpty()) {
            if (!a.getType().isValueProposalDependenciesCapable()) {
                result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_NOT_VALUE_PROPOSAL_CAPABLE, a.getType().toString());
            } else {

                // no self dependency
                Long selfDependency = a.getValueProposalDependencies().stream().filter(d -> a.getId() != null && a.getId().equals(d)).findAny().orElse(null);
                if (selfDependency != null) {
                    result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_VALUE_PROPOSAL_SELF_REFERENCE_INVALID);
                }

                // check all dependencies do exist
                Set<Long> allAttributeIds = allAttributes.stream().map(att -> att.getId()).collect(Collectors.toSet());
                Set<Long> invalidDependencies = a.getValueProposalDependencies().stream().filter(d -> !allAttributeIds.contains(d)).collect(Collectors.toSet());
                if (invalidDependencies != null && !invalidDependencies.isEmpty()) {
                    invalidDependencies.forEach(d -> result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_VALUE_PROPOSAL_INVALID, d));
                }
            }
        }

        // validate units
        if (a.isUnitBased()) {
            if (!a.getType().isUnitCapable()) {
                result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_NOT_UNIT_CAPABLE, a.getType().toString());
            } else {

                // be sure to have a base unit
                if (a.getUnits().stream().filter(u -> u.isBase()).count() != 1) {
                    result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_EXACTLY_ONE_BASE_UNIT_MANDATORY);
                }

                // be sure all units are named
                if (a.getUnits().stream().filter(u -> StringUtils.isBlank(u.getName())).count() > 0) {
                    result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_UNIT_NAME_MANDATORY);
                }

                // be sure all unit names are distinct
                if (a.getUnits().size() != a.getUnits().stream().map(u -> u.getName()).distinct().count()) {
                    result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_UNIT_AMBIGIOUS_NAME);
                }
            }
        }

        // validate default values
        if (StringUtils.isNotBlank(a.getDefaultValue())) {

            // not allowed for unit based attributes
            if (a.isUnitBased()) {
                result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_UNIT_BASED_DEFAULT_VALUE_NOT_ALLOWED);
            } else if (a.getType() instanceof DefaultGenericAttributeType) {

                // validate default attribute types
                switch ((DefaultGenericAttributeType) a.getType()) {
                    case BOOLEAN:
                        // fine, nothing to validate for Booleans based on java.lang.Boolean#parseBoolean(String)
                        break;
                    case STRING:
                        validateTypeAttributeStringDefaultValue(result, a);
                        break;
                    case LONG:
                        validateTypeAttributeLongDefaultValue(result, a);
                        break;
                    case DOUBLE:
                        validateTypeAttributeDoubleDefaultValue(result, a);
                        break;
                    case DATE:
                    case DATETIME:
                    case TIME:
                    case LIST:
                    case STRUCTURE:
                        result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_DEFAULT_VALUE_NOT_ALLOWED);
                        break;
                    default:
                        // nothing to do here
                        break;
                }
            }
        }

        // call type attribute hook
        hooks.typeAttributeValidation(result, a, path);

        // check collection attribute
        if (a.isList()) {
            validateTypeListAttribute(result, a, path);
        } else if (a.isStructure()) {
            validateTypeStructureAttribute(result, a, path);
        } else {
            validateTypeSingleAttribute(result, a, path);
        }
    }

    private void validateTypeAttributeStringDefaultValue(ValidationResult<GenericType> result, GenericAttribute a) {

        // validate min
        int length = a.getDefaultValue().length();
        if (a.getMin() != null && a.getMin() > length) {
            result.error(a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_DEFAULT_VALUE_STRING_MIN_UNDERCUT, a.getMin());
        }

        // validate max
        if (a.getMax() != null && a.getMax() < length) {
            result.error(a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_DEFAULT_VALUE_STRING_MAX_EXCEEDED, a.getMax());
        }

        // validate pattern
        if (StringUtils.isNotBlank(a.getPattern())) {
            Pattern pattern = Pattern.compile(a.getPattern());
            Matcher matcher = pattern.matcher(a.getDefaultValue());
            if (!matcher.matches()) {
                result.error(a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_DEFAULT_VALUE_STRING_PATTERN_VIOLATED, a.getPattern());
            }
        }
    }

    private void validateTypeAttributeLongDefaultValue(ValidationResult<GenericType> result, GenericAttribute a) {

        // parse default value
        Long value = null;
        try {
            value = Long.parseLong(a.getDefaultValue());
        } catch (NumberFormatException e) {
            result.error(a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_DEFAULT_VALUE_LONG_INVALID);
            return;
        }

        // validate min
        if (a.getMin() != null && a.getMin() > value) {
            result.error(a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_DEFAULT_VALUE_LONG_MIN_UNDERCUT, a.getMin());
        }

        // validate max
        if (a.getMax() != null && a.getMax() < value) {
            result.error(a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_DEFAULT_VALUE_LONG_MAX_EXCEEDED, a.getMax());
        }
    }

    private void validateTypeAttributeDoubleDefaultValue(ValidationResult<GenericType> result, GenericAttribute a) {

        // parse default value
        Double value = null;
        try {
            value = Double.parseDouble(a.getDefaultValue());
        } catch (NumberFormatException e) {
            result.error(a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_DEFAULT_VALUE_DOUBLE_INVALID);
            return;
        }

        // validate min
        if (a.getMin() != null && a.getMin() > value) {
            result.error(a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_DEFAULT_VALUE_DOUBLE_MIN_UNDERCUT, a.getMin());
        }

        // validate max
        if (a.getMax() != null && a.getMax() < value) {
            result.error(a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_DEFAULT_VALUE_DOUBLE_MAX_EXCEEDED, a.getMax());
        }
    }

    private void validateTypeListAttribute(ValidationResult<GenericType> result, GenericAttribute a, String path) {

        // value type mandatory
        if (a.getValueType() == null) {
            result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_LIST_VALUE_TYPE_MANDATORY);
        } else if (a.getValueType().isList()) {
            result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_LIST_NESTED_LISTS_NOT_ALLOWED);
        }

        // list structure
        if (a.getValueType() != null) {
            if (a.getValueType().isStructure() && a.getStructure() == null) {
                result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_LIST_STRUCTURE_MANDATORY);
            } else if (!a.getValueType().isStructure() && a.getStructure() != null) {
                result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_LIST_STRUCTURE_NOT_ALLOWED, a.getValueType().toString());
            }
        }

        // call list type attribute hook
        hooks.typeListAttributeValidation(result, a, path);
    }

    private void validateTypeStructureAttribute(ValidationResult<GenericType> result, GenericAttribute a, String path) {

        // no value type allowed
        if (a.getValueType() != null) {
            result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_STRUCTURE_VALUE_TYPE_NOT_ALLOWED);
        }

        // structure is mandatory
        if (a.getStructure() == null) {
            result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_STRUCTURE_STRUCTURE_MANDATORY);
            return;
        }

        // delegate
        validateStructure(result, a.getStructure(), path + a.getName() + ".");

        // call structure type attribute hook
        hooks.typeStructureAttributeValidation(result, a, path);
    }

    private void validateTypeSingleAttribute(ValidationResult<GenericType> result, GenericAttribute a, String path) {

        // no value type allowed
        if (a.getValueType() != null) {
            result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_VALUE_TYPE_NOT_ALLOWED);
        }

        // no structure is allowed
        if (a.getStructure() != null) {
            result.error(path + a.getName(), DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_STRUCTURE_NOT_ALLOWED);
        }

        // call simple type attribute hook
        hooks.typeSimpleAttributeValidation(result, a, path);
    }

    @Override
    public ValidationResult<GenericItem> validate(GenericType type, GenericItem item) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("validating item: " + item);
        }

        // type null guard
        ValidationResult<GenericItem> result = new ValidationResult<>(item);
        if (type == null) {
            result.error("", DefaultValidationServiceMessageKey.GENERAL_TYPE_NOT_PROVIDED);
            return result;
        }

        // null guard
        if (item == null) {
            result.error("", DefaultValidationServiceMessageKey.GENERAL_ITEM_NOT_PROVIDED);
            return result;
        }

        // validate type matches
        if (item.getTypeId() == null) {
            result.error("", DefaultValidationServiceMessageKey.ITEM_TYPE_MANDATORY);
            return result;
        }
        if (!item.getTypeId().equals(type.getId())) {
            result.error("", DefaultValidationServiceMessageKey.ITEM_TYPE_DOES_NOT_MATCH);
            return result;
        }

        // abort on invalid type
        ValidationResult<GenericType> typeValidationResult = validate(type);
        if (!typeValidationResult.isValid()) {
            result.error("", DefaultValidationServiceMessageKey.ITEM_TYPE_INVALID);
            return result;
        }

        // validate item level
        validateItemLevel(result, type, item, "");

        // check all values
        item.get().entrySet().forEach(e -> {
            if (type.attribute(e.getKey()) == null) {
                result.error(e.getKey(), DefaultValidationServiceMessageKey.ITEM_ATTRIBUTE_UNDEFINED);
            }
        });

        // call item hook
        hooks.itemValidation(result, type, item);

        // done
        return result;
    }

    private void validateItemLevel(ValidationResult<GenericItem> result, GenericStructure structure, GenericItem item, String path) {

        // check all attributes on this level
        structure.getAttributes().forEach(a -> validateItemAttribute(result, structure, a, item, path));

        // call item level hook
        hooks.itemLevelValidation(result, structure, item, path);
    }

    private void validateItemAttribute(ValidationResult<GenericItem> result, GenericStructure structure, GenericAttribute a, GenericItem item, String path) {

        // check mandatory value
        Object value = item.get(a.getName());

        // check if value is unit based
        boolean isUnitValue = value instanceof UnitValue;

        // check unit based / non unit based value
        Object checkValue = value;
        if (checkValue != null) {

            // validate unit based value
            if (a.isUnitBased() && !isUnitValue) {
                result.error(path + a.getName(), DefaultValidationServiceMessageKey.ITEM_VALUE_NOT_UNIT_BASED);
            } else if (!a.isUnitBased() && isUnitValue) {
                result.error(path + a.getName(), DefaultValidationServiceMessageKey.ITEM_VALUE_UNIT_BASED);
            }

            // unbox unit based value
            if (isUnitValue) {
                checkValue = ((UnitValue) value).getValue();
            }
        }

        // check mandatory value
        boolean nullOrEmptyValue = checkValue == null || a.getType().isText() && StringUtils.isBlank(checkValue.toString());
        if (a.isMandatory() && nullOrEmptyValue) {
            result.error(path + a.getName(), DefaultValidationServiceMessageKey.ITEM_VALUE_MANDATORY);
        }

        // unit based checks
        if (isUnitValue) {
            UnitValue unitValue = (UnitValue) value;

            // check unit is registered for attribute
            GenericAttributeUnit attributeUnit = !a.isUnitBased() ? null
                    : a.getUnits().stream().filter(u -> StringUtils.equals(u.getName(), unitValue.getUnit())).findFirst().orElse(null);
            if (attributeUnit == null) {
                result.error(path + a.getName(), DefaultValidationServiceMessageKey.ITEM_VALUE_UNIT_INVALID);
            }
        }

        // no more checks if we don't have a value
        if (checkValue == null) {
            return;
        }

        // check type value
        Class<?> checkClass = checkValue.getClass();
        boolean valueAssignableToType = a.getType().isAssignableFrom(checkClass);
        if (!valueAssignableToType) {
            result.error(path + a.getName(), DefaultValidationServiceMessageKey.ITEM_VALUE_TYPE_INVALID, a.getType().toString(), checkClass.getName());
            return;
        }

        // call item attribute hook
        hooks.itemAttributeValidation(result, structure, a, item, path);

        // recurse for nested structures
        if (a.isStructure()) {
            validateItemLevel(result, a.getStructure(), (GenericItem) checkValue, path + a.getName() + ".");
        } else {

            // handle list attributes
            if (a.isList()) {
                validateItemAttributeListValue(result, structure, a, item, (List<?>) checkValue, path);
            } else if (valueAssignableToType) {

                // handle simple attributes
                validateItemAttributeValue(result, structure, a, item, checkValue, path);
            }
        }
    }

    private <T> void validateItemAttributeListValue(ValidationResult<GenericItem> result, GenericStructure structure, GenericAttribute a, GenericItem item, List<T> value,
            String path) {

        // check mandatory value
        if (value.isEmpty() && a.isMandatory()) {
            result.error(path + a.getName(), DefaultValidationServiceMessageKey.ITEM_VALUE_MANDATORY);
        }
        if (value.isEmpty()) {
            return;
        }

        // check containing items
        Set<T> mismatchingItems = value.stream().filter(i -> !a.getValueType().isAssignableFrom(i.getClass())).collect(Collectors.toSet());
        if (!mismatchingItems.isEmpty()) {
            result.error(path + a.getName(), DefaultValidationServiceMessageKey.ITEM_LIST_VALUE_TYPE_INVALID, a.getValueType().toString());
        }

        // call item list attribute value hook
        hooks.itemListAttributeValueValidation(result, structure, a, item, value, path);
    }

    private void validateItemAttributeValue(ValidationResult<GenericItem> result, GenericStructure structure, GenericAttribute a, GenericItem item, Object value, String path) {

        // string validation
        if (a.getType().isText()) {
            validateItemAttributeStringValue(result, a, value.toString(), path);
        }

        // numeric validation
        if (a.getType().isNumeric()) {
            Double dValue = null;
            if (value instanceof Integer) {
                dValue = new Double((Integer) value);
            } else if (value instanceof Long) {
                dValue = new Double((Long) value);
            } else if (value instanceof Float) {
                dValue = new Double((Float) value);
            } else if (value instanceof Double) {
                dValue = (Double) value;
            }
            validateItemAttributeDoubleValue(result, a, dValue, path);
        }

        // call item simple attribute value hook
        hooks.itemSimpleAttributeValueValidation(result, structure, a, item, value, path);
    }

    private void validateItemAttributeStringValue(ValidationResult<GenericItem> result, GenericAttribute a, String value, String path) {

        // validate min
        int length = value.length();
        if (a.getMin() != null && a.getMin() > length) {
            result.error(path + a.getName(), DefaultValidationServiceMessageKey.ITEM_VALUE_MIN_UNDERCUT, a.getMin());
        }

        // validate max
        if (a.getMax() != null && a.getMax() < length) {
            result.error(path + a.getName(), DefaultValidationServiceMessageKey.ITEM_VALUE_MAX_EXCEEDED, a.getMax());
        }

        // validate pattern
        if (StringUtils.isNotBlank(a.getPattern())) {
            Pattern pattern = Pattern.compile(a.getPattern());
            Matcher matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                result.error(path + a.getName(), DefaultValidationServiceMessageKey.ITEM_VALUE_PATTERN_VIOLATED, a.getPattern());
            }
        }
    }

    private void validateItemAttributeDoubleValue(ValidationResult<GenericItem> result, GenericAttribute a, Double value, String path) {

        // validate min
        if (a.getMin() != null && a.getMin() > value) {
            result.error(path + a.getName(), DefaultValidationServiceMessageKey.ITEM_VALUE_MIN_UNDERCUT, a.getMin());
        }

        // validate max
        if (a.getMax() != null && a.getMax() < value) {
            result.error(path + a.getName(), DefaultValidationServiceMessageKey.ITEM_VALUE_MAX_EXCEEDED, a.getMax());
        }
    }
}
