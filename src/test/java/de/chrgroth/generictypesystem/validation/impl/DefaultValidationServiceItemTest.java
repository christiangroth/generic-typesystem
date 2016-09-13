package de.chrgroth.generictypesystem.validation.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericAttributeType;
import de.chrgroth.generictypesystem.model.GenericAttributeUnit;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericStructure;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.model.UnitValue;
import de.chrgroth.generictypesystem.validation.BaseValidationServiceTest;
import de.chrgroth.generictypesystem.validation.ValidationMessageKey;

public class DefaultValidationServiceItemTest extends BaseValidationServiceTest {

    @Before
    public void setup() {
        service = new DefaultValidationService(null);
        type = new GenericType(0l, 0, "testType", "testGroup", null);
        item = new GenericItem(0l, type.getId(), null);
    }

    @Test
    public void nullItemWithNullType() {
        type = null;
        item = null;
        validateItem(DefaultValidationServiceMessageKey.GENERAL_TYPE_NOT_PROVIDED);
    }

    @Test
    public void nullItem() {
        item = null;
        validateItem(DefaultValidationServiceMessageKey.GENERAL_ITEM_NOT_PROVIDED);
    }

    @Test
    public void itemWithoutTypeId() {
        item.setGenericTypeId(null);
        validateItem(DefaultValidationServiceMessageKey.ITEM_TYPE_MANDATORY);
    }

    @Test
    public void itemWithMismatchingTypeId() {
        item.setGenericTypeId(type.getId() + 1);
        validateItem(DefaultValidationServiceMessageKey.ITEM_TYPE_DOES_NOT_MATCH);
    }

    @Test
    public void invalidType() {
        type.setName(null);
        validateItem(DefaultValidationServiceMessageKey.ITEM_TYPE_INVALID);
    }

    @Test
    public void noAttributesNoValues() {
        validateItem();
    }

    @Test
    public void unitValueForNonUnitBasedAttribute() {
        attribute(GenericAttributeType.DOUBLE, null, true, null);
        UnitValue value = new UnitValue();
        value.setUnit("minutes");
        value.setValue(1);
        value(value);
        validateItem(DefaultValidationServiceMessageKey.ITEM_VALUE_UNIT_BASED, DefaultValidationServiceMessageKey.ITEM_VALUE_UNIT_INVALID);
    }

    @Test
    public void nonUnitValueForUnitBasedAttribute() {
        GenericAttribute attribute = attribute(GenericAttributeType.DOUBLE, null, true, null);
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
        validateItem(DefaultValidationServiceMessageKey.ITEM_VALUE_NOT_UNIT_BASED);
    }

    @Test
    public void validUnitBasedValue() {
        GenericAttribute attribute = attribute(GenericAttributeType.DOUBLE, null, true, null);
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
        attribute(GenericAttributeType.DOUBLE, null, true, null);
        validateItem(DefaultValidationServiceMessageKey.ITEM_VALUE_MANDATORY);
    }

    @Test
    public void mandatoryDoubleValue() {
        attribute(GenericAttributeType.DOUBLE, null, true, null);
        value(2.0d);
        validateItem();
    }

    @Test
    public void missingMandatoryStringValue() {
        attribute(GenericAttributeType.STRING, null, true, null);
        validateItem(DefaultValidationServiceMessageKey.ITEM_VALUE_MANDATORY);
    }

    @Test
    public void emptyMandatoryStringValue() {
        attribute(GenericAttributeType.STRING, null, true, null);
        value(" ");
        validateItem(DefaultValidationServiceMessageKey.ITEM_VALUE_MANDATORY);
    }

    @Test
    public void mandatoryStringValue() {
        attribute(GenericAttributeType.STRING, null, true, null);
        value("foo");
        validateItem();
    }

    @Test
    public void undefinedAttribute() {
        value("value");
        validateItem(DefaultValidationServiceMessageKey.ITEM_ATTRIBUTE_UNDEFINED);
    }

    @Test
    public void valueTypeMismatch() {
        attribute(GenericAttributeType.STRING, null, true, null);
        value(2.0d);
        validateItem(DefaultValidationServiceMessageKey.ITEM_VALUE_TYPE_INVALID);
    }

    @Test
    public void collectionValueTypeMismatch() {
        attribute(GenericAttributeType.LIST, GenericAttributeType.STRING, false, null);
        value(new HashSet<>());
        validateItem(DefaultValidationServiceMessageKey.ITEM_VALUE_TYPE_INVALID);
    }

    @Test
    public void emptyMandatoryList() {
        attribute(GenericAttributeType.LIST, GenericAttributeType.STRING, true, null);
        value(new ArrayList<>());
        validateItem(DefaultValidationServiceMessageKey.ITEM_VALUE_MANDATORY);
    }

