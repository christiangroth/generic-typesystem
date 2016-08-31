package de.chrgroth.generictypesystem.query;

public class ItemPagingData {

    // TODO move somewhere else!!
    public static final int DEFAULT_PAGE_SIZE = 50;

    private int page;
    private int size = DEFAULT_PAGE_SIZE;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
