package de.chrgroth.generictypesystem.validation.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.validation.ValidationResult;
import de.chrgroth.generictypesystem.validation.ValidationService;

public class NoValidationServiceTest {

    private ValidationService service;
    private GenericType type;
    private GenericItem item;

    @Before
    public void setup() {
        service = new NoValidationService();
        type = new GenericType(null, 0, null, null, null, null, null, null, null, null);
        item = new GenericItem();
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
