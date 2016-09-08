package de.chrgroth.generictypesystem.validation;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericAttributeType;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericStructure;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.model.UnitValue;

// TODO complete unit tests
// TODO change unittests to check for message key enums
// TODO private -> protected, add hooks??
public class DefaultValidationService implements ValidationService {

    @Override
    public ValidationResult<GenericType> validate(GenericType type) {

        // null guard
        ValidationResult<GenericType> result = new ValidationResult<>(type);
        if (type == null) {
            result.error("", ValidationMessageKey.GENERAL_TYPE_NOT_PROVIDED);
            return result;
        }

        // name mandatory
        if (StringUtils.isBlank(type.getName())) {
            result.error("name", ValidationMessageKey.TYPE_NAME_MANDATORY);
        }

        // group mandatory
        if (StringUtils.isBlank(type.getGroup())) {
            result.error("group", ValidationMessageKey.TYPE_GROUP_MANDATORY);
        }

        // paging mandatory
        if (type.getPageSize() < 1) {
            result.error("pageSize", ValidationMessageKey.TYPE_PAGE_SIZE_NEGATIVE);
        }

        // validate structure
        validateStructure(result, type, "");

        // done
        return result;
    }

    private void validateStructure(ValidationResult<GenericType> result, GenericStructure structure, String path) {

        // null guard
        if (structure == null) {
            result.error(path, ValidationMessageKey.GENERAL_STRUCTURE_NOT_PROVIDED);
            return;
        }

        // validate structure attributes
        structure.getAttributes().forEach(a -> validateTypeAttribute(result, a, path));

        // validate attribute ids are unique
        Map<Long, Long> countByIds = structure.getAttributes().stream().map(a -> a.getId()).filter(Objects::nonNull).collect(Collectors.groupingBy(a -> a, Collectors.counting()));
        countByIds.entrySet().stream().filter(e -> e.getValue() > 1).forEach(e -> {
            result.error(path, ValidationMessageKey.TYPE_AMBIGIOUS_ATTRIBUTE_ID, String.valueOf(e.getKey().longValue()));
        });
    }

