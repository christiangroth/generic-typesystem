package de.chrgroth.generictypesystem.model;

import org.junit.Assert;
import org.junit.Test;

import de.chrgroth.generictypesystem.TestUtils;

public class GenericStructureUniqueKeyTest {

    private static final String ATTRIBUTE_TWO = "one";
    private static final String ATTRIBUTE_ONE = "two";

    private GenericStructure structure = new GenericStructure();
    private GenericItem item = new GenericItem();

    @Test
    public void nullArgumentNoUniqueKey() {
        Assert.assertNull(structure.computeUniqueKey(null));
    }

    @Test
    public void noAttributes() {
        assertUniqueKey(false);
    }

    @Test
    public void nonUniqueAttributes() {
        addNonUniqueAttribute(ATTRIBUTE_ONE, "foo");
        assertUniqueKey(false);
    }

    @Test
    public void uniqueAttribute() {
        addUniqueAttribute(ATTRIBUTE_ONE, "foo");
        assertUniqueKey(true, ATTRIBUTE_ONE, "foo");
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
        assertUniqueKey(true, ATTRIBUTE_ONE, "foo");
    }

    @Test
    public void uniqueAttributes() {
        addUniqueAttribute(ATTRIBUTE_ONE, "foo");
        addUniqueAttribute(ATTRIBUTE_TWO, "bar");
        assertUniqueKey(true, ATTRIBUTE_ONE, "foo", ATTRIBUTE_TWO, "bar");
    }

    private void addNonUniqueAttribute(String name, String value) {
        addAttribute(name, false, value);
    }

    private void addUniqueAttribute(String name, String value) {
        addAttribute(name, true, value);
    }

    private void addAttribute(String name, boolean unique, String value) {
        structure.getAttributes().add(new GenericAttribute(0l, 0, name, GenericAttributeType.STRING, null, null, unique, unique, unique, null));
        item.set(name, value);
    }

    private void assertUniqueKey(boolean hasUniqueKey, Object... uniqueKey) {
        Assert.assertEquals(hasUniqueKey, structure.hasUniqueKey());
        Assert.assertEquals(TestUtils.buildMap(uniqueKey), structure.computeUniqueKey(item));
    }
}
