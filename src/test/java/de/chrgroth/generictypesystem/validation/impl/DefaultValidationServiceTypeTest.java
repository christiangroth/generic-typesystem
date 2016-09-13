package de.chrgroth.generictypesystem.validation.impl;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericAttributeType;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.validation.BaseValidationServiceTest;

public class DefaultValidationServiceTypeTest extends BaseValidationServiceTest {

    @Before
    public void setup() {
        service = new DefaultValidationService(null);
        attribute = new GenericAttribute(0l, 0, "foo", GenericAttributeType.STRING, null, false, false, false, null);
        type = new GenericType(0l, 0, "testType", "testGroup", new HashSet<>(Arrays.asList(attribute)));
    }

    @Test
    public void nullType() {
        type = null;
        validateType(DefaultValidationServiceMessageKey.GENERAL_TYPE_NOT_PROVIDED);
    }

    @Test
    public void nullName() {
        type.setName(null);
        validateType(DefaultValidationServiceMessageKey.TYPE_NAME_MANDATORY);
    }

    @Test
    public void emptyName() {
        type.setName("");
        validateType(DefaultValidationServiceMessageKey.TYPE_NAME_MANDATORY);
    }

    @Test
    public void nullGroup() {
        type.setGroup(null);
        validateType(DefaultValidationServiceMessageKey.TYPE_GROUP_MANDATORY);
    }

    @Test
    public void emptyGroup() {
        type.setGroup("");
        validateType(DefaultValidationServiceMessageKey.TYPE_GROUP_MANDATORY);
    }

    @Test
    public void pageSizeZero() {
        type.setPageSize(0);
        validateType(DefaultValidationServiceMessageKey.TYPE_PAGE_SIZE_INVALID);
    }

    @Test
    public void pageSizeNegative() {
        type.setPageSize(-1);
        validateType(DefaultValidationServiceMessageKey.TYPE_PAGE_SIZE_INVALID);
    }

    @Test
    public void noAttributes() {
        type.getAttributes().clear();
        validateType();
    }

    @Test
    public void attributeNullId() {
        attribute.setId(null);
        validateType(DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_ID_MANDATORY);
    }

    @Test
    public void attributeAmbigiousId() {
        type.getAttributes().add(new GenericAttribute(0l, 1, "some other", GenericAttributeType.LONG));
        validateType(DefaultValidationServiceMessageKey.TYPE_AMBIGIOUS_ATTRIBUTE_ID);
    }

    @Test
    public void attributeNullName() {
        attribute.setName(null);
        validateType(DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_NAME_MANDATORY);
    }

    @Test
    public void attributeEmptyName() {
        attribute.setName("");
        validateType(DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_NAME_MANDATORY);
    }

    @Test
    public void attributeDottedName() {
        attribute.setName("foo.bar");
        validateType(DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_NAME_CONTAINS_DOT);
    }

    @Test
    public void attributeNoType() {
        attribute.setType(null);
        validateType(DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_TYPE_MANDATORY);
    }

    @Test
    public void attributeUniqueNotMandatory() {
        attribute.setUnique(true);
        validateType(DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_UNIQUE_BUT_NOT_MANDATORY);
    }

    @Test
    public void attributeUniqueAndMandatory() {
        attribute.setUnique(true);
        attribute.setMandatory(true);
        validateType();
    }
}
