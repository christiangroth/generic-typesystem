package de.chrgroth.generictypesystem.context;

import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;

/**
 * Basic context interface for all service operations.
 *
 * @author Christian Groth
 */
public interface GenericTypesystemContext {

    /**
     * Returns the current user, if any.
     *
     * @return current user or null
     */
    Long currentUser();

    /**
     * Checks if the given type is accessible for current user identified by {@link #currentUser()}.<br>
     * <br>
     * By default a type is accessible if
     * <ul>
     * <li>the type has no visibility</li>
     * <li>the type has public visibility</li>
     * <li>the type has private visibility and the type owner equals current user</li>
     * </ul>
     *
     * @param type
     *            type to be checked
     * @return true if accessible, false otherwise
     */
    boolean isTypeAccessible(GenericType type);

    /**
     * Checks if the given item for given type is accessible for current user identified by {@link #currentUser()}. This method is called only if the type is
     * accessible for current user, based on {@link #isTypeAccessible(GenericType)}.<br>
     * <br>
     * By default an item is accessible if
     * <ul>
     * <li>the type owner equals current user</li>
     * <li>the item has no visibility</li>
     * <li>the item has public visibility</li>
     * <li>the item has private visibility and the item owner equals current user</li>
     * </ul>
     *
     * @param type
     *            accessible type
     * @param item
     *            item to be checked
     * @return true if accessible, false otherwise
     */
    boolean isItemAccessible(GenericType type, GenericItem item);
}
