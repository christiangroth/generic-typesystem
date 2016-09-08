package de.chrgroth.generictypesystem.validation.impl;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericAttributeType;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.validation.ValidationResult;
import de.chrgroth.generictypesystem.validation.ValidationService;

// TODO assert message keys
public class DefaultValidationServiceTypeTest {

    private ValidationService service;

    private GenericType type;
    private GenericAttribute attribute;

    @Before
    public void setup() {
        service = new DefaultValidationService(null);
        attribute = new GenericAttribute(0l, 0, "foo", GenericAttributeType.STRING, null, null, false, false, false, null);
        type = new GenericType(0l, 0, "testType", "testGroup", new HashSet<>(Arrays.asList(attribute)));
    }

    @Test
    public void nullType() {
        type = null;
        expectInvalidType(service, type);
    }

    @Test
    public void nullName() {
        type.setName(null);
        expectInvalidType(service, type);
    }

    @Test
    public void emptyName() {
        type.setName("");
        expectInvalidType(service, type);
    }

    @Test
    public void nullGroup() {
        type.setGroup(null);
        expectInvalidType(service, type);
    }

    @Test
    public void emptyGroup() {
        type.setGroup("");
        expectInvalidType(service, type);
    }

    @Test
    public void pageSizeZero() {
        type.setPageSize(0);
        expectInvalidType(service, type);
    }

    @Test
    public void pageSizeNegative() {
        type.setPageSize(-1);
        expectInvalidType(service, type);
    }

    @Test
    public void noAttributes() {
        type.getAttributes().clear();
        expectValidType(service, type);
    }

    @Test
    public void attributeNullId() {
        attribute.setId(null);
        expectInvalidType(service, type);
    }

    @Test
    public void attributeAmbigiousId() {
        type.getAttributes().add(new GenericAttribute(0l, 1, "some other", GenericAttributeType.LONG));
        expectInvalidType(service, type);
    }

    @Test
    public void attributeNullName() {
        attribute.setName(null);
        expectInvalidType(service, type);
    }

    @Test
    public void attributeEmptyName() {
        attribute.setName("");
        expectInvalidType(service, type);
    }

    @Test
    public void attributeDottedName() {
        attribute.setName("foo.bar");
        expectInvalidType(service, type);
    }

    @Test
    public void attributeNoType() {
        attribute.setType(null);
        expectInvalidType(service, type);
    }

    @Test
    public void attributeUniqueNotMandatory() {
        attribute.setUnique(true);
        expectInvalidType(service, type);
    }

    @Test
    public void attributeUniqueAndMandatory() {
        attribute.setUnique(true);
        attribute.setMandatory(true);
        expectValidType(service, type);
    }

    private void expectValidType(ValidationService service, GenericType type) {
        ValidationResult<GenericType> result = validateType(service, type);
        Assert.assertTrue("got errors: " + result.getErrors(), result.isValid());
    }

    private void expectInvalidType(ValidationService service, GenericType type) {
        Assert.assertFalse("got no errors", validateType(service, type).isValid());
    }

    private ValidationResult<GenericType> validateType(ValidationService service, GenericType type) {
        ValidationResult<GenericType> result = service.validate(type);
        Assert.assertEquals(type, result.getItem());
        return result;
    }
}