    @Test
    public void listElementTypeMismatch() {
        attribute(GenericAttributeType.LIST, GenericAttributeType.STRING, false, null);
        value(Arrays.asList(2.0d));
        validateItem(DefaultValidationServiceMessageKey.ITEM_LIST_VALUE_TYPE_INVALID);
    }

    @Test
    public void stringUndercut() {
        GenericAttribute a = attribute(GenericAttributeType.STRING, null, false, null);
        a.setMin(10.0d);
        value("012345");
        validateItem(DefaultValidationServiceMessageKey.ITEM_VALUE_MIN_UNDERCUT);
    }

    @Test
    public void stringExceeded() {
        GenericAttribute a = attribute(GenericAttributeType.STRING, null, false, null);
        a.setMax(10.0d);
        value("01234567890");
        validateItem(DefaultValidationServiceMessageKey.ITEM_VALUE_MAX_EXCEEDED);
    }

    @Test
    public void stringPatternMismatch() {
        GenericAttribute a = attribute(GenericAttributeType.STRING, null, false, null);
        a.setPattern("\\d+");
        value("12345abc");
        validateItem(DefaultValidationServiceMessageKey.ITEM_VALUE_PATTERN_VIOLATED);
    }

    @Test
    public void longIntegerMinUndercut() {
        assertNumericValue(GenericAttributeType.LONG, 2.0d, null, 1, DefaultValidationServiceMessageKey.ITEM_VALUE_MIN_UNDERCUT);
    }

    @Test
    public void longIntegerMaxExceeded() {
        assertNumericValue(GenericAttributeType.LONG, null, 2.0d, 3, DefaultValidationServiceMessageKey.ITEM_VALUE_MAX_EXCEEDED);
    }

    @Test
    public void longLongMinUndercut() {
        assertNumericValue(GenericAttributeType.LONG, 2.0d, null, 1l, DefaultValidationServiceMessageKey.ITEM_VALUE_MIN_UNDERCUT);
    }

    @Test
    public void longLongMaxExceeded() {
        assertNumericValue(GenericAttributeType.LONG, null, 2.0d, 3l, DefaultValidationServiceMessageKey.ITEM_VALUE_MAX_EXCEEDED);
    }

    @Test
    public void doubleIntegerMinUndercut() {
        assertNumericValue(GenericAttributeType.DOUBLE, 2.0d, null, 1, DefaultValidationServiceMessageKey.ITEM_VALUE_MIN_UNDERCUT);
    }

    @Test
    public void doubleIntegerMaxExceeded() {
        assertNumericValue(GenericAttributeType.DOUBLE, null, 2.0d, 3, DefaultValidationServiceMessageKey.ITEM_VALUE_MAX_EXCEEDED);
    }

    @Test
    public void doubleLongMinUndercut() {
        assertNumericValue(GenericAttributeType.DOUBLE, 2.0d, null, 1l, DefaultValidationServiceMessageKey.ITEM_VALUE_MIN_UNDERCUT);
    }

    @Test
    public void doubleLongMaxExceeded() {
        assertNumericValue(GenericAttributeType.DOUBLE, null, 2.0d, 3l, DefaultValidationServiceMessageKey.ITEM_VALUE_MAX_EXCEEDED);
    }

    @Test
    public void doubleFloatMinUndercut() {
        assertNumericValue(GenericAttributeType.DOUBLE, 2.0d, null, 1.0f, DefaultValidationServiceMessageKey.ITEM_VALUE_MIN_UNDERCUT);
    }

    @Test
    public void doubleFloatMaxExceeded() {
        assertNumericValue(GenericAttributeType.DOUBLE, null, 2.0d, 3.0f, DefaultValidationServiceMessageKey.ITEM_VALUE_MAX_EXCEEDED);
    }

    @Test
    public void doubleDoubleMinUndercut() {
        assertNumericValue(GenericAttributeType.DOUBLE, 2.0d, null, 1.0d, DefaultValidationServiceMessageKey.ITEM_VALUE_MIN_UNDERCUT);
    }

    @Test
    public void doubleDoubleMaxExceeded() {
        assertNumericValue(GenericAttributeType.DOUBLE, null, 2.0d, 3.0d, DefaultValidationServiceMessageKey.ITEM_VALUE_MAX_EXCEEDED);
    }

    private void assertNumericValue(GenericAttributeType attributeType, Double min, Double max, Object value, ValidationMessageKey... errorKeys) {
        GenericAttribute a = attribute(attributeType, null, false, null);
        a.setMin(min);
        a.setMax(max);
        value(value);
        validateItem(errorKeys);
    }

    private <T, K, V> GenericAttribute attribute(GenericAttributeType type, GenericAttributeType valueType, boolean mandatory, GenericStructure structure) {
        GenericAttribute a = new GenericAttribute(0l, 0, "name", type, valueType, false, false, mandatory, structure);
        this.type.getAttributes().add(a);
        return a;
    }

    private void value(Object value) {
        item.set("name", value);
    }
}
