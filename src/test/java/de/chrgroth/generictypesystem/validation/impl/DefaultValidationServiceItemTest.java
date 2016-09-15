package de.chrgroth.generictypesystem.validation.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import de.chrgroth.generictypesystem.model.DefaultGenericAttributeType;
import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericAttributeUnit;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericStructure;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.model.UnitValue;
import de.chrgroth.generictypesystem.validation.BaseValidationServiceTest;
import de.chrgroth.generictypesystem.validation.ValidationError;

public class DefaultValidationServiceItemTest extends BaseValidationServiceTest {

    private static final String ATTRIBUTE_NAME = "name";

    @Before
    public void setup() {
        service = new DefaultValidationService(null);
        type = new GenericType(0l, 0, "testType", "testGroup", null, null, null, null, null, null);
        item = new GenericItem(0l, type.getId(), null, null, null);
    }

    @Test
    public void nullItemWithNullType() {
        type = null;
        item = null;
        validateItem(new ValidationError("", DefaultValidationServiceMessageKey.GENERAL_TYPE_NOT_PROVIDED));
    }

    @Test
    public void nullItem() {
        item = null;
        validateItem(new ValidationError("", DefaultValidationServiceMessageKey.GENERAL_ITEM_NOT_PROVIDED));
    }

    @Test
    public void itemWithoutTypeId() {
        item.setTypeId(null);
        validateItem(new ValidationError("", DefaultValidationServiceMessageKey.ITEM_TYPE_MANDATORY));
    }

    @Test
    public void itemWithMismatchingTypeId() {
        item.setTypeId(type.getId() + 1);
        validateItem(new ValidationError("", DefaultValidationServiceMessageKey.ITEM_TYPE_DOES_NOT_MATCH));
    }

    @Test
    public void invalidType() {
        type.setName(null);
        validateItem(new ValidationError("", DefaultValidationServiceMessageKey.ITEM_TYPE_INVALID));
    }

    @Test
    public void noAttributesNoValues() {
        validateItem();
    }

