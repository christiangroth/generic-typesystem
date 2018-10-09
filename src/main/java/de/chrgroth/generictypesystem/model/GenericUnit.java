package de.chrgroth.generictypesystem.model;

/**
 * A simple POJO class holding information about an unit definition.
 *
 * @author Christian Groth
 */
public class GenericUnit {

    private Long id;
    private String name;
    private String symbol;
    private double factor;

    public GenericUnit() {
        this(null, null, null, 0.0d);
    }

    public GenericUnit(Long id, String name, String symbol, double factor) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.factor = factor;
    }

    public boolean isBase() {
        return factor == GenericUnits.FACTOR_BASE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
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
        result = prime * result + (id == null ? 0 : id.hashCode());
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
        GenericUnit other = (GenericUnit) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GenericUnit [id=" + id + ", name=" + name + ", symbol=" + symbol + ", factor=" + factor + "]";
    }
}
