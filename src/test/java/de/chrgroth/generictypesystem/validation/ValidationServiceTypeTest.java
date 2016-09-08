package de.chrgroth.generictypesystem.validation;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import de.chrgroth.generictypesystem.TestUtils;
import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericAttributeType;
import de.chrgroth.generictypesystem.model.GenericType;

public class ValidationServiceTypeTest {

    private ValidationService service = new DefaultValidationService(new NullDefaultValidationServiceHooks());
    private GenericAttribute attribute = new GenericAttribute(0l, 0, "foo", GenericAttributeType.STRING, null, null, false, false, false, null);
    private GenericType type = new GenericType(0l, 0, "testType", "testGroup", new HashSet<>(Arrays.asList(attribute)));

    @Test
    public void nullType() {
        type = null;
        TestUtils.expectInvalidType(service, type);
    }

    @Test
    public void nullName() {
        type.setName(null);
        TestUtils.expectInvalidType(service, type);
    }

    @Test
    public void emptyName() {
        type.setName("");
        TestUtils.expectInvalidType(service, type);
    }

    @Test
    public void noAttributes() {
        type.getAttributes().clear();
        TestUtils.expectValidType(service, type);
    }

    @Test
    public void attributeNullId() {
        attribute.setId(null);
        TestUtils.expectInvalidType(service, type);
    }

    @Test
    public void attributeAmbigiousId() {
        type.getAttributes().add(new GenericAttribute(0l, 1, "some other", GenericAttributeType.LONG));
        TestUtils.expectInvalidType(service, type);
    }

    @Test
    public void attributeNullName() {
        attribute.setName(null);
        TestUtils.expectInvalidType(service, type);
    }

    @Test
    public void attributeEmptyName() {
        attribute.setName("");
        TestUtils.expectInvalidType(service, type);
    }

    @Test
    public void attributeDottedName() {
        attribute.setName("foo.bar");
        TestUtils.expectInvalidType(service, type);
    }

    @Test
    public void attributeNoType() {
        attribute.setType(null);
        TestUtils.expectInvalidType(service, type);
    }

    @Test
    public void attributeUniqueNotMandatory() {
        attribute.setUnique(true);
        TestUtils.expectInvalidType(service, type);
    }

    @Test
    public void attributeUniqueAndMandatory() {
        attribute.setUnique(true);
        attribute.setMandatory(true);
        TestUtils.expectValidType(service, type);
    }

    @Test
    public void attributeIndexedNotMandatory() {
        attribute.setIndexed(true);
        TestUtils.expectValidType(service, type);
    }

    @Test
    public void attributeIndexedAndMandatory() {
        attribute.setIndexed(true);
        attribute.setMandatory(true);
        TestUtils.expectValidType(service, type);
    }
}
