package de.chrgroth.generictypesystem.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GenericUnitsTest {

    private GenericUnits units;
    private GenericUnit seconds = new GenericUnit(0l, "seconds", "s", GenericUnits.FACTOR_BASE);
    private GenericUnit minutes = new GenericUnit(1l, "minutes", "m", 60);
    private GenericUnit hours = new GenericUnit(2l, "hours", "h", 60 * 60);

    @Before
    public void setup() {
        units = new GenericUnits();
        units.setId(0l);
        units.setName("time");
        units.setDescription("Optional description about time units.");
        units.getUnits().add(seconds);
        units.getUnits().add(minutes);
        units.getUnits().add(hours);
    }

    @Test
    public void isBaseUnit() {
        Assert.assertTrue(seconds.isBase());
        Assert.assertFalse(minutes.isBase());
        Assert.assertFalse(hours.isBase());
    }

    @Test
    public void baseUnit() {
        Assert.assertEquals(seconds, units.baseUnit());
    }

    @Test
    public void nonBaseUnit() {
        seconds.setFactor(0);
        Assert.assertNull(units.baseUnit());
    }

    @Test
    public void unit() {
        Assert.assertEquals(seconds, units.unit(seconds.getId()));
        Assert.assertEquals(minutes, units.unit(minutes.getId()));
        Assert.assertEquals(hours, units.unit(hours.getId()));
    }
}
