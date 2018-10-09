package de.chrgroth.generictypesystem.validation.impl;

import org.junit.Before;
import org.junit.Test;

import de.chrgroth.generictypesystem.model.DefaultGenericAttributeType;
import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericStructure;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.model.GenericUnit;
import de.chrgroth.generictypesystem.model.GenericUnits;
import de.chrgroth.generictypesystem.model.GenericValue;
import de.chrgroth.generictypesystem.model.UnitValue;
import de.chrgroth.generictypesystem.validation.BaseValidationServiceTypeAndItemTest;
import de.chrgroth.generictypesystem.validation.ValidationError;

public class DefaultValidationServiceTypeAttributeDefaultValueTest extends BaseValidationServiceTypeAndItemTest {

    private static final String ATTRIBUTE_NAME = "dummy";

    private UnitsLookupTestHelper unitsLookupTestHelper;

    @Before
    public void setup() {
        unitsLookupTestHelper = new UnitsLookupTestHelper();
        service = new DefaultValidationService(unitsLookupTestHelper, null);
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

    @Test
    public void valueTypeMismatch() {

        // set data
        type.attribute(ATTRIBUTE_NAME).setType(DefaultGenericAttributeType.STRING);
        type.attribute(ATTRIBUTE_NAME).setDefaultValue(new GenericValue<>(Long.class, 12l));

        // validate type
        validateType(new ValidationError[] { new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_DEFAULT_VALUE_TYPE_INVALID, Long.class) });
    }

    @Test
    public void unitBasedValue() {

        // set data
        type.attribute(ATTRIBUTE_NAME).setType(DefaultGenericAttributeType.LONG);
        GenericUnits units = timeUnits();
        type.attribute(ATTRIBUTE_NAME).setUnitsId(units.getId());
        type.attribute(ATTRIBUTE_NAME).setDefaultValue(new GenericValue<>(UnitValue.class, new UnitValue(0l, 0l, new GenericValue<>(Long.class, 12l))));

        // validate type
        validateType(new ValidationError[] {});
    }

    @Test
    public void unitBasedValueUnknownUnits() {

        // set data
        type.attribute(ATTRIBUTE_NAME).setType(DefaultGenericAttributeType.LONG);
        GenericUnits units = timeUnits();
        type.attribute(ATTRIBUTE_NAME).setUnitsId(units.getId());
        type.attribute(ATTRIBUTE_NAME).setDefaultValue(new GenericValue<>(UnitValue.class, new UnitValue(1l, 0l, new GenericValue<>(Long.class, 12l))));

        // validate type
        validateType(new ValidationError[] { new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_DEFAULT_VALUE_INVALID_UNITS) });
    }

    @Test
    public void unitBasedValueUnknownUnit() {

        // set data
        type.attribute(ATTRIBUTE_NAME).setType(DefaultGenericAttributeType.LONG);
        GenericUnits units = timeUnits();
        type.attribute(ATTRIBUTE_NAME).setUnitsId(units.getId());
        type.attribute(ATTRIBUTE_NAME).setDefaultValue(new GenericValue<>(UnitValue.class, new UnitValue(0l, 2l, new GenericValue<>(Long.class, 12l))));

        // validate type
        validateType(new ValidationError[] { new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_DEFAULT_VALUE_INVALID_UNIT) });
    }

    @Test
    public void unitBasedValueNoUnitValue() {

        // set data
        type.attribute(ATTRIBUTE_NAME).setType(DefaultGenericAttributeType.LONG);
        GenericUnits units = timeUnits();
        type.attribute(ATTRIBUTE_NAME).setUnitsId(units.getId());
        type.attribute(ATTRIBUTE_NAME).setDefaultValue(new GenericValue<>(Long.class, 12l));

        // validate type
        validateType(new ValidationError[] { new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_DEFAULT_VALUE_NOT_UNIT_BASED) });
    }

    private GenericUnits timeUnits() {
        GenericUnits units = new GenericUnits(0l, "time", "time stuff");
        units.getUnits().add(new GenericUnit(0l, "seconds", "s", GenericUnits.FACTOR_BASE));
        units.getUnits().add(new GenericUnit(1l, "minutes", "m", 60));
        unitsLookupTestHelper.register(units);
        return units;
    }
}
