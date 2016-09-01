package de.chrgroth.generictypesystem.validation;

import org.junit.Test;

import de.chrgroth.generictypesystem.TestUtils;
import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericStructure;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.model.GenericAttributeType;

public class ValidationServiceItemTest {

    private ValidationService service = new DefaultValidationService();
    private GenericType type = new GenericType(0l, 0, "testType", "testGroup", null);
    private GenericItem item = new GenericItem(0l, type.getId(), null);

    @Test
    public void nullItemWithNullType() {
        TestUtils.expectInvalidItem(service, null, null);
    }

    @Test
    public void nullItem() {
        TestUtils.expectInvalidItem(service, type, null);
    }

    @Test
    public void itemWithoutTypeId() {
        item.setGenericTypeId(null);
        TestUtils.expectInvalidItem(service, type, item);
    }

    @Test
    public void itemWithMismatchingTypeId() {
        item.setGenericTypeId(type.getId() + 1);
        TestUtils.expectInvalidItem(service, type, item);
    }

    @Test
    public void invalidType() {
        type.setName(null);
        TestUtils.expectInvalidItem(service, type, item);
    }

    @Test
    public void noAttributesNoValues() {
        TestUtils.expectValidItem(service, type, item);
    }

    @Test
    public void missingMandatoryDoubleValue() {
        attribute(GenericAttributeType.DOUBLE, null, null, true, null);
        TestUtils.expectInvalidItem(service, type, item);
    }

    @Test
    public void mandatoryDoubleValue() {
        attribute(GenericAttributeType.DOUBLE, null, null, true, null);
        value(2.0d);
        TestUtils.expectValidItem(service, type, item);
    }

    @Test
    public void missingMandatoryStringValue() {
        attribute(GenericAttributeType.STRING, null, null, true, null);
        TestUtils.expectInvalidItem(service, type, item);
    }

    @Test
    public void emptyMandatoryStringValue() {
        attribute(GenericAttributeType.STRING, null, null, true, null);
        value(" ");
        TestUtils.expectInvalidItem(service, type, item);
    }

    @Test
    public void mandatoryStringValue() {
        attribute(GenericAttributeType.STRING, null, null, true, null);
        value("foo");
        TestUtils.expectValidItem(service, type, item);
    }

    @Test
    public void undefinedAttribute() {
        value("value");
        TestUtils.expectInvalidItem(service, type, item);
    }

    private <T, K, V> void attribute(GenericAttributeType type, GenericAttributeType keyType, GenericAttributeType valueType, boolean mandatory, GenericStructure structure) {
        this.type.getAttributes().add(new GenericAttribute(0l, 0, "name", type, keyType, valueType, false, false, mandatory, structure));
    }

    private void value(Object value) {
        item.set("name", value);
    }
}
