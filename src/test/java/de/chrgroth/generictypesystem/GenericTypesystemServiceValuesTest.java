package de.chrgroth.generictypesystem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericAttribute.Type;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.persistence.PersistenceService;

public class GenericTypesystemServiceValuesTest {

    private static final String STRING_ATTRIBUTE = "stringAttribute";
    private static final String DOUBLE_ATTRIBUTE = "doubleAttribute";

    private GenericTypesystemService service;

    private GenericType type = new GenericType(0l, 0, "testType", "testGroup", null);

    @Mock
    private PersistenceService persistence;

    public GenericTypesystemServiceValuesTest() {

        // init mockito
        MockitoAnnotations.initMocks(this);

        // init service
        service = new GenericTypesystemService(null, persistence);

        // prepare type
        type.getAttributes().add(new GenericAttribute(0l, 0, STRING_ATTRIBUTE, Type.STRING));
        type.getAttributes().add(new GenericAttribute(1l, 1, DOUBLE_ATTRIBUTE, Type.DOUBLE));
        Mockito.when(persistence.type(Mockito.eq(type.getId().longValue()))).thenReturn(type);

        // prepare items
        Set<GenericItem> items = new HashSet<>();
        items.add(new GenericItem(0l, type.getId(), TestUtils.buildStringKeyMap(STRING_ATTRIBUTE, "foo", DOUBLE_ATTRIBUTE, 0.0d)));
        items.add(new GenericItem(1l, type.getId(), TestUtils.buildStringKeyMap(STRING_ATTRIBUTE, "bar", DOUBLE_ATTRIBUTE, 0.0d)));
        items.add(new GenericItem(2l, type.getId(), TestUtils.buildStringKeyMap(STRING_ATTRIBUTE, "bar", DOUBLE_ATTRIBUTE, 0.0d)));
        items.add(new GenericItem(3l, type.getId(), TestUtils.buildStringKeyMap(STRING_ATTRIBUTE, " bar", DOUBLE_ATTRIBUTE, 0.0d)));
        items.add(new GenericItem(4l, type.getId(), TestUtils.buildStringKeyMap(STRING_ATTRIBUTE, " bar ", DOUBLE_ATTRIBUTE, 0.0d)));
        items.add(new GenericItem(5l, type.getId(), TestUtils.buildStringKeyMap(STRING_ATTRIBUTE, "Bar", DOUBLE_ATTRIBUTE, 0.0d)));
        items.add(new GenericItem(6l, type.getId(), TestUtils.buildStringKeyMap(STRING_ATTRIBUTE, "Foo", DOUBLE_ATTRIBUTE, 0.0d)));
        items.add(new GenericItem(7l, type.getId(), TestUtils.buildStringKeyMap(STRING_ATTRIBUTE, null, DOUBLE_ATTRIBUTE, 0.0d)));
        items.add(new GenericItem(8l, type.getId(), TestUtils.buildStringKeyMap(STRING_ATTRIBUTE, "", DOUBLE_ATTRIBUTE, 0.0d)));
        items.add(new GenericItem(9l, type.getId(), TestUtils.buildStringKeyMap(STRING_ATTRIBUTE, " ", DOUBLE_ATTRIBUTE, 0.0d)));
        Mockito.when(persistence.items(Mockito.eq(type.getId().longValue()))).thenReturn(items);
    }

    @Test
    public void unknownType() {
        assertValues(null, null, null);
    }

    @Test
    public void nullPath() {
        assertValues(type, null, null);
    }

    @Test
    public void emptyPath() {
        assertValues(type, "", null);
    }

    @Test
    public void unknownPath() {
        assertValues(type, "unknownAttribute", null);
    }

    @Test
    public void nonStringPath() {
        assertValues(type, DOUBLE_ATTRIBUTE, null);
    }

    @Test
    public void stringPath() {
        assertValues(type, STRING_ATTRIBUTE, Arrays.asList("bar", "Bar", "foo", "Foo"));
    }

    private void assertValues(GenericType type, String attributePath, List<?> values) {
        Map<String, List<?>> result = service.values(type != null ? type.getId() : -1l, null);
        if (type == null) {
            Assert.assertNull(result);
        } else {
            Assert.assertNotNull(result);
            Assert.assertEquals(values, result.get(attributePath));
        }
    }
}
