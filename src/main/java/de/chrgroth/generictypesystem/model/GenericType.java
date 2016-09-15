package de.chrgroth.generictypesystem.model;

import java.util.Set;

/**
 * Enhances the generic structure to a generic type.
 * <dl>
 * <dt>id</dt>
 * <dd>The id value used for persistence purposes.</dd>
 * <dt>order</dt>
 * <dd>Numeric value defining an order over types.</dd>
 * <dt>name</dt>
 * <dd>The type name.</dd>
 * <dt>group</dt>
 * <dd>An optional type group.</dd>
 * <dt>description</dt>
 * <dd>An optional type description.</dd>
 * <dt>color</dt>
 * <dd>An optional color value. Primarily used for UI purposes.</dd>
 * <dt>owner</dt>
 * <dd>An optional owner key.</dd>
 * <dt>visibility</dt>
 * <dd>An optional visibility.</dd>
 * <dt>pageSize</dt>
 * <dd>An optional page size to be used for querying.</dd>
 * </dl>
 *
 * @author Christian Groth
 */
public class GenericType extends GenericStructure {

    private Long id;
    private long order;
    private String name;
    private String group;

    private String description;
    private String color;

    private Long owner;
    private Visibility visibility;

    private Long pageSize;

    public GenericType() {
        this(null, 0, null, null, null, null, null, null, null, null);
    }

    public GenericType(Long id, long order, String name, String group, Set<GenericAttribute> attributes, String description, String color, Long owner, Visibility visibility,
            Long pageSize) {
        super(attributes);
        this.id = id;
        this.order = order;
        this.name = name;
        this.group = group;
        this.description = description;
        this.color = color;
        this.owner = owner;
        this.visibility = visibility;
        this.pageSize = pageSize;
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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
        return "GenericType [id=" + id + ", order=" + order + ", name=" + name + ", group=" + group + ", description=" + description + ", color=" + color + ", owner=" + owner
                + ", visibility=" + visibility + ", pageSize=" + pageSize + ", structure=" + super.toString() + "]";
    }
}
