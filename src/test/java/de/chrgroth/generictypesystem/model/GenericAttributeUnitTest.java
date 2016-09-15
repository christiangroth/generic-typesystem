package de.chrgroth.generictypesystem.model;

import org.junit.Assert;
import org.junit.Test;

public class GenericAttributeUnitTest {

    @Test
    public void baseUnit() {
        Assert.assertTrue(new GenericAttributeUnit("someName", GenericAttributeUnit.FACTOR_BASE).isBase());
    }

    @Test
    public void nonBaseUnit() {
        Assert.assertFalse(new GenericAttributeUnit("someName", GenericAttributeUnit.FACTOR_BASE + 1).isBase());
    }
}
