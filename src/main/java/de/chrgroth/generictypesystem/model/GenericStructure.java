package de.chrgroth.generictypesystem.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import de.chrgroth.generictypesystem.model.GenericAttribute.Type;

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

    public boolean hasUniqueKey() {
        return !uniqueAttributes().isEmpty();
    }

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

    public String attribute(Long id) {

        // null guard
        if (id == null) {
            return null;
        }

        // find attribute
        GenericAttribute attribute = attributes.stream().filter(a -> id.equals(a.getId())).findFirst().orElse(null);
        if (attribute == null) {

            // search recursive
            return attributes.stream().filter(a -> a.getType() == Type.STRUCTURE || a.getType() == Type.LIST && a.getValueType() == Type.STRUCTURE)
                    .filter(a -> a.getStructure().attribute(id) != null).map(a -> a.getName() + "." + a.getStructure().attribute(id)).findFirst().orElse(null);
        }

        // done
        return attribute.getName();
    }

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

    // TODO json handling
    // @JSON
    // @JsonProperty
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
