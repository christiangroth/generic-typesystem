package de.chrgroth.generictypesystem.validation.impl;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.chrgroth.generictypesystem.model.DefaultGenericAttributeType;
import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.validation.BaseValidationServiceTest;
import de.chrgroth.generictypesystem.validation.ValidationError;

public class DefaultValidationServiceTypeTest extends BaseValidationServiceTest {

    private static final String ATTRIBUTE_NAME = "foo";

    @Before
    public void setup() {
        service = new DefaultValidationService(null);
        attribute = new GenericAttribute(0l, ATTRIBUTE_NAME, DefaultGenericAttributeType.STRING, null, false, false, null, null, null, null, null, null, null, null, null);
        type = new GenericType(0l, "testType", "testGroup", Arrays.asList(attribute), null, null, null);
    }

    @Test
    public void nullType() {
        type = null;
        validateType(new ValidationError("", DefaultValidationServiceMessageKey.GENERAL_TYPE_NOT_PROVIDED));
    }

    @Test
    public void nullName() {
        type.setName(null);
        validateType(new ValidationError("name", DefaultValidationServiceMessageKey.TYPE_NAME_MANDATORY));
    }

    @Test
    public void emptyName() {
        type.setName("");
        validateType(new ValidationError("name", DefaultValidationServiceMessageKey.TYPE_NAME_MANDATORY));
    }

    @Test
    public void nullGroup() {
        type.setGroup(null);
        validateType(new ValidationError("group", DefaultValidationServiceMessageKey.TYPE_GROUP_MANDATORY));
    }

    @Test
    public void emptyGroup() {
        type.setGroup("");
        validateType(new ValidationError("group", DefaultValidationServiceMessageKey.TYPE_GROUP_MANDATORY));
    }

    @Test
    public void pageSizeNull() {
        type.setPageSize(null);
        validateType();
    }

    @Test
    public void pageSizeZero() {
        type.setPageSize(0l);
        validateType(new ValidationError("pageSize", DefaultValidationServiceMessageKey.TYPE_PAGE_SIZE_INVALID));
    }

    @Test
    public void pageSizeNegative() {
        type.setPageSize(-1l);
        validateType(new ValidationError("pageSize", DefaultValidationServiceMessageKey.TYPE_PAGE_SIZE_INVALID));
    }

    @Test
    public void noAttributes() {
        type.getAttributes().clear();
        validateType();
    }

    @Test
    public void attributeNullId() {
        attribute.setId(null);
        validateType(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_ID_MANDATORY));
    }

    @Test
    public void attributeAmbigiousId() {
        type.getAttributes()
                .add(new GenericAttribute(0l, "some other", DefaultGenericAttributeType.LONG, null, false, false, null, null, null, null, null, null, null, null, null));
        validateType(new ValidationError("", DefaultValidationServiceMessageKey.TYPE_AMBIGIOUS_ATTRIBUTE_ID, attribute.getId().longValue()));
    }

    @Test
    public void attributeNullName() {
        attribute.setName(null);
        validateType(new ValidationError("", DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_NAME_MANDATORY));
    }

    @Test
    public void attributeEmptyName() {
        attribute.setName("");
        validateType(new ValidationError("", DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_NAME_MANDATORY));
    }

    @Test
    public void attributeDottedName() {
        attribute.setName("foo.bar");
        validateType(new ValidationError("foo.bar", DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_NAME_CONTAINS_DOT));
    }

    @Test
    public void attributeNoType() {
        attribute.setType(null);
        validateType(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_TYPE_MANDATORY));
    }

    @Test
    public void attributeUniqueNotMandatory() {
        attribute.setUnique(true);
        validateType(new ValidationError(ATTRIBUTE_NAME, DefaultValidationServiceMessageKey.TYPE_ATTRIBUTE_UNIQUE_BUT_NOT_MANDATORY));
    }

    @Test
    public void attributeUniqueAndMandatory() {
        attribute.setUnique(true);
        attribute.setMandatory(true);
        validateType();
    }
}
