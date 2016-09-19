package de.chrgroth.generictypesystem.persistence.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.persistence.PersistenceService;
import de.chrgroth.generictypesystem.persistence.query.ItemPagingData;
import de.chrgroth.generictypesystem.persistence.query.ItemQueryResult;
import de.chrgroth.generictypesystem.persistence.query.ItemsQueryData;
import de.chrgroth.generictypesystem.persistence.query.impl.InMemoryItemsQueryService;
import de.chrgroth.generictypesystem.persistence.values.impl.InMemoryValueProposalService;

/**
 * Very simple in memory persistence service storing all types ad items in internal transient collections.
 *
 * @author Christian Groth
 */
// TODO type/item id handling before save
public class InMemoryPersistenceService implements PersistenceService {

    private final Set<GenericType> types;
    private final Map<Long, Set<GenericItem>> items;

    private final InMemoryItemsQueryService query;
    private final InMemoryValueProposalService values;

    public InMemoryPersistenceService(InMemoryItemsQueryService query, InMemoryValueProposalService values) {

        // storage
        types = new HashSet<>();
        items = new HashMap<>();

        // services
        if (query == null || values == null) {
            throw new IllegalArgumentException("query and value services must be given!!");
        }
        this.query = query;
        this.values = values;

    }

    @Override
    public Set<String> typeGroups() {
        return types.stream().map(t -> t.getGroup()).distinct().collect(Collectors.toSet());
    }

    @Override
    public Set<GenericType> types() {
        return new HashSet<>(types);
    }

    @Override
    public GenericType type(long typeId) {
        return types.stream().filter(t -> t.getId() != null && t.getId().longValue() == typeId).findFirst().orElse(null);
    }

    @Override
    public void type(GenericType type) {
        if (type != null) {
            types.add(type);
            if (type.getId() != null && !items.containsKey(type.getId())) {
                items.put(type.getId(), new HashSet<>());
            }
        }
    }

    @Override
    public Set<GenericItem> items(long typeId) {

        // be null safe
        Set<GenericItem> typeItems = items.get(typeId);
        return typeItems != null ? new HashSet<>(typeItems) : Collections.emptySet();
    }

    @Override
    public ItemQueryResult query(long typeId, ItemsQueryData data) {

        // check for paging data
        if (data != null && data.getPaging() != null) {

            // check for missing or invalid page size
            ItemPagingData paging = data.getPaging();
            if (paging.getPageSize() == null || paging.getPageSize().intValue() < 1) {

                // use types configured and valid page size
                GenericType type = type(typeId);
                if (type != null && type.getPageSize() != null && type.getPageSize().longValue() > 0) {
                    paging.setPageSize(type.getPageSize());
                }
            }
        }

        // delegate
        return query.query(items(typeId), data != null ? data.getFilter() : null, data != null ? data.getSorts() : null, data != null ? data.getPaging() : null);
    }

    @Override
    public Map<String, List<?>> values(long typeId, GenericItem template) {

        // ensure type
        GenericType type = type(typeId);
        if (type == null) {
            return Collections.emptyMap();
        }

        // collect all items
        Set<GenericItem> items = items(typeId);
        if (items == null || items.isEmpty()) {
            return Collections.emptyMap();
        }

        // delegate
        return values.values(type, items, template);
    }

    @Override
    public GenericItem item(long typeId, long id) {
        return items(typeId).stream().filter(i -> i.getId() != null && i.getId().longValue() == id).findFirst().orElse(null);
    }

    @Override
    public void item(GenericType type, GenericItem item) {
        if (type != null && item != null) {
            type(type);
            if (type.getId() != null) {
                items.get(type.getId()).add(item);
            }
        }
    }

    @Override
    public boolean removeItem(long typeId, long id) {

        // just remove
        Set<GenericItem> typeItems = items.get(typeId);
        if (typeItems != null) {
            typeItems.removeIf(i -> i.getId() != null && i.getId().longValue() == id);
        }

        // always success, no error handling
        return true;
    }

    @Override
    public boolean removeType(long typeId) {

        // just remove
        types.removeIf(t -> t.getId() != null && t.getId().longValue() == typeId);
        items.remove(typeId);

        // always success, no error handling
        return true;
    }
}
