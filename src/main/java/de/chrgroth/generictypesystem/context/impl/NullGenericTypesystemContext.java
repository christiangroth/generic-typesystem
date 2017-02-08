package de.chrgroth.generictypesystem.context.impl;

import de.chrgroth.generictypesystem.context.GenericTypesystemContext;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;

/**
 * Context implementation to be used instead of null. Always returns true for {@link #isTypeAccessible(GenericType)} and
 * {@link #isItemAccessible(GenericType, GenericItem)}.
 *
 * @author Christian Groth
 */
public class NullGenericTypesystemContext implements GenericTypesystemContext {

    @Override
    public Long currentUser() {
        return null;
    }

    @Override
    public boolean isTypeAccessible(GenericType type) {
        return true;
    }

    @Override
    public boolean isItemAccessible(GenericType type, GenericItem item) {
        return true;
    }
}
