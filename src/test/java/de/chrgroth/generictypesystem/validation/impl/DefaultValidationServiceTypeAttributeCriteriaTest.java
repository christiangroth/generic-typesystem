package de.chrgroth.generictypesystem.validation.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import de.chrgroth.generictypesystem.model.DefaultGenericAttributeType;
import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericStructure;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.model.GenericUnit;
import de.chrgroth.generictypesystem.model.GenericUnits;
import de.chrgroth.generictypesystem.model.GenericValue;
import de.chrgroth.generictypesystem.validation.BaseValidationServiceTypeAndItemTest;
import de.chrgroth.generictypesystem.validation.ValidationError;

@RunWith(Parameterized.class)
public class DefaultValidationServiceTypeAttributeCriteriaTest extends BaseValidationServiceTypeAndItemTest {

    private static final String ATTRIBUTE_NAME = "dummy";

    @Parameters(name = "attribute type {0}")
    public static Iterable<DefaultGenericAttributeType> data() {
        return Arrays.asList(DefaultGenericAttributeType.values());
    }

    @Parameter
    public DefaultGenericAttributeType testType;

    private UnitsLookupTestHelper unitsLookupTestHelper;
    private List<ValidationError> errorKeys = new ArrayList<>();

    @Before
    public void setup() {
        unitsLookupTestHelper = new UnitsLookupTestHelper();
        service = new DefaultValidationService(unitsLookupTestHelper, null);
        type = new GenericType(0l, "testType", "testGroup", null, null, null, null);
        errorKeys = new ArrayList<>();
        if (testType.isEnum()) {
            errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_ENUM_VALUE_NOT_AVAILABLE));
        } else if (testType.isList()) {
            errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_LIST_VALUE_TYPE_MANDATORY));
        } else if (testType.isStructure()) {
            errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_STRUCTURE_STRUCTURE_MANDATORY));
        }
    }

    @Test
    public void minMaxTest() {

        // create attribute with min / max
        createAttribute(null, false, false, null, 0.0d, 10.0d, null, null, null, null, null, null, null);

        // define expected error message keys
        if (!testType.isMinMaxCapable()) {
            errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_NOT_MIN_CAPABLE, testType.toString()));
            errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_NOT_MAX_CAPABLE, testType.toString()));
        }

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));

        // no further checks if not min / max capable
        if (!testType.isMinMaxCapable()) {
            return;
        }

        // create attribute with min / max and min = max
        clearAttributes();
        createAttribute(null, false, false, null, 10.0d, 10.0d, null, null, null, null, null, null, null);

        // define expected error message keys
        errorKeys.clear();
        errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_MIN_GREATER_MAX));

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));

        // create attribute with min / max and min > max
        clearAttributes();
        createAttribute(null, false, false, null, 15.0d, 10.0d, null, null, null, null, null, null, null);

        // define expected error message keys
        errorKeys.clear();
        errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_MIN_GREATER_MAX));

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));
    }

    @Test
    public void stepTest() {

        // create attribute with step
        createAttribute(null, false, false, null, null, null, 1.0d, null, null, null, null, null, null);

        // define expected error message keys
        if (!testType.isStepCapable()) {
            errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_NOT_STEP_CAPABLE, testType.toString()));
        }

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));

        // no further checks if not step capable
        if (!testType.isStepCapable()) {
            return;
        }

        // create attribute with negative step
        clearAttributes();
        createAttribute(null, false, false, null, null, null, -0.1d, null, null, null, null, null, null);

        // define expected error message keys
        errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_STEP_NEGATIVE));

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));
    }

    @Test
    public void patternTest() {

        // create attribute with pattern
        createAttribute(null, false, false, null, null, null, null, "test pattern", null, null, null, null, null);

        // define expected error message keys
        if (!testType.isPatternCapable()) {
            errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_NOT_PATTERN_CAPABLE, testType.toString()));
        }

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));
    }

    @Test
    public void valueProposalDependenciesTest() {

        // create attribute to depend upon
        createAttribute(null, false, false, null, null, null, null, null, null, null, null, null, null);
        attribute.setType(DefaultGenericAttributeType.STRING);
        attribute.setId(100l);
        attribute.setName("dependOnMe");
        GenericAttribute attributeToDependUpon = attribute;

        // create attribute with value proposal dependency
        createAttribute(null, false, false, null, null, null, null, null, null, null, new HashSet<>(Arrays.asList(attributeToDependUpon.getId())), null, null);

        // define expected error message keys
        if (!testType.isValueProposalDependenciesCapable()) {
            errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_NOT_VALUE_PROPOSAL_CAPABLE, testType.toString()));
        }

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));

        // no further checks if not unit capable
        if (!testType.isValueProposalDependenciesCapable()) {
            return;
        }

        // value proposal dependency on non existing attribute
        clearAttributes();
        createAttribute(null, false, false, null, null, null, null, null, null, null, new HashSet<>(Arrays.asList(123l)), null, null);

        // define expected error message keys
        errorKeys.clear();
        errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_VALUE_PROPOSAL_INVALID, 123l));

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));

        // value proposal dependency on myself
        clearAttributes();
        createAttribute(null, false, false, null, null, null, null, null, null, null, new HashSet<>(Arrays.asList(0l)), null, null);

        // define expected error message keys
        errorKeys.clear();
        errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_VALUE_PROPOSAL_SELF_REFERENCE_INVALID));

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));
    }

    @Test
    public void unitsTest() {

        // create attribute with base unit only
        createAttribute(null, false, false, null, null, null, null, null, null, null, null, base(units()), null);

        // define expected error message keys
        if (!testType.isUnitCapable()) {
            errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_NOT_UNIT_CAPABLE, testType.toString()));
        }

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));

        // no further checks if not unit capable
        if (!testType.isUnitCapable()) {
            return;
        }

        // create invalid unit
        clearAttributes();
        createAttribute(null, false, false, null, null, null, null, null, null, null, null, nonBase(units()), null);

        // validate type
        errorKeys.clear();
        errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_UNITS_INVALID, 0l));
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));
    }

    @Test
    public void enumValuesTest() {

        // create attribute without enum values
        createAttribute(null, false, false, null, null, null, null, null, null, null, null, null, null);

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));

        // clear
        clearAttributes();

        // create attribute with empty enum values
        createAttribute(null, false, false, null, null, null, null, null, null, null, null, null, new HashSet<>());

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));

        // no further checks if not enum
        if (!testType.isEnum()) {
            return;
        }

        // clear
        clearAttributes();
        errorKeys.clear();

        // create attribute with enum values
        Set<String> enumValues = new HashSet<>();
        enumValues.add("foo");
        createAttribute(null, false, false, null, null, null, null, null, null, null, null, null, enumValues);

        if (!testType.isEnum()) {
            errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_ENUM_VALUE_NOT_ALLOWED));
        }

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));
    }

    private GenericUnits units() {
        GenericUnits units = new GenericUnits(0l, "units", "desc");
        return units;
    }

    private GenericUnits base(GenericUnits units) {
        units.getUnits().add(new GenericUnit(0l, "base", "b", GenericUnits.FACTOR_BASE));
        return units;
    }

    private GenericUnits nonBase(GenericUnits units) {
        units.getUnits().add(new GenericUnit(1l, "nonbase", "x", 2.0d));
        return units;
    }

    public void createAttribute(DefaultGenericAttributeType valueType, boolean unique, boolean mandatory, GenericStructure structure, Double min, Double max, Double step,
            String pattern, GenericValue<?> defaultValue, String defaultValueCallback, Set<Long> valueProposalDependencies, GenericUnits units, Set<String> enumValues) {
        attribute = new GenericAttribute(0l, ATTRIBUTE_NAME, testType, valueType, unique, mandatory, structure, min, max, step, pattern, defaultValue, defaultValueCallback,
                valueProposalDependencies, units != null ? units.getId() : null, enumValues);
        if (units != null) {
            unitsLookupTestHelper.register(units);
        }
        type.getAttributes().add(attribute);
    }

    private void clearAttributes() {
        type.getAttributes().clear();
    }
}
