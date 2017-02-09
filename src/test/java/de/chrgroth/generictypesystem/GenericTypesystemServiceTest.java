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
import de.chrgroth.generictypesystem.model.GenericValue;
import de.chrgroth.generictypesystem.persistence.PersistenceService;
import de.chrgroth.generictypesystem.validation.ValidationResult;
import de.chrgroth.generictypesystem.validation.ValidationService;

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
        Mockito.when(validation.validate(Mockito.any())).thenReturn(validationResult);
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
        Mockito.when(validation.validate(Mockito.any())).thenReturn(validationResult);
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
}
