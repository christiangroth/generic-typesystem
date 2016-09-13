package de.chrgroth.generictypesystem.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;

import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;

public class BaseValidationServiceTest {

    protected ValidationService service;
    protected GenericType type;
    protected GenericAttribute attribute;
    protected GenericItem item;

    protected void validateType(ValidationMessageKey... errorKeys) {
        assertValidationResult(service.validate(type), type, errorKeys);
    }

    protected void validateItem(ValidationMessageKey... errorKeys) {
        assertValidationResult(service.validate(type, item), item, errorKeys);
    }

    private <T> void assertValidationResult(ValidationResult<T> result, T validatedItem, ValidationMessageKey... errorKeys) {

        // check validated item
        Assert.assertEquals("validated item does not match", validatedItem, result.getItem());

        // check validity
        boolean expectValid = errorKeys == null || errorKeys.length < 1;
        if (expectValid) {
            Assert.assertTrue("got errors: " + result.getErrors(), result.isValid());
        } else {
            Assert.assertFalse("got no errors", result.isValid());

            // check error keys present
            List<ValidationMessageKey> actualErrorKeys = result.getErrors().stream().map(e -> e.getMessageKey()).collect(Collectors.toList());
            List<ValidationMessageKey> expectedErrorKeys = new ArrayList<>(Arrays.asList(errorKeys));
            Iterator<ValidationMessageKey> iterator = expectedErrorKeys.iterator();
            while (iterator.hasNext()) {
                if (actualErrorKeys.remove(iterator.next())) {
                    iterator.remove();
                }
            }
            Assert.assertTrue("error keys not expected: " + actualErrorKeys, actualErrorKeys.isEmpty());
            Assert.assertTrue("error keys not found: " + expectedErrorKeys, expectedErrorKeys.isEmpty());
        }
    }
}
