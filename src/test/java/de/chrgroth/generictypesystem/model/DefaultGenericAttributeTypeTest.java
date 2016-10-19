package de.chrgroth.generictypesystem.model;

import org.junit.Assert;
import org.junit.Test;

public class DefaultGenericAttributeTypeTest {

    @Test
    public void convertNull() {
        Assert.assertNull(DefaultGenericAttributeType.STRING.convert(null));
    }

    @Test
    public void convertUnassignableValueType() {
        Assert.assertNull(DefaultGenericAttributeType.STRING.convert(Integer.MAX_VALUE));
    }

    @Test
    public void convertOnlyAssignableValueType() {
        String testValue = "foo";
        Assert.assertSame(testValue, DefaultGenericAttributeType.STRING.convert(testValue));
    }

    @Test
    public void convertIntegerForLong() {
        Assert.assertEquals(12l, DefaultGenericAttributeType.LONG.convert(12));
    }

    @Test
    public void convertLongForLong() {
        Long testValue = 12l;
        Assert.assertSame(testValue, DefaultGenericAttributeType.LONG.convert(testValue));
    }

    @Test
    public void convertIntegerForDouble() {
        Assert.assertEquals(12.0d, DefaultGenericAttributeType.DOUBLE.convert(12));
    }

    @Test
    public void convertLongForDouble() {
        Assert.assertEquals(12.0d, DefaultGenericAttributeType.DOUBLE.convert(12l));
    }

    @Test
    public void convertFloatForDouble() {
        Assert.assertEquals(12.0d, DefaultGenericAttributeType.DOUBLE.convert(12));
    }

    @Test
    public void convertDoubleForDouble() {
        Double testValue = 12.0d;
        Assert.assertSame(testValue, DefaultGenericAttributeType.DOUBLE.convert(testValue));
    }
}
