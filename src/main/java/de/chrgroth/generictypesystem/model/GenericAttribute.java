package de.chrgroth.generictypesystem.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple POJO class holding all information about attribute definitions.
 * <dl>
 * <dt>id</dt>
 * <dd>The id value used for persistence purposes.</dd>
 * <dt>order</dt>
 * <dd>Numeric value defining an order over attributes.</dd>
 * <dt>name</dt>
 * <dd>The attribute name. Used for {@link GenericAttribute#equals(Object)} and {@link GenericAttribute#hashCode()}.</dd>
 * <dt>type</dt>
 * <dd>The attribute type.</dd>
 * <dt>valueType</dt>
 * <dd>The attribute value type. Primarily used for list type attributes.</dd>
 * <dt>unique</dt>
 * <dd>Flag to indicate if this attribute is part of the items unique key.</dd>
 * <dt>indexed</dt>
 * <dd>Flag to indicate if this attribute is indexed. May be used for persistence or UI purposes.</dd>
 * <dt>mandatory</dt>
 * <dd>Flag to indicate if this attribute has a mandatory value.</dd>
 * <dt>structure</dt>
 * <dd>The structure connected to this attribute. Used for attributes with structured type or a list type holding a structured value type.</dd>
 * <dt>min</dt>
 * <dd>An optional minimum for attribute values. Numeric or value length for string based attribute types.</dd>
 * <dt>max</dt>
 * <dd>An optional maximum for attribute values. Numeric or value length for string based attribute types.</dd>
 * <dt>step</dt>
 * <dd>A step value for numeric typed attributes. Primarily used for UI purposes, not used for validaton at all.</dd>
 * <dt>pattern</dt>
 * <dd>An optional pattern for string based attribute types.</dd>
 * <dt>defaultValue</dt>
 * <dd>An optional string representation of the default value.</dd>
 * <dt>defaultValueCallback</dt>
 * <dd>An optional default value callback script to be executed.</dd>
 * <dt>valueProposalDependencies</dt>
 * <dd>An optional list of value proposal dependencies, referencing other attribute ids. Primarily used for UI purposes.</dd>
 * <dt>units</dt>
 * <dd>An optional list of attribute value units.</dd>
 * </dl>
 *
 * @author Christian Groth
 */
public class GenericAttribute {

    private Long id;
    private long order;
    private String name;
    private GenericAttributeType type;
    private GenericAttributeType valueType;
    private boolean unique;
    private boolean indexed;
    private boolean mandatory;
    private GenericStructure structure;

    private Double min;
    private Double max;
    private Double step;
    private String pattern;

    // TODO change to object??
    private String defaultValue;
    private String defaultValueCallback;

    private List<Long> valueProposalDependencies;

    private List<GenericAttributeUnit> units;

    public GenericAttribute() {
        this(null, 0, null, null, null, false, false, false, null, null, null, null, null, null, null, null, null);
    }

    public GenericAttribute(Long id, long order, String name, GenericAttributeType type, GenericAttributeType valueType, boolean unique, boolean indexed, boolean mandatory,
            GenericStructure structure, Double min, Double max, Double step, String pattern, String defaultValue, String defaultValueCallback, List<Long> valueProposalDependencies,
            List<GenericAttributeUnit> units) {
        this.id = id;
        this.order = order;
        this.name = name;
        this.type = type;
        this.valueType = valueType;
        this.unique = unique;
        this.indexed = indexed;
        this.mandatory = mandatory;
        this.structure = structure;
        this.min = min;
        this.max = max;
        this.step = step;
        this.pattern = pattern;
        this.defaultValue = defaultValue;
        this.defaultValueCallback = defaultValueCallback;
        if (valueProposalDependencies != null) {
            this.valueProposalDependencies = new ArrayList<>(valueProposalDependencies);
        }
        if (units != null) {
            this.units = new ArrayList<>(units);
        }
    }

    /**
     * Returns true if the attributes type returns true for {@link GenericAttributeType#isList()}.
     *
     * @return true if list, false otherwise
     */
    public boolean isList() {
        return type != null && type.isList();
    }

    /**
     * Returns true if {@link #isList()} returns true and the attributes value type return true for {@link GenericAttributeType#isStructure()} or if
     * {@link #isList()} returns false and the attributes type returns true for {@link GenericAttributeType#isStructure()}.
     *
     * @return true if list, false otherwise
     */
    public boolean isStructure() {
        if (isList()) {
            return valueType != null && valueType.isStructure();
        } else {
            return type != null && type.isStructure();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GenericAttributeType getType() {
        return type;
    }

    public void setType(GenericAttributeType type) {
        this.type = type;
    }

    public GenericAttributeType getValueType() {
        return valueType;
    }

    public void setValueType(GenericAttributeType valueType) {
        this.valueType = valueType;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean isIndexed() {
        return indexed;
    }

    public void setIndexed(boolean indexed) {
        this.indexed = indexed;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public GenericStructure getStructure() {
        return structure;
    }

    public void setStructure(GenericStructure structure) {
        this.structure = structure;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Double getStep() {
        return step;
    }

    public void setStep(Double step) {
        this.step = step;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValueCallback() {
        return defaultValueCallback;
    }

    public void setDefaultValueCallback(String defaultValueCallback) {
        this.defaultValueCallback = defaultValueCallback;
    }

    public List<Long> getValueProposalDependencies() {
        return valueProposalDependencies;
    }

    public void setValueProposalDependencies(List<Long> valueProposalDependencies) {
        this.valueProposalDependencies = valueProposalDependencies;
    }

    public boolean isUnitBased() {
        return units != null && !units.isEmpty();
    }

    public List<GenericAttributeUnit> getUnits() {
        return units;
    }

    public void setUnits(List<GenericAttributeUnit> units) {
        this.units = units;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
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
        GenericAttribute other = (GenericAttribute) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GenericAttribute [id=" + id + ", order=" + order + ", name=" + name + ", type=" + type + ", valueType=" + valueType + ", unique=" + unique + ", indexed=" + indexed
                + ", mandatory=" + mandatory + ", structure=" + structure + ", min=" + min + ", max=" + max + ", step=" + step + ", pattern=" + pattern + ", defaultValue="
                + defaultValue + ", defaultValueCallback=" + defaultValueCallback + ", valueProposalDependencies=" + valueProposalDependencies + ", units=" + units + "]";
    }
}
