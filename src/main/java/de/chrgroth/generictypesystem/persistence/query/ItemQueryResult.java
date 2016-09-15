package de.chrgroth.generictypesystem.persistence.query;

import java.util.ArrayList;
import java.util.List;

import de.chrgroth.generictypesystem.model.GenericItem;

/**
 * Simple POJO holding the results for querying {@link GenericItem} instances.
 *
 * @author Christian Groth
 */
public class ItemQueryResult {
    private final List<GenericItem> items = new ArrayList<>();
    private final boolean moreAvailable;

    public ItemQueryResult(List<GenericItem> items, boolean moreAvailable) {
        if (items != null) {
            this.items.addAll(items);
        }
        this.moreAvailable = moreAvailable;
    }

    public List<GenericItem> getItems() {
        return items;
    }

    public boolean isMoreAvailable() {
        return moreAvailable;
    }
}
