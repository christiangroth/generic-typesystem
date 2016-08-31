package de.chrgroth.generictypesystem.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.query.ItemSortData;

public class CascadingAttributeComparator implements Serializable, Comparator<GenericItem> {

    private static final long serialVersionUID = -1988541741809268872L;

    private final List<ItemSortData> sorts = new ArrayList<>();

    public CascadingAttributeComparator(List<ItemSortData> sorts) {
        if (sorts != null) {
            this.sorts.addAll(sorts);
        }
    }

    @Override
    public int compare(GenericItem o1, GenericItem o2) {

        // sort by parameters
        for (ItemSortData sort : sorts) {
            @SuppressWarnings("unchecked")
            Comparable<Object> firstObject = (Comparable<Object>) o1.get(sort.getPath());
            @SuppressWarnings("unchecked")
            Comparable<Object> secondObject = (Comparable<Object>) o2.get(sort.getPath());

            // swap for non ascending compare
            if (!sort.isAscending()) {
                Comparable<Object> swapObject = firstObject;
                firstObject = secondObject;
                secondObject = swapObject;
            }

            // compare
            int compare = ObjectUtils.compare(firstObject, secondObject, true);
            if (compare != 0) {

                // done
                return compare;
            }
        }

        // fallback to id sort, desc
        return ObjectUtils.compare(o2.getId(), o1.getId());
    }
}
