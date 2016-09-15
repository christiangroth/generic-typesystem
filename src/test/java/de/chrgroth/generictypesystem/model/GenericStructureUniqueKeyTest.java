package de.chrgroth.generictypesystem.model;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class GenericStructureUniqueKeyTest {

    private static final String ATTRIBUTE_TWO = "one";
    private static final String ATTRIBUTE_ONE = "two";

    private GenericStructure structure = new GenericStructure();
    private GenericItem item;

    @Before
    public void init() {
        structure = new GenericStructure();
        item = new GenericItem();
    }

    @Test
    public void nullArgumentNoUniqueKey() {
        Assert.assertNull(structure.computeUniqueKey(null));
    }

    @Test
    public void noAttributes() {
        assertUniqueKey(false, null);
    }

    @Test
    public void nonUniqueAttributes() {
        addNonUniqueAttribute(ATTRIBUTE_ONE, "foo");
        assertUniqueKey(false, null);
    }

    @Test
    public void uniqueAttribute() {
        addUniqueAttribute(ATTRIBUTE_ONE, "foo");
        assertUniqueKey(true, ImmutableMap.<String, Object> builder().put(ATTRIBUTE_ONE, "foo").build());
    }

    @Test
    public void nullArgumentWithUniqueKey() {
        addUniqueAttribute(ATTRIBUTE_ONE, null);
        Assert.assertNull(structure.computeUniqueKey(null));
    }

    @Test
    public void mixedAttributes() {
        addUniqueAttribute(ATTRIBUTE_ONE, "foo");
        addNonUniqueAttribute(ATTRIBUTE_TWO, "bar");
        assertUniqueKey(true, ImmutableMap.<String, Object> builder().put(ATTRIBUTE_ONE, "foo").build());
    }

    @Test
    public void uniqueAttributes() {
        addUniqueAttribute(ATTRIBUTE_ONE, "foo");
        addUniqueAttribute(ATTRIBUTE_TWO, "bar");
        assertUniqueKey(true, ImmutableMap.<String, Object> builder().put(ATTRIBUTE_ONE, "foo").put(ATTRIBUTE_TWO, "bar").build());
    }

    private void addNonUniqueAttribute(String name, String value) {
        addAttribute(name, false, value);
    }

    private void addUniqueAttribute(String name, String value) {
        addAttribute(name, true, value);
    }

    private void addAttribute(String name, boolean unique, String value) {
        structure.getAttributes()
                .add(new GenericAttribute(0l, 0, name, DefaultGenericAttributeType.STRING, null, unique, false, false, null, null, null, null, null, null, null, null, null));
        item.set(name, value);
    }

    private void assertUniqueKey(boolean hasUniqueKey, Map<String, Object> uniqueKey) {
        Assert.assertEquals(hasUniqueKey, structure.hasUniqueKey());
        Assert.assertEquals(uniqueKey, structure.computeUniqueKey(item));
    }
}
