package de.chrgroth.generictypesystem.validation.impl;

import org.junit.Before;
import org.junit.Test;

import de.chrgroth.generictypesystem.model.GenericUnit;
import de.chrgroth.generictypesystem.model.GenericUnits;
import de.chrgroth.generictypesystem.validation.ValidationError;
import de.chrgroth.generictypesystem.validation.ValidationResultUtils;

public class DefaultValidationServiceUnitsTest {

    private GenericUnits units;
    private DefaultValidationService service;
    private UnitsLookupTestHelper unitsLookupTestHelper;

    @Before
    public void setup() {
        unitsLookupTestHelper = new UnitsLookupTestHelper();
        service = new DefaultValidationService(unitsLookupTestHelper, null);
        units = new GenericUnits(0l, "some units", "some description");
        units.getUnits().add(new GenericUnit(0l, "base", "x", GenericUnits.FACTOR_BASE));
        units.getUnits().add(new GenericUnit(1l, "other", "y", 2.00));
    }

    @Test
    public void nullRoot() {
        units = null;
        validateUnits(new ValidationError("", DefaultValidationServiceMessageKey.GENERAL_UNITS_NOT_PROVIDED));
    }

    @Test
    public void nullName() {
        units.setName(null);
        validateUnits(new ValidationError("", DefaultValidationServiceMessageKey.UNITS_NAME_MANDATORY));
    }

    @Test
    public void emptyName() {
        units.setName("");
        validateUnits(new ValidationError("", DefaultValidationServiceMessageKey.UNITS_NAME_MANDATORY));
    }

    @Test
    public void whitespaceName() {
        units.setName(" ");
        validateUnits(new ValidationError("", DefaultValidationServiceMessageKey.UNITS_NAME_MANDATORY));
    }

    @Test
    public void nullUnits() {
        units.setUnits(null);
        validateUnits(new ValidationError("", DefaultValidationServiceMessageKey.UNITS_UNITS_NOT_PROVIDED));
    }

    @Test
    public void emptyUnits() {
        units.getUnits().clear();
        validateUnits(new ValidationError("", DefaultValidationServiceMessageKey.UNITS_UNITS_NOT_PROVIDED));
    }

    @Test
    public void unitIdAmbigious() {
        units.getUnits().forEach(u -> u.setId(0l));
        validateUnits(new ValidationError("", DefaultValidationServiceMessageKey.UNITS_AMBIGIOUS_UNIT_ID, 0l));
    }

    @Test
    public void noBaseUnit() {
        units.getUnits().forEach(u -> u.setFactor(2.0d));
        validateUnits(new ValidationError("", DefaultValidationServiceMessageKey.UNITS_UNITS_EXACTLY_ONE_BASE_UNIT_MANDATORY));
    }

    @Test
    public void multipleBaseUnits() {
        units.getUnits().forEach(u -> u.setFactor(GenericUnits.FACTOR_BASE));
        validateUnits(new ValidationError("", DefaultValidationServiceMessageKey.UNITS_UNITS_EXACTLY_ONE_BASE_UNIT_MANDATORY));
    }

    @Test
    public void unitNullId() {
        units.getUnits().stream().filter(GenericUnit::isBase).findFirst().get().setId(null);
        validateUnits(new ValidationError("base", DefaultValidationServiceMessageKey.UNITS_UNIT_ID_MANDATORY));
    }

    @Test
    public void unitNullName() {
        units.getUnits().stream().filter(GenericUnit::isBase).findFirst().get().setName(null);
        validateUnits(new ValidationError("", DefaultValidationServiceMessageKey.UNITS_UNIT_NAME_MANDATORY));
    }

    @Test
    public void unitEmptyName() {
        units.getUnits().stream().filter(GenericUnit::isBase).findFirst().get().setName("");
        validateUnits(new ValidationError("", DefaultValidationServiceMessageKey.UNITS_UNIT_NAME_MANDATORY));
    }

    @Test
    public void unitWhitespaceName() {
        units.getUnits().stream().filter(GenericUnit::isBase).findFirst().get().setName(" ");
        validateUnits(new ValidationError("", DefaultValidationServiceMessageKey.UNITS_UNIT_NAME_MANDATORY));
    }

    @Test
    public void unitFactorZero() {
        units.getUnits().stream().filter(u -> !u.isBase()).findFirst().get().setFactor(0.0d);
        validateUnits(new ValidationError("other", DefaultValidationServiceMessageKey.UNITS_UNIT_FACTOR_NOT_POSITIVE, 0.0d));
    }

    @Test
    public void unitFactorNegative() {
        units.getUnits().stream().filter(u -> !u.isBase()).findFirst().get().setFactor(-2.0d);
        validateUnits(new ValidationError("other", DefaultValidationServiceMessageKey.UNITS_UNIT_FACTOR_NOT_POSITIVE, -2.0d));
    }

    protected void validateUnits(ValidationError... errors) {
        ValidationResultUtils.assertValidationResult(service.validate(units), units, errors);
    }
}
