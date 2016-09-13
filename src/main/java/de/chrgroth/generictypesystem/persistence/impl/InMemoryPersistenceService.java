package de.chrgroth.generictypesystem.persistence.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.persistence.PersistenceService;

/**
 * Very simple in memory persistence service storing all types ad items in internal transient collections.
 *
 * @author Christian Groth
 */
public class InMemoryPersistenceService implements PersistenceService {

    private final Set<GenericType> types = new HashSet<>();
    private final Map<Long, Set<GenericItem>> items = new HashMap<>();

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
