package de.chrgroth.generictypesystem.validation.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericAttributeType;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericStructure;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.validation.BaseValidationServiceTest;

public class DefaultValidationServiceHooksTest extends BaseValidationServiceTest {

    @Mock
    private DefaultValidationServiceHooks hooks;

    @Before
    public void setup() {

        // hooks and service
        MockitoAnnotations.initMocks(this);
        service = new DefaultValidationService(hooks);

        // type
        Set<GenericAttribute> typeAttributes = new HashSet<>();
        typeAttributes.add(new GenericAttribute(0l, 0, "simple", GenericAttributeType.STRING, null, false, false, false, null));
        typeAttributes.add(new GenericAttribute(1l, 0, "list", GenericAttributeType.LIST, GenericAttributeType.LONG, false, false, false, null));
        Set<GenericAttribute> subStructureAttributes = new HashSet<>();
        subStructureAttributes.add(new GenericAttribute(3l, 0, "sub-simple", GenericAttributeType.DOUBLE, null, false, false, false, null));
        GenericStructure subStructure = new GenericStructure(subStructureAttributes);
        typeAttributes.add(new GenericAttribute(2l, 0, "struct", GenericAttributeType.STRUCTURE, null, false, false, false, subStructure));
        type = new GenericType(0l, 0, "testType", "testGroup", typeAttributes);

        // item
        Map<String, Object> values = new HashMap<>();
        values.put("simple", "foo");
        values.put("list", Arrays.asList(2l, 3l, 7l));
        Map<String, Object> subValues = new HashMap<>();
        subValues.put("sub-simple", 12.34d);
        GenericItem subItem = new GenericItem(null, null, subValues);
        values.put("struct", subItem);
        item = new GenericItem(0l, type.getId(), values);
    }

    @Test
    public void hooks() {

        // validate
        validateItem();

        // type hooks
        Mockito.verify(hooks, Mockito.times(1)).typeValidation(Mockito.any(), Mockito.any());
        Mockito.verify(hooks, Mockito.times(2)).structureValidation(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(hooks, Mockito.times(4)).typeAttributeValidation(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(hooks, Mockito.times(1)).typeListAttributeValidation(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(hooks, Mockito.times(1)).typeStructureAttributeValidation(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(hooks, Mockito.times(2)).typeSimpleAttributeValidation(Mockito.any(), Mockito.any(), Mockito.any());

        // item hooks
        Mockito.verify(hooks, Mockito.times(1)).itemValidation(Mockito.any(), Mockito.any());
        Mockito.verify(hooks, Mockito.times(3)).itemAttributeValidation(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(hooks, Mockito.times(1)).itemListAttributeValueValidation(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(hooks, Mockito.times(2)).itemSimpleAttributeValueValidation(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }
}
