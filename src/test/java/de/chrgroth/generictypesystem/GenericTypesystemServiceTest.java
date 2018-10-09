package de.chrgroth.generictypesystem;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.chrgroth.generictypesystem.context.GenericTypesystemContext;
import de.chrgroth.generictypesystem.context.impl.DefaultGenericTypesystemContext;
import de.chrgroth.generictypesystem.context.impl.NullGenericTypesystemContext;
import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericStructure;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.model.GenericUnit;
import de.chrgroth.generictypesystem.model.GenericUnits;
import de.chrgroth.generictypesystem.model.GenericValue;
import de.chrgroth.generictypesystem.model.UnitValue;
import de.chrgroth.generictypesystem.persistence.PersistenceService;
import de.chrgroth.generictypesystem.validation.ValidationError;
import de.chrgroth.generictypesystem.validation.ValidationResult;
import de.chrgroth.generictypesystem.validation.ValidationResultUtils;
import de.chrgroth.generictypesystem.validation.ValidationService;
import de.chrgroth.generictypesystem.validation.impl.DefaultValidationServiceMessageKey;

public class GenericTypesystemServiceTest {

    private static final String ATTRIBUTE_DEFAULT_VALUE_VALUE = "foo";

    private static final String ATTRIBUTE_NESTED = "nested";

    private static final String ATTRIBUTE_DEFAULT_VALUE = "defaultValue";

    private static final String ATTRIBUTE_NO_DEFAULT_VALUE = "noDefaultValue";

    @Mock
    private ValidationService validation;

    @Mock
    private PersistenceService persistence;

    private GenericTypesystemContext context;
    private GenericTypesystemService service;

    private GenericType createTestType;

    @Before
    public void setup() {

        // init mocks
        MockitoAnnotations.initMocks(this);

        // create context & service
        context = new NullGenericTypesystemContext();
        service = new GenericTypesystemService(validation, persistence);

        // create type definition for create test
        createTestType = new GenericType();
        GenericAttribute noDefaultValueAttribute = new GenericAttribute();
        noDefaultValueAttribute.setName(ATTRIBUTE_NO_DEFAULT_VALUE);
        createTestType.getAttributes().add(noDefaultValueAttribute);
        GenericAttribute defaultValueAttribute = new GenericAttribute();
        defaultValueAttribute.setName(ATTRIBUTE_DEFAULT_VALUE);
        defaultValueAttribute.setDefaultValue(new GenericValue<String>(String.class, ATTRIBUTE_DEFAULT_VALUE_VALUE));
        createTestType.getAttributes().add(defaultValueAttribute);
        GenericAttribute nestedStructureAttribute = new GenericAttribute();
        nestedStructureAttribute.setName(ATTRIBUTE_NESTED);
        nestedStructureAttribute.setStructure(new GenericStructure());
        nestedStructureAttribute.getStructure().getAttributes().add(noDefaultValueAttribute);
        nestedStructureAttribute.getStructure().getAttributes().add(defaultValueAttribute);
        createTestType.getAttributes().add(nestedStructureAttribute);
    }

    @Test
    public void units() {
        service.units(context);
        Mockito.verify(persistence, Mockito.times(1)).units(context);
    }

    @Test
    public void unitsNull() {

        // null is invalid units
        ValidationResult<GenericUnits> validationResult = Mockito.mock(ValidationResult.class);
        Mockito.when(validation.validate(Mockito.nullable(GenericUnits.class))).thenReturn(validationResult);
        Mockito.when(validationResult.isValid()).thenReturn(Boolean.FALSE);

        service.units(context, null);
        Mockito.verify(persistence, Mockito.times(0)).units(Mockito.any(), Mockito.any());
    }

