package de.chrgroth.generictypesystem.persistence.query.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.persistence.query.ItemFilterData;
import de.chrgroth.generictypesystem.persistence.query.ItemPagingData;
import de.chrgroth.generictypesystem.persistence.query.ItemQueryResult;
import de.chrgroth.generictypesystem.persistence.query.ItemSortData;

/**
 * A naive filter, sort and paging implementation independent from persistence layer.
 *
 * @author Christian Groth
 */
public class InMemoryItemsQueryService {

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryItemsQueryService.class);

    private final int defaultPageSize;

    public InMemoryItemsQueryService(int defaultPageSize) {
        if (defaultPageSize < 1) {
            throw new IllegalArgumentException("default page size must be greater zero!!");
        }
        this.defaultPageSize = defaultPageSize;
    }

    /**
     * Executed the given filter, sorts and paging operation based on given items.
     *
     * @param allItems
     *            items to be processed
     * @param filter
     *            filter operations, or null
     * @param sorts
     *            sorting operation, or null
     * @param paging
     *            paging operation, or null
     * @return result, never null
     */
    public ItemQueryResult query(Set<GenericItem> allItems, ItemFilterData filter, List<ItemSortData> sorts, ItemPagingData paging) {

        // get items
        if (allItems == null || allItems.isEmpty()) {
            return new ItemQueryResult(Collections.emptyList(), false);
        }

        // add all
        List<GenericItem> items = new ArrayList<>(allItems);

        // filter
        if (filter != null) {

            // TODO implement filtering
            LOG.warn("filtering not implemented yet!!");
        }

        // sorting
        Collections.sort(items, new CascadingAttributeComparator(sorts));

        // paging
        boolean moreAvailable;
        if (paging != null) {

            // validate paging parameters
            int page = paging.getPage();
            if (page < 0) {
                LOG.error("illegal page number: " + page + "!! Setting page to 0.");
                page = 0;
            }
            int pageSize = paging.getSize();
            if (pageSize < 1) {
                LOG.error("illegal page size: " + pageSize + "!! Falling back to configured default: " + defaultPageSize + ".");
                pageSize = defaultPageSize;
            }

            // compute beginning
            int firstIdx = 0;
            if (page > 0) {
                firstIdx = pageSize * (page - 1);
            }

            // compute end
            int lastIdx = firstIdx + pageSize;

            // slice
            if (items.size() >= firstIdx + 1) {

                // compute if more than the actual page is available
                moreAvailable = items.size() > lastIdx;

                // slive to actual page
                items = items.subList(firstIdx, items.size() > lastIdx ? lastIdx : items.size());
            } else {

                // return empty result
                LOG.error("query page is out of bounds: " + page + "x" + pageSize + "!!");
                moreAvailable = false;
                items.clear();
            }
        } else {

            // all items served, nothing more available
            moreAvailable = false;
        }

        // done
        return new ItemQueryResult(items, moreAvailable);
    }
}
