package de.chrgroth.generictypesystem.context.impl;

import java.util.Objects;

import de.chrgroth.generictypesystem.context.GenericTypesystemContext;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.model.Visibility;

/**
 * Default context implementation.
 *
 * @author Christian Groth
 */
public class DefaultGenericTypesystemContext implements GenericTypesystemContext {

    private Long currentUser;

    public DefaultGenericTypesystemContext() {
        this(null);
    }

    public DefaultGenericTypesystemContext(Long currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public Long currentUser() {
        return currentUser;
    }

    @Override
    public boolean isTypeAccessible(GenericType type) {

        // null guard
        if (type == null) {
            return false;
        }

        // public type
        if (type.getVisibility() == null || type.getVisibility() == Visibility.PUBLIC) {
            return true;
        }

        // private visibility
        if (type.getVisibility() == Visibility.PRIVATE) {
            return Objects.equals(type.getOwner(), currentUser());
        }

        // failed
        return false;
    }

    @Override
    public boolean isItemAccessible(GenericType type, GenericItem item) {

        // null guard
        if (item == null) {
            return false;
        }

        // current users type
        if (Objects.equals(type.getOwner(), currentUser())) {
            return true;
        }

        // other users public type and public item
        if (item.getVisibility() == null || item.getVisibility() == Visibility.PUBLIC) {
            return true;
        }

        // public type, private item
        if (item.getVisibility() == Visibility.PRIVATE) {
            return Objects.equals(item.getOwner(), currentUser());
        }

        // failed
        return false;
    }
}
