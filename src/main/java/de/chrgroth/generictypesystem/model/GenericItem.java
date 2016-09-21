package de.chrgroth.generictypesystem.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * Represents a generic item belonging to a {@link GenericType} and holding the attribute values.
 *
 * @author Christian Groth
 */
public class GenericItem {

    private Long id;
    private Long typeId;
    private Map<String, Object> values;

    private Long owner;
    private Visibility visibility;

    public GenericItem() {
        this(null, null, null, null, null);
    }

    public GenericItem(Long id, Long typeId, Map<String, Object> values, Long owner, Visibility visibility) {
        this.id = id;
        this.typeId = typeId;
        this.values = new HashMap<>();
        if (values != null) {
            values.forEach((k, v) -> set(k, v));
        }
        this.owner = owner;
        this.visibility = visibility;
    }

    /**
     * Returns the value for the given name. Names containing dot notation will be resolved recursively.
     *
     * @param name
     *            value name
     * @return value
     */
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

    /**
     * Returns a recursive view for all values. Values of nested items are combined with a dot in their names. Any changes won't be reflected to the item
     * itself.
     *
     * @return recursive values
     */
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

    /**
     * Sets the given value for the given name. Names containing dot notation will be resolved recursively.
     *
     * @param name
     *            value name
     * @param value
     *            value
     * @return the old value
     */
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

    /**
     * Removes the value for the given name. Names containing dot notation will be resolved recursively
     *
     * @param name
     *            value name
     * @return the removed value
     */
    public Object remove(String name) {

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
            return object.remove(name.substring(dotIndex + 1));
        } else {
            return values.remove(name);
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public void setValues(Map<String, Object> values) {
        this.values = values;
    }

    public Long getOwner() {
        return owner;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (typeId == null ? 0 : typeId.hashCode());
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
        if (typeId == null) {
            if (other.typeId != null) {
                return false;
            }
        } else if (!typeId.equals(other.typeId)) {
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
        return "GenericItem [id=" + id + ", typeId=" + typeId + ", values=" + values + ", owner=" + owner + ", visibility=" + visibility + "]";
    }
}
