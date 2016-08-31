package de.chrgroth.generictypesystem.model;

public class GenericAttributeUnit {
    private String name;
    private double factor;

    public boolean isBase() {
        return factor == 1.0d;
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
        result = prime * result + (int) (temp ^ temp >>> 32);
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
