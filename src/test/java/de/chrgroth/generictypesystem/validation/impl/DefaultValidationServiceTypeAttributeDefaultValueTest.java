package de.chrgroth.generictypesystem.validation.impl;

import org.junit.Before;
import org.junit.Test;

import de.chrgroth.generictypesystem.model.DefaultGenericAttributeType;
import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericStructure;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.model.GenericValue;
import de.chrgroth.generictypesystem.validation.BaseValidationServiceTest;
import de.chrgroth.generictypesystem.validation.ValidationError;

// TODO recap all unit tests regarding default values to increase test coverage
public class DefaultValidationServiceTypeAttributeDefaultValueTest extends BaseValidationServiceTest {

    private static final String ATTRIBUTE_NAME = "dummy";

    @Before
    public void setup() {
        service = new DefaultValidationService(null);
        type = new GenericType(0l, "testType", "testGroup", null, null, null, null);
        attribute = new GenericAttribute(0l, ATTRIBUTE_NAME, null, null, false, false, null, null, null, null, null, null, null, null, null);
        type.getAttributes().add(attribute);
    }

    @Test
    public void defaultValueBoolean() {

        // set data
        type.attribute(ATTRIBUTE_NAME).setType(DefaultGenericAttributeType.BOOLEAN);
        type.attribute(ATTRIBUTE_NAME).setDefaultValue(new GenericValue<>(Boolean.class, Boolean.TRUE));

        // validate type
        validateType(new ValidationError[] {});
    }

    @Test
    public void defaultValueString() {

        // set data
        type.attribute(ATTRIBUTE_NAME).setType(DefaultGenericAttributeType.STRING);
        type.attribute(ATTRIBUTE_NAME).setDefaultValue(new GenericValue<>(String.class, "foo"));

        // validate type
        validateType(new ValidationError[] {});
    }

