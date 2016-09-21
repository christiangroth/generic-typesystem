package de.chrgroth.generictypesystem.validation.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import de.chrgroth.generictypesystem.model.DefaultGenericAttributeType;
import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericAttributeUnit;
import de.chrgroth.generictypesystem.model.GenericStructure;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.validation.BaseValidationServiceTest;
import de.chrgroth.generictypesystem.validation.ValidationError;

// TODO validate nested item
@RunWith(Parameterized.class)
public class DefaultValidationServiceTypeAttributeCriteriaTest extends BaseValidationServiceTest {

    private static final String ATTRIBUTE_NAME = "dummy";

    @Parameters(name = "attribute type {0}")
    public static Iterable<DefaultGenericAttributeType> data() {
        return Arrays.asList(DefaultGenericAttributeType.values());
    }

    @Parameter
    public DefaultGenericAttributeType testType;

    private List<ValidationError> errorKeys = new ArrayList<>();

    @Before
    public void setup() {
        service = new DefaultValidationService(null);
        type = new GenericType(0l, 0, "testType", "testGroup", null, null, null, null, null, null);
        errorKeys = new ArrayList<>();
        if (testType.isList()) {
            errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_LIST_VALUE_TYPE_MANDATORY));
        } else if (testType.isStructure()) {
            errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_STRUCTURE_STRUCTURE_MANDATORY));
        }
    }

    @Test
    public void minMaxTest() {

        // create attribute with min / max
        createAttribute(null, false, false, false, null, 0.0d, 10.0d, null, null, null, null, null, null);

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
        createAttribute(null, false, false, false, null, 10.0d, 10.0d, null, null, null, null, null, null);

        // define expected error message keys
        errorKeys.clear();
        errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_MIN_GREATER_MAX));

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));

        // create attribute with min / max and min > max
        clearAttributes();
        createAttribute(null, false, false, false, null, 15.0d, 10.0d, null, null, null, null, null, null);

        // define expected error message keys
        errorKeys.clear();
        errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_MIN_GREATER_MAX));

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));
    }

    @Test
    public void stepTest() {

        // create attribute with step
        createAttribute(null, false, false, false, null, null, null, 1.0d, null, null, null, null, null);

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
        createAttribute(null, false, false, false, null, null, null, -0.1d, null, null, null, null, null);

        // define expected error message keys
        errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_STEP_NEGATIVE));

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));
    }

    @Test
    public void patternTest() {

        // create attribute with pattern
        createAttribute(null, false, false, false, null, null, null, null, "test pattern", null, null, null, null);

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
        createAttribute(null, false, false, false, null, null, null, null, null, null, null, null, null);
        attribute.setType(DefaultGenericAttributeType.STRING);
        attribute.setId(100l);
        attribute.setName("dependOnMe");
        GenericAttribute attributeToDependUpon = attribute;

        // create attribute with value proposal dependency
        createAttribute(null, false, false, false, null, null, null, null, null, null, null, Arrays.asList(attributeToDependUpon.getId()), null);

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
        createAttribute(null, false, false, false, null, null, null, null, null, null, null, Arrays.asList(123l), null);

        // define expected error message keys
        errorKeys.clear();
        errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_VALUE_PROPOSAL_INVALID, 123l));

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));

        // value proposal dependency on myself
        clearAttributes();
        createAttribute(null, false, false, false, null, null, null, null, null, null, null, Arrays.asList(0l), null);

        // define expected error message keys
        errorKeys.clear();
        errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_VALUE_PROPOSAL_SELF_REFERENCE_INVALID));

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));
    }

    @Test
    public void unitsTest() {

        // create attribute with base unit only
        createAttribute(null, false, false, false, null, null, null, null, null, null, null, null, Arrays.asList(baseUnit()));

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

        // create attribute with base unit only
        clearAttributes();
        createAttribute(null, false, false, false, null, null, null, null, null, null, null, null, Arrays.asList(nonBaseUnit()));

        // define expected error message keys
        errorKeys.clear();
        errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_EXACTLY_ONE_BASE_UNIT_MANDATORY));

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));

        // create attribute with both units - name missing
        clearAttributes();
        GenericAttributeUnit nonBaseUnitWithoutName = nonBaseUnit();
        nonBaseUnitWithoutName.setName(null);
        createAttribute(null, false, false, false, null, null, null, null, null, null, null, null, Arrays.asList(baseUnit(), nonBaseUnitWithoutName));

        // define expected error message keys
        errorKeys.clear();
        errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_UNIT_NAME_MANDATORY));

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));

        // create attribute with both units - ambigious name
        clearAttributes();
        GenericAttributeUnit nonBaseUnitAmbigiousName = nonBaseUnit();
        nonBaseUnitAmbigiousName.setName(baseUnit().getName());
        createAttribute(null, false, false, false, null, null, null, null, null, null, null, null, Arrays.asList(baseUnit(), nonBaseUnitAmbigiousName));

        // define expected error message keys
        errorKeys.clear();
        errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_UNIT_AMBIGIOUS_NAME));

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));

        // create attribute with both units - ambigious factor
        clearAttributes();
        double ambigiousFactor = 0.5d;
        GenericAttributeUnit nonBaseUnitAmbigiousFactorOne = nonBaseUnit();
        nonBaseUnitAmbigiousFactorOne.setFactor(ambigiousFactor);
        GenericAttributeUnit nonBaseUnitAmbigiousFactorTwo = nonBaseUnit();
        nonBaseUnitAmbigiousFactorTwo.setName(nonBaseUnitAmbigiousFactorOne.getName() + "-2");
        nonBaseUnitAmbigiousFactorTwo.setFactor(ambigiousFactor);
        createAttribute(null, false, false, false, null, null, null, null, null, null, null, null,
                Arrays.asList(baseUnit(), nonBaseUnitAmbigiousFactorOne, nonBaseUnitAmbigiousFactorTwo));

        // define expected error message keys
        errorKeys.clear();
        errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_UNIT_AMBIGIOUS_FACTOR));

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));

        // create attribute with both units
        clearAttributes();
        createAttribute(null, false, false, false, null, null, null, null, null, null, null, null, Arrays.asList(baseUnit(), nonBaseUnit()));

        // define expected error message keys
        errorKeys.clear();

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));
    }

    public GenericAttributeUnit baseUnit() {
        return new GenericAttributeUnit("baseUnit", GenericAttributeUnit.FACTOR_BASE);
    }

    public GenericAttributeUnit nonBaseUnit() {
        return new GenericAttributeUnit("nonBaseUnit", GenericAttributeUnit.FACTOR_BASE + 1);
    }

    public void createAttribute(DefaultGenericAttributeType valueType, boolean unique, boolean indexed, boolean mandatory, GenericStructure structure, Double min, Double max,
            Double step, String pattern, String defaultValue, String defaultValueCallback, List<Long> valueProposalDependencies, List<GenericAttributeUnit> units) {
        attribute = new GenericAttribute(0l, 0, ATTRIBUTE_NAME, testType, valueType, unique, indexed, mandatory, structure, min, max, step, pattern, defaultValue,
                defaultValueCallback, valueProposalDependencies, units);
        type.getAttributes().add(attribute);
    }

    private void clearAttributes() {
        type.getAttributes().clear();
    }
}
