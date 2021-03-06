package de.chrgroth.generictypesystem.validation.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.model.GenericUnits;
import de.chrgroth.generictypesystem.validation.ValidationResult;
import de.chrgroth.generictypesystem.validation.ValidationService;

public class NoValidationServiceTest {

    private ValidationService service;
    private GenericUnits units;
    private GenericType type;
    private GenericItem item;

    @Before
    public void setup() {
        service = new NoValidationService();
        units = new GenericUnits(null, null, null);
        type = new GenericType(null, null, null, null, null, null, null);
        item = new GenericItem();
    }

    @Test
    public void units() {
        ValidationResult<GenericUnits> result = service.validate(units);
        Assert.assertTrue(result.isValid());
        Assert.assertEquals(units, result.getItem());
        Assert.assertTrue(result.getErrors().isEmpty());
    }

    @Test
    public void type() {
        ValidationResult<GenericType> result = service.validate(type);
        Assert.assertTrue(result.isValid());
        Assert.assertEquals(type, result.getItem());
        Assert.assertTrue(result.getErrors().isEmpty());
    }

    @Test
    public void item() {
        ValidationResult<GenericItem> result = service.validate(type, item);
        Assert.assertTrue(result.isValid());
        Assert.assertEquals(item, result.getItem());
        Assert.assertTrue(result.getErrors().isEmpty());
    }
}
