package de.chrgroth.generictypesystem.persistence.impl;

import java.util.HashMap;
import java.util.Map;

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
        Assert.assertTrue(service.units(context, units));

        // ensure id was set
        Assert.assertEquals(1l, units.getId().longValue());

        // assert units data
        Assert.assertFalse(service.units(context).isEmpty());
        Assert.assertEquals(1, service.units(context).size());
        Assert.assertEquals(units, service.units(context).iterator().next());
        Assert.assertNotNull(service.units(context, units.getId()));
        Assert.assertEquals("time units", service.units(context, units.getId()).getDescription());

        // add units again
        Assert.assertTrue(service.units(context, units));

        // assert units data not duplicate
        Assert.assertEquals(1, service.units(context).size());

        // update units
        GenericUnits newUnits = new GenericUnits(units.getId(), "time", "empty units");
        Assert.assertTrue(service.units(context, newUnits));

        // assert updated units data
        Assert.assertEquals(1, service.units(context).size());
        Assert.assertEquals("empty units", service.units(context, units.getId()).getDescription());

        // remove non existing units
        Assert.assertTrue(service.removeUnits(context, Long.MAX_VALUE));
        Assert.assertEquals(1, service.units(context).size());

        // remove units
        Assert.assertTrue(service.removeUnits(context, units.getId().longValue()));
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
        Assert.assertTrue(service.type(context, type));

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
        Assert.assertNull(service.type(context, type.getId()).getPageSize());

        // add same type again
        Assert.assertTrue(service.type(context, type));

        // assert type data not duplicate
        Assert.assertEquals(1, service.types(context).size());
        Assert.assertEquals(1, service.typeGroups(context).size());

        // update type
        GenericType newType = new GenericType(type.getId(), "name", "group", null, null, null, 100l);
        Assert.assertTrue(service.type(context, newType));

        // assert updated type data
        Assert.assertEquals(1, service.types(context).size());
        Assert.assertEquals(Long.valueOf(100l), service.type(context, type.getId()).getPageSize());

        // remove non existing type
        Assert.assertTrue(service.removeType(context, Long.MAX_VALUE));
        Assert.assertEquals(1, service.types(context).size());
        Assert.assertEquals(1, service.typeGroups(context).size());

        // add item for type
        Map<String, Object> itemValues = new HashMap<>();
        itemValues.put("foo", "bar");
        GenericItem item = new GenericItem(null, type.getId(), itemValues, null, null);
        Assert.assertTrue(service.item(context, type.getId(), item));

        // ensure item data
        Assert.assertEquals(1l, item.getId().longValue());
        Assert.assertEquals(1, service.items(context, type.getId()).size());
        Assert.assertNotNull(service.item(context, type.getId(), 1l));
        Assert.assertEquals("bar", service.item(context, type.getId(), 1l).get("foo"));

        // update item (new reference)
        Map<String, Object> newItemValues = new HashMap<>();
        newItemValues.put("foo", "baz");
        GenericItem newItem = new GenericItem(item.getId(), type.getId(), newItemValues, null, null);
        Assert.assertTrue(service.item(context, type.getId(), newItem));

        // ensure updated item data
        Assert.assertEquals(1l, newItem.getId().longValue());
        Assert.assertEquals(1, service.items(context, type.getId()).size());
        Assert.assertNotNull(service.item(context, type.getId(), 1l));
        Assert.assertEquals("baz", service.item(context, type.getId(), 1l).get("foo"));

        // remove item
        Assert.assertTrue(service.removeItem(context, type.getId(), 1l));
        Assert.assertTrue(service.items(context, type.getId()).isEmpty());

        // add item for non existing type
        GenericType unknownType = new GenericType(666l, "new-name", "group", null, null, null, null);
        Assert.assertFalse(service.item(context, unknownType.getId(), new GenericItem(1l, unknownType.getId(), null, null, null)));

        // assert type and item data still the same (nothing was persisted)
        Assert.assertEquals(1, service.types(context).size());
        Assert.assertEquals(1, service.typeGroups(context).size());
        Assert.assertEquals(0, service.items(context, type.getId()).size());
        Assert.assertEquals(0, service.items(context, unknownType.getId()).size());

        // add item again
        item.setId(null);
        Assert.assertTrue(service.item(context, type.getId(), item));
        newItem.setId(null);
        Assert.assertTrue(service.item(context, type.getId(), newItem));
        Assert.assertEquals(2, service.items(context, type.getId()).size());

        // remove type and assert items are also removed
        Assert.assertTrue(service.removeType(context, type.getId().longValue()));
        Assert.assertTrue(service.types(context).isEmpty());
        Assert.assertTrue(service.typeGroups(context).isEmpty());
        Assert.assertNull(service.type(context, type.getId()));
        Assert.assertEquals(0, service.items(context, type.getId()).size());
    }
}
