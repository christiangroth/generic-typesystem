package de.chrgroth.generictypesystem.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

/**
 * The default enumeration of all supported attribute types.
 *
 * @author Christian Groth
 */
public enum DefaultGenericAttributeType implements GenericAttributeType {

    STRING(String.class),
    LONG(Long.class, Integer.class) {

        @Override
        protected Object convertAssignableValueForMultipleTypes(Class<?> valueClass, Object value) {

            // convert from integer
            if (Integer.class.equals(valueClass)) {
                return Long.valueOf((Integer) value);
            }

            // is long
            return value;
        }
    },
    DOUBLE(Double.class, Float.class, Long.class, Integer.class) {

        @Override
        protected Object convertAssignableValueForMultipleTypes(Class<?> valueClass, Object value) {

            // convert from integer
            if (Integer.class.equals(valueClass)) {
                return Double.valueOf((Integer) value);
            }

            // convert from long
            if (Long.class.equals(valueClass)) {
                return Double.valueOf((Long) value);
            }

            // convert from float
            if (Float.class.equals(valueClass)) {
                return Double.valueOf((Float) value);
            }

            // is double
            return value;
        }
    },
    BOOLEAN(Boolean.class),
    DATE(LocalDate.class, String.class),
    TIME(LocalTime.class, String.class),
    DATETIME(LocalDateTime.class, String.class),
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
    public boolean isDefaultValueCapable() {
        return this == STRING || this == LONG || this == DOUBLE || this == BOOLEAN;
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

    @Override
    public Object convert(java.lang.Object value) {

        // check value is assignable
        if (value == null || !isAssignableFrom(value.getClass())) {
            return null;
        }

        // just type cast if only one allowed type is configured
        if (typeClasses.size() == 1) {
            return value;
        }

        // delegate
        return convertAssignableValueForMultipleTypes(value.getClass(), value);
    }

    protected Object convertAssignableValueForMultipleTypes(Class<?> valueClass, Object value) {
        return null;
    }
}
