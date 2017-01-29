package de.chrgroth.generictypesystem.model;

/**
 * Data class to store generic values including type information.
 *
 * @author Christian Groth
 * @param <T>
 *            value type
 */
public class GenericValue<T> {
    private Class<T> type;
    private T value;

    public GenericValue() {
        this(null, null);
    }

    public GenericValue(Class<T> type, T value) {
        this.type = type;
        this.value = value;
    }

    public Class<T> getType() {
        return type;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "GenericValue [type=" + type + ", value=" + value + "]";
    }
}
