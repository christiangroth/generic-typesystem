package de.chrgroth.generictypesystem.validation.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.chrgroth.generictypesystem.model.DefaultGenericAttributeType;
import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericStructure;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.model.GenericUnit;
import de.chrgroth.generictypesystem.model.GenericUnits;
import de.chrgroth.generictypesystem.validation.BaseValidationServiceTypeAndItemTest;

public class DefaultValidationServiceHooksTest extends BaseValidationServiceTypeAndItemTest {

    @Mock
    private DefaultValidationServiceHooks hooks;

    private UnitsLookupTestHelper unitsLookupTestHelper;

    @Before
    public void setup() {

        // hooks and service
        unitsLookupTestHelper = new UnitsLookupTestHelper();
        MockitoAnnotations.initMocks(this);
        service = new DefaultValidationService(unitsLookupTestHelper, hooks);

        // type
        List<GenericAttribute> typeAttributes = new ArrayList<>();
        typeAttributes.add(new GenericAttribute(0l, "simple", DefaultGenericAttributeType.STRING, null, false, false, null, null, null, null, null, null, null, null, null, null));

        GenericUnits units = new GenericUnits(0l, "units", "desc");
        units.getUnits().add(new GenericUnit(0l, "base", null, GenericUnits.FACTOR_BASE));
        unitsLookupTestHelper.register(units);
        typeAttributes.add(
                new GenericAttribute(1l, "units", DefaultGenericAttributeType.DOUBLE, null, false, false, null, null, null, null, null, null, null, null, units.getId(), null));

        typeAttributes.add(new GenericAttribute(2l, "list", DefaultGenericAttributeType.LIST, DefaultGenericAttributeType.LONG, false, false, null, null, null, null, null, null,
                null, null, null, null));

        List<GenericAttribute> subStructureAttributes = new ArrayList<>();
        subStructureAttributes
                .add(new GenericAttribute(4l, "sub-simple", DefaultGenericAttributeType.DOUBLE, null, false, false, null, null, null, null, null, null, null, null, null, null));
        GenericStructure subStructure = new GenericStructure(subStructureAttributes);
        typeAttributes.add(
                new GenericAttribute(3l, "struct", DefaultGenericAttributeType.STRUCTURE, null, false, false, subStructure, null, null, null, null, null, null, null, null, null));

        type = new GenericType(0l, "testType", "testGroup", typeAttributes, null, null, null);

        // item
        Map<String, Object> values = new HashMap<>();
        values.put("simple", "foo");
        values.put("list", Arrays.asList(2l, 3l, 7l));
        Map<String, Object> subValues = new HashMap<>();
        subValues.put("sub-simple", 12.34d);
        GenericItem subItem = new GenericItem(null, null, subValues, null, null);
        values.put("struct", subItem);
        item = new GenericItem(0l, type.getId(), values, null, null);
    }

    @Test
    public void hooks() {

        // validate
        validateItem();

        // unit hooks
        Mockito.verify(hooks, Mockito.times(1)).unitsValidation(Mockito.any(), Mockito.any());
        Mockito.verify(hooks, Mockito.times(1)).unitsUnitValidation(Mockito.any(), Mockito.any(), Mockito.any());

        // type hooks
        Mockito.verify(hooks, Mockito.times(1)).typeValidation(Mockito.any(), Mockito.any());
        Mockito.verify(hooks, Mockito.times(2)).structureValidation(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(hooks, Mockito.times(5)).typeAttributeValidation(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(hooks, Mockito.times(1)).typeListAttributeValidation(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(hooks, Mockito.times(1)).typeStructureAttributeValidation(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(hooks, Mockito.times(3)).typeSimpleAttributeValidation(Mockito.any(), Mockito.any(), Mockito.any());

        // item hooks
        Mockito.verify(hooks, Mockito.times(1)).itemValidation(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(hooks, Mockito.times(2)).itemLevelValidation(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(hooks, Mockito.times(4)).itemAttributeValidation(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(hooks, Mockito.times(1)).itemListAttributeValueValidation(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(hooks, Mockito.times(2)).itemSimpleAttributeValueValidation(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }
}
