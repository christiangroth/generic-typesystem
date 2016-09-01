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
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericStructure;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.model.UnitValue;
import de.chrgroth.generictypesystem.model.GenericAttributeType;

// TODO replace hardcoded values by ids
// TODO private -> protected, add hooks??
// TODO unit tests
public class DefaultValidationService implements ValidationService {

    @Override
    public ValidationResult validate(GenericType type) {

        // null guard
        ValidationResult result = new ValidationResult(type);
        if (type == null) {
            result.error("", "type must be provided!!");
            return result;
        }

        // name mandatory
        if (StringUtils.isBlank(type.getName())) {
            result.error("name", "name for type must be provided!!");
        }

        // group mandatory
        if (StringUtils.isBlank(type.getGroup())) {
            result.error("group", "group for type must be provided!!");
        }

        // paging mandatory
        if (type.getPageSize() < 1) {
            result.error("pageSize", "page size for type must be greater zero!!");
        }

        // validate structure
        return validateStructure(result, type, "");
    }

    private ValidationResult validateStructure(ValidationResult result, GenericStructure structure, String path) {

        // null guard
        if (structure == null) {
            result.error(path, "structure must be provided!!");
            return result;
        }

        // validate structure attributes
        structure.getAttributes().forEach(a -> validateTypeAttribute(result, a, path));

        // validate attribute ids are unique
        Map<Long, Long> countByIds = structure.getAttributes().stream().map(a -> a.getId()).filter(Objects::nonNull).collect(Collectors.groupingBy(a -> a, Collectors.counting()));
        countByIds.entrySet().stream().filter(e -> e.getValue() > 1).forEach(e -> {
            result.error(path, "ambigious attribute id " + e.getKey());
        });

        // done
        return result;
    }

