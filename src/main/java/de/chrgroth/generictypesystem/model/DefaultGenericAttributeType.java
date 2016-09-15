package de.chrgroth.generictypesystem.model;

import java.util.Arrays;
import java.util.List;

/**
 * The default enumeration of all supported attribute types.
 *
 * @author Christian Groth
 */
public enum DefaultGenericAttributeType implements GenericAttributeType {

    STRING(true, false, true, true, false, String.class),
    LONG(true, true, false, false, true, Long.class, Integer.class),
    DOUBLE(true, true, false, false, true, Double.class, Float.class, Long.class, Integer.class),
    BOOLEAN(false, false, false, false, false, Boolean.class),
    DATE(false, false, false, false, false, String.class),
    TIME(false, true, false, false, false, String.class),
    DATETIME(false, true, false, false, false, String.class),
    STRUCTURE(false, false, false, false, false, GenericItem.class),
    LIST(false, false, false, false, false, List.class);

    private final boolean minMaxCapable;
    private final boolean stepCapable;
    private final boolean patternCapable;
    private final boolean valueProposalDependenciesCapable;
    private final boolean unitCapable;
    private final List<Class<?>> typeClasses;

    DefaultGenericAttributeType(boolean minMaxCapable, boolean stepCapable, boolean patternCapable, boolean valueProposalDependenciesCapable, boolean unitCapable,
            Class<?>... typeClasses) {
        this.minMaxCapable = minMaxCapable;
        this.stepCapable = stepCapable;
        this.patternCapable = patternCapable;
        this.valueProposalDependenciesCapable = valueProposalDependenciesCapable;
        this.unitCapable = unitCapable;
        this.typeClasses = Arrays.asList(typeClasses);
    }

    @Override
    public boolean isMinMaxCapable() {
        return minMaxCapable;
    }

    @Override
    public boolean isStepCapable() {
        return stepCapable;
    }

    @Override
    public boolean isPatternCapable() {
        return patternCapable;
    }

    @Override
    public boolean isValueProposalDependenciesCapable() {
        return valueProposalDependenciesCapable;
    }

    @Override
    public boolean isUnitCapable() {
        return unitCapable;
    }

    @Override
    public boolean isList() {
        return this == LIST;
    }

    @Override
    public boolean isStructure() {
        return this == DefaultGenericAttributeType.STRUCTURE;
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