    @Test
    public void defaultValueStringMinUndercut() {

        // set data
        type.attribute(ATTRIBUTE_NAME).setType(DefaultGenericAttributeType.STRING);
        type.attribute(ATTRIBUTE_NAME).setDefaultValue(new GenericValue<>(String.class, "foo"));
        type.attribute(ATTRIBUTE_NAME).setMin(5.0d);

        // validate type
        validateType(new ValidationError[] { new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_DEFAULT_VALUE_STRING_MIN_UNDERCUT, 5.0d) });
    }

    @Test
    public void defaultValueStringMaxExceeded() {

        // set data
        type.attribute(ATTRIBUTE_NAME).setType(DefaultGenericAttributeType.STRING);
        type.attribute(ATTRIBUTE_NAME).setDefaultValue(new GenericValue<>(String.class, "foo"));
        type.attribute(ATTRIBUTE_NAME).setMax(2.0d);

        // validate type
        validateType(new ValidationError[] { new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_DEFAULT_VALUE_STRING_MAX_EXCEEDED, 2.0d) });
    }

    @Test
    public void defaultValueStringPatternViolated() {

        // set data
        type.attribute(ATTRIBUTE_NAME).setType(DefaultGenericAttributeType.STRING);
        type.attribute(ATTRIBUTE_NAME).setDefaultValue(new GenericValue<>(String.class, "foo"));
        type.attribute(ATTRIBUTE_NAME).setPattern("\\d+");

        // validate type
        validateType(
                new ValidationError[] { new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_DEFAULT_VALUE_STRING_PATTERN_VIOLATED, "\\d+") });
    }

    @Test
    public void defaultValueLongMinUndercut() {

        // set data
        type.attribute(ATTRIBUTE_NAME).setType(DefaultGenericAttributeType.LONG);
        type.attribute(ATTRIBUTE_NAME).setDefaultValue(new GenericValue<>(Long.class, -4l));
        type.attribute(ATTRIBUTE_NAME).setMin(5.0d);

        // validate type
        validateType(new ValidationError[] { new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_DEFAULT_VALUE_LONG_MIN_UNDERCUT, 5.0d) });
    }

    @Test
    public void defaultValueLongMaxExceeded() {

        // set data
        type.attribute(ATTRIBUTE_NAME).setType(DefaultGenericAttributeType.LONG);
        type.attribute(ATTRIBUTE_NAME).setDefaultValue(new GenericValue<>(Long.class, 4l));
        type.attribute(ATTRIBUTE_NAME).setMax(2.0d);

        // validate type
        validateType(new ValidationError[] { new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_DEFAULT_VALUE_LONG_MAX_EXCEEDED, 2.0d) });
    }

    @Test
    public void defaultValueDoubleMinUndercut() {

        // set data
        type.attribute(ATTRIBUTE_NAME).setType(DefaultGenericAttributeType.DOUBLE);
        type.attribute(ATTRIBUTE_NAME).setDefaultValue(new GenericValue<>(Double.class, 4.0d));
        type.attribute(ATTRIBUTE_NAME).setMin(5.0d);

        // validate type
        validateType(new ValidationError[] { new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_DEFAULT_VALUE_DOUBLE_MIN_UNDERCUT, 5.0d) });
    }

    @Test
    public void defaultValueDoubleMaxExceeded() {

        // set data
        type.attribute(ATTRIBUTE_NAME).setType(DefaultGenericAttributeType.DOUBLE);
        type.attribute(ATTRIBUTE_NAME).setDefaultValue(new GenericValue<>(Double.class, 4.0d));
        type.attribute(ATTRIBUTE_NAME).setMax(2.0d);

        // validate type
        validateType(new ValidationError[] { new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_DEFAULT_VALUE_DOUBLE_MAX_EXCEEDED, 2.0d) });
    }

    @Test
    public void defaultValueDate() {

        // set data
        type.attribute(ATTRIBUTE_NAME).setType(DefaultGenericAttributeType.DATE);
        type.attribute(ATTRIBUTE_NAME).setDefaultValue(new GenericValue<>(String.class, "01.01.2016"));

        // validate type
        validateType(new ValidationError[] { new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_DEFAULT_VALUE_NOT_ALLOWED) });
    }

    @Test
    public void defaultValueDatetime() {

        // set data
        type.attribute(ATTRIBUTE_NAME).setType(DefaultGenericAttributeType.DATETIME);
        type.attribute(ATTRIBUTE_NAME).setDefaultValue(new GenericValue<>(String.class, "01.01.2016 12:00:00.000"));

        // validate type
        validateType(new ValidationError[] { new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_DEFAULT_VALUE_NOT_ALLOWED) });
    }

    @Test
    public void defaultValueTime() {

        // set data
        type.attribute(ATTRIBUTE_NAME).setType(DefaultGenericAttributeType.TIME);
        type.attribute(ATTRIBUTE_NAME).setDefaultValue(new GenericValue<>(String.class, "12:00:00.000"));

        // validate type
        validateType(new ValidationError[] { new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_DEFAULT_VALUE_NOT_ALLOWED) });
    }

    @Test
    public void defaultValueList() {

        // set data
        type.attribute(ATTRIBUTE_NAME).setType(DefaultGenericAttributeType.LIST);
        type.attribute(ATTRIBUTE_NAME).setValueType(DefaultGenericAttributeType.STRING);
        type.attribute(ATTRIBUTE_NAME).setDefaultValue(new GenericValue<>(String.class, "[foo]"));

        // validate type
        validateType(new ValidationError[] { new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_DEFAULT_VALUE_NOT_ALLOWED) });
    }

    @Test
    public void defaultValueStructure() {

        // set data
        type.attribute(ATTRIBUTE_NAME).setType(DefaultGenericAttributeType.STRUCTURE);
        type.attribute(ATTRIBUTE_NAME).setStructure(new GenericStructure());
        type.attribute(ATTRIBUTE_NAME).setDefaultValue(new GenericValue<>(String.class, "{foo:bar}"));

        // validate type
        validateType(new ValidationError[] { new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_DEFAULT_VALUE_NOT_ALLOWED) });
    }
}