    @Test
    public void unitsUnitIdsMissing() {

        // create type
        GenericUnits units = new GenericUnits();
        units.getUnits().add(new GenericUnit(0l, "seconds", "s", GenericUnits.FACTOR_BASE));
        units.getUnits().add(new GenericUnit(null, "minutes", "m", 60));
        units.getUnits().add(new GenericUnit(null, "hours", "h", 60 * 60));
        units.getUnits().add(new GenericUnit(2l, "days", "d", 60 * 60 * 24));

        // units is valid
        ValidationResult<GenericUnits> validationResult = Mockito.mock(ValidationResult.class);
        Mockito.when(validation.validate(Mockito.any(GenericUnits.class))).thenReturn(validationResult);
        Mockito.when(validationResult.isValid()).thenReturn(Boolean.TRUE);

        // call service
        service.units(context, units);
        long unitsWithoutId = units.getUnits().stream().filter(u -> u.getId() == null).count();
        Assert.assertEquals(0, unitsWithoutId);
        Mockito.verify(persistence, Mockito.times(1)).units(Mockito.any(), Mockito.any());
    }

    @Test
    public void convertNull() {
        ValidationResult<UnitValue> result = service.convert(context, null, 0l);
        ValidationResultUtils.assertValidationResult(result, null, new ValidationError("", DefaultValidationServiceMessageKey.UNITS_CONVERT_VALUE_NOT_AVAILABLE));
    }

    @Test
    public void convertValueNull() {
        final UnitValue value = new UnitValue(0l, 0l, null);
        ValidationResult<UnitValue> result = service.convert(context, value, 1l);
        ValidationResultUtils.assertValidationResult(result, value, new ValidationError("", DefaultValidationServiceMessageKey.UNITS_CONVERT_VALUE_NOT_AVAILABLE));
    }

    @Test
    public void convertValueValueNull() {
        final UnitValue value = new UnitValue(0l, 0l, new GenericValue<>(Double.class, null));
        ValidationResult<UnitValue> result = service.convert(context, value, 1l);
        ValidationResultUtils.assertValidationResult(result, value, new ValidationError("", DefaultValidationServiceMessageKey.UNITS_CONVERT_VALUE_NOT_AVAILABLE));
    }

    @Test
    public void convertValueTypeNull() {
        final UnitValue value = new UnitValue(0l, 0l, new GenericValue<>(null, 12.34d));
        ValidationResult<UnitValue> result = service.convert(context, value, 1l);
        ValidationResultUtils.assertValidationResult(result, value, new ValidationError("", DefaultValidationServiceMessageKey.UNITS_CONVERT_VALUE_TYPE_NOT_SUPPORTED));
    }

    @Test
    public void convertValueTypeNonNumeric() {
        final UnitValue value = new UnitValue(0l, 0l, new GenericValue<>(String.class, "12.34d"));
        ValidationResult<UnitValue> result = service.convert(context, value, 1l);
        ValidationResultUtils.assertValidationResult(result, value, new ValidationError("", DefaultValidationServiceMessageKey.UNITS_CONVERT_VALUE_TYPE_NOT_SUPPORTED));
    }

    @Test
    public void convertUnitsIdNull() {
        final UnitValue value = new UnitValue(null, 0l, new GenericValue<>(Double.class, 12.34d));
        ValidationResult<UnitValue> result = service.convert(context, value, 1l);
        ValidationResultUtils.assertValidationResult(result, value, new ValidationError("unitsId", DefaultValidationServiceMessageKey.UNITS_CONVERT_UNITS_NOT_AVAILABLE));
    }

    @Test
    public void convertUnitsNotExistent() {
        mockUnits(null);
        final UnitValue value = new UnitValue(0l, 0l, new GenericValue<>(Double.class, 12.34d));
        ValidationResult<UnitValue> result = service.convert(context, value, 1l);
        ValidationResultUtils.assertValidationResult(result, value, new ValidationError("unitsId", DefaultValidationServiceMessageKey.UNITS_CONVERT_UNITS_NOT_AVAILABLE));
    }

    @Test
    public void convertUnitsInvalid() {
        mockUnits(Mockito.mock(GenericUnits.class));
        mockUnitsResult(false);

        final UnitValue value = new UnitValue(0l, 0l, new GenericValue<>(Double.class, 12.34d));
        ValidationResult<UnitValue> result = service.convert(context, value, 1l);
        ValidationResultUtils.assertValidationResult(result, value, new ValidationError("unitsId", DefaultValidationServiceMessageKey.UNITS_CONVERT_UNITS_NOT_VALID));
    }

