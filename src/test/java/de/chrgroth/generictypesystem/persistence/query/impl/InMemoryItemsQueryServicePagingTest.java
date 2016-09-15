package de.chrgroth.generictypesystem.persistence.query.impl;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.persistence.query.ItemPagingData;
import de.chrgroth.generictypesystem.persistence.query.ItemQueryResult;

public class InMemoryItemsQueryServicePagingTest {

    private static final String DOUBLE_ATTRIBUTE = "doubleAttribute";

    private Set<GenericItem> items;
    private InMemoryItemsQueryService service;

    @Before
    public void setup() {

        // init query service
        service = new InMemoryItemsQueryService(10);

        // prepare items
        items = new HashSet<>();
        items.add(new GenericItem(0l, 0l, ImmutableMap.<String, Object> builder().put(DOUBLE_ATTRIBUTE, 0.0d).build()));
        items.add(new GenericItem(1l, 0l, ImmutableMap.<String, Object> builder().put(DOUBLE_ATTRIBUTE, 1.0d).build()));
        items.add(new GenericItem(2l, 0l, ImmutableMap.<String, Object> builder().put(DOUBLE_ATTRIBUTE, 2.0d).build()));
        items.add(new GenericItem(3l, 0l, ImmutableMap.<String, Object> builder().put(DOUBLE_ATTRIBUTE, 3.0d).build()));
        items.add(new GenericItem(4l, 0l, ImmutableMap.<String, Object> builder().put(DOUBLE_ATTRIBUTE, 4.0d).build()));
        items.add(new GenericItem(5l, 0l, ImmutableMap.<String, Object> builder().put(DOUBLE_ATTRIBUTE, 5.0d).build()));
        items.add(new GenericItem(6l, 0l, ImmutableMap.<String, Object> builder().put(DOUBLE_ATTRIBUTE, 6.0d).build()));
        items.add(new GenericItem(7l, 0l, ImmutableMap.<String, Object> builder().put(DOUBLE_ATTRIBUTE, 7.0d).build()));
        items.add(new GenericItem(8l, 0l, ImmutableMap.<String, Object> builder().put(DOUBLE_ATTRIBUTE, 8.0d).build()));
        items.add(new GenericItem(9l, 0l, ImmutableMap.<String, Object> builder().put(DOUBLE_ATTRIBUTE, 9.0d).build()));
    }

    @Test
    public void nullPaging() {
        paging(null, 10);
    }

    @Test
    public void beginning() {
        paging(0, 2, 2);
    }

    @Test
    public void middle() {
        paging(2, 2, 2);
    }

    @Test
    public void end() {
        paging(5, 2, 2);
    }

    @Test
    public void endOverflow() {
        paging(3, 4, 2);
    }

    public void paging(int page, int size, int results) {
        paging(pagingData(page, size), results);
    }

    private ItemPagingData pagingData(int page, int size) {
        ItemPagingData paging = new ItemPagingData();
        paging.setPage(page);
        paging.setSize(size);
        return paging;
    }

    public void paging(ItemPagingData paging, int results) {
        ItemQueryResult result = service.query(items, null, null, paging);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getItems());
        Assert.assertEquals(results, result.getItems().size());
    }
}
