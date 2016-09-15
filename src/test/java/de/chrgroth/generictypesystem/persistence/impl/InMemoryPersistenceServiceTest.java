package de.chrgroth.generictypesystem.persistence.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;

public class InMemoryPersistenceServiceTest {

    private InMemoryPersistenceService service;

    @Before
    public void setup() {
        service = new InMemoryPersistenceService(10);
    }

    @Test
    public void typeAndItemLifecycle() {

        // assert empty
        Assert.assertTrue(service.types().isEmpty());
        Assert.assertTrue(service.typeGroups().isEmpty());

        // add type
        GenericType type = new GenericType(0l, 0, "name", "group", null, null, null, null, null, 10l);
        service.type(type);

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
        service.item(type, new GenericItem(0l, type.getId(), null, null, null));
        service.item(type, new GenericItem(1l, type.getId(), null, null, null));
        service.item(type, new GenericItem(2l, type.getId(), null, null, null));

        // assert item data
        Assert.assertEquals(3, service.items(type.getId()).size());
        Assert.assertNotNull(service.item(type.getId(), 0l));
        Assert.assertNotNull(service.item(type.getId(), 1l));
        Assert.assertNotNull(service.item(type.getId(), 2l));

        // remove item
        Assert.assertTrue(service.removeItem(type.getId(), 1l));
        Assert.assertEquals(2, service.items(type.getId()).size());
        Assert.assertNotNull(service.item(type.getId(), 0l));
        Assert.assertNotNull(service.item(type.getId(), 2l));

        // add item for non existing type
        GenericType newType = new GenericType(1l, 0, "new-name", "group", null, null, null, null, null, 10l);
        service.item(newType, new GenericItem(0l, newType.getId(), null, null, null));

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
