package de.chrgroth.generictypesystem.validation;

import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;

public class BaseValidationServiceTypeAndItemTest {

    protected ValidationService service;

    protected GenericType type;
    protected GenericAttribute attribute;
    protected GenericItem item;

    protected void validateType(ValidationError... errors) {
        ValidationResultUtils.assertValidationResult(service.validate(type), type, errors);
    }

    protected void validateItem(ValidationError... errors) {
        ValidationResultUtils.assertValidationResult(service.validate(type, item), item, errors);
    }
}
