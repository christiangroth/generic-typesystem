package de.chrgroth.generictypesystem.persistence;

import java.util.List;
import java.util.Map;
import java.util.Set;

import de.chrgroth.generictypesystem.context.GenericTypesystemContext;
import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.model.GenericUnits;
import de.chrgroth.generictypesystem.persistence.query.ItemQueryResult;
import de.chrgroth.generictypesystem.persistence.query.ItemsQueryData;

/**
 * Common persistence service interface for instances of {@link GenericType} and {@link GenericItem}.
 *
 * @author Christian Groth
 */
// TODO add unit tests with visibility checks
public interface PersistenceService {

    /**
     * Returns all known units.
     *
     * @param context
     *            current context
     * @return known units
     */
    Set<GenericUnits> units(GenericTypesystemContext context);

    /**
     * Returns the units with given id.
     *
     * @param context
     *            current context
     * @param unitsId
     *            units id
     * @return known units or null
     */
    GenericUnits units(GenericTypesystemContext context, long unitsId);

    /**
     * Saves the given units or updates it if units with same id are already known.
     *
     * @param context
     *            current context
     * @param units
     *            units to be saved
     * @return true if successful, false otherwise
     */
    boolean units(GenericTypesystemContext context, GenericUnits units);

    /**
     * Returns groups for all known and accessible types.
     *
     * @param context
     *            current context
     * @return known type groups
     */
    Set<String> typeGroups(GenericTypesystemContext context);

    /**
     * Returns all known and accessible types.
     *
     * @param context
     *            current context
     * @return known types
     */
    Set<GenericType> types(GenericTypesystemContext context);

    /**
     * Returns the type with given id. If the type is not accessible null will be returned instead.
     *
     * @param context
     *            current context
     * @param typeId
     *            type id
     * @return known type or null
     */
    GenericType type(GenericTypesystemContext context, long typeId);

    /**
     * Saves the given type or updates it if a type with same id is already known. If the type is not accessible the operation will be ignored.
     *
     * @param context
     *            current context
     * @param type
     *            type to be saved
     * @return true if successful, false otherwise
     */
    boolean type(GenericTypesystemContext context, GenericType type);

    /**
     * Returns all known and accessible items for given type id.
     *
     * @param context
     *            current context
     * @param typeId
     *            type id
     * @return known items
     */
    Set<GenericItem> items(GenericTypesystemContext context, long typeId);

    /**
     * Returns item query result for given query data.
     *
     * @param context
     *            current context
     * @param typeId
     *            type id
     * @param data
     *            query data
     * @return query result
     */
    ItemQueryResult query(GenericTypesystemContext context, long typeId, ItemsQueryData data);

    /**
     * Returns all value proposals wrapped in a map with key representing the attribute path and value the list of value proposals. If the given template item
     * is not null, only items matching the values for defined {@link GenericAttribute#getValueProposalDependencies()} will be processed.
     *
     * @param context
     *            current context
     * @param typeId
     *            type id
     * @param template
     *            optional template item
     * @return value proposals
     */
    Map<String, List<?>> values(GenericTypesystemContext context, long typeId, GenericItem template);

    /**
     * Returns the item with given type id and item id. If type or item are not accessible null will be returned instead.
     *
     * @param context
     *            current context
     * @param typeId
     *            type id
     * @param id
     *            item id
     * @return known item or null
     */
    GenericItem item(GenericTypesystemContext context, long typeId, long id);

    /**
     * Saves the given item or updates it if an item with same id is already known. If the type or item is not accessible the operation will be ignored.
     *
     * @param context
     *            current context
     * @param typeId
     *            type id the item belongs to
     * @param item
     *            item to be saved
     * @return true if successful, false otherwise
     */
    boolean item(GenericTypesystemContext context, long typeId, GenericItem item);

    /**
     * Removes the item for given type id and item id. If the type or item is not accessible the operation will be ignored.
     *
     * @param context
     *            current context
     * @param typeId
     *            type id
     * @param id
     *            item id
     * @return true if item is no longer known after this operation, false otherwise
     */
    boolean removeItem(GenericTypesystemContext context, long typeId, long id);

    /**
     * Removes the type and all it's items for given type id. If the type is not accessible the operation will be ignored.
     *
     * @param context
     *            current context
     * @param typeId
     *            type id
     * @return true if type is no longer known after this operation, false otherwise
     */
    boolean removeType(GenericTypesystemContext context, long typeId);

    /**
     * Removes the units with given id. If the type is not accessible the operation will be ignored.
     *
     * @param context
     *            current context
     * @param unitsId
     *            units id
     * @return true if units is no longer known after this operation, false otherwise
     */
    boolean removeUnits(GenericTypesystemContext context, long unitsId);
}
