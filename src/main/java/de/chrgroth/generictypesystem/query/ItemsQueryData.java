package de.chrgroth.generictypesystem.query;

import java.util.List;

public class ItemsQueryData {
    private ItemFilterData filter;
    private List<ItemSortData> sorts;
    private ItemPagingData paging;

    public ItemFilterData getFilter() {
        return filter;
    }

    public void setFilter(ItemFilterData filter) {
        this.filter = filter;
    }

    public List<ItemSortData> getSorts() {
        return sorts;
    }

    public void setSorts(List<ItemSortData> sorts) {
        this.sorts = sorts;
    }

    public ItemPagingData getPaging() {
        return paging;
    }

    public void setPaging(ItemPagingData paging) {
        this.paging = paging;
    }
}
