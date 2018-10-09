package de.chrgroth.generictypesystem.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A simple POJO class holding information about an units definition containing multiple {@link GenericUnit} definitions for the same purpose.
 *
 * @author Christian Groth
 */
public class GenericUnits {

    public static final double FACTOR_BASE = 1.0d;

    private Long id;
    private String name;
    private String description;
    private Set<GenericUnit> units;

    public GenericUnits() {
        this(null, null, null);
    }

    public GenericUnits(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        units = new HashSet<>();
    }

    /**
     * Returns the unit definition acting as base unit.
     *
     * @return base unit or null if not found
     */
    public GenericUnit baseUnit() {
        return units.stream().filter(u -> u.isBase()).findFirst().orElse(null);
    }

    /**
     * Returns the unit definition with the given id or null if no unit with given id is defined.
     *
     * @param id
     *            unit id to match
     * @return unit definition or null
     */
    public GenericUnit unit(Long id) {
        return id == null ? null : units.stream().filter(u -> Objects.equals(u.getId(), id)).findFirst().orElse(null);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<GenericUnit> getUnits() {
        return units;
    }

    public void setUnits(Set<GenericUnit> units) {
        this.units = units;
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
        GenericUnits other = (GenericUnits) obj;
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
        return "GenericUnits [id=" + id + ", name=" + name + ", description=" + description + ", units=" + units + "]";
    }
}
