package de.chrgroth.generictypesystem.query;

import java.io.Serializable;

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
