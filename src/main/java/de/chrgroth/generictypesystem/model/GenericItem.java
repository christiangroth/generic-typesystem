package de.chrgroth.generictypesystem.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class GenericItem {

    // TODO extract to class
    public static final class UnitValue {
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

    public static final Integer VERSION = 1;

    private Long id;
    private Long genericTypeId;
    private Map<String, Object> values;
    // TODO add visibility??

    public GenericItem() {
        this(null, null, new HashMap<>());
    }

    public GenericItem(Long id, Long genericTypeId, Map<String, Object> values) {
        this.id = id;
        this.genericTypeId = genericTypeId;
        this.values = new HashMap<>();
        if (values != null) {
            this.values.putAll(values);
        }
    }

    public Object get(String name) {

        // null guard
        if (StringUtils.isBlank(name)) {
            return null;
        }

        // check for nested value
        int dotIndex = name.indexOf('.');
        if (dotIndex > 0) {

            // get nested
            GenericItem object = getNested(name.substring(0, dotIndex));
            if (object == null) {
                return null;
            }

            // delegate
            return object.get(name.substring(dotIndex + 1));
        } else {
            Object value = values.get(name);
            if (value instanceof String) {
                value = ((String) value).trim();
            }
            return value;
        }
    }

    public Map<String, Object> get() {
        Map<String, Object> flattened = new HashMap<>();
        values.entrySet().forEach(e -> {
            Object value = e.getValue();
            String key = e.getKey();
            if (value instanceof GenericItem) {

                // recurse
                ((GenericItem) value).get().entrySet().forEach(subE -> {
                    flattened.put(key + "." + subE.getKey(), subE.getValue());
                });
            } else {

                // add
                flattened.put(key, value);
            }
        });

        return flattened;
    }

    public Object set(String name, Object value) {

        // null guard
        if (StringUtils.isBlank(name)) {
            return null;
        }

        // check for nested value
        int dotIndex = name.indexOf('.');
        if (dotIndex > 0) {

            // get nested
            String key = name.substring(0, dotIndex);
            GenericItem object = getNested(key);

            // create lazily
            if (object == null) {
                object = new GenericItem();
                values.put(key, object);
            }

            // delegate
            return object.set(name.substring(dotIndex + 1), value);
        } else {

            // get simple
            return values.put(name, value);
        }
    }

    private GenericItem getNested(String key) {

        // null guard
        if (StringUtils.isBlank(key)) {
            return null;
        }

        // get
        Object object = values.get(key);
        if (object == null) {
            return null;
        }

        // check type
        if (!(object instanceof GenericItem)) {
            throw new IllegalArgumentException("corrupt value for " + key + ", generic item was expected: " + object);
        }

        // done
        return (GenericItem) object;
    }

    public Object remove(String name) {

        // null guard
        if (StringUtils.isBlank(name)) {
            return null;
        }

        // remove
        return values.remove(name);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGenericTypeId() {
        return genericTypeId;
    }

    public void setGenericTypeId(Long genericTypeId) {
        this.genericTypeId = genericTypeId;
    }

    // TODO json handling
    // @JSON
    // @JsonProperty
    public Map<String, Object> getValues() {
        return values;
    }

    public void setValues(Map<String, Object> values) {
        this.values = values;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (genericTypeId == null ? 0 : genericTypeId.hashCode());
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
        GenericItem other = (GenericItem) obj;
        if (genericTypeId == null) {
            if (other.genericTypeId != null) {
                return false;
            }
        } else if (!genericTypeId.equals(other.genericTypeId)) {
            return false;
        }
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
        return "GenericItem [id=" + id + ", genericTypeId=" + genericTypeId + ", values=" + values + "]";
    }
}
