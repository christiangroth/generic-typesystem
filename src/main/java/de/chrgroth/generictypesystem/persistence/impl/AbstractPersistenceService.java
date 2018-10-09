package de.chrgroth.generictypesystem.persistence.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrgroth.generictypesystem.context.GenericTypesystemContext;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.model.GenericUnits;
import de.chrgroth.generictypesystem.persistence.PersistenceService;
import de.chrgroth.generictypesystem.persistence.query.ItemPagingData;
import de.chrgroth.generictypesystem.persistence.query.ItemQueryResult;
import de.chrgroth.generictypesystem.persistence.query.ItemsQueryData;
import de.chrgroth.generictypesystem.persistence.query.impl.InMemoryItemsQueryService;
import de.chrgroth.generictypesystem.persistence.values.impl.InMemoryValueProposalService;

/**
 * Abstract persistence service implementing common functionality. Uses {@link InMemoryItemsQueryService} and {@link InMemoryValueProposalService}. Enhance this
 * class and implement abstract protected methods to connect to your persistence layer.
 *
 * @author Christian Groth
 */
public abstract class AbstractPersistenceService implements PersistenceService {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPersistenceService.class);

    private final InMemoryItemsQueryService query;
    private final InMemoryValueProposalService values;

    public AbstractPersistenceService(InMemoryItemsQueryService query, InMemoryValueProposalService values) {

        // services
        if (query == null || values == null) {
            throw new IllegalArgumentException("query and value services must be given!!");
        }
        this.query = query;
        this.values = values;
    }

    @Override
    public Set<GenericUnits> units(GenericTypesystemContext context) {
        return units();
    }

    @Override
    public GenericUnits units(GenericTypesystemContext context, long unitsId) {
        return units(context).stream().filter(u -> Objects.equals(u.getId(), unitsId)).findFirst().orElse(null);
    }

    @Override
    public void units(GenericTypesystemContext context, GenericUnits units) {
        if (units != null) {

            // ensure id
            if (units.getId() == null) {
                units.setId(nextUnitsId());
            }

            // re-add
            boolean removed = removeUnits(units.getId().longValue());
            addUnits(units);
            if (LOG.isDebugEnabled()) {
                LOG.debug((removed ? "updated" : "added") + " units " + units);
            }
        }
    }

    @Override
    public Set<String> typeGroups(GenericTypesystemContext context) {
        return types(context).stream().map(t -> t.getGroup()).distinct().collect(Collectors.toSet());
    }

    @Override
    public Set<GenericType> types(GenericTypesystemContext context) {
        return types().stream().filter(context::isTypeAccessible).collect(Collectors.toSet());
    }

    @Override
    public GenericType type(GenericTypesystemContext context, long typeId) {
        return types(context).stream().filter(t -> t.getId() != null && t.getId().longValue() == typeId).findFirst().orElse(null);
    }

    @Override
    public void type(GenericTypesystemContext context, GenericType type) {
        if (type != null) {

            // check if accessible
            if (!context.isTypeAccessible(type)) {
                LOG.error("unable to save/update inaccessible type " + type + ": " + context.currentUser());
                return;
            }

            // ensure id
            if (type.getId() == null) {
                type.setId(nextTypeId());
            }

            // re-add
            boolean removed = removeType(type.getId().longValue());
            addType(type);
            if (LOG.isDebugEnabled()) {
                LOG.debug((removed ? "updated" : "added") + " type " + type);
            }
        }
    }

    @Override
    public Set<GenericItem> items(GenericTypesystemContext context, long typeId) {

        // check type access
        GenericType type = type(context, typeId);
        if (!context.isTypeAccessible(type)) {
            LOG.error("unable to retrieve items for inaccessible type " + type + ": " + context.currentUser());
            return Collections.emptySet();
        }

        // be null safe
        Collection<GenericItem> typeItems = items(typeId);
        if (typeItems == null) {
            return Collections.emptySet();
        }

        // return filtered types
        return typeItems.stream().filter(i -> context.isItemAccessible(type, i)).collect(Collectors.toSet());
    }

    @Override
    public ItemQueryResult query(GenericTypesystemContext context, long typeId, ItemsQueryData data) {

        // check if accessible
        GenericType type = type(context, typeId);
        if (!context.isTypeAccessible(type)) {
            LOG.error("unable to query items for inaccessible type " + type + ": " + context.currentUser());
            new ItemQueryResult(Collections.emptyList(), false);
        }

        // check for paging data
        if (data != null && data.getPaging() != null) {

            // check for missing or invalid page size and use types configured and valid page size
            ItemPagingData paging = data.getPaging();
            if ((paging.getPageSize() == null || paging.getPageSize().intValue() < 1) && type != null && type.getPageSize() != null && type.getPageSize().longValue() > 0) {
                paging.setPageSize(type.getPageSize());
            }
        }

        // delegate
        return query.query(items(context, typeId), data != null ? data.getFilter() : null, data != null ? data.getSorts() : null, data != null ? data.getPaging() : null);
    }

    @Override
    public Map<String, List<?>> values(GenericTypesystemContext context, long typeId, GenericItem template) {

        // ensure type
        GenericType type = type(context, typeId);
        if (!context.isTypeAccessible(type)) {
            LOG.error("unable to calculate item values for inaccessible type " + type + ": " + context.currentUser());
            return Collections.emptyMap();
        }

        // collect all items
        Set<GenericItem> items = items(context, typeId);
        if (items == null || items.isEmpty()) {
            return Collections.emptyMap();
        }

        // delegate
        return values.values(type, items, template);
    }

    @Override
    public GenericItem item(GenericTypesystemContext context, long typeId, long id) {
        return items(context, typeId).stream().filter(i -> i.getId() != null && i.getId().longValue() == id).findFirst().orElse(null);
    }

    @Override
    public void item(GenericTypesystemContext context, GenericType type, GenericItem item) {
        if (type != null && item != null) {

            // check if accessible
            if (!context.isTypeAccessible(type)) {
                LOG.error("unable to save/update item for inaccessible type " + type + ": " + context.currentUser());
                return;
            }

            // check if accessible
            if (!context.isItemAccessible(type, item)) {
                LOG.error("unable to save/update inaccessible item " + item + ": " + context.currentUser());
                return;
            }

            // ensure type
            type(context, type);
            final Long typeId = type.getId();
            if (typeId != null) {

                // ensure id
                if (item.getId() == null) {
                    item.setId(nextItemId(typeId));
                }

                // re-add item
                boolean removed = removeItem(typeId, item.getId().longValue());
                addItem(typeId, item);
                if (LOG.isDebugEnabled()) {
                    LOG.debug((removed ? "updated" : "added") + " item " + item);
                }
            }
        }
    }

    @Override
    public boolean removeItem(GenericTypesystemContext context, long typeId, long id) {

        // check if accessible
        GenericType type = type(context, typeId);
        if (!context.isTypeAccessible(type)) {
            LOG.error("unable to remove item for inaccessible type " + type + ": " + context.currentUser());
            return false;
        }

        // check if accessible
        GenericItem item = item(context, typeId, id);
        if (!context.isItemAccessible(type, item)) {
            LOG.error("unable to remove inaccessible item " + item + ": " + context.currentUser());
            return false;
        }

        // just remove
        removeItem(typeId, id);

        // always success, no error handling
        if (LOG.isDebugEnabled()) {
            LOG.debug("removed item " + typeId + "/" + id);
        }
        return true;
    }

    @Override
    public boolean removeType(GenericTypesystemContext context, long typeId) {

        // check if accessible
        GenericType type = type(context, typeId);
        if (!context.isTypeAccessible(type)) {
            LOG.error("unable to remove inaccessible type " + type + ": " + context.currentUser());
            return false;
        }

        // just remove
        removeType(typeId);
        removeAllItems(typeId);

        // always success, no error handling
        if (LOG.isDebugEnabled()) {
            LOG.debug("removed type " + typeId);
        }
        return true;
    }

    @Override
    public boolean removeUnits(GenericTypesystemContext context, long unitsId) {

        // just remove
        removeUnits(unitsId);

        // always success, no error handling
        if (LOG.isDebugEnabled()) {
            LOG.debug("removed units " + unitsId);
        }
        return true;
    }

    protected abstract Set<GenericUnits> units();

    protected abstract long nextUnitsId();

    protected abstract void addUnits(GenericUnits units);

    protected abstract boolean removeUnits(long id);

    protected abstract Collection<GenericType> types();

    protected abstract long nextTypeId();

    protected abstract void addType(GenericType type);

    protected abstract boolean removeType(long id);

    protected abstract Collection<GenericItem> items(long typeId);

    protected abstract long nextItemId(long typeId);

    protected abstract void addItem(long typeId, GenericItem item);

    protected abstract boolean removeItem(long typeId, long id);

    protected abstract void removeAllItems(long typeId);
}
