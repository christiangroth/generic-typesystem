package de.chrgroth.generictypesystem.model;

import org.junit.Assert;
import org.junit.Test;

import de.chrgroth.generictypesystem.model.GenericAttribute.Type;

public class GenericStructureAttributeTest {

    private GenericStructure structure = new GenericStructure();

    @Test
    public void pathByIdNullArgument() {
        assertAttributePath(null, null);
    }

    @Test
    public void pathByIdNonExistingId() {
        assertAttributePath(2l, null);
    }

    @Test
    public void pathByIdSimple() {
        attribute(structure, 1l, "foo");
        assertAttributePath(1l, "foo");
    }

    @Test
    public void pathByIdNested() {
        GenericAttribute attribute = attribute(structure, 1l, "foo");
        attribute.setType(Type.STRUCTURE);
        GenericStructure nested = new GenericStructure();
        attribute.setStructure(nested);
        attribute(nested, 2l, "bar");
        assertAttributePath(1l, "foo");
        assertAttributePath(2l, "foo.bar");
    }

    @Test
    public void nullArgument() {
        assertAttribute(null);
    }

    @Test
    public void emptyArgument() {
        assertAttribute("");
    }

    @Test
    public void unknownAttribute() {
        assertAttribute("dummy");
    }

    @Test
    public void unknownNestedAttribute() {
        attribute(structure, "dummy");
        assertAttribute("dummy.foo.bar");
    }

    @Test
    public void completeUnknownNestedAttribute() {
        assertAttribute("dummy.foo.bar");
    }

    @Test
    public void emptyNestedPathAttribute() {
        assertAttribute("dummy..bar");
    }

    @Test
    public void simpleAttribute() {
        GenericAttribute attribute = attribute(structure, "foo");
        assertAttribute("foo", attribute);
    }

    @Test
    public void nestedAttribute() {
        GenericAttribute attribute = attribute(structure, "foo");
        GenericStructure nested = new GenericStructure();
        attribute.setStructure(nested);
        GenericAttribute nestedAttribute = attribute(nested, "bar");
        assertAttribute("foo.bar", nestedAttribute);
    }

    private GenericAttribute attribute(GenericStructure structure, String name) {
        return attribute(structure, 0l, name);
    }

    private GenericAttribute attribute(GenericStructure structure, Long id, String name) {
        GenericAttribute attribute = new GenericAttribute(id, 0, name, Type.STRING);
        structure.getAttributes().add(attribute);
        return attribute;
    }

    private void assertAttribute(String path) {
        assertAttribute(path, null);
    }

    private void assertAttribute(String path, GenericAttribute expectedAttribute) {
        GenericAttribute actualAttribute = structure.attribute(path);
        Assert.assertEquals(expectedAttribute, actualAttribute);
    }

    private void assertAttributePath(Long id, String path) {
        Assert.assertEquals(path, structure.attribute(id));
    }
}
