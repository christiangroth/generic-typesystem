package de.chrgroth.generictypesystem.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * Represents a generic structure holding a number of generic attributes.
 *
 * @author Christian Groth
 */
public class GenericStructure {

    private Set<GenericAttribute> attributes;

    public GenericStructure() {
        this(null);
    }

    public GenericStructure(Set<GenericAttribute> attributes) {
        this.attributes = new HashSet<>();
        if (attributes != null) {
            this.attributes.addAll(attributes);
        }
    }

    /**
     * Checks if the attributes contain unique key attributes.
     *
     * @return true if unique key attributes are contained, false otherwise
     */
    public boolean hasUniqueKey() {
        return !uniqueAttributes().isEmpty();
    }

    /**
     * Computes the unique key for the given item.
     *
     * @param item
     *            item to compute unique key for
     * @return unique key, or null
     */
    public Map<String, Object> computeUniqueKey(GenericItem item) {
        if (item == null || !hasUniqueKey()) {
            return null;
        }

        Map<String, Object> uniqueKey = new HashMap<>();
        for (GenericAttribute a : uniqueAttributes()) {
            uniqueKey.put(a.getName(), item.get(a.getName()));
        }
        return uniqueKey;
    }

    private Set<GenericAttribute> uniqueAttributes() {
        return attributes.stream().filter(a -> a.isUnique()).collect(Collectors.toSet());
    }

    /**
     * Returns all attributes, recursing into nested structures.
     *
     * @return all attributes, never null
     */
    public Set<GenericAttribute> attributes() {
        HashSet<GenericAttribute> allAttributes = new HashSet<>();
        if (attributes != null) {
            allAttributes.addAll(attributes);
            allAttributes.addAll(
                    attributes.stream().filter(a -> a.isStructure() && a.getStructure() != null).flatMap(a -> a.getStructure().attributes().stream()).collect(Collectors.toSet()));
        }
        return allAttributes;
    }

    /**
     * Returns the path name of the attribute with given id, if existent.
     *
     * @param id
     *            attribute id
     * @return attribute path name, or null
     */
    public String attributePath(Long id) {

        // null guard
        if (id == null) {
            return null;
        }

        // find attribute directly
        GenericAttribute attribute = attributes.stream().filter(a -> id.equals(a.getId())).findFirst().orElse(null);
        if (attribute == null) {

            // search recursively
            return attributes.stream().filter(a -> a.isStructure() && a.getStructure() != null && a.getStructure().attributePath(id) != null)
                    .map(a -> a.getName() + "." + a.getStructure().attributePath(id)).findFirst().orElse(null);
        }

        // done
        return attribute.getName();
    }

    /**
     * Resolves the attribute for given name. Names containing dot notation will be resolved recursively.
     *
     * @param name
     *            attribute name
     * @return attribute
     */
    public GenericAttribute attribute(String name) {

        // null guard
        if (StringUtils.isBlank(name)) {
            return null;
        }

        // check for nested structure
        int dotIndex = name.indexOf('.');
        if (dotIndex > 0) {

            // get nested
            GenericAttribute attribute = attributeInternal(name.substring(0, dotIndex));
            if (attribute == null || attribute.getStructure() == null) {
                return null;
            }

            // delegate
            return attribute.getStructure().attribute(name.substring(dotIndex + 1));
        } else {
            return attributeInternal(name);
        }
    }

    private GenericAttribute attributeInternal(String name) {
        return getAttributes().stream().filter(a -> a.getName().equals(name)).findFirst().orElse(null);
    }

    public Set<GenericAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<GenericAttribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "GenericStructure [attributes=" + attributes + "]";
    }
}
