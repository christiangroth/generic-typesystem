package de.chrgroth.generictypesystem.persistence.impl;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.persistence.query.impl.InMemoryItemsQueryService;
import de.chrgroth.generictypesystem.persistence.values.impl.InMemoryValueProposalService;

public class InMemoryPersistenceServiceValuesTest {

    private static final long TYPE_ID = 0l;

    @Mock
    private InMemoryValueProposalService values;
    private InMemoryPersistenceService service;
    private GenericType type;

    @Before
    public void setup() {

        // init mocks
        MockitoAnnotations.initMocks(this);

        // create service
        service = new InMemoryPersistenceService(new InMemoryItemsQueryService(10l), values);

        // prepare test type
        type = new GenericType(TYPE_ID, 0, "name", "group", null, null, null, null, null, null);
        service.type(type);
    }

    @Test
    public void unknownTypeId() {
        Map<String, List<?>> result = service.values(type.getId() + 1, null);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
        Mockito.verify(values, Mockito.times(0)).values(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void correctTypeIdNoItems() {
        Map<String, List<?>> result = service.values(type.getId(), null);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
        Mockito.verify(values, Mockito.times(0)).values(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void correctTypeIdWthItems() {
        service.item(type, new GenericItem());
        Map<String, List<?>> result = service.values(type.getId(), null);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
        Mockito.verify(values, Mockito.times(1)).values(Mockito.any(), Mockito.any(), Mockito.any());
    }
}
