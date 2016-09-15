package de.chrgroth.generictypesystem.persistence.query.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.persistence.query.ItemQueryResult;
import de.chrgroth.generictypesystem.persistence.query.ItemSortData;

public class InMemoryItemsQueryServiceSortTest {

    private static final String S = "stringAttribute";
    private static final String D = "doubleAttribute";
    private static final String B = "booleanAttribute";
    private static final String L = "longAttribute";

    private Set<GenericItem> items;
    private InMemoryItemsQueryService service;

    @Before
    public void setup() {

        // init query service
        service = new InMemoryItemsQueryService(10);

        // add items
        items = new HashSet<>();
        items.add(new GenericItem(0l, 0l, ImmutableMap.<String, Object> builder().put(S, "foo").put(D, 0.0d).put(B, true).put(L, 0).build(), null, null));
        items.add(new GenericItem(1l, 0l, ImmutableMap.<String, Object> builder().put(S, "bar").put(D, 1.0d).put(B, false).put(L, 1).build(), null, null));
        items.add(new GenericItem(2l, 0l, ImmutableMap.<String, Object> builder().put(S, "bar").put(D, 2.0d).put(B, true).put(L, 2).build(), null, null));
        items.add(new GenericItem(3l, 0l, ImmutableMap.<String, Object> builder().put(S, " bar").put(D, 3.0d).put(B, false).put(L, 3).build(), null, null));
        items.add(new GenericItem(4l, 0l, ImmutableMap.<String, Object> builder().put(S, " bar ").put(D, 4.0d).put(B, true).put(L, 4).build(), null, null));
        items.add(new GenericItem(5l, 0l, ImmutableMap.<String, Object> builder().put(S, "Bar").put(D, 5.0d).put(B, false).put(L, 5).build(), null, null));
        items.add(new GenericItem(6l, 0l, ImmutableMap.<String, Object> builder().put(S, "Foo").put(D, 6.0d).put(B, true).put(L, 6).build(), null, null));
        HashMap<String, Object> valuesWithNull = Maps.newHashMap(ImmutableMap.<String, Object> builder().put(D, 7.0d).put(B, false).put(L, 7).build());
        valuesWithNull.put(S, null);
        items.add(new GenericItem(7l, 0l, valuesWithNull, null, null));
        items.add(new GenericItem(8l, 0l, ImmutableMap.<String, Object> builder().put(S, "").put(D, 8.0d).put(B, true).put(L, 8).build(), null, null));
        items.add(new GenericItem(9l, 0l, ImmutableMap.<String, Object> builder().put(S, " ").put(D, 9.0d).put(B, false).put(L, 9).build(), null, null));
    }

    @Test
    public void noAttribute() {
        sort(Arrays.asList(sort("non-existent", true)), 9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
    }

    @Test
    public void nullSorts() {
        sort(null, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
    }

    @Test
    public void emptySorts() {
        sort(null, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
    }

    @Test
    public void stringAsc() {
        sort(Arrays.asList(sort(S, true)), 9, 8, 5, 6, 4, 3, 2, 1, 0, 7);
    }

    @Test
    public void stringDesc() {
        sort(Arrays.asList(sort(S, false)), 7, 0, 4, 3, 2, 1, 6, 5, 9, 8);
    }

    @Test
    public void doubleAsc() {
        sort(Arrays.asList(sort(D, true)), 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

    @Test
    public void doubleDesc() {
        sort(Arrays.asList(sort(D, false)), 9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
    }

    @Test
    public void booleanAsc() {
        sort(Arrays.asList(sort(B, true)), 9, 7, 5, 3, 1, 8, 6, 4, 2, 0);
    }

    @Test
    public void booleanDesc() {
        sort(Arrays.asList(sort(B, false)), 8, 6, 4, 2, 0, 9, 7, 5, 3, 1);
    }

    @Test
    public void longAsc() {
        sort(Arrays.asList(sort(L, true)), 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

    @Test
    public void longDesc() {
        sort(Arrays.asList(sort(L, false)), 9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
    }

    @Test
    public void cascade() {
        sort(Arrays.asList(sort(S, true), sort(L, false)), 9, 8, 5, 6, 4, 3, 2, 1, 0, 7);
    }

    private ItemSortData sort(String path, boolean ascending) {
        ItemSortData sort = new ItemSortData();
        sort.setPath(path);
        sort.setAscending(ascending);
        return sort;
    }

    private void sort(List<ItemSortData> sorts, long... ids) {
        ItemQueryResult result = service.query(items, null, sorts, null);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getItems());
        Assert.assertEquals(items.size(), result.getItems().size());
        long[] actualIds = result.getItems().stream().mapToLong(i -> i.getId()).toArray();
        Assert.assertArrayEquals(Arrays.toString(ids) + " <-> " + Arrays.toString(actualIds), ids, actualIds);
    }
}