    private void validateTypeAttribute(ValidationResult result, GenericAttribute a, String path) {

        // validate id
        if (a.getId() == null) {
            result.error(path, "attribute id must be provided" + (StringUtils.isNotBlank(a.getName()) ? ": " + a.getName() : ""));
        }

        // validate name
        if (StringUtils.isBlank(a.getName())) {
            result.error(path, "attribute name must be provided");
        } else if (a.getName().indexOf(".") >= 0) {
            result.error(path + a.getName(), "name must not contain dot '.'");
        }

        // type must be be set
        if (a.getType() == null) {
            result.error(path + a.getName(), "type must be provided");
            return;
        }

        // must be mandatory if unique
        if (a.isUnique() && !a.isMandatory()) {
            result.error(path + a.getName(), "unique attribute " + a.getName() + " must be mandatory");
        }

        // check min and max values
        boolean minMaxAppliable = a.getType().isMinMaxCapable();
        if (!minMaxAppliable && a.getMin() != null) {
            result.error(path + a.getName(), "attribute " + a.getName() + " with type " + a.getType() + " must not define min value");
        }
        if (!minMaxAppliable && a.getMax() != null) {
            result.error(path + a.getName(), "attribute " + a.getName() + " with type " + a.getType() + " must not define max value");
        }
        if (minMaxAppliable && a.getMin() != null && a.getMax() != null && a.getMin() >= a.getMax()) {
            result.error(path + a.getName(), "attribute " + a.getName() + " must define min value < max value");
        }

        // check step value
        boolean stepAppliable = a.getType().isStepCapable();
        if (stepAppliable && a.getStep() != null && a.getStep().doubleValue() <= 0.0) {
            result.error(path + a.getName(), "attribute " + a.getName() + " with type " + a.getType() + " must define step greater zero");
        }
        if (!stepAppliable && a.getStep() != null) {
            result.error(path + a.getName(), "attribute " + a.getName() + " with type " + a.getType() + " must not define step value");
        }

        // check pattern value
        boolean patternAppliable = a.getType().isPatternCapable();
        if (!patternAppliable && StringUtils.isNotBlank(a.getPattern())) {
            result.error(path + a.getName(), "attribute " + a.getName() + " with type " + a.getType() + " must not define pattern value");
        }

        // TODO validate default values
        // TODO validate default values callback

        // validate valueProposalDependencies
        if (a.getValueProposalDependencies() != null && !a.getValueProposalDependencies().isEmpty() && !a.getType().isValueProposalDependenciesCapable()) {
            result.error(path + a.getName(), "attribute " + a.getName() + " with type " + a.getType() + " must not define value proposal dependencies");
        }

        // validate units
        if (a.getUnits() != null && !a.getUnits().isEmpty()) {
            if (!a.getType().isUnitCapable()) {
                result.error(path + a.getName(), "attribute " + a.getName() + " with type " + a.getType() + " must not define units");
            } else {

                // be sure to have a base unit
                if (a.getUnits().stream().filter(u -> u.isBase()).count() != 1) {
                    result.error(path + a.getName(), "attribute " + a.getName() + " must define exactly one base unit");
                }

                // be sure all units are named
                if (a.getUnits().stream().filter(u -> StringUtils.isBlank(u.getName())).count() > 0) {
                    result.error(path + a.getName(), "attribute " + a.getName() + " must not define units without name");
                }

                // be sure all unit names are distinct
                if (a.getUnits().size() != a.getUnits().stream().map(u -> u.getName()).distinct().count()) {
                    result.error(path + a.getName(), "attribute " + a.getName() + " must not define multiple units with same name");
                }

                // be sure all unit factors are distinct
                if (a.getUnits().size() != a.getUnits().stream().map(u -> u.getFactor()).distinct().count()) {
                    result.error(path + a.getName(), "attribute " + a.getName() + " must not define multiple units with same name");
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

    private void validateTypeCollectionAttribute(ValidationResult result, GenericAttribute a, String path) {

        // no key type allowed
        if (a.getKeyType() != null) {
            result.error(path + a.getName(), "key type not allowed for collection value attribute type");
        }

        // value type mandatory
        if (a.getValueType() == null) {
            result.error(path + a.getName(), "value must be provided for collection value attribute type");
        } else if (!GenericAttribute.VALID_VALUE_TYPES.contains(a.getValueType())) {
            result.error(path + a.getName(), "value for collection attribute value type must be one of valid types: " + GenericAttribute.VALID_VALUE_TYPES);
        }

        // no structure is allowed
        if (a.getValueType() == GenericAttributeType.STRUCTURE && a.getStructure() == null) {
            result.error(path + a.getName(), "nested structure must be provided for collection with value type " + a.getValueType());
        } else if (a.getValueType() != GenericAttributeType.STRUCTURE && a.getStructure() != null) {
            result.error(path + a.getName(), "nested structure is not allowed for collection with value type " + a.getValueType());
        }
    }

    private void validateTypeStructureAttribute(ValidationResult result, GenericAttribute a, String path) {

        // no key and value types allowed
        if (a.getKeyType() != null || a.getValueType() != null) {
            result.error(path + a.getName(), "key or value types are not allowed for structured attribute type");
        }

        // structure is mandaory
        if (a.getStructure() == null) {
            result.error(path + a.getName(), "nested structure must be provided for structured attribute type");
            return;
        }

        // delegate
        validateStructure(result, a.getStructure(), path + a.getName() + ".");
    }

    private void validateTypeSingleAttribute(ValidationResult result, GenericAttribute a, String path) {

        // no key and value types allowed
        if (a.getKeyType() != null || a.getValueType() != null) {
            result.error(path + a.getName(), "key or value types are not allowed for single value attribute type");
        }

        // no structure is allowed
        if (a.getStructure() != null) {
            result.error(path + a.getName(), "nested structure is not allowed for single value attribute type");
        }
    }

    @Override
    public ValidationResult validate(GenericType type, GenericItem item) {

        // type null guard
        ValidationResult result = new ValidationResult(item);
        if (type == null) {
            result.error("", "type must be provided!!");
            return result;
        }

        // null guard
        if (item == null) {
            result.error("", "item must be provided!!");
            return result;
        }

        // validate type matches
        if (item.getGenericTypeId() == null || !item.getGenericTypeId().equals(type.getId())) {
            result.error("", "item type invalid/mismatch: " + type);
            return result;
        }

        // abort on invalid type
        ValidationResult typeValidationResult = validate(type);
        if (!typeValidationResult.isValid()) {
            result.error("", "referenced type is invalid, please correct type definition first");
            return result;
        }

        // check all attributes
        type.getAttributes().forEach(a -> validateItemAttribute(result, a, item));

        // check all values
        item.get().entrySet().forEach(e -> validateItemValue(result, e, type));

        // done
        return result;
    }

    private void validateItemAttribute(ValidationResult result, GenericAttribute a, GenericItem item) {

        // check mandatory value
        Object value = item.get(a.getName());
        boolean unitValue = value instanceof UnitValue;
        if (a.isMandatory()) {
            Object checkValue = value;
            if (a.isUnitBased()) {
                if (!unitValue && value != null) {
                    result.error(a.getName(), "non unit based value for unit based attribute " + a.getName());
                } else if (unitValue) {
                    checkValue = ((UnitValue) value).getValue();
                }
            }

            if (checkValue == null || a.getType() == GenericAttributeType.STRING && StringUtils.isBlank(checkValue.toString())) {
                result.error(a.getName(), "missing value for mandatory attribute " + a.getName());
            }
        }
        if (value == null) {
            return;
        }

        // check type value
        Class<?> checkClass = unitValue ? ((UnitValue) value).getValue().getClass() : value.getClass();
        boolean valueAssignableToType = a.getType().isAssignableFrom(checkClass);
        if (!valueAssignableToType) {
            result.error(a.getName(), "mismatching type " + a.getType() + " for attribute " + a.getName() + ": " + checkClass.getName());
        }

        // check collection and map type
        boolean isListType = a.isList();
        if (isListType) {
            if (value instanceof Collection<?>) {
                vaidateItemAttributeCollectionValue(result, a, (Collection<?>) value);
            } else {
                result.error(a.getName(), "mismatching type " + a.getType() + " for attribute " + a.getName() + ": " + value.getClass().getName());
            }
        } else if (valueAssignableToType) {
            validateItemAttributeValue(result, a, value);
        }
    }

    private void vaidateItemAttributeCollectionValue(ValidationResult result, GenericAttribute a, Collection<?> value) {

        // check mandatory value
        if (value.isEmpty() && a.isMandatory()) {
            result.error(a.getName(), "empty collection for mandatory attribute " + a.getName());
        }
        if (value.isEmpty()) {
            return;
        }

        // check containing items
        Set<?> mismatchingItems = value.stream().filter(i -> !a.getValueType().isAssignableFrom(i.getClass())).collect(Collectors.toSet());
        if (!mismatchingItems.isEmpty()) {
            result.error(a.getName(), "mismatching items for collection of type " + a.getValueType() + " in attribute " + a.getName() + ": " + mismatchingItems);
        }
    }

    private void validateItemAttributeValue(ValidationResult result, GenericAttribute a, Object value) {
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
                    result.error(a.getName(), "unable to validate value, unknown type " + a.getClass().getSimpleName());
                    return;
                }
                validateItemAttributeDoubleValue(result, a, dValue);
                break;
            default:
                break;
        }
    }

    private void validateItemAttributeStringValue(ValidationResult result, GenericAttribute a, String value) {

        // validate min
        int length = value.length();
        if (a.getMin() != null && a.getMin() > length) {
            result.error(a.getName(), "value for " + a.getName() + " must be longer than " + a.getMin().intValue() + " characters");
        }

        // validate max
        if (a.getMax() != null && a.getMax() < length) {
            result.error(a.getName(), "value for " + a.getName() + " must be less than " + a.getMax().intValue() + " characters");
        }

        // validate pattern
        if (StringUtils.isNotBlank(a.getPattern())) {
            Pattern pattern = Pattern.compile(a.getPattern());
            Matcher matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                result.error(a.getName(), "value for " + a.getName() + " must match pattern " + a.getPattern());
            }
        }
    }

    private void validateItemAttributeDoubleValue(ValidationResult result, GenericAttribute a, Double value) {

        // validate min
        if (a.getMin() != null && a.getMin() > value) {
            result.error(a.getName(), "value for " + a.getName() + " must be greater than " + a.getMin());
        }

        // validate max
        if (a.getMax() != null && a.getMax() < value) {
            result.error(a.getName(), "value for " + a.getName() + " must be less than " + a.getMax());
        }
    }

    private void validateItemValue(ValidationResult result, Entry<String, Object> e, GenericType type) {
        if (type.attribute(e.getKey()) == null) {
            result.error(e.getKey(), "value for undefined attribute " + e.getKey());
        }
    }
}
