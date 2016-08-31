package de.chrgroth.generictypesystem.model;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.chrgroth.generictypesystem.TestUtils;

public class GenericItemTest {

    private GenericItem item;

    @Before
    public void init() {
        item = new GenericItem();
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
    public void valueLifecycle() {
        Assert.assertNull(item.set("foo", "bar"));
        Assert.assertEquals(TestUtils.buildMap("foo", "bar"), item.get());
        Assert.assertEquals("bar", item.get("foo"));

        Assert.assertEquals("bar", item.set("foo", "baz"));
        Assert.assertEquals(TestUtils.buildMap("foo", "baz"), item.get());

        Assert.assertEquals("baz", item.remove("foo"));
        Assert.assertNull(item.get("foo"));
        Assert.assertEquals(new HashMap<>(), item.get());

        Assert.assertNull(item.set("foo", "boom"));
        Assert.assertEquals(TestUtils.buildMap("foo", "boom"), item.get());

        Assert.assertEquals("boom", item.set("foo", null));
        Assert.assertNull(item.get("foo"));
        Assert.assertEquals(TestUtils.buildMap("foo", null), item.get());

        Assert.assertNull(item.remove("foo"));
        Assert.assertEquals(new HashMap<>(), item.get());
    }

    @Test
    public void nestedValue() {
        Assert.assertNull(item.set("foo.bar.baz", "boom"));
        Assert.assertEquals(new GenericItem(), item.get("foo"));
        Assert.assertEquals(new GenericItem(), item.get("foo.bar"));
        Assert.assertEquals("boom", item.get("foo.bar.baz"));
        Assert.assertEquals("boom", ((GenericItem) item.get("foo")).get("bar.baz"));
        Assert.assertEquals("boom", ((GenericItem) ((GenericItem) item.get("foo")).get("bar")).get("baz"));
    }
}
