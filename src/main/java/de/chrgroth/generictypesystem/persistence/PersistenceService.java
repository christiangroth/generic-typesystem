package de.chrgroth.generictypesystem.persistence;

import java.util.Set;

import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;

// TODO add exception to interface to indicate persistence problems??
/**
 * Common persistence service interface for instances of {@link GenericType} and {@link GenericItem}.
 *
 * @author Christian Groth
 */
public interface PersistenceService {

    /**
     * Returns all known type groups.
     *
     * @return known type groups
     */
    Set<String> typeGroups();

    /**
     * Returns all known types.
     *
     * @return known types
     */
    Set<GenericType> types();

    /**
     * Returns the type with given id.
     *
     * @param typeId
     *            type id
     * @return known type or null
     */
    GenericType type(long typeId);

    /**
     * Saves the given type or updates it if a type with same id is already known.
     *
     * @param type
     *            type to be saved
     */
    void type(GenericType type);

    /**
     * Returns all known items for given type id.
     *
     * @param typeId
     *            type id
     * @return known items
     */
    Set<GenericItem> items(long typeId);

    /**
     * Returns the item with given type id and item id.
     *
     * @param typeId
     *            type id
     * @param id
     *            item id
     * @return known item or null
     */
    GenericItem item(long typeId, long id);

    /**
     * Saves the given item or updates it if an item with same id is already known.
     *
     * @param type
     *            type to be saved
     */
    void item(GenericType type, GenericItem item);

    /**
     * Removes the item for given type id and item id.
     *
     * @param typeId
     *            type id
     * @param id
     *            item id
     * @return true if item is no longer known after this operation, false otherwise
     */
    boolean removeItem(long typeId, long id);

    /**
     * Removes the type and all it's items for given type id.
     *
     * @param typeId
     *            type id
     * @return true if type is no longer known after this operation, false otherwise
     */
    boolean removeType(long typeId);
}
