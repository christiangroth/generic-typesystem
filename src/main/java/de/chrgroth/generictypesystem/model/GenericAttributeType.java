package de.chrgroth.generictypesystem.model;

import java.util.Arrays;
import java.util.List;

public enum GenericAttributeType {
    STRING(true, false, true, true, false, String.class),

    LONG(true, true, false, false, true, Long.class, Integer.class), DOUBLE(true, true, false, false, true, Double.class, Long.class, Integer.class),

    BOOLEAN(Boolean.class),

    DATE(String.class), TIME(false, true, false, false, false, String.class), DATETIME(false, true, false, false, false, String.class),

    STRUCTURE(GenericItem.class),

    LIST(List.class);

    private final boolean minMaxCapable;
    private final boolean stepCapable;
    private final boolean patternCapable;
    private final boolean valueProposalDependenciesCapable;
    private final boolean unitCapable;
    private final List<Class<?>> typeClasses;

    GenericAttributeType(Class<?>... typeClasses) {
        this(false, false, false, false, false, typeClasses);
    }

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