    @Test
    public void convertSourceUnitIdNull() {
        GenericUnits units = Mockito.mock(GenericUnits.class);
        mockUnits(units);
        mockUnitsResult(true);

        final UnitValue value = new UnitValue(0l, null, new GenericValue<>(Double.class, 12.34d));
        ValidationResult<UnitValue> result = service.convert(context, value, 1l);
        ValidationResultUtils.assertValidationResult(result, value, new ValidationError("unitId", DefaultValidationServiceMessageKey.UNITS_CONVERT_UNIT_SOURCE_NOT_AVAILABLE));
    }

    @Test
    public void convertSourceUnitNotAvailable() {
        GenericUnits units = Mockito.mock(GenericUnits.class);
        Mockito.when(units.unit(Mockito.anyLong())).thenReturn(null);
        mockUnits(units);
        mockUnitsResult(true);

        final UnitValue value = new UnitValue(0l, 0l, new GenericValue<>(Double.class, 12.34d));
        ValidationResult<UnitValue> result = service.convert(context, value, 1l);
        ValidationResultUtils.assertValidationResult(result, value, new ValidationError("unitId", DefaultValidationServiceMessageKey.UNITS_CONVERT_UNIT_SOURCE_NOT_AVAILABLE));
    }

    @Test
    public void convertTargetUnitNotAvailable() {
        GenericUnits units = Mockito.mock(GenericUnits.class);
        GenericUnit sourceUnit = Mockito.mock(GenericUnit.class);
        Mockito.when(units.unit(Mockito.eq(0l))).thenReturn(sourceUnit);
        Mockito.when(units.unit(Mockito.longThat(l -> l != null && l != 0l))).thenReturn(null);
        mockUnits(units);
        mockUnitsResult(true);

        final UnitValue value = new UnitValue(0l, 0l, new GenericValue<>(Double.class, 12.34d));
        ValidationResult<UnitValue> result = service.convert(context, value, 1l);
        ValidationResultUtils.assertValidationResult(result, value, new ValidationError("", DefaultValidationServiceMessageKey.UNITS_CONVERT_UNIT_TARGET_NOT_AVAILABLE));
    }

    @Test
    public void convertSomeUnitsMinorToMinor() {
        assertConversion(new UnitValue(someUnits().getId(), 0l, new GenericValue<>(Integer.class, 1)), 0l, 1);
    }

    @Test
    public void convertSomeUnitsMinorToBase() {
        assertConversion(new UnitValue(someUnits().getId(), 0l, new GenericValue<>(Long.class, 1l)), 1l, 10l);
    }

    @Test
    public void convertSomeUnitsMinorToMajor() {
        assertConversion(new UnitValue(someUnits().getId(), 0l, new GenericValue<>(Float.class, 1.0f)), 2l, 100.0f);
    }

    @Test
    public void convertSomeUnitsBaseToMinor() {
        assertConversion(new UnitValue(someUnits().getId(), 1l, new GenericValue<>(Double.class, 1.0d)), 0l, 0.1d);
    }

    @Test
    public void convertSomeUnitsBaseToBase() {
        assertConversion(new UnitValue(someUnits().getId(), 1l, new GenericValue<>(Long.class, 1l)), 1l, 1l);
    }

    @Test
    public void convertSomeUnitsBaseToMajor() {
        assertConversion(new UnitValue(someUnits().getId(), 1l, new GenericValue<>(Long.class, 1l)), 2l, 10l);
    }

    @Test
    public void convertSomeUnitsMajorToMinor() {
        assertConversion(new UnitValue(someUnits().getId(), 2l, new GenericValue<>(Integer.class, 1)), 0l, 0.01d);
    }

    @Test
    public void convertSomeUnitsMajorToBase() {
        assertConversion(new UnitValue(someUnits().getId(), 2l, new GenericValue<>(Long.class, 1l)), 1l, 0.1d);
    }

    @Test
    public void convertSomeUnitsMajorToMajor() {
        assertConversion(new UnitValue(someUnits().getId(), 2l, new GenericValue<>(Long.class, 1l)), 2l, 1l);
    }

