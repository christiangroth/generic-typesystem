package de.chrgroth.generictypesystem.persistence.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.persistence.query.impl.InMemoryItemsQueryService;
import de.chrgroth.generictypesystem.persistence.values.impl.InMemoryValueProposalService;

/**
 * Very simple in memory persistence service storing all types ad items in internal transient collections.
 *
 * @author Christian Groth
 */
public class InMemoryPersistenceService extends AbstractPersistenceService {

    private final Set<GenericType> types;
    private final Map<Long, Set<GenericItem>> items;

    public InMemoryPersistenceService(InMemoryItemsQueryService query, InMemoryValueProposalService values) {
        super(query, values);

        // storage
        types = new HashSet<>();
        items = new HashMap<>();
    }

    @Override
    protected Collection<GenericType> types() {
        return new HashSet<>(types);
    }

    @Override
    protected long nextTypeId() {
        return types.stream().mapToLong(t -> t.getId()).max().orElse(0) + 1;
    }

    @Override
    protected void addType(GenericType type) {
        types.add(type);
    }

    @Override
    protected boolean removeType(long id) {
        return types.removeIf(t -> t.getId() != null && t.getId().longValue() == id);
    }

    @Override
    protected Collection<GenericItem> items(long typeId) {
        final Set<GenericItem> typeItems = items.get(typeId);
        return new HashSet<>(typeItems != null ? typeItems : Collections.emptySet());
    }

    @Override
    protected long nextItemId(long typeId) {
        final Set<GenericItem> typeItems = items.get(typeId);
        return typeItems != null ? typeItems.stream().mapToLong(i -> i.getId()).max().orElse(0) + 1 : 1;
    }

    @Override
    protected void addItem(long typeId, GenericItem item) {
        Set<GenericItem> typeItems = items.get(typeId);
        if (typeItems == null) {
            typeItems = new HashSet<>();
            items.put(typeId, typeItems);
        }
        typeItems.add(item);
    }

    @Override
    protected boolean removeItem(long typeId, long id) {
        final Set<GenericItem> typeItems = items.get(typeId);
        return typeItems != null && typeItems.removeIf(t -> t.getId() != null && t.getId().longValue() == id);
    }

    @Override
    protected void removeAllItems(long typeId) {
        items.remove(typeId);
    }
}