    private void validateTypeAttribute(ValidationResult<GenericType> result, GenericAttribute a, String path) {

        // validate id
        if (a.getId() == null) {
            result.error(path, ValidationMessageKey.TYPE_ATTRIBUTE_ID_MANDATORY, a.getName());
        }

        // validate name
        if (StringUtils.isBlank(a.getName())) {
            result.error(path, ValidationMessageKey.TYPE_ATTRIBUTE_NAME_MANDATORY);
        } else if (a.getName().indexOf(".") >= 0) {
            result.error(path + a.getName(), ValidationMessageKey.TYPE_ATTRIBUTE_NAME_CONTAINS_DOT);
        }

        // type must be be set
        if (a.getType() == null) {
            result.error(path + a.getName(), ValidationMessageKey.TYPE_ATTRIBUTE_TYPE_MANDATORY);
            return;
        }

        // must be mandatory if unique
        if (a.isUnique() && !a.isMandatory()) {
            result.error(path + a.getName(), ValidationMessageKey.TYPE_ATTRIBUTE_UNIQUE_BUT_NOT_MANDATORY);
        }

        // check min and max values
        boolean minMaxAppliable = a.getType().isMinMaxCapable();
        if (!minMaxAppliable && a.getMin() != null) {
            result.error(path + a.getName(), ValidationMessageKey.TYPE_ATTRIBUTE_NOT_MIN_CAPABLE, a.getType().toString());
        }
        if (!minMaxAppliable && a.getMax() != null) {
            result.error(path + a.getName(), ValidationMessageKey.TYPE_ATTRIBUTE_NOT_MAX_CAPABLE, a.getType().toString());
        }
        if (minMaxAppliable && a.getMin() != null && a.getMax() != null && a.getMin() >= a.getMax()) {
            result.error(path + a.getName(), ValidationMessageKey.TYPE_ATTRIBUTE_MIN_GREATER_MAX);
        }

        // check step value
        boolean stepAppliable = a.getType().isStepCapable();
        if (stepAppliable && a.getStep() != null && a.getStep().doubleValue() <= 0.0) {
            result.error(path + a.getName(), ValidationMessageKey.TYPE_ATTRIBUTE_STEP_NEGATIVE, a.getType().toString());
        }
        if (!stepAppliable && a.getStep() != null) {
            result.error(path + a.getName(), ValidationMessageKey.TYPE_ATTRIBUTE_NOT_STEP_CAPABLE, a.getType().toString());
        }

        // check pattern value
        boolean patternAppliable = a.getType().isPatternCapable();
        if (!patternAppliable && StringUtils.isNotBlank(a.getPattern())) {
            result.error(path + a.getName(), ValidationMessageKey.TYPE_ATTRIBUTE_NOT_PATTERN_CAPABLE, a.getType().toString());
        }

        // TODO validate default values
        // TODO validate default values callback

        // validate valueProposalDependencies
        if (a.getValueProposalDependencies() != null && !a.getValueProposalDependencies().isEmpty() && !a.getType().isValueProposalDependenciesCapable()) {
            result.error(path + a.getName(), ValidationMessageKey.TYPE_ATTRIBUTE_NOT_VALUE_PROPOSAL_CAPABLE, a.getType().toString());
        }

        // validate units
        if (a.getUnits() != null && !a.getUnits().isEmpty()) {
            if (!a.getType().isUnitCapable()) {
                result.error(path + a.getName(), ValidationMessageKey.TYPE_ATTRIBUTE_NOT_UNIT_CAPABLE, a.getType().toString());
            } else {

                // be sure to have a base unit
                if (a.getUnits().stream().filter(u -> u.isBase()).count() != 1) {
                    result.error(path + a.getName(), ValidationMessageKey.TYPE_ATTRIBUTE_BASE_UNIT_MANDATORY);
                }

                // be sure all units are named
                if (a.getUnits().stream().filter(u -> StringUtils.isBlank(u.getName())).count() > 0) {
                    result.error(path + a.getName(), ValidationMessageKey.TYPE_ATTRIBUTE_UNIT_NAME_MANDATORY);
                }

                // be sure all unit names are distinct
                if (a.getUnits().size() != a.getUnits().stream().map(u -> u.getName()).distinct().count()) {
                    result.error(path + a.getName(), ValidationMessageKey.TYPE_ATTRIBUTE_UNIT_AMBIGIOUS_NAME);
                }

                // be sure all unit factors are distinct
                if (a.getUnits().size() != a.getUnits().stream().map(u -> u.getFactor()).distinct().count()) {
                    result.error(path + a.getName(), ValidationMessageKey.TYPE_ATTRIBUTE_UNIT_AMBIGIOUS_FACTOR);
                }
            }
        }

        // check collection attribute
        if (a.isList()) {
            validateTypeCollectionAttribute(result, a, path);
        } else if (a.isStructure()) {
            validateTypeStructureAttribute(result, a, path);
        } else {
            validateTypeSingleAttribute(result, a, path);
        }
    }

    private void validateTypeCollectionAttribute(ValidationResult<GenericType> result, GenericAttribute a, String path) {

        // no key type allowed
        if (a.getKeyType() != null) {
            result.error(path + a.getName(), ValidationMessageKey.TYPE_ATTRIBUTE_LIST_KEY_TYPE_NOT_ALLOWED);
        }

        // value type mandatory
        if (a.getValueType() == null) {
            result.error(path + a.getName(), ValidationMessageKey.TYPE_ATTRIBUTE_LIST_VALUE_TYPE_MANDATORY);
        } else if (!GenericAttribute.VALID_VALUE_TYPES.contains(a.getValueType())) {
            result.error(path + a.getName(), ValidationMessageKey.TYPE_ATTRIBUTE_LIST_VALUE_TYPE_INVALID, GenericAttribute.VALID_VALUE_TYPES.toString());
        }

        // no structure is allowed
        if (a.getValueType() == GenericAttributeType.STRUCTURE && a.getStructure() == null) {
            result.error(path + a.getName(), ValidationMessageKey.TYPE_ATTRIBUTE_LIST_STRUCTURE_MANDATORY);
        } else if (a.getValueType() != null && a.getValueType() != GenericAttributeType.STRUCTURE && a.getStructure() != null) {
            result.error(path + a.getName(), ValidationMessageKey.TYPE_ATTRIBUTE_LIST_STRUCTURE_NOT_ALLOWED, a.getValueType().toString());
        }
    }

