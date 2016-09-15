package de.chrgroth.generictypesystem.model;

import java.util.Arrays;
import java.util.List;

/**
 * The default enumeration of all supported attribute types.
 *
 * @author Christian Groth
 */
public enum DefaultGenericAttributeType implements GenericAttributeType {

    STRING(String.class),
    LONG(Long.class, Integer.class),
    DOUBLE(Double.class, Float.class, Long.class, Integer.class),
    BOOLEAN(Boolean.class),
    DATE(String.class),
    TIME(String.class),
    DATETIME(String.class),
    STRUCTURE(GenericItem.class),
    LIST(List.class);

    private final List<Class<?>> typeClasses;

    DefaultGenericAttributeType(Class<?>... typeClasses) {
        this.typeClasses = Arrays.asList(typeClasses);
    }

    @Override
    public boolean isNumeric() {
        return this == LONG || this == DOUBLE;
    }

    @Override
    public boolean isText() {
        return this == STRING;
    }

    @Override
    public boolean isMinMaxCapable() {
        return isNumeric() || isText();
    }

    @Override
    public boolean isStepCapable() {
        return isNumeric() || this == TIME || this == DATETIME;
    }

    @Override
    public boolean isPatternCapable() {
        return isText();
    }

    @Override
    public boolean isValueProposalDependenciesCapable() {
        return isText();
    }

    @Override
    public boolean isUnitCapable() {
        return isNumeric();
    }

    @Override
    public boolean isList() {
        return this == LIST;
    }

    @Override
    public boolean isStructure() {
        return this == STRUCTURE;
    }

    @Override
    public boolean isAssignableFrom(Class<?> actualClass) {

        // check all classes
        for (Class<?> typeClass : typeClasses) {
            if (typeClass.isAssignableFrom(actualClass)) {
                return true;
            }
        }

        // none found
        return false;
    }
}
