package de.chrgroth.generictypesystem.validation.impl;

import java.util.ArrayList;
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
import de.chrgroth.generictypesystem.model.GenericAttributeUnit;
import de.chrgroth.generictypesystem.model.GenericStructure;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.validation.BaseValidationServiceTest;
import de.chrgroth.generictypesystem.validation.ValidationError;

@RunWith(Parameterized.class)
public class DefaultValidationServiceTypeAttributeTypeTest extends BaseValidationServiceTest {

    private static final String ATTRIBUTE_NAME = "dummy";

    @Before
    public void setup() {
        service = new DefaultValidationService(null);
        type = new GenericType(0l, "testType", "testGroup", null, null, null, null);
    }

    @Parameters(name = "attribute type {0} value type {1}")
    public static Iterable<DefaultGenericAttributeType[]> data() {
        List<DefaultGenericAttributeType[]> data = new ArrayList<>();
        for (DefaultGenericAttributeType type : DefaultGenericAttributeType.values()) {
            for (DefaultGenericAttributeType valueType : DefaultGenericAttributeType.values()) {
                data.add(new DefaultGenericAttributeType[] { type, valueType });
            }
        }
        return data;
    }

    @Parameter(value = 0)
    public DefaultGenericAttributeType testType;

    @Parameter(value = 1)
    public DefaultGenericAttributeType testValueType;

    @Test
    public void attributeTypeTest() {

        // create attribute with type only
        createAttribute(null, false, false, false, null, null, null, null, null, null, null, null, null);

        // define expected error message keys
        List<ValidationError> errorKeys = new ArrayList<>();
        if (testType.isList()) {
            errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_LIST_VALUE_TYPE_MANDATORY));
        } else if (testType.isStructure()) {
            errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_STRUCTURE_STRUCTURE_MANDATORY));
        }

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));

        // create attribute with value type
        clearAttributes();
        createAttribute(testValueType, false, false, false, null, null, null, null, null, null, null, null, null);

        // define expected error message keys
        errorKeys = new ArrayList<>();
        if (testType.isList()) {
            if (testValueType.isList()) {
                errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_LIST_NESTED_LISTS_NOT_ALLOWED));
            } else if (testValueType.isStructure()) {
                errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_LIST_STRUCTURE_MANDATORY));
            }
        } else {
            if (testType.isStructure()) {
                errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_STRUCTURE_STRUCTURE_MANDATORY));
                errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_STRUCTURE_VALUE_TYPE_NOT_ALLOWED));
            } else {
                errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_VALUE_TYPE_NOT_ALLOWED));
            }
        }

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));

        // create attribute with structure
        clearAttributes();
        createAttribute(null, false, false, false, new GenericStructure(), null, null, null, null, null, null, null, null);

        // define expected error message keys
        errorKeys = new ArrayList<>();
        if (testType.isList()) {
            errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_LIST_VALUE_TYPE_MANDATORY));
        } else if (!testType.isStructure()) {
            errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_STRUCTURE_NOT_ALLOWED));
        }

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));

        // create attribute with value type and structure
        clearAttributes();
        createAttribute(testValueType, false, false, false, new GenericStructure(), null, null, null, null, null, null, null, null);

        // define expected error message keys
        errorKeys = new ArrayList<>();
        if (testType.isList()) {
            if (testValueType.isList()) {
                errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_LIST_NESTED_LISTS_NOT_ALLOWED));
            }
            if (!testValueType.isStructure()) {
                errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_LIST_STRUCTURE_NOT_ALLOWED, testValueType.toString()));
            }
        } else if (testType.isStructure()) {
            errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_STRUCTURE_VALUE_TYPE_NOT_ALLOWED));
        } else {
            errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_VALUE_TYPE_NOT_ALLOWED));
            errorKeys.add(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_STRUCTURE_NOT_ALLOWED));
        }

        // validate type
        validateType(errorKeys.toArray(new ValidationError[errorKeys.size()]));
    }

    public void createAttribute(DefaultGenericAttributeType valueType, boolean unique, boolean indexed, boolean mandatory, GenericStructure structure, Double min, Double max,
            Double step, String pattern, String defaultValue, String defaultValueCallback, Set<Long> valueProposalDependencies, Set<GenericAttributeUnit> units) {
        attribute = new GenericAttribute(0l, ATTRIBUTE_NAME, testType, valueType, unique, indexed, mandatory, structure, min, max, step, pattern, defaultValue,
                defaultValueCallback, valueProposalDependencies, units);
        type.getAttributes().add(attribute);
    }

    private void clearAttributes() {
        type.getAttributes().clear();
    }
}