    private void validateTypeStructureAttribute(ValidationResult<GenericType> result, GenericAttribute a, String path) {

        // no key and value types allowed
        if (a.getKeyType() != null) {
            result.error(path + a.getName(), ValidationMessageKey.TYPE_ATTRIBUTE_STRUCTURE_KEY_TYPE_NOT_ALLOWED);
        }
        if (a.getValueType() != null) {
            result.error(path + a.getName(), ValidationMessageKey.TYPE_ATTRIBUTE_STRUCTURE_VALUE_TYPE_NOT_ALLOWED);
        }

        // structure is mandaory
        if (a.getStructure() == null) {
            result.error(path + a.getName(), ValidationMessageKey.TYPE_ATTRIBUTE_STRUCTURE_STRUCTURE_MANDATORY);
            return;
        }

        // delegate
        validateStructure(result, a.getStructure(), path + a.getName() + ".");
    }

    private void validateTypeSingleAttribute(ValidationResult<GenericType> result, GenericAttribute a, String path) {

        // no key and value types allowed
        if (a.getKeyType() != null) {
            result.error(path + a.getName(), ValidationMessageKey.TYPE_ATTRIBUTE_KEY_TYPE_NOT_ALLOWED);
        }
        if (a.getValueType() != null) {
            result.error(path + a.getName(), ValidationMessageKey.TYPE_ATTRIBUTE_VALUE_TYPE_NOT_ALLOWED);
        }

        // no structure is allowed
        if (a.getStructure() != null) {
            result.error(path + a.getName(), ValidationMessageKey.TYPE_ATTRIBUTE_STRUCTURE_NOT_ALLOWED);
        }
    }

    @Override
    public ValidationResult<GenericItem> validate(GenericType type, GenericItem item) {

        // type null guard
        ValidationResult<GenericItem> result = new ValidationResult<>(item);
        if (type == null) {
            result.error("", ValidationMessageKey.GENERAL_TYPE_NOT_PROVIDED);
            return result;
        }

        // null guard
        if (item == null) {
            result.error("", ValidationMessageKey.GENERAL_ITEM_NOT_PROVIDED);
            return result;
        }

        // validate valid and type matches
        if (item.getGenericTypeId() == null) {
            result.error("", ValidationMessageKey.ITEM_TYPE_MANDATORY);
            return result;
        }
        if (!item.getGenericTypeId().equals(type.getId())) {
            result.error("", ValidationMessageKey.ITEM_TYPE_DOES_NOT_MATCH);
            return result;
        }

        // abort on invalid type
        ValidationResult<GenericType> typeValidationResult = validate(type);
        if (!typeValidationResult.isValid()) {
            result.error("", ValidationMessageKey.ITEM_TYPE_INVALID);
            return result;
        }

        // check all attributes
        type.getAttributes().forEach(a -> validateItemAttribute(result, a, item));

        // check all values
        item.get().entrySet().forEach(e -> validateItemValue(result, e, type));

        // done
        return result;
    }

    private void validateItemAttribute(ValidationResult<GenericItem> result, GenericAttribute a, GenericItem item) {

        // check mandatory value
        Object value = item.get(a.getName());

        // check unit based / non unit based value
        Object checkValue = value;
        if (checkValue != null) {

            // check if value is unit based
            boolean unitValue = value instanceof UnitValue;

            // validate unit based value
            if (a.isUnitBased() && !unitValue) {
                result.error(a.getName(), ValidationMessageKey.ITEM_VALUE_NOT_UNIT_BASED);
            } else if (!a.isUnitBased() && unitValue) {
                result.error(a.getName(), ValidationMessageKey.ITEM_VALUE_UNIT_BASED);
            }

            // unbox unit based value
            if (unitValue) {
                checkValue = ((UnitValue) value).getValue();
            }
        }

        // check mandatory value
        boolean nullOrEmptyValue = checkValue == null || a.getType() == GenericAttributeType.STRING && StringUtils.isBlank(checkValue.toString());
        if (a.isMandatory() && nullOrEmptyValue) {
            result.error(a.getName(), ValidationMessageKey.ITEM_VALUE_MANDATORY);
        }

        // TODO more unit based checks??

        // no more checks if we don't have a value
        if (checkValue == null) {
            return;
        }

        // check type value
        Class<?> checkClass = checkValue.getClass();
        boolean valueAssignableToType = a.getType().isAssignableFrom(checkClass);
        if (!valueAssignableToType) {
            result.error(a.getName(), ValidationMessageKey.ITEM_VALUE_TYPE_INVALID, a.getType().toString(), checkClass.getName());
        }

        // check collection and map type
        boolean isListType = a.isList();
        if (isListType) {
            if (checkValue instanceof Collection<?>) {
                vaidateItemAttributeCollectionValue(result, a, (Collection<?>) checkValue);
            } else {
                result.error(a.getName(), ValidationMessageKey.ITEM_VALUE_TYPE_INVALID, a.getType().toString(), checkValue.getClass().getName());
            }
        } else if (valueAssignableToType) {
            validateItemAttributeValue(result, a, checkValue);
        }
    }

