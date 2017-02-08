package de.chrgroth.generictypesystem.persistence.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.chrgroth.generictypesystem.context.GenericTypesystemContext;
import de.chrgroth.generictypesystem.context.impl.NullGenericTypesystemContext;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.persistence.query.ItemFilterData;
import de.chrgroth.generictypesystem.persistence.query.ItemPagingData;
import de.chrgroth.generictypesystem.persistence.query.ItemSortData;
import de.chrgroth.generictypesystem.persistence.query.ItemsQueryData;
import de.chrgroth.generictypesystem.persistence.query.impl.InMemoryItemsQueryService;
import de.chrgroth.generictypesystem.persistence.values.impl.InMemoryValueProposalService;

public class InMemoryPersistenceServiceQueryTest {

    private static final long DEFAULT_PAGE_SIZE = 10l;

    private static final long TYPE_ID = 0l;

    @Mock
    private InMemoryItemsQueryService query;

    private GenericTypesystemContext context;
    private InMemoryPersistenceService service;

    private GenericType type;

    @Before
    public void setup() {

        // init mocks
        MockitoAnnotations.initMocks(this);

        // create context & service
        context = new NullGenericTypesystemContext();
        service = new InMemoryPersistenceService(query, new InMemoryValueProposalService());

        // prepare test type
        type = new GenericType(TYPE_ID, "name", "group", null, null, null, DEFAULT_PAGE_SIZE);
        service.type(context, type);
    }

    @Test
    public void nullQuery() {
        service.query(context, TYPE_ID, null);
        Mockito.verify(query, Mockito.times(1)).query(Mockito.any(), Mockito.isNull(ItemFilterData.class), Mockito.isNull(List.class), Mockito.isNull(ItemPagingData.class));
    }

    @Test
    public void defaultQuery() {
        ItemsQueryData data = new ItemsQueryData();
        data.setFilter(new ItemFilterData());
        data.setSorts(new ArrayList<>());
        ItemSortData sort = new ItemSortData();
        sort.setPath("path");
        data.getSorts().add(sort);
        data.setPaging(new ItemPagingData());
        data.getPaging().setPage(0);
        data.getPaging().setPageSize(5l);
        service.query(context, TYPE_ID, data);
        Mockito.verify(query, Mockito.times(1)).query(Mockito.any(), Mockito.isNotNull(ItemFilterData.class), Mockito.isNotNull(List.class),
                Matchers.argThat(a -> a instanceof ItemPagingData && a.getPageSize() != null && a.getPageSize().longValue() == 5l));
    }

    @Test
    public void queryWithoutPageSize() {
        ItemsQueryData data = new ItemsQueryData();
        data.setFilter(new ItemFilterData());
        data.setSorts(new ArrayList<>());
        ItemSortData sort = new ItemSortData();
        sort.setPath("path");
        data.getSorts().add(sort);
        data.setPaging(new ItemPagingData());
        data.getPaging().setPage(0);
        data.getPaging().setPageSize(null);
        service.query(context, TYPE_ID, data);
        Mockito.verify(query, Mockito.times(1)).query(Mockito.any(), Mockito.isNotNull(ItemFilterData.class), Mockito.isNotNull(List.class),
                Matchers.argThat(a -> a instanceof ItemPagingData && a.getPageSize() != null && a.getPageSize().longValue() == type.getPageSize().longValue()));
    }
}
