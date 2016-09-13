package de.chrgroth.generictypesystem.model;

import java.util.ArrayList;
import java.util.List;

// TODO JSON handling for isList, isStructure, structure, defaultValue, valueProposalDependencies, isUnitBased, units
// TODO use builder pattern
public class GenericAttribute {

    private Long id;
    private int order;
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

    private String defaultValue;
    private String defaultValueCallback;

    private List<Long> valueProposalDependencies;

    private List<GenericAttributeUnit> units;

    public GenericAttribute() {
        this(null, 0, null, null);
    }

    public GenericAttribute(Long id, int order, String name, GenericAttributeType type) {
        this(id, order, name, type, null, false, false, true, null);
    }

    public GenericAttribute(Long id, int order, String name, GenericAttributeType type, GenericAttributeType valueType, boolean unique, boolean indexed, boolean mandatory,
            GenericStructure structure) {
        this(id, order, name, type, valueType, unique, indexed, mandatory, structure, null, null, null, null);
    }

    public GenericAttribute(Long id, int order, String name, GenericAttributeType type, GenericAttributeType valueType, boolean unique, boolean indexed, boolean mandatory,
            GenericStructure structure, Double min, Double max, Double step, String pattern) {
        this(id, order, name, type, valueType, unique, indexed, mandatory, structure, min, max, step, pattern, null, null);
    }

    public GenericAttribute(Long id, int order, String name, GenericAttributeType type, GenericAttributeType valueType, boolean unique, boolean indexed, boolean mandatory,
            GenericStructure structure, Double min, Double max, Double step, String pattern, String defaultValue, String defaultValueCallback) {
        this(id, order, name, type, valueType, unique, indexed, mandatory, structure, min, max, step, pattern, defaultValue, defaultValueCallback, null, null);
    }

    public GenericAttribute(Long id, int order, String name, GenericAttributeType type, GenericAttributeType valueType, boolean unique, boolean indexed, boolean mandatory,
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
     * Returns true if the attributes type equals to {@link GenericAttributeType#LIST}.
     *
     * @return true if list, false otherwise
     */
    public boolean isList() {
        return GenericAttributeType.LIST.equals(type);
    }

    /**
     * Returns true if {@link #isList()} returns true and the attributes value type equals to {@link GenericAttributeType#STRUCTURE} or if {@link #isList()}
     * returns false and the attributes type equals to {@link GenericAttributeType#STRUCTURE}.
     *
     * @return true if list, false otherwise
     */
    public boolean isStructure() {
        if (isList()) {
            return GenericAttributeType.STRUCTURE.equals(valueType);
        } else {
            return GenericAttributeType.STRUCTURE.equals(type);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
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
        return "GenericAttribute [order=" + order + ", name=" + name + ", type=" + type + ", valueType=" + valueType + ", unique=" + unique + ", indexed=" + indexed
                + ", mandatory=" + mandatory + ", structure=" + structure + ", min=" + min + ", max=" + max + ", step=" + step + ", pattern=" + pattern + ", defaultValue="
                + defaultValue + ", defaultValueCallback=" + defaultValueCallback + ", valueProposalDependencies=" + valueProposalDependencies + ", units=" + units + "]";
    }
}
