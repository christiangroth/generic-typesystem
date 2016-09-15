package de.chrgroth.generictypesystem.model;

/**
 * A simple POJO class holding information about attribute unit definitions.
 *
 * @author Christian Groth
 */
public class GenericAttributeUnit {

    public static final double FACTOR_BASE = 1.0d;

    private static final int HASH_CODE_SHIFT = 32;

    private String name;
    private double factor;

    public GenericAttributeUnit() {
        this(null, 0.0d);
    }

    public GenericAttributeUnit(String name, double factor) {
        this.name = name;
        this.factor = factor;
    }

    public boolean isBase() {
        return factor == FACTOR_BASE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(factor);
        result = prime * result + (int) (temp ^ temp >>> HASH_CODE_SHIFT);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GenericAttributeUnit other = (GenericAttributeUnit) obj;
        if (Double.doubleToLongBits(factor) != Double.doubleToLongBits(other.factor)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GenericAttributeUnit [name=" + name + ", factor=" + factor + "]";
    }
}
