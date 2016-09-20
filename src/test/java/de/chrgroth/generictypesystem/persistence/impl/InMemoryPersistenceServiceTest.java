package de.chrgroth.generictypesystem.persistence.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.persistence.query.impl.InMemoryItemsQueryService;
import de.chrgroth.generictypesystem.persistence.values.impl.InMemoryValueProposalService;

public class InMemoryPersistenceServiceTest {

    private InMemoryPersistenceService service;

    @Before
    public void setup() {
        service = new InMemoryPersistenceService(new InMemoryItemsQueryService(10), new InMemoryValueProposalService());
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingQueryService() {
        new InMemoryPersistenceService(null, new InMemoryValueProposalService());
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingValuesService() {
        new InMemoryPersistenceService(new InMemoryItemsQueryService(10l), null);
    }

    @Test
    public void typeAndItemLifecycle() {

        // assert empty
        Assert.assertTrue(service.types().isEmpty());
        Assert.assertTrue(service.typeGroups().isEmpty());

        // add type
        GenericType type = new GenericType(null, 0, "name", "group", null, null, null, null, null, null);
        service.type(type);

        // ensure id was set
        Assert.assertEquals(1l, type.getId().longValue());

        // assert type data
        Assert.assertFalse(service.types().isEmpty());
        Assert.assertEquals(1, service.types().size());
        Assert.assertEquals(type, service.types().iterator().next());
        Assert.assertNotNull(service.type(type.getId()));
        Assert.assertFalse(service.typeGroups().isEmpty());
        Assert.assertEquals(1, service.typeGroups().size());
        Assert.assertEquals(type.getGroup(), service.typeGroups().iterator().next());

        // add type again
        service.type(type);

        // assert type data not duplicate
        Assert.assertEquals(1, service.types().size());
        Assert.assertEquals(1, service.typeGroups().size());

        // remove non existing type
        Assert.assertTrue(service.removeType(Long.MAX_VALUE));
        Assert.assertEquals(1, service.types().size());
        Assert.assertEquals(1, service.typeGroups().size());

        // add items for type
        GenericItem item = new GenericItem(null, type.getId(), null, null, null);
        service.item(type, item);
        service.item(type, new GenericItem(2l, type.getId(), null, null, null));
        service.item(type, new GenericItem(3l, type.getId(), null, null, null));

        // ensure id was set
        Assert.assertEquals(1l, item.getId().longValue());

        // assert item data
        Assert.assertEquals(3, service.items(type.getId()).size());
        Assert.assertNotNull(service.item(type.getId(), 1l));
        Assert.assertNotNull(service.item(type.getId(), 2l));
        Assert.assertNotNull(service.item(type.getId(), 3l));

        // remove item
        Assert.assertTrue(service.removeItem(type.getId(), 1l));
        Assert.assertEquals(2, service.items(type.getId()).size());
        Assert.assertNotNull(service.item(type.getId(), 2l));
        Assert.assertNotNull(service.item(type.getId(), 3l));

        // add item for non existing type
        GenericType newType = new GenericType(2l, 0, "new-name", "group", null, null, null, null, null, null);
        service.item(newType, new GenericItem(1l, newType.getId(), null, null, null));

        // assert type and item data
        Assert.assertEquals(2, service.types().size());
        Assert.assertEquals(1, service.typeGroups().size());
        Assert.assertEquals(2, service.items(type.getId()).size());
        Assert.assertEquals(1, service.items(newType.getId()).size());

        // remove new type and assert items are also removed
        Assert.assertTrue(service.removeType(newType.getId()));
        Assert.assertEquals(1, service.types().size());
        Assert.assertEquals(1, service.typeGroups().size());
        Assert.assertEquals(2, service.items(type.getId()).size());
        Assert.assertEquals(0, service.items(newType.getId()).size());

        // remove type
        service.removeType(type.getId().longValue());
        Assert.assertTrue(service.types().isEmpty());
        Assert.assertNull(service.type(type.getId()));
        Assert.assertTrue(service.typeGroups().isEmpty());
    }
}
