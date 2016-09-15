package de.chrgroth.generictypesystem.persistence.query;

import de.chrgroth.generictypesystem.model.GenericItem;

/**
 * Simple POJO holding all paging operations for querying {@link GenericItem} instances.
 *
 * @author Christian Groth
 */
public class ItemPagingData {

    private int page;
    private int size;

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
