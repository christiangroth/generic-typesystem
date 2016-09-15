package de.chrgroth.generictypesystem.model;

/**
 * A simple POJO representing a unit based value.
 *
 * @author Christian Groth
 */
public final class UnitValue {
    private String unit;
    private Object value;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "UnitValue [unit=" + unit + ", value=" + value + "]";
    }
}
