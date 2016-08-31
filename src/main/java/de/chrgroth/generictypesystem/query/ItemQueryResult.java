package de.chrgroth.generictypesystem.query;

import java.util.ArrayList;
import java.util.List;

import de.chrgroth.generictypesystem.model.GenericItem;

public class ItemQueryResult {
    private final List<GenericItem> items = new ArrayList<>();
    private final boolean moreAvailable;

    public ItemQueryResult(List<GenericItem> items, boolean moreAvailable) {
        if (items != null) {
            this.items.addAll(items);
        }
        this.moreAvailable = moreAvailable;
    }

    // TODO json handling
    // @JSON
    // @JsonProperty
    public List<GenericItem> getItems() {
        return items;
    }

    public boolean isMoreAvailable() {
        return moreAvailable;
    }
}
