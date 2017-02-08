package de.chrgroth.generictypesystem.context.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import de.chrgroth.generictypesystem.context.GenericTypesystemContext;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.model.Visibility;

@RunWith(Parameterized.class)
public class DefaultGenericTypesystemContextVisibilityTest {

    @Parameters(name = "user {0}, type {1}-{2}, item {3}-{4}")
    public static Iterable<Object[]> data() {
        List<Object[]> data = new ArrayList<>();

        /*
         * null type owner, null item owner
         */

        // no restrictions at all
        data.add(new Object[] { null, null, Visibility.PUBLIC, null, Visibility.PUBLIC, true, true });
        data.add(new Object[] { null, null, Visibility.PUBLIC, null, Visibility.PRIVATE, true, true });
        data.add(new Object[] { null, null, Visibility.PRIVATE, null, Visibility.PUBLIC, true, true });
        data.add(new Object[] { null, null, Visibility.PRIVATE, null, Visibility.PRIVATE, true, true });

        // user, but not data restrictions
        data.add(new Object[] { 1l, null, Visibility.PUBLIC, null, Visibility.PUBLIC, true, true });
        data.add(new Object[] { 1l, null, Visibility.PUBLIC, null, Visibility.PRIVATE, true, false });
        data.add(new Object[] { 1l, null, Visibility.PRIVATE, null, Visibility.PUBLIC, false, false });
        data.add(new Object[] { 1l, null, Visibility.PRIVATE, null, Visibility.PRIVATE, false, false });

        /*
         * null type owner, item owner
         */

        // no type restrictions (it's the users type)
        data.add(new Object[] { null, null, Visibility.PUBLIC, 1l, Visibility.PUBLIC, true, true });
        data.add(new Object[] { null, null, Visibility.PUBLIC, 1l, Visibility.PRIVATE, true, true });
        data.add(new Object[] { null, null, Visibility.PRIVATE, 1l, Visibility.PUBLIC, true, true });
        data.add(new Object[] { null, null, Visibility.PRIVATE, 1l, Visibility.PRIVATE, true, true });

        // users items
        data.add(new Object[] { 1l, null, Visibility.PUBLIC, 1l, Visibility.PUBLIC, true, true });
        data.add(new Object[] { 1l, null, Visibility.PUBLIC, 1l, Visibility.PRIVATE, true, true });
        data.add(new Object[] { 1l, null, Visibility.PRIVATE, 1l, Visibility.PUBLIC, false, false });
        data.add(new Object[] { 1l, null, Visibility.PRIVATE, 1l, Visibility.PRIVATE, false, false });

        /*
         * type owner, null item owner
         */

        // other users type
        data.add(new Object[] { null, 1l, Visibility.PUBLIC, null, Visibility.PUBLIC, true, true });
        data.add(new Object[] { null, 1l, Visibility.PUBLIC, null, Visibility.PRIVATE, true, true });
        data.add(new Object[] { null, 1l, Visibility.PRIVATE, null, Visibility.PUBLIC, false, false });
        data.add(new Object[] { null, 1l, Visibility.PRIVATE, null, Visibility.PRIVATE, false, false });

        // same users type
        data.add(new Object[] { 1l, 1l, Visibility.PUBLIC, null, Visibility.PUBLIC, true, true });
        data.add(new Object[] { 1l, 1l, Visibility.PUBLIC, null, Visibility.PRIVATE, true, true });
        data.add(new Object[] { 1l, 1l, Visibility.PRIVATE, null, Visibility.PUBLIC, true, true });
        data.add(new Object[] { 1l, 1l, Visibility.PRIVATE, null, Visibility.PRIVATE, true, true });

        /*
         * type owner, item owner
         */

        // other users type
        data.add(new Object[] { null, 1l, Visibility.PUBLIC, 1l, Visibility.PUBLIC, true, true });
        data.add(new Object[] { null, 1l, Visibility.PUBLIC, 1l, Visibility.PRIVATE, true, false });
        data.add(new Object[] { null, 1l, Visibility.PRIVATE, 1l, Visibility.PUBLIC, false, false });
        data.add(new Object[] { null, 1l, Visibility.PRIVATE, 1l, Visibility.PRIVATE, false, false });

        // same users type
        data.add(new Object[] { 1l, 1l, Visibility.PUBLIC, 1l, Visibility.PUBLIC, true, true });
        data.add(new Object[] { 1l, 1l, Visibility.PUBLIC, 1l, Visibility.PRIVATE, true, true });
        data.add(new Object[] { 1l, 1l, Visibility.PRIVATE, 1l, Visibility.PUBLIC, true, true });
        data.add(new Object[] { 1l, 1l, Visibility.PRIVATE, 1l, Visibility.PRIVATE, true, true });

        // done
        return data;
    }

    private GenericTypesystemContext context;
    @Parameter(value = 0)
    public Long contextUser;

    private GenericType type;
    @Parameter(value = 1)
    public Long typeOwner;
    @Parameter(value = 2)
    public Visibility typeVisibility;

    private GenericItem item;
    @Parameter(value = 3)
    public Long itemOwner;
    @Parameter(value = 4)
    public Visibility itemVisibility;

    @Parameter(value = 5)
    public boolean typeAccessible;
    @Parameter(value = 6)
    public boolean itemAccessible;

    @Before
    public void setup() {

        // create context
        context = new DefaultGenericTypesystemContext(contextUser);

        // create type
        type = new GenericType();
        type.setId(1l);
        type.setOwner(typeOwner);
        type.setVisibility(typeVisibility);

        // create item
        item = new GenericItem();
        item.setId(1l);
        item.setOwner(itemOwner);
        item.setVisibility(itemVisibility);
    }

    @Test
    public void accessibility() {

        // check if type is accessible
        final boolean typeIsAccessible = context.isTypeAccessible(type);
        Assert.assertEquals("TYPE", typeAccessible, typeIsAccessible);

        // only check if type is accessible as this is a pre-condition for this method
        if (typeIsAccessible) {
            Assert.assertEquals("ITEM", itemAccessible, context.isItemAccessible(type, item));
        }
    }
}
