package de.chrgroth.generictypesystem.persistence.values.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import de.chrgroth.generictypesystem.model.DefaultGenericAttributeType;
import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericStructure;
import de.chrgroth.generictypesystem.model.GenericType;

public class InMemoryValueProposalServiceTest {

    private static final String STRING_ATTRIBUTE = "stringAttribute";
    private static final String DOUBLE_ATTRIBUTE = "doubleAttribute";
    private static final String NESTED_ATTRIBUTE = "sub";

    private InMemoryValueProposalService service;

    private GenericType type;
    private Set<GenericItem> items;

    public InMemoryValueProposalServiceTest() {

        // init service
        service = new InMemoryValueProposalService();

        // prepare type
        type = new GenericType(0l, "testType", "testGroup", null, null, null, null);
        type.getAttributes().add(new GenericAttribute(0l, STRING_ATTRIBUTE, DefaultGenericAttributeType.STRING, null, false, false, false, null, null, null, null, null, null, null,
                new HashSet<>(Arrays.asList(1l)), null));
        type.getAttributes().add(
                new GenericAttribute(1l, DOUBLE_ATTRIBUTE, DefaultGenericAttributeType.DOUBLE, null, false, false, false, null, null, null, null, null, null, null, null, null));
        GenericStructure nestedStructure = new GenericStructure();
        nestedStructure.getAttributes().add(
                new GenericAttribute(2l, STRING_ATTRIBUTE, DefaultGenericAttributeType.STRING, null, false, false, false, null, null, null, null, null, null, null, null, null));
        type.getAttributes().add(new GenericAttribute(3l, NESTED_ATTRIBUTE, DefaultGenericAttributeType.STRUCTURE, null, false, false, false, nestedStructure, null, null, null,
                null, null, null, null, null));

        // prepare items
        items = new HashSet<>();
        items.add(new GenericItem(0l, type.getId(),
                ImmutableMap.<String, Object> builder().put(STRING_ATTRIBUTE, "foo").put(DOUBLE_ATTRIBUTE, 1.0d).put(NESTED_ATTRIBUTE + "." + STRING_ATTRIBUTE, "one").build(),
                null, null));
        items.add(new GenericItem(1l, type.getId(), ImmutableMap.<String, Object> builder().put(STRING_ATTRIBUTE, "bar").put(DOUBLE_ATTRIBUTE, 0.0d).build(), null, null));
        items.add(new GenericItem(2l, type.getId(), ImmutableMap.<String, Object> builder().put(STRING_ATTRIBUTE, "bar").put(DOUBLE_ATTRIBUTE, 0.0d).build(), null, null));
        items.add(new GenericItem(3l, type.getId(), ImmutableMap.<String, Object> builder().put(STRING_ATTRIBUTE, " bar").put(DOUBLE_ATTRIBUTE, 0.0d).build(), null, null));
        items.add(new GenericItem(4l, type.getId(), ImmutableMap.<String, Object> builder().put(STRING_ATTRIBUTE, " bar ").put(DOUBLE_ATTRIBUTE, 0.0d).build(), null, null));
        items.add(new GenericItem(5l, type.getId(), ImmutableMap.<String, Object> builder().put(STRING_ATTRIBUTE, "Bar").put(DOUBLE_ATTRIBUTE, 0.0d).build(), null, null));
        items.add(new GenericItem(6l, type.getId(), ImmutableMap.<String, Object> builder().put(STRING_ATTRIBUTE, "Foo").put(DOUBLE_ATTRIBUTE, 1.0d).build(), null, null));
        HashMap<String, Object> valuesWithNull = Maps.newHashMap(ImmutableMap.<String, Object> builder().put(DOUBLE_ATTRIBUTE, 0.0d).build());
        valuesWithNull.put(STRING_ATTRIBUTE, null);
        items.add(new GenericItem(7l, type.getId(), valuesWithNull, null, null));
        items.add(new GenericItem(8l, type.getId(),
                ImmutableMap.<String, Object> builder().put(STRING_ATTRIBUTE, "").put(DOUBLE_ATTRIBUTE, 0.0d).put(NESTED_ATTRIBUTE + "." + STRING_ATTRIBUTE, "one").build(), null,
                null));
        items.add(new GenericItem(9l, type.getId(),
                ImmutableMap.<String, Object> builder().put(STRING_ATTRIBUTE, " ").put(DOUBLE_ATTRIBUTE, 0.0d).put(NESTED_ATTRIBUTE + "." + STRING_ATTRIBUTE, "two").build(), null,
                null));
    }

    @Test
    public void unknownType() {
        assertValues(null, null, null, null);
    }

    @Test
    public void nullItems() {
        assertValues(type, null, null, null);
    }

    @Test
    public void emptyItems() {
        assertValues(type, null, null, null);
    }

    @Test
    public void noValueProposalCapableAttributes() {
        type.getAttributes().removeIf(a -> a.getType().isValueProposalDependenciesCapable() || a.isStructure());
        assertValues(type, items, null, null);
    }

    @Test
    public void withoutTemplate() {
        assertValues(type, items, null, ImmutableMap.<String, List<?>> builder().put(STRING_ATTRIBUTE, Arrays.asList("bar", "Bar", "foo", "Foo"))
                .put(NESTED_ATTRIBUTE + "." + STRING_ATTRIBUTE, Arrays.asList("one", "two")).build());
    }

    @Test
    public void withTemplate() {
        GenericItem template = new GenericItem(null, null, ImmutableMap.<String, Object> builder().put(DOUBLE_ATTRIBUTE, 1.0d).build(), null, null);
        assertValues(type, items, template, ImmutableMap.<String, List<?>> builder().put(STRING_ATTRIBUTE, Arrays.asList("foo", "Foo"))
                .put(NESTED_ATTRIBUTE + "." + STRING_ATTRIBUTE, Arrays.asList("one", "two")).build());
    }

    private void assertValues(GenericType type, Set<GenericItem> items, GenericItem template, Map<String, List<?>> expectedValues) {

        // invoke
        Map<String, List<?>> result = service.values(type, items, template);
        Assert.assertNotNull(result);

        // check for invalid testcases
        if (expectedValues == null) {
            Assert.assertTrue(result.isEmpty());
            return;
        }

        // check expected data
        Assert.assertEquals(expectedValues, result);
    }
}
