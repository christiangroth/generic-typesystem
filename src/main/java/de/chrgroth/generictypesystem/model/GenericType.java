package de.chrgroth.generictypesystem.model;

import java.util.Set;

public class GenericType extends GenericStructure {

    // TODO move somewhere else
    public static final Integer VERSION = 3;

    // TODO move somewhere else
    private static final long DEFAULT_PAGE_SIZE = 10;

    private Long id;
    private long order;
    private String name;
    private String group;

    // TODO use for 3rd party project customizing
    // private Map<String, Object> customAttributes = new HashMap<>();

    // TODO move to custom attributes map
    private String description;
    // TODO move to custom attributes map
    private String color;

    // TODO core concepts?? owner is tied to long??
    private long owner;
    private Visibility visibility = Visibility.PRIVATE;

    private long pageSize;

    public GenericType() {
        this(null, 0, null, null, null);
    }

    public GenericType(Long id, long order, String name, String group, Set<GenericAttribute> attributes) {
        this(id, order, name, group, attributes, DEFAULT_PAGE_SIZE);
    }

    public GenericType(Long id, long order, String name, String group, Set<GenericAttribute> attributes, long pageSize) {
        super(attributes);
        this.order = order;
        this.id = id;
        this.name = name;
        this.group = group;
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

    public long getOwner() {
        return owner;
    }

    public void setOwner(long owner) {
        this.owner = owner;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
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
        return "GenericType [id=" + id + ", order=" + order + ", name=" + name + ", description=" + description + ", color=" + color + ", group=" + group + ", owner=" + owner
                + ", visibility=" + visibility + ", pageSize=" + pageSize + ", structure=" + super.toString() + "]";
    }
}