    private GenericUnits someUnits() {
        GenericUnits units = new GenericUnits(0l, null, null);
        GenericUnit minor = new GenericUnit(0l, "minor", null, 0.1d);
        GenericUnit base = new GenericUnit(1l, "base", null, GenericUnits.FACTOR_BASE);
        GenericUnit major = new GenericUnit(2l, "major", null, 10);
        units.getUnits().add(minor);
        units.getUnits().add(base);
        units.getUnits().add(major);
        mockUnits(units);
        mockUnitsResult(true);
        return units;
    }

    private void assertConversion(final UnitValue value, long targetUnitId, Number expectedValue) {
        ValidationResult<UnitValue> result = service.convert(context, value, targetUnitId);
        Assert.assertTrue(result.isValid());
        final UnitValue item = result.getItem();
        Assert.assertNotNull(item);
        Assert.assertNotEquals(value, item);
        Assert.assertEquals(value.getUnitsId(), item.getUnitsId());
        Assert.assertEquals(Long.valueOf(targetUnitId), item.getUnitId());
        Assert.assertEquals(expectedValue, item.getValue().getValue());
        Assert.assertEquals(expectedValue.getClass(), item.getValue().getType());
    }

    private void mockUnits(GenericUnits units) {
        Mockito.when(persistence.units(Mockito.any(), Mockito.anyLong())).thenReturn(units);
    }

    private void mockUnitsResult(boolean valid) {
        ValidationResult<GenericUnits> unitsResult = Mockito.mock(ValidationResult.class);
        Mockito.when(validation.validate(Mockito.any(GenericUnits.class))).thenReturn(unitsResult);
        Mockito.when(unitsResult.isValid()).thenReturn(valid);
    }

    @Test
    public void typeGroups() {
        service.typeGroups(context);
        Mockito.verify(persistence, Mockito.times(1)).typeGroups(context);
    }

    @Test
    public void types() {
        service.types(context);
        Mockito.verify(persistence, Mockito.times(1)).types(context);
    }

    @Test
    public void typeNull() {

        // null is invalid type
        ValidationResult<GenericType> validationResult = Mockito.mock(ValidationResult.class);
        Mockito.when(validation.validate(Mockito.nullable(GenericType.class))).thenReturn(validationResult);
        Mockito.when(validationResult.isValid()).thenReturn(Boolean.FALSE);

        service.type(context, null);
        Mockito.verify(persistence, Mockito.times(0)).type(Mockito.any(), Mockito.any());
    }

    @Test
    public void typeAttributeIdsMissing() {

        // create type
        GenericType type = new GenericType();
        type.getAttributes().add(new GenericAttribute(0l, null, null, null, false, false, null, null, null, null, null, null, null, null, null));
        type.getAttributes().add(new GenericAttribute(1l, null, null, null, false, false, null, null, null, null, null, null, null, null, null));
        type.getAttributes().add(new GenericAttribute(null, null, null, null, false, false, null, null, null, null, null, null, null, null, null));
        type.getAttributes().add(new GenericAttribute(3l, null, null, null, false, false, null, null, null, null, null, null, null, null, null));
        type.getAttributes().add(new GenericAttribute(null, null, null, null, false, false, null, null, null, null, null, null, null, null, null));

        // type is valid
        ValidationResult<GenericType> validationResult = Mockito.mock(ValidationResult.class);
        Mockito.when(validation.validate(Mockito.any(GenericType.class))).thenReturn(validationResult);
        Mockito.when(validationResult.isValid()).thenReturn(Boolean.TRUE);

        // call service
        service.type(context, type);
        long attributesWithoutId = type.getAttributes().stream().filter(a -> a.getId() == null).count();
        Assert.assertEquals(0, attributesWithoutId);
        Mockito.verify(persistence, Mockito.times(1)).type(Mockito.any(), Mockito.any());
    }

    @Test
    public void createNullType() {
        Mockito.when(persistence.type(Mockito.any(), Mockito.any(Long.class))).thenReturn(null);
        Assert.assertNull(service.create(context, 0l));
    }

