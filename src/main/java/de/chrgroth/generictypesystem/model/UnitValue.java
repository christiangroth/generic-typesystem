package de.chrgroth.generictypesystem.model;

/**
 * A simple POJO representing a unit based value.
 *
 * @author Christian Groth
 */
public final class UnitValue {
    private String unit;
    private GenericValue<?> value;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public GenericValue<?> getValue() {
        return value;
    }

    public void setValue(GenericValue<?> value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "UnitValue [unit=" + unit + ", value=" + value + "]";
    }
}
