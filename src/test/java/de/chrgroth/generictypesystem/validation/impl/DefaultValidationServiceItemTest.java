package de.chrgroth.generictypesystem.validation.impl;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericAttributeType;
import de.chrgroth.generictypesystem.model.GenericAttributeUnit;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericStructure;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.model.UnitValue;
import de.chrgroth.generictypesystem.validation.ValidationResult;
import de.chrgroth.generictypesystem.validation.ValidationService;

// TODO assert message keys
public class DefaultValidationServiceItemTest {

    private ValidationService service;
    private GenericType type;
    private GenericItem item;

    @Before
    public void setup() {
        service = new DefaultValidationService(null);
        type = new GenericType(0l, 0, "testType", "testGroup", null);
        item = new GenericItem(0l, type.getId(), null);
    }

    @Test
    public void nullItemWithNullType() {
        expectInvalidItem(service, null, null);
    }

    @Test
    public void nullItem() {
        expectInvalidItem(service, type, null);
    }

    @Test
    public void itemWithoutTypeId() {
        item.setGenericTypeId(null);
        expectInvalidItem(service, type, item);
    }

    @Test
    public void itemWithMismatchingTypeId() {
        item.setGenericTypeId(type.getId() + 1);
        expectInvalidItem(service, type, item);
    }

    @Test
    public void invalidType() {
        type.setName(null);
        expectInvalidItem(service, type, item);
    }

    @Test
    public void noAttributesNoValues() {
        expectValidItem(service, type, item);
    }

    @Test
    public void unitValueForNonUnitBasedAttribute() {
        attribute(GenericAttributeType.DOUBLE, null, null, true, null);
        UnitValue value = new UnitValue();
        value.setUnit("minutes");
        value.setValue(1);
        value(value);
        expectInvalidItem(service, type, item);
    }

    @Test
    public void nonUnitValueForUnitBasedAttribute() {
        GenericAttribute attribute = attribute(GenericAttributeType.DOUBLE, null, null, true, null);
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
        expectInvalidItem(service, type, item);
    }

    @Test
    public void validUnitBasedValue() {
        GenericAttribute attribute = attribute(GenericAttributeType.DOUBLE, null, null, true, null);
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
        expectValidItem(service, type, item);
    }

    @Test
    public void missingMandatoryDoubleValue() {
        attribute(GenericAttributeType.DOUBLE, null, null, true, null);
        expectInvalidItem(service, type, item);
    }

    @Test
    public void mandatoryDoubleValue() {
        attribute(GenericAttributeType.DOUBLE, null, null, true, null);
        value(2.0d);
        expectValidItem(service, type, item);
    }

    @Test
    public void missingMandatoryStringValue() {
        attribute(GenericAttributeType.STRING, null, null, true, null);
        expectInvalidItem(service, type, item);
    }

    @Test
    public void emptyMandatoryStringValue() {
        attribute(GenericAttributeType.STRING, null, null, true, null);
        value(" ");
        expectInvalidItem(service, type, item);
    }

    @Test
    public void mandatoryStringValue() {
        attribute(GenericAttributeType.STRING, null, null, true, null);
        value("foo");
        expectValidItem(service, type, item);
    }

    @Test
    public void undefinedAttribute() {
        value("value");
        expectInvalidItem(service, type, item);
    }

    private <T, K, V> GenericAttribute attribute(GenericAttributeType type, GenericAttributeType keyType, GenericAttributeType valueType, boolean mandatory,
            GenericStructure structure) {
        GenericAttribute a = new GenericAttribute(0l, 0, "name", type, keyType, valueType, false, false, mandatory, structure);
        this.type.getAttributes().add(a);
        return a;
    }

    private void value(Object value) {
        item.set("name", value);
    }

    public static void expectValidItem(ValidationService service, GenericType type, GenericItem item) {
        ValidationResult<GenericItem> result = validateItem(service, type, item);
        Assert.assertTrue("got errors: " + result.getErrors(), result.isValid());
    }

    public static void expectInvalidItem(ValidationService service, GenericType type, GenericItem item) {
        Assert.assertFalse("got no errors", validateItem(service, type, item).isValid());
    }

    public static ValidationResult<GenericItem> validateItem(ValidationService service, GenericType type, GenericItem item) {
        ValidationResult<GenericItem> result = service.validate(type, item);
        Assert.assertEquals(item, result.getItem());
        return result;
    }
}
