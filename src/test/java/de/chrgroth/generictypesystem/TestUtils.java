package de.chrgroth.generictypesystem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.validation.ValidationResult;
import de.chrgroth.generictypesystem.validation.ValidationService;

// TODO clean up
// TODO clean up tests: use before instead of constructor
@Deprecated
public final class TestUtils {

    private TestUtils() {
        // utility class
    }

    public static Map<String, Object> buildStringKeyMap(Object... values) {
        return buildMap(String.class, true, values);
    }

    public static Map<Object, Object> buildMap(Object... values) {
        return buildMap(Object.class, false, values);
    }

    @SuppressWarnings("unchecked")
    private static <T> Map<T, Object> buildMap(Class<T> keyType, boolean stringKey, Object... values) {

        // null guard
        if (values == null || values.length == 0) {
            return null;
        }

        // even number of arguments
        if (values.length % 2 != 0) {
            throw new IllegalArgumentException("invalid map values: " + Arrays.toString(values));
        }

        // build map
        Map<T, Object> resultMap = new HashMap<>();;
        for (int i = 0; i < values.length; i = i + 2) {
            resultMap.put(stringKey ? (T) values[i].toString() : (T) values[i], values[i + 1]);
        }

        // done
        return resultMap;
    }

    public static void expectValidType(ValidationService service, GenericType type) {
        ValidationResult result = validateType(service, type);
        Assert.assertTrue("got errors: " + result.getErrors(), result.isValid());
    }

    public static void expectInvalidType(ValidationService service, GenericType type) {
        Assert.assertFalse("got no errors", validateType(service, type).isValid());
    }

    public static ValidationResult validateType(ValidationService service, GenericType type) {
        ValidationResult result = service.validate(type);
        Assert.assertEquals(type, result.getItem());
        return result;
    }

    public static void expectValidItem(ValidationService service, GenericType type, GenericItem item) {
        ValidationResult result = validateItem(service, type, item);
        Assert.assertTrue("got errors: " + result.getErrors(), result.isValid());
    }

    public static void expectInvalidItem(ValidationService service, GenericType type, GenericItem item) {
        Assert.assertFalse("got no errors", validateItem(service, type, item).isValid());
    }

    public static ValidationResult validateItem(ValidationService service, GenericType type, GenericItem item) {
        ValidationResult result = service.validate(type, item);
        Assert.assertEquals(item, result.getItem());
        return result;
    }
}
