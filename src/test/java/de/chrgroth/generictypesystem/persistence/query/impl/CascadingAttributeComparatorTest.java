package de.chrgroth.generictypesystem.persistence.query.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.persistence.query.ItemSortData;

// TODO test with different value types
// TODO test with mismatching value types per attribute (inconsistent data)
// TODO test with non comparable value types (inconsistent data)
public class CascadingAttributeComparatorTest {

    private static final String ATTRIBUTE_TWO = "attributeTwo";
    private static final String ATTRIBUTE_ONE = "attributeOne";
    private CascadingAttributeComparator comparator;
    private List<GenericItem> items = new ArrayList<>();
    private GenericItem itemOne;
    private GenericItem itemTwo;
    private GenericItem itemThree;

    @Before
    public void setup() {
        itemOne = new GenericItem(0l, 0l, itemValues("a", "one"), null, null);
        itemTwo = new GenericItem(1l, 0l, itemValues("b", "two"), null, null);
        itemThree = new GenericItem(2l, 0l, itemValues("a", "two"), null, null);
    }

    private Map<String, Object> itemValues(String valueOne, String valueTwo) {
        return ImmutableMap.<String, Object> builder().put(ATTRIBUTE_ONE, valueOne).put(ATTRIBUTE_TWO, valueTwo).build();
    }

    @Test
    public void fallbacktoIdSort() {
        comparator = new CascadingAttributeComparator(null);
        items(itemOne, itemTwo);
        expect(itemTwo, itemOne);
    }

    @Test
    public void sortAscending() {
        comparator = new CascadingAttributeComparator(Arrays.asList(sort(ATTRIBUTE_ONE, true)));
        items(itemTwo, itemOne);
        expect(itemOne, itemTwo);
    }

    @Test
    public void sortDescending() {
        comparator = new CascadingAttributeComparator(Arrays.asList(sort(ATTRIBUTE_ONE, false)));
        items(itemOne, itemTwo);
        expect(itemTwo, itemOne);
    }

    @Test
    public void sortAscendingWithSecondAttribute() {
        comparator = new CascadingAttributeComparator(Arrays.asList(sort(ATTRIBUTE_ONE, true), sort(ATTRIBUTE_TWO, true)));
        items(itemThree, itemOne);
        expect(itemOne, itemThree);
    }

    @Test
    public void sortDescendingWithSecondAttribute() {
        comparator = new CascadingAttributeComparator(Arrays.asList(sort(ATTRIBUTE_ONE, true), sort(ATTRIBUTE_TWO, false)));
        items(itemOne, itemThree);
        expect(itemThree, itemOne);
    }

    private void items(GenericItem... items) {
        for (GenericItem item : items) {
            this.items.add(item);
        }
    }

    private ItemSortData sort(String path, boolean ascending) {
        ItemSortData data = new ItemSortData();
        data.setPath(path);
        data.setAscending(ascending);
        return data;
    }

    private void expect(GenericItem... items) {
        Collections.sort(this.items, comparator);
        Assert.assertEquals(items.length, this.items.size());
        for (int i = 0; i < items.length; i++) {
            Assert.assertEquals(items[i], this.items.get(i));
        }
    }
}
