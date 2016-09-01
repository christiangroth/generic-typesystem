package de.chrgroth.generictypesystem.model;

// TODO JSON handling?
public final class UnitValue {
    private String unit;
    private Object value;
    private Object baseValue;

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

    public Object getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(Object baseValue) {
        this.baseValue = baseValue;
    }

    @Override
    public String toString() {
        return "UnitValue [unit=" + unit + ", value=" + value + ", baseValue=" + baseValue + "]";
    }
}