    @Test
    public void unitValueForNonUnitBasedAttribute() {
        attribute(DefaultGenericAttributeType.DOUBLE, null, true, null);
        UnitValue value = new UnitValue();
        value.setUnit("minutes");
        value.setValue(1);
        value(value);
        validateItem(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.ITEM_VALUE_UNIT_BASED),
                new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.ITEM_VALUE_UNIT_INVALID));
    }

    @Test
    public void nonUnitValueForUnitBasedAttribute() {
        GenericAttribute attribute = attribute(DefaultGenericAttributeType.DOUBLE, null, true, null);
        attribute.setUnits(new ArrayList<>());
        GenericAttributeUnit minutesUnit = new GenericAttributeUnit();
        minutesUnit.setName("minutes");
        minutesUnit.setFactor(60);
        attribute.getUnits().add(minutesUnit);
        GenericAttributeUnit secondsUnit = new GenericAttributeUnit();
        secondsUnit.setName("seconds");
        secondsUnit.setFactor(GenericAttributeUnit.FACTOR_BASE);
        attribute.getUnits().add(secondsUnit);
        value(60);
        validateItem(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.ITEM_VALUE_NOT_UNIT_BASED));
    }

    @Test
    public void validUnitBasedValue() {
        GenericAttribute attribute = attribute(DefaultGenericAttributeType.DOUBLE, null, true, null);
        attribute.setUnits(new ArrayList<>());
        GenericAttributeUnit minutesUnit = new GenericAttributeUnit();
        minutesUnit.setName("minutes");
        minutesUnit.setFactor(60);
        attribute.getUnits().add(minutesUnit);
        GenericAttributeUnit secondsUnit = new GenericAttributeUnit();
        secondsUnit.setName("seconds");
        secondsUnit.setFactor(GenericAttributeUnit.FACTOR_BASE);
        attribute.getUnits().add(secondsUnit);
        UnitValue value = new UnitValue();
        value.setUnit("minutes");
        value.setValue(1);
        value(value);
        validateItem();
    }

    @Test
    public void missingMandatoryDoubleValue() {
        attribute(DefaultGenericAttributeType.DOUBLE, null, true, null);
        validateItem(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.ITEM_VALUE_MANDATORY));
    }

    @Test
    public void mandatoryDoubleValue() {
        attribute(DefaultGenericAttributeType.DOUBLE, null, true, null);
        value(2.0d);
        validateItem();
    }

    @Test
    public void missingMandatoryStringValue() {
        attribute(DefaultGenericAttributeType.STRING, null, true, null);
        validateItem(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.ITEM_VALUE_MANDATORY));
    }

    @Test
    public void emptyMandatoryStringValue() {
        attribute(DefaultGenericAttributeType.STRING, null, true, null);
        value(" ");
        validateItem(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.ITEM_VALUE_MANDATORY));
    }

    @Test
    public void mandatoryStringValue() {
        attribute(DefaultGenericAttributeType.STRING, null, true, null);
        value("foo");
        validateItem();
    }

    @Test
    public void undefinedAttribute() {
        value("value");
        validateItem(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.ITEM_ATTRIBUTE_UNDEFINED));
    }

    @Test
    public void valueTypeMismatch() {
        attribute(DefaultGenericAttributeType.STRING, null, true, null);
        value(2.0d);
        validateItem(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.ITEM_VALUE_TYPE_INVALID, DefaultGenericAttributeType.STRING.toString(),
                Double.class.getName()));
    }

    @Test
    public void collectionValueTypeMismatch() {
        attribute(DefaultGenericAttributeType.LIST, DefaultGenericAttributeType.STRING, false, null);
        value(new HashSet<>());
        validateItem(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.ITEM_VALUE_TYPE_INVALID, DefaultGenericAttributeType.LIST.toString(),
                HashSet.class.getName()));
    }

    @Test
    public void emptyMandatoryList() {
        attribute(DefaultGenericAttributeType.LIST, DefaultGenericAttributeType.STRING, true, null);
        value(new ArrayList<>());
        validateItem(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.ITEM_VALUE_MANDATORY));
    }

    @Test
    public void listElementTypeMismatch() {
        attribute(DefaultGenericAttributeType.LIST, DefaultGenericAttributeType.STRING, false, null);
        value(Arrays.asList(2.0d));
        validateItem(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.ITEM_LIST_VALUE_TYPE_INVALID, DefaultGenericAttributeType.STRING.toString()));
    }

    @Test
    public void stringUndercut() {
        GenericAttribute a = attribute(DefaultGenericAttributeType.STRING, null, false, null);
        a.setMin(10.0d);
        value("012345");
        validateItem(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.ITEM_VALUE_MIN_UNDERCUT, new Double(10.0d)));
    }

    @Test
    public void stringExceeded() {
        GenericAttribute a = attribute(DefaultGenericAttributeType.STRING, null, false, null);
        a.setMax(10.0d);
        value("01234567890");
        validateItem(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.ITEM_VALUE_MAX_EXCEEDED, 10.0d));
    }

    @Test
    public void stringPatternMismatch() {
        GenericAttribute a = attribute(DefaultGenericAttributeType.STRING, null, false, null);
        a.setPattern("\\d+");
        value("12345abc");
        validateItem(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.ITEM_VALUE_PATTERN_VIOLATED, "\\d+"));
    }

    @Test
    public void longIntegerMinUndercut() {
        assertNumericValue(DefaultGenericAttributeType.LONG, 2.0d, null, 1, new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.ITEM_VALUE_MIN_UNDERCUT, 2.0d));
    }

    @Test
    public void longIntegerMaxExceeded() {
        assertNumericValue(DefaultGenericAttributeType.LONG, null, 2.0d, 3, new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.ITEM_VALUE_MAX_EXCEEDED, 2.0d));
    }

    @Test
    public void longLongMinUndercut() {
        assertNumericValue(DefaultGenericAttributeType.LONG, 2.0d, null, 1l, new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.ITEM_VALUE_MIN_UNDERCUT, 2.0d));
    }

    @Test
    public void longLongMaxExceeded() {
        assertNumericValue(DefaultGenericAttributeType.LONG, null, 2.0d, 3l, new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.ITEM_VALUE_MAX_EXCEEDED, 2.0d));
    }

    @Test
    public void doubleIntegerMinUndercut() {
        assertNumericValue(DefaultGenericAttributeType.DOUBLE, 2.0d, null, 1,
                new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.ITEM_VALUE_MIN_UNDERCUT, 2.0d));
    }

    @Test
    public void doubleIntegerMaxExceeded() {
        assertNumericValue(DefaultGenericAttributeType.DOUBLE, null, 2.0d, 3,
                new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.ITEM_VALUE_MAX_EXCEEDED, 2.0d));
    }

    @Test
    public void doubleLongMinUndercut() {
        assertNumericValue(DefaultGenericAttributeType.DOUBLE, 2.0d, null, 1l,
                new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.ITEM_VALUE_MIN_UNDERCUT, 2.0d));
    }

    @Test
    public void doubleLongMaxExceeded() {
        assertNumericValue(DefaultGenericAttributeType.DOUBLE, null, 2.0d, 3l,
                new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.ITEM_VALUE_MAX_EXCEEDED, 2.0d));
    }

    @Test
    public void doubleFloatMinUndercut() {
        assertNumericValue(DefaultGenericAttributeType.DOUBLE, 2.0d, null, 1.0f,
                new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.ITEM_VALUE_MIN_UNDERCUT, 2.0d));
    }

    @Test
    public void doubleFloatMaxExceeded() {
        assertNumericValue(DefaultGenericAttributeType.DOUBLE, null, 2.0d, 3.0f,
                new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.ITEM_VALUE_MAX_EXCEEDED, 2.0d));
    }

    @Test
    public void doubleDoubleMinUndercut() {
        assertNumericValue(DefaultGenericAttributeType.DOUBLE, 2.0d, null, 1.0d,
                new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.ITEM_VALUE_MIN_UNDERCUT, 2.0d));
    }

    @Test
    public void doubleDoubleMaxExceeded() {
        assertNumericValue(DefaultGenericAttributeType.DOUBLE, null, 2.0d, 3.0d,
                new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.ITEM_VALUE_MAX_EXCEEDED, 2.0d));
    }

    private void assertNumericValue(DefaultGenericAttributeType attributeType, Double min, Double max, Object value, ValidationError... errors) {
        GenericAttribute a = attribute(attributeType, null, false, null);
        a.setMin(min);
        a.setMax(max);
        value(value);
        validateItem(errors);
    }

    private <T, K, V> GenericAttribute attribute(DefaultGenericAttributeType type, DefaultGenericAttributeType valueType, boolean mandatory, GenericStructure structure) {
        GenericAttribute a = new GenericAttribute(0l, 0, ATTRIBUTE_NAME, type, valueType, false, false, mandatory, structure, null, null, null, null, null, null, null, null);
        this.type.getAttributes().add(a);
        return a;
    }

    private void value(Object value) {
        item.set(ATTRIBUTE_NAME, value);
    }
}
