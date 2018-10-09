package de.chrgroth.generictypesystem.persistence.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.chrgroth.generictypesystem.context.GenericTypesystemContext;
import de.chrgroth.generictypesystem.context.impl.NullGenericTypesystemContext;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.model.GenericUnit;
import de.chrgroth.generictypesystem.model.GenericUnits;
import de.chrgroth.generictypesystem.persistence.query.impl.InMemoryItemsQueryService;
import de.chrgroth.generictypesystem.persistence.values.impl.InMemoryValueProposalService;

public class InMemoryPersistenceServiceTest {

    private GenericTypesystemContext context;
    private InMemoryPersistenceService service;

    @Before
    public void setup() {

        // create context & service
        context = new NullGenericTypesystemContext();
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
    public void unitsLifecycle() {

        // assert empty
        Assert.assertTrue(service.units(context).isEmpty());

        // add units
        GenericUnits units = new GenericUnits(null, "time", "time units");
        units.getUnits().add(new GenericUnit(0l, "seconds", "s", GenericUnits.FACTOR_BASE));
        units.getUnits().add(new GenericUnit(1l, "minutes", "m", 60));
        service.units(context, units);

        // ensure id was set
        Assert.assertEquals(1l, units.getId().longValue());

        // assert units data
        Assert.assertFalse(service.units(context).isEmpty());
        Assert.assertEquals(1, service.units(context).size());
        Assert.assertEquals(units, service.units(context).iterator().next());
        Assert.assertNotNull(service.units(context, units.getId()));

        // add type again
        service.units(context, units);

        // assert type data not duplicate
        Assert.assertEquals(1, service.units(context).size());

        // remove non existing units
        Assert.assertTrue(service.removeUnits(context, Long.MAX_VALUE));
        Assert.assertEquals(1, service.units(context).size());

        // remove type
        service.removeUnits(context, units.getId().longValue());
        Assert.assertTrue(service.units(context).isEmpty());
        Assert.assertNull(service.units(context, units.getId()));
    }

    @Test
    public void typeAndItemLifecycle() {

        // assert empty
        Assert.assertTrue(service.types(context).isEmpty());
        Assert.assertTrue(service.typeGroups(context).isEmpty());

        // add type
        GenericType type = new GenericType(null, "name", "group", null, null, null, null);
        service.type(context, type);

        // ensure id was set
        Assert.assertEquals(1l, type.getId().longValue());

        // assert type data
        Assert.assertFalse(service.types(context).isEmpty());
        Assert.assertEquals(1, service.types(context).size());
        Assert.assertEquals(type, service.types(context).iterator().next());
        Assert.assertNotNull(service.type(context, type.getId()));
        Assert.assertFalse(service.typeGroups(context).isEmpty());
        Assert.assertEquals(1, service.typeGroups(context).size());
        Assert.assertEquals(type.getGroup(), service.typeGroups(context).iterator().next());

        // add type again
        service.type(context, type);

        // assert type data not duplicate
        Assert.assertEquals(1, service.types(context).size());
        Assert.assertEquals(1, service.typeGroups(context).size());

        // remove non existing type
        Assert.assertTrue(service.removeType(context, Long.MAX_VALUE));
        Assert.assertEquals(1, service.types(context).size());
        Assert.assertEquals(1, service.typeGroups(context).size());

        // add items for type
        GenericItem item = new GenericItem(null, type.getId(), null, null, null);
        service.item(context, type, item);
        service.item(context, type, new GenericItem(2l, type.getId(), null, null, null));
        service.item(context, type, new GenericItem(3l, type.getId(), null, null, null));

        // ensure id was set
        Assert.assertEquals(1l, item.getId().longValue());

        // assert item data
        Assert.assertEquals(3, service.items(context, type.getId()).size());
        Assert.assertNotNull(service.item(context, type.getId(), 1l));
        Assert.assertNotNull(service.item(context, type.getId(), 2l));
        Assert.assertNotNull(service.item(context, type.getId(), 3l));

        // remove item
        Assert.assertTrue(service.removeItem(context, type.getId(), 1l));
        Assert.assertEquals(2, service.items(context, type.getId()).size());
        Assert.assertNotNull(service.item(context, type.getId(), 2l));
        Assert.assertNotNull(service.item(context, type.getId(), 3l));

        // add item for non existing type
        GenericType newType = new GenericType(2l, "new-name", "group", null, null, null, null);
        service.item(context, newType, new GenericItem(1l, newType.getId(), null, null, null));

        // assert type and item data
        Assert.assertEquals(2, service.types(context).size());
        Assert.assertEquals(1, service.typeGroups(context).size());
        Assert.assertEquals(2, service.items(context, type.getId()).size());
        Assert.assertEquals(1, service.items(context, newType.getId()).size());

        // remove new type and assert items are also removed
        Assert.assertTrue(service.removeType(context, newType.getId()));
        Assert.assertEquals(1, service.types(context).size());
        Assert.assertEquals(1, service.typeGroups(context).size());
        Assert.assertEquals(2, service.items(context, type.getId()).size());
        Assert.assertEquals(0, service.items(context, newType.getId()).size());

        // remove type
        service.removeType(context, type.getId().longValue());
        Assert.assertTrue(service.types(context).isEmpty());
        Assert.assertNull(service.type(context, type.getId()));
        Assert.assertTrue(service.typeGroups(context).isEmpty());
    }
}