    @Test
    public void createOwner() {
        Mockito.when(persistence.type(Mockito.any(), Mockito.any(Long.class))).thenReturn(createTestType);
        Long currentUser = 13l;
        Long typeId = 7l;
        final GenericItem item = service.create(new DefaultGenericTypesystemContext(currentUser), typeId);
        Assert.assertNotNull(item);
        Assert.assertNull(item.getId());
        Assert.assertEquals(typeId, item.getTypeId());
        Assert.assertEquals(currentUser, item.getOwner());
        Assert.assertNull(item.getVisibility());
        Assert.assertNull(item.get(ATTRIBUTE_NO_DEFAULT_VALUE));
        Assert.assertEquals(ATTRIBUTE_DEFAULT_VALUE_VALUE, item.get(ATTRIBUTE_DEFAULT_VALUE));
        Assert.assertNull(item.get(ATTRIBUTE_NESTED + "." + ATTRIBUTE_NO_DEFAULT_VALUE));
        Assert.assertEquals(ATTRIBUTE_DEFAULT_VALUE_VALUE, item.get(ATTRIBUTE_NESTED + "." + ATTRIBUTE_DEFAULT_VALUE));
    }

    @Test
    public void query() {
        service.query(context, 2l, null);
        Mockito.verify(persistence, Mockito.times(1)).query(Mockito.any(), Mockito.eq(2l), Mockito.isNull());
    }

    @Test
    public void item() {
        service.item(context, 2l, 3l);
        Mockito.verify(persistence, Mockito.times(1)).item(Mockito.any(), Mockito.eq(2l), Mockito.eq(3l));
    }

    @Test
    public void itemNull() {

        // null is invalid
        ValidationResult<GenericItem> validationResult = Mockito.mock(ValidationResult.class);
        Mockito.when(validation.validate(Mockito.any(), Mockito.any())).thenReturn(validationResult);
        Mockito.when(validationResult.isValid()).thenReturn(Boolean.FALSE);

        service.item(context, 2l, null);
        Mockito.verify(persistence, Mockito.times(0)).item(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void itemTypeUnknown() {

        // unknown type
        Mockito.when(persistence.type(context, 2l)).thenReturn(null);

        // null type is invalid
        ValidationResult<GenericItem> validationResult = Mockito.mock(ValidationResult.class);
        Mockito.when(validation.validate(Mockito.any(), Mockito.any())).thenReturn(validationResult);
        Mockito.when(validationResult.isValid()).thenReturn(Boolean.FALSE);

        service.item(context, 2l, new GenericItem());
        Mockito.verify(persistence, Mockito.times(0)).item(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void itemMissingTypeId() {

        // unknown type
        GenericType type = new GenericType();
        type.setId(666l);
        Mockito.when(persistence.type(context, 666l)).thenReturn(type);

        // type and item are valid
        ValidationResult<GenericItem> validationResult = Mockito.mock(ValidationResult.class);
        Mockito.when(validation.validate(Mockito.any(), Mockito.any())).thenReturn(validationResult);
        Mockito.when(validationResult.isValid()).thenReturn(Boolean.TRUE);

        GenericItem item = new GenericItem();
        service.item(context, type.getId(), item);
        Assert.assertEquals(666l, item.getTypeId().longValue());
        Mockito.verify(persistence, Mockito.times(1)).item(Mockito.any(), Mockito.eq(type), Mockito.eq(item));
    }

    @Test
    public void values() {
        service.values(context, 0l, null);
        Mockito.verify(persistence, Mockito.times(1)).values(context, 0l, null);
    }

    @Test
    public void removeItem() {
        service.removeItem(context, 0l, 0l);
        Mockito.verify(persistence, Mockito.times(1)).removeItem(context, 0l, 0l);
    }

    @Test
    public void removeType() {
        service.removeType(context, 0l);
        Mockito.verify(persistence, Mockito.times(1)).removeType(context, 0l);
    }

    @Test
    public void removeUnits() {
        service.removeUnits(context, 0l);
        Mockito.verify(persistence, Mockito.times(1)).removeUnits(context, 0l);
    }
}
