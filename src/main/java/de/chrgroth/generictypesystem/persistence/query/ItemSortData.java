package de.chrgroth.generictypesystem.persistence.query;

import java.io.Serializable;

import de.chrgroth.generictypesystem.model.GenericItem;

/**
 * Simple POJO holding all sorting operations for querying {@link GenericItem} instances.
 *
 * @author Christian Groth
 */
public class ItemSortData implements Serializable {

    private static final long serialVersionUID = 5500148850674226875L;

    private String path;
    private boolean ascending;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }
}
