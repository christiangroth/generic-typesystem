package de.chrgroth.generictypesystem.persistence.impl;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.chrgroth.generictypesystem.context.GenericTypesystemContext;
import de.chrgroth.generictypesystem.context.impl.NullGenericTypesystemContext;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.persistence.query.impl.InMemoryItemsQueryService;
import de.chrgroth.generictypesystem.persistence.values.impl.InMemoryValueProposalService;

public class InMemoryPersistenceServiceValuesTest {

    @Mock
    private InMemoryValueProposalService values;

    private GenericTypesystemContext context;
    private InMemoryPersistenceService service;

    private GenericType type;

    @Before
    public void setup() {

        // init mocks
        MockitoAnnotations.initMocks(this);

        // create context & service
        context = new NullGenericTypesystemContext();
        service = new InMemoryPersistenceService(new InMemoryItemsQueryService(10l), values);

        // prepare test type
        type = new GenericType(0l, "name", "group", null, null, null, null);
        service.type(context, type);
    }

    @Test
    public void unknownTypeId() {
        Map<String, List<?>> result = service.values(context, type.getId() + 1, null);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
        Mockito.verify(values, Mockito.times(0)).values(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void correctTypeIdNoItems() {
        Map<String, List<?>> result = service.values(context, type.getId(), null);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
        Mockito.verify(values, Mockito.times(0)).values(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void correctTypeIdWthItems() {
        service.item(context, type, new GenericItem());
        Map<String, List<?>> result = service.values(context, type.getId(), null);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
        Mockito.verify(values, Mockito.times(1)).values(Mockito.any(), Mockito.any(), Mockito.any());
    }
}
