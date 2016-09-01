package de.chrgroth.generictypesystem;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.model.GenericAttributeType;
import de.chrgroth.generictypesystem.persistence.PersistenceService;
import de.chrgroth.generictypesystem.query.ItemPagingData;
import de.chrgroth.generictypesystem.query.ItemQueryResult;
import de.chrgroth.generictypesystem.query.ItemsQueryData;

public class GenericTypesystemServiceQueryPagingTest {

    private static final String DOUBLE_ATTRIBUTE = "doubleAttribute";

    private GenericTypesystemService service;
    private GenericType type = new GenericType(0l, 0, "testType", "testGroup", null);

    @Mock
    private PersistenceService persistence;

    public GenericTypesystemServiceQueryPagingTest() {

        // init mockito
        MockitoAnnotations.initMocks(this);

        // init data service
        service = new GenericTypesystemService(null, persistence);

        // prepare type
        type.getAttributes().add(new GenericAttribute(0l, 0, DOUBLE_ATTRIBUTE, GenericAttributeType.DOUBLE));
        Mockito.when(persistence.type(Mockito.eq(type.getId().longValue()))).thenReturn(type);

        // prepare items
        Set<GenericItem> items = new HashSet<>();
        items.add(new GenericItem(0l, type.getId(), TestUtils.buildStringKeyMap(DOUBLE_ATTRIBUTE, 0.0d)));
        items.add(new GenericItem(1l, type.getId(), TestUtils.buildStringKeyMap(DOUBLE_ATTRIBUTE, 1.0d)));
        items.add(new GenericItem(2l, type.getId(), TestUtils.buildStringKeyMap(DOUBLE_ATTRIBUTE, 2.0d)));
        items.add(new GenericItem(3l, type.getId(), TestUtils.buildStringKeyMap(DOUBLE_ATTRIBUTE, 3.0d)));
        items.add(new GenericItem(4l, type.getId(), TestUtils.buildStringKeyMap(DOUBLE_ATTRIBUTE, 4.0d)));
        items.add(new GenericItem(5l, type.getId(), TestUtils.buildStringKeyMap(DOUBLE_ATTRIBUTE, 5.0d)));
        items.add(new GenericItem(6l, type.getId(), TestUtils.buildStringKeyMap(DOUBLE_ATTRIBUTE, 6.0d)));
        items.add(new GenericItem(7l, type.getId(), TestUtils.buildStringKeyMap(DOUBLE_ATTRIBUTE, 7.0d)));
        items.add(new GenericItem(8l, type.getId(), TestUtils.buildStringKeyMap(DOUBLE_ATTRIBUTE, 8.0d)));
        items.add(new GenericItem(9l, type.getId(), TestUtils.buildStringKeyMap(DOUBLE_ATTRIBUTE, 9.0d)));
        Mockito.when(persistence.items(Mockito.eq(type.getId().longValue()))).thenReturn(items);
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
        ItemsQueryData itemsQueryData = new ItemsQueryData();
        itemsQueryData.setFilter(null);
        itemsQueryData.setSorts(null);
        itemsQueryData.setPaging(paging);
        ItemQueryResult result = service.items(0l, itemsQueryData);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getItems());
        Assert.assertEquals(results, result.getItems().size());
    }
}
