package de.chrgroth.generictypesystem.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

public class ValidationResultUtils {

    public static <T> void assertValidationResult(ValidationResult<T> result, T validatedItem, ValidationError... errors) {

        // check validated item
        Assert.assertEquals("validated item does not match", validatedItem, result.getItem());

        // check validity
        boolean expectValid = errors == null || errors.length < 1;
        if (expectValid) {
            Assert.assertTrue("got errors: " + result.getErrors(), result.isValid());
        } else {
            Assert.assertFalse("got no errors", result.isValid());

            // check errors present
            List<ValidationError> actualErrors = result.getErrors();
            List<ValidationError> expectedErrors = new ArrayList<>(Arrays.asList(errors));
            Iterator<ValidationError> expectedIterator = expectedErrors.iterator();
            expectedErrorsLoop: while (expectedIterator.hasNext()) {
                ValidationError expectedError = expectedIterator.next();

                // loop actual errors
                Iterator<ValidationError> actualIterator = actualErrors.iterator();
                while (actualIterator.hasNext()) {
                    ValidationError actualError = actualIterator.next();

                    // check all values match
                    if (StringUtils.equals(expectedError.getPath(), actualError.getPath()) && expectedError.getMessageKey().equals(actualError.getMessageKey())
                            && Arrays.equals(expectedError.getMessageParameters(), actualError.getMessageParameters())) {
                        expectedIterator.remove();
                        actualIterator.remove();
                        continue expectedErrorsLoop;
                    }
                }
            }
            Assert.assertTrue("error keys not expected: " + actualErrors, actualErrors.isEmpty());
            Assert.assertTrue("error keys not found: " + expectedErrors, expectedErrors.isEmpty());
        }
    }
}
