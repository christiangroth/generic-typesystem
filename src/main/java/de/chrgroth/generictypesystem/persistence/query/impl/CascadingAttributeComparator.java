package de.chrgroth.generictypesystem.persistence.query.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.persistence.query.ItemSortData;

/**
 * Sorts {@link GenericItem} instances by a given list of {@link ItemSortData} instances. As long as the result is equal the next sort data will be used. If all
 * sort data leads to an equality a fallback comparison based on the items ids is made.
 *
 * @author Christian Groth
 */
public class CascadingAttributeComparator implements Serializable, Comparator<GenericItem> {

    private static final long serialVersionUID = -1988541741809268872L;

    private final List<ItemSortData> sorts = new ArrayList<>();

    /**
     * Creates a new comparator instance with teh given sort data.
     *
     * @param sorts
     *            sort data
     */
    public CascadingAttributeComparator(List<ItemSortData> sorts) {
        if (sorts != null) {
            this.sorts.addAll(sorts);
        }
    }

    @Override
    public int compare(GenericItem o1, GenericItem o2) {

        // sort by parameters
        for (ItemSortData sort : sorts) {
            @SuppressWarnings({ "rawtypes" })
            Comparable firstObject = (Comparable) o1.get(sort.getPath());
            @SuppressWarnings({ "rawtypes" })
            Comparable secondObject = (Comparable) o2.get(sort.getPath());

            // swap for non ascending compare
            if (!sort.isAscending()) {
                @SuppressWarnings("rawtypes")
                Comparable swapObject = firstObject;
                firstObject = secondObject;
                secondObject = swapObject;
            }

            // ensure same types for ambigious cases
            if (firstObject != null && secondObject != null && !firstObject.getClass().equals(secondObject.getClass())) {

                // check types
                final boolean haveOneLong = isType(Long.class, firstObject) || isType(Long.class, secondObject);
                final boolean haveOneInteger = isType(Integer.class, firstObject) || isType(Integer.class, secondObject);
                final boolean haveOneDouble = isType(Double.class, firstObject) || isType(Double.class, secondObject);
                final boolean haveOneFloat = isType(Float.class, firstObject) || isType(Float.class, secondObject);

                // try to repair
                if (haveOneLong && haveOneInteger) {
                    if (isType(Integer.class, firstObject)) {
                        firstObject = Long.valueOf(((Integer) firstObject).longValue());
                    } else {
                        secondObject = Long.valueOf(((Integer) secondObject).longValue());
                    }
                } else if (haveOneDouble && haveOneFloat) {
                    if (isType(Float.class, firstObject)) {
                        firstObject = Double.valueOf(((Float) firstObject).doubleValue());
                    } else {
                        secondObject = Double.valueOf(((Float) secondObject).doubleValue());
                    }
                }
            }

            // compare
            @SuppressWarnings("unchecked")
            int compare = ObjectUtils.compare(firstObject, secondObject, true);
            if (compare != 0) {

                // done
                return compare;
            }
        }

        // fallback to id sort, desc
        return ObjectUtils.compare(o2.getId(), o1.getId());
    }

    private boolean isType(Class<?> type, Object object) {
        return type != null && type == object.getClass();
    }
}
