package de.chrgroth.generictypesystem.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A simple POJO class holding all information about attribute definitions.
 * <dl>
 * <dt>id</dt>
 * <dd>The id value used for persistence purposes.</dd>
 * <dt>name</dt>
 * <dd>The attribute name. Used for {@link GenericAttribute#equals(Object)} and {@link GenericAttribute#hashCode()}.</dd>
 * <dt>type</dt>
 * <dd>The attribute type.</dd>
 * <dt>valueType</dt>
 * <dd>The attribute value type. Primarily used for list type attributes.</dd>
 * <dt>unique</dt>
 * <dd>Flag to indicate if this attribute is part of the items unique key.</dd>
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
 * <dd>An optional default value.</dd>
 * <dt>defaultValueCallback</dt>
 * <dd>An optional default value callback script to be executed.</dd>
 * <dt>valueProposalDependencies</dt>
 * <dd>An optional list of value proposal dependencies, referencing other attribute ids. Primarily used for UI purposes.</dd>
 * <dt>unitsId</dt>
 * <dd>An optional units id to be used for attribute values.</dd>
 * <dt>customProperties</dt>
 * <dd>A map holding optional custom properties to be used by concrete projects for simple type attribute extension.</dd>
 * </dl>
 *
 * @author Christian Groth
 */
public class GenericAttribute {

    private Long id;
    private String name;
    private GenericAttributeType type;
    private GenericAttributeType valueType;
    private boolean unique;
    private boolean mandatory;
    private GenericStructure structure;

    private Double min;
    private Double max;
    private Double step;
    private String pattern;

    private GenericValue<?> defaultValue;
    private String defaultValueCallback;

    private Set<Long> valueProposalDependencies;

    private Long unitsId;

    private Set<String> enumValues;

    private Map<String, GenericValue<?>> customProperties;

    public GenericAttribute() {
        this(null, null, null, null, false, false, null, null, null, null, null, null, null, null, null, null);
    }

    public GenericAttribute(Long id, String name, GenericAttributeType type, GenericAttributeType valueType, boolean unique, boolean mandatory, GenericStructure structure,
            Double min, Double max, Double step, String pattern, GenericValue<?> defaultValue, String defaultValueCallback, Set<Long> valueProposalDependencies, Long unitsId,
            Set<String> enumValues) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.valueType = valueType;
        this.unique = unique;
        this.mandatory = mandatory;
        this.structure = structure;
        this.min = min;
        this.max = max;
        this.step = step;
        this.pattern = pattern;
        this.defaultValue = defaultValue;
        this.defaultValueCallback = defaultValueCallback;
        if (valueProposalDependencies != null) {
            this.valueProposalDependencies = new HashSet<>(valueProposalDependencies);
        }
        this.unitsId = unitsId;
        if (enumValues != null) {
            this.enumValues = new HashSet<>(enumValues);
        }
        customProperties = new HashMap<>();
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

    /**
     * Returns true if the attribute definition is based on units definition.
     *
     * @return true if unit base, false otherwise
     */
    public boolean isUnitBased() {
        return unitsId != null;
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

    public GenericValue<?> getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(GenericValue<?> defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValueCallback() {
        return defaultValueCallback;
    }

    public void setDefaultValueCallback(String defaultValueCallback) {
        this.defaultValueCallback = defaultValueCallback;
    }

    public Set<Long> getValueProposalDependencies() {
        return valueProposalDependencies;
    }

    public void setValueProposalDependencies(Set<Long> valueProposalDependencies) {
        this.valueProposalDependencies = valueProposalDependencies;
    }

    public Long getUnitsId() {
        return unitsId;
    }

    public void setUnitsId(Long unitsId) {
        this.unitsId = unitsId;
    }

    public Set<String> getEnumValues() {
        return enumValues;
    }

    public void setEnumValues(Set<String> enumValues) {
        this.enumValues = enumValues;
    }

    public Map<String, GenericValue<?>> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(Map<String, GenericValue<?>> customProperties) {
        this.customProperties = customProperties;
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
        return "GenericAttribute [id=" + id + ", name=" + name + ", type=" + type + ", valueType=" + valueType + ", unique=" + unique + ", mandatory=" + mandatory + ", structure="
                + structure + ", min=" + min + ", max=" + max + ", step=" + step + ", pattern=" + pattern + ", defaultValue=" + defaultValue + ", defaultValueCallback="
                + defaultValueCallback + ", valueProposalDependencies=" + valueProposalDependencies + ", unitsId=" + unitsId + ", enumValues=" + enumValues + ", customProperties="
                + customProperties + "]";
    }
}
