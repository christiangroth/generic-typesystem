package de.chrgroth.generictypesystem.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GenericAttribute {

    public static final List<Type> VALID_KEY_TYPES = Collections.unmodifiableList(Arrays.asList(Type.STRING, Type.LONG, Type.DOUBLE));
    public static final List<Type> VALID_VALUE_TYPES = Collections.unmodifiableList(Arrays.asList(Type.STRING, Type.LONG, Type.DOUBLE, Type.BOOLEAN, Type.DATE, Type.STRUCTURE));

    // TODO external enum
    public enum Type {
        STRING(true, false, true, true, false, String.class),

        LONG(true, true, false, false, true, Long.class, Integer.class), DOUBLE(true, true, false, false, true, Double.class, Long.class, Integer.class),

        BOOLEAN(Boolean.class),

        DATE(String.class), TIME(false, true, false, false, false, String.class), DATETIME(false, true, false, false, false, String.class),

        STRUCTURE(GenericItem.class),

        LIST(List.class);

        private final boolean minMaxCapable;
        private final boolean stepCapable;
        private final boolean patternCapable;
        private final boolean valueProposalDependenciesCapable;
        private final boolean unitCapable;
        private final List<Class<?>> typeClasses;

        Type(Class<?>... typeClasses) {
            this(false, false, false, false, false, typeClasses);
        }

        Type(boolean minMaxCapable, boolean stepCapable, boolean patternCapable, boolean valueProposalDependenciesCapable, boolean unitCapable, Class<?>... typeClasses) {
            this.minMaxCapable = minMaxCapable;
            this.stepCapable = stepCapable;
            this.patternCapable = patternCapable;
            this.valueProposalDependenciesCapable = valueProposalDependenciesCapable;
            this.unitCapable = unitCapable;
            this.typeClasses = Arrays.asList(typeClasses);
        }

        public boolean isMinMaxCapable() {
            return minMaxCapable;
        }

        public boolean isStepCapable() {
            return stepCapable;
        }

        public boolean isPatternCapable() {
            return patternCapable;
        }

        public boolean isValueProposalDependenciesCapable() {
            return valueProposalDependenciesCapable;
        }

        public boolean isUnitCapable() {
            return unitCapable;
        }

        public boolean isAssignableFrom(Class<?> actualClass) {

            // check all classes
            for (Class<?> typeClass : typeClasses) {
                if (typeClass.isAssignableFrom(actualClass)) {
                    return true;
                }
            }

            // none found
            return false;
        }
    }

    private Long id;
    private int order;
    private String name;
    private Type type;
    private Type keyType;
    private Type valueType;
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

    public GenericAttribute(Long id, int order, String name, Type type) {
        this(id, order, name, type, null, null, false, false, true, null);
    }

    public GenericAttribute(Long id, int order, String name, Type type, Type keyType, Type valueType, boolean unique, boolean indexed, boolean mandatory,
            GenericStructure structure) {
        this(id, order, name, type, keyType, valueType, unique, indexed, mandatory, structure, null, null, null, null);
    }

    public GenericAttribute(Long id, int order, String name, Type type, Type keyType, Type valueType, boolean unique, boolean indexed, boolean mandatory,
            GenericStructure structure, Double min, Double max, Double step, String pattern) {
        this(id, order, name, type, keyType, valueType, unique, indexed, mandatory, structure, min, max, step, pattern, null, null);
    }

    public GenericAttribute(Long id, int order, String name, Type type, Type keyType, Type valueType, boolean unique, boolean indexed, boolean mandatory,
            GenericStructure structure, Double min, Double max, Double step, String pattern, String defaultValue, String defaultValueCallback) {
        this.id = id;
        this.order = order;
        this.name = name;
        this.type = type;
        this.keyType = keyType;
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
    }

    // TODO json handling
    // @JSON(include = false)
    // @JsonIgnore
    public boolean isList() {
        return Type.LIST.equals(type);
    }

    // TODO json handling
    // @JSON(include = false)
    // @JsonIgnore
    public boolean isStructure() {
        if (isList()) {
            return Type.STRUCTURE.equals(valueType);
        } else {
            return Type.STRUCTURE.equals(type);
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getKeyType() {
        return keyType;
    }

    public void setKeyType(Type keyType) {
        this.keyType = keyType;
    }

    public Type getValueType() {
        return valueType;
    }

    public void setValueType(Type valueType) {
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

    // TODO json handling
    // @JSON
    // @JsonProperty
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

    // TODO json handling
    // @JSON
    // @JsonProperty
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

    // TODO json handling
    // @JSON
    // @JsonProperty
    public List<Long> getValueProposalDependencies() {
        return valueProposalDependencies;
    }

    public void setValueProposalDependencies(List<Long> valueProposalDependencies) {
        this.valueProposalDependencies = valueProposalDependencies;
    }

    // TODO json handling
    // @JSON(include = false)
    // @JsonIgnore
    public boolean isUnitBased() {
        return units != null && !units.isEmpty();
    }

    // TODO json handling
    // @JSON
    // @JsonProperty
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
        return "GenericAttribute [order=" + order + ", name=" + name + ", type=" + type + ", keyType=" + keyType + ", valueType=" + valueType + ", unique=" + unique + ", indexed="
                + indexed + ", mandatory=" + mandatory + ", structure=" + structure + ", min=" + min + ", max=" + max + ", step=" + step + ", pattern=" + pattern
                + ", defaultValue=" + defaultValue + ", defaultValueCallback=" + defaultValueCallback + ", valueProposalDependencies=" + valueProposalDependencies + ", units="
                + units + "]";
    }
}
