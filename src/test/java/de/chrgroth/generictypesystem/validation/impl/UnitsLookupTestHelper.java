package de.chrgroth.generictypesystem.validation.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import de.chrgroth.generictypesystem.model.GenericUnits;

public class UnitsLookupTestHelper implements Function<Long, GenericUnits> {

    private final Map<Long, GenericUnits> units = new HashMap<>();

    public void register(GenericUnits units) {
        this.units.put(units.getId(), units);
    }

    @Override
    public GenericUnits apply(Long unitsId) {
        return units.get(unitsId);
    }
}
