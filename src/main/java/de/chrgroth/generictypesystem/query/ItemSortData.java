package de.chrgroth.generictypesystem.query;

public class ItemSortData {
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
