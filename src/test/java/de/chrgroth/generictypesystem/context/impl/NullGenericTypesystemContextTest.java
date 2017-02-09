package de.chrgroth.generictypesystem.context.impl;

import org.junit.Assert;
import org.junit.Test;

public class NullGenericTypesystemContextTest {

    @Test
    public void noUser() {
        Assert.assertNull(new NullGenericTypesystemContext().currentUser());
    }

    @Test
    public void typeAccessible() {
        Assert.assertTrue(new NullGenericTypesystemContext().isTypeAccessible(null));
    }

    @Test
    public void itemAccessible() {
        Assert.assertTrue(new NullGenericTypesystemContext().isItemAccessible(null, null));
    }
}
