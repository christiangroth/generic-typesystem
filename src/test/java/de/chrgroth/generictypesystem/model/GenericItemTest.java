package de.chrgroth.generictypesystem.model;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GenericItemTest {

    private GenericItem item;
    private Map<String, Object> values;

    @Before
    public void init() {
        item = new GenericItem();
        values = new HashMap<>();
    }

    @Test
    public void nullArguments() {
        Assert.assertNull(item.get(null));
        Assert.assertNull(item.set(null, null));
        Assert.assertNull(item.remove(null));
    }

    @Test
    public void emptyItem() {
        Assert.assertNull(item.get(""));
        Assert.assertNull(item.get("foo"));

        Assert.assertNull(item.remove(""));
        Assert.assertNull(item.remove("foo"));

        Assert.assertEquals(new HashMap<>(), item.get());
    }

    @Test
    public void unknownNestedItem() {
        Assert.assertNull(item.get("foo."));
        Assert.assertNull(item.get("foo.bar"));
        Assert.assertNull(item.remove("foo.bar"));
    }

    @Test
    public void valueLifecycle() {
        Assert.assertNull(item.set("foo", "bar"));
        values.put("foo", "bar");
        Assert.assertEquals(values, item.get());
        Assert.assertEquals("bar", item.get("foo"));

        Assert.assertEquals("bar", item.set("foo", "baz"));
        values.put("foo", "baz");
        Assert.assertEquals(values, item.get());

        Assert.assertEquals("baz", item.remove("foo"));
        Assert.assertNull(item.get("foo"));
        Assert.assertEquals(new HashMap<>(), item.get());

        Assert.assertNull(item.set("foo", "boom"));
        values.put("foo", "boom");
        Assert.assertEquals(values, item.get());

        Assert.assertEquals("boom", item.set("foo", null));
        Assert.assertNull(item.get("foo"));
        values.put("foo", null);
        Assert.assertEquals(values, item.get());

        Assert.assertNull(item.remove("foo"));
        Assert.assertEquals(new HashMap<>(), item.get());
    }

    @Test
    public void nestedValue() {

        // set some nested values
        Assert.assertNull(item.set("foo.bar.baz", "boom"));

        // check nested items and value are created
        Assert.assertEquals(new GenericItem(), item.get("foo"));
        Assert.assertEquals(new GenericItem(), item.get("foo.bar"));
        Assert.assertEquals("boom", item.get("foo.bar.baz"));

        // check direct access via nested items
        Assert.assertEquals("boom", ((GenericItem) item.get("foo")).get("bar.baz"));
        Assert.assertEquals("boom", ((GenericItem) ((GenericItem) item.get("foo")).get("bar")).get("baz"));
        Assert.assertEquals("boom", item.get("foo.bar.baz"));

        // check value maps directly
        Map<String, Object> valuesMap = item.getValues();
        Assert.assertEquals(1, valuesMap.size());
        Map<String, Object> nestedValuesMap = ((GenericItem) valuesMap.values().iterator().next()).getValues();
        Assert.assertEquals(1, nestedValuesMap.size());
        Map<String, Object> nestedNestedValuesMap = ((GenericItem) nestedValuesMap.values().iterator().next()).getValues();
        Assert.assertEquals(1, nestedNestedValuesMap.size());
    }

    @Test
    public void illegalPath() {
        String invalidPath = "foo.bar.";
        Assert.assertNull(item.set(invalidPath, "boom"));
        Assert.assertNull(item.get(invalidPath));
        Assert.assertNull(item.set(invalidPath, "boom2"));
        Assert.assertNull(item.remove(invalidPath));
    }
}
