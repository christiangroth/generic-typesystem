package de.chrgroth.generictypesystem.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enhances the generic structure to a generic type.
 * <dl>
 * <dt>id</dt>
 * <dd>The id value used for persistence purposes.</dd>
 * <dt>name</dt>
 * <dd>The type name.</dd>
 * <dt>group</dt>
 * <dd>An optional type group.</dd>
 * <dt>owner</dt>
 * <dd>An optional owner key.</dd>
 * <dt>visibility</dt>
 * <dd>An optional visibility.</dd>
 * <dt>pageSize</dt>
 * <dd>An optional page size to be used for querying.</dd>
 * <dt>customProperties</dt>
 * <dd>A map holding optional custom properties to be used by concrete projects for simple type extension.</dd>
 * </dl>
 *
 * @author Christian Groth
 */
public class GenericType extends GenericStructure {

    private Long id;
    private String name;
    private String group;

    private Long owner;
    private Visibility visibility;

    private Long pageSize;

    private Map<String, GenericValue<?>> customProperties;

    public GenericType() {
        this(null, null, null, null, null, null, null);
    }

    public GenericType(Long id, String name, String group, List<GenericAttribute> attributes, Long owner, Visibility visibility, Long pageSize) {
        super(attributes);
        this.id = id;
        this.name = name;
        this.group = group;
        this.owner = owner;
        this.visibility = visibility;
        this.pageSize = pageSize;
        customProperties = new HashMap<>();
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
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

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
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
        GenericType other = (GenericType) obj;
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
        return "GenericType [id=" + id + ", name=" + name + ", group=" + group + ", owner=" + owner + ", visibility=" + visibility + ", pageSize=" + pageSize
                + ", customProperties=" + customProperties + ", structure=" + super.toString() + "]";
    }
}
