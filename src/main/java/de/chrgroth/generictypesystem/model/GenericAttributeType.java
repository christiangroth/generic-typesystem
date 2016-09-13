package de.chrgroth.generictypesystem.model;

import java.util.Arrays;
import java.util.List;

// TODO make extensible?
public enum GenericAttributeType {

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

    GenericAttributeType(boolean minMaxCapable, boolean stepCapable, boolean patternCapable, boolean valueProposalDependenciesCapable, boolean unitCapable,
            Class<?>... typeClasses) {
        this.minMaxCapable = minMaxCapable;
        this.stepCapable = stepCapable;
        this.patternCapable = patternCapable;
        this.valueProposalDependenciesCapable = valueProposalDependenciesCapable;
        this.unitCapable = unitCapable;
        this.typeClasses = Arrays.asList(typeClasses);
    }

    public boolean isMinMaxCapable() {
        return minMaxCapable;
    }

    public boolean isStepCapable() {
        return stepCapable;
    }

    public boolean isPatternCapable() {
        return patternCapable;
    }

    public boolean isValueProposalDependenciesCapable() {
        return valueProposalDependenciesCapable;
    }

    public boolean isUnitCapable() {
        return unitCapable;
    }

    public boolean isList() {
        return this == LIST;
    }

    public boolean isStructure() {
        return this == GenericAttributeType.STRUCTURE;
    }

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