    private <T> void vaidateItemAttributeCollectionValue(ValidationResult<GenericItem> result, GenericAttribute a, Collection<T> value) {

        // check mandatory value
        if (value.isEmpty() && a.isMandatory()) {
            result.error(a.getName(), ValidationMessageKey.ITEM_VALUE_MANDATORY);
        }
        if (value.isEmpty()) {
            return;
        }

        // check containing items
        Set<T> mismatchingItems = value.stream().filter(i -> !a.getValueType().isAssignableFrom(i.getClass())).collect(Collectors.toSet());
        if (!mismatchingItems.isEmpty()) {
            result.error(a.getName(), ValidationMessageKey.ITEM_LIST_VALUE_TYPE_INVALID, a.getValueType().toString());
        }
    }

    private void validateItemAttributeValue(ValidationResult<GenericItem> result, GenericAttribute a, Object value) {
        switch (a.getType()) {
            case STRING:
                validateItemAttributeStringValue(result, a, value.toString());
                break;
            case LONG:
            case DOUBLE:
                Double dValue;
                if (value instanceof Integer) {
                    dValue = new Double((Integer) value);
                } else if (value instanceof Long) {
                    dValue = new Double((Long) value);
                } else if (value instanceof Float) {
                    dValue = new Double((Float) value);
                } else if (value instanceof Double) {
                    dValue = (Double) value;
                } else {
                    result.error(a.getName(), ValidationMessageKey.ITEM_VALUE_TYPE_INVALID, a.getType().toString(), a.getClass().getName());
                    return;
                }
                validateItemAttributeDoubleValue(result, a, dValue);
                break;
            default:
                break;
        }
    }

    private void validateItemAttributeStringValue(ValidationResult<GenericItem> result, GenericAttribute a, String value) {

        // validate min
        int length = value.length();
        if (a.getMin() != null && a.getMin() > length) {
            result.error(a.getName(), ValidationMessageKey.ITEM_VALUE_MIN_UNDERCUT, String.valueOf(a.getMin().intValue()));
        }

        // validate max
        if (a.getMax() != null && a.getMax() < length) {
            result.error(a.getName(), ValidationMessageKey.ITEM_VALUE_MAX_EXCEEDED, String.valueOf(a.getMax().intValue()));
        }

        // validate pattern
        if (StringUtils.isNotBlank(a.getPattern())) {
            Pattern pattern = Pattern.compile(a.getPattern());
            Matcher matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                result.error(a.getName(), ValidationMessageKey.ITEM_VALUE_PATTERN_VIOLATED, a.getPattern());
            }
        }
    }

    private void validateItemAttributeDoubleValue(ValidationResult<GenericItem> result, GenericAttribute a, Double value) {

        // validate min
        if (a.getMin() != null && a.getMin() > value) {
            result.error(a.getName(), ValidationMessageKey.ITEM_VALUE_MIN_UNDERCUT, String.valueOf(a.getMin().intValue()));
        }

        // validate max
        if (a.getMax() != null && a.getMax() < value) {
            result.error(a.getName(), ValidationMessageKey.ITEM_VALUE_MAX_EXCEEDED, String.valueOf(a.getMax().intValue()));
        }
    }

    private void validateItemValue(ValidationResult<GenericItem> result, Entry<String, Object> e, GenericType type) {
        if (type.attribute(e.getKey()) == null) {
            result.error(e.getKey(), ValidationMessageKey.ITEM_ATTRIBUTE_UNDEFINED, e.getKey());
        }
    }
}
