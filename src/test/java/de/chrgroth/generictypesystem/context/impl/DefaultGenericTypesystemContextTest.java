package de.chrgroth.generictypesystem.context.impl;

import org.junit.Assert;
import org.junit.Test;

public class DefaultGenericTypesystemContextTest {

    @Test
    public void emptyContext() {
        DefaultGenericTypesystemContext context = new DefaultGenericTypesystemContext();
        Assert.assertNull(context.currentUser());
    }

    @Test
    public void nullUserContext() {
        DefaultGenericTypesystemContext context = new DefaultGenericTypesystemContext(null);
        Assert.assertNull(context.currentUser());
    }

    @Test
    public void userContext() {
        Long user = 1l;
        DefaultGenericTypesystemContext context = new DefaultGenericTypesystemContext(user);
        Assert.assertEquals(user, context.currentUser());
    }

    @Test
    public void nullTypeAccessible() {
        Assert.assertFalse(new DefaultGenericTypesystemContext().isTypeAccessible(null));
    }

    @Test
    public void nullTypeNullItemAccessible() {
        Assert.assertFalse(new DefaultGenericTypesystemContext().isItemAccessible(null, null));
    }
}
