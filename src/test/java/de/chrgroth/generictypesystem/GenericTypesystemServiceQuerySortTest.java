package de.chrgroth.generictypesystem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
import de.chrgroth.generictypesystem.query.ItemPagingData;
import de.chrgroth.generictypesystem.query.ItemQueryResult;
import de.chrgroth.generictypesystem.query.ItemSortData;
import de.chrgroth.generictypesystem.query.ItemsQueryData;

public class GenericTypesystemServiceQuerySortTest {

    private static final String S = "stringAttribute";
    private static final String D = "doubleAttribute";
    private static final String B = "booleanAttribute";
    private static final String L = "longAttribute";

    private GenericTypesystemService service;
    private GenericType type = new GenericType(0l, 0, "testType", "testGroup", null);

    @Mock
    private PersistenceService persistence;
    private Set<GenericItem> items;

    public GenericTypesystemServiceQuerySortTest() {

        // init mockito
        MockitoAnnotations.initMocks(this);

        // init data service
        service = new GenericTypesystemService(null, persistence);

        // prepare type
        type.getAttributes().add(new GenericAttribute(0l, 0, S, Type.STRING));
        type.getAttributes().add(new GenericAttribute(0l, 1, D, Type.DOUBLE));
        type.getAttributes().add(new GenericAttribute(0l, 1, B, Type.BOOLEAN));
        type.getAttributes().add(new GenericAttribute(0l, 1, L, Type.LONG));
        Mockito.when(persistence.type(Mockito.eq(type.getId().longValue()))).thenReturn(type);

        // add items
        items = new HashSet<>();
        items.add(new GenericItem(0l, type.getId(), TestUtils.buildStringKeyMap(S, "foo", D, 0.0d, B, true, L, 0)));
        items.add(new GenericItem(1l, type.getId(), TestUtils.buildStringKeyMap(S, "bar", D, 1.0d, B, false, L, 1)));
        items.add(new GenericItem(2l, type.getId(), TestUtils.buildStringKeyMap(S, "bar", D, 2.0d, B, true, L, 2)));
        items.add(new GenericItem(3l, type.getId(), TestUtils.buildStringKeyMap(S, " bar", D, 3.0d, B, false, L, 3)));
        items.add(new GenericItem(4l, type.getId(), TestUtils.buildStringKeyMap(S, " bar ", D, 4.0d, B, true, L, 4)));
        items.add(new GenericItem(5l, type.getId(), TestUtils.buildStringKeyMap(S, "Bar", D, 5.0d, B, false, L, 5)));
        items.add(new GenericItem(6l, type.getId(), TestUtils.buildStringKeyMap(S, "Foo", D, 6.0d, B, true, L, 6)));
        items.add(new GenericItem(7l, type.getId(), TestUtils.buildStringKeyMap(S, null, D, 7.0d, B, false, L, 7)));
        items.add(new GenericItem(8l, type.getId(), TestUtils.buildStringKeyMap(S, "", D, 8.0d, B, true, L, 8)));
        items.add(new GenericItem(9l, type.getId(), TestUtils.buildStringKeyMap(S, " ", D, 9.0d, B, false, L, 9)));
        Mockito.when(persistence.items(Mockito.eq(type.getId().longValue()))).thenReturn(items);
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

    public void sort(List<ItemSortData> sorts, long... ids) {
        ItemsQueryData itemsQueryData = new ItemsQueryData();
        itemsQueryData.setFilter(null);
        itemsQueryData.setSorts(sorts);
        itemsQueryData.setPaging(new ItemPagingData());
        ItemQueryResult result = service.items(0l, itemsQueryData);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getItems());
        Assert.assertEquals(items.size(), result.getItems().size());
        long[] actualIds = result.getItems().stream().mapToLong(i -> i.getId()).toArray();
        Assert.assertArrayEquals(Arrays.toString(ids) + " <-> " + Arrays.toString(actualIds), ids, actualIds);
    }
}
