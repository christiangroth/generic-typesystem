package de.chrgroth.generictypesystem.persistence.query;

import de.chrgroth.generictypesystem.model.GenericItem;

/**
 * Simple POJO holding all paging operations for querying {@link GenericItem} instances.
 *
 * @author Christian Groth
 */
public class ItemPagingData {

    private long page;
    private Long pageSize;

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }
}
