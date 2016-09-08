package de.chrgroth.generictypesystem.validation.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import de.chrgroth.generictypesystem.TestUtils;
import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericAttributeType;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericStructure;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.validation.ValidationService;
import de.chrgroth.generictypesystem.validation.impl.DefaultValidationService;

@RunWith(Parameterized.class)
public class DefaultValidationServiceItemValueTypeTest {

    private static List<GenericAttributeType> untestedTypes;

    @BeforeClass
    public static void setup() {
        untestedTypes = new ArrayList<>(Arrays.asList(GenericAttributeType.values()));
    }

    @AfterClass
    public static void assertAllTypesTested() {
        Assert.assertTrue("uncovered attribute type tests for : " + untestedTypes, untestedTypes.isEmpty());
    }

    public static class TestData {
        GenericAttributeType type;
        GenericAttributeType keyType;
        GenericAttributeType valueType;
        boolean mandatory;
        Double min;
        Double max;
        String pattern;
        Object value;
        boolean valid;

        public TestData(GenericAttributeType type, GenericAttributeType keyType, GenericAttributeType valueType, boolean mandatory, Object value, boolean valid) {
            this(type, keyType, valueType, mandatory, null, null, null, value, valid);
        }

        public TestData(GenericAttributeType type, GenericAttributeType keyType, GenericAttributeType valueType, boolean mandatory, Double min, Double max, String pattern,
                Object value, boolean valid) {
            this.type = type;
            this.keyType = keyType;
            this.valueType = valueType;
            this.mandatory = mandatory;
            this.min = min;
            this.max = max;
            this.pattern = pattern;
            this.value = value;
            this.valid = valid;
        }

        public boolean isStructure() {
            return type == GenericAttributeType.STRUCTURE || valueType == GenericAttributeType.STRUCTURE;
        }

        @Override
        public String toString() {
            return "TestData " + type + (keyType != null ? " k=" + keyType : "") + (valueType != null ? " v=" + valueType : "") + (value != null ? " " + value : "");
        }
    }

    @Parameters(name = "{0}")
    public static Iterable<TestData> data() {
        List<TestData> testdata = new ArrayList<>();

        // optional
        testdata.add(new TestData(GenericAttributeType.STRING, null, null, false, null, true));
        testdata.add(new TestData(GenericAttributeType.STRING, null, null, false, "", true));
        testdata.add(new TestData(GenericAttributeType.STRING, null, null, false, " ", true));
        testdata.add(new TestData(GenericAttributeType.STRING, null, null, false, "foo", true));
        testdata.add(new TestData(GenericAttributeType.STRING, null, null, false, 3.0d, null, null, "fo", false));
        testdata.add(new TestData(GenericAttributeType.STRING, null, null, false, 3.0d, null, null, "foo", true));
        testdata.add(new TestData(GenericAttributeType.STRING, null, null, false, null, 3.0d, null, "foo", true));
        testdata.add(new TestData(GenericAttributeType.STRING, null, null, false, null, 3.0d, null, "foobar", false));
        testdata.add(new TestData(GenericAttributeType.STRING, null, null, false, null, null, "-.*-", "foobar", false));
        testdata.add(new TestData(GenericAttributeType.STRING, null, null, false, null, null, "-.*-", "-foobar", false));
        testdata.add(new TestData(GenericAttributeType.STRING, null, null, false, null, null, "-.*-", "-foobar-", true));
        testdata.add(new TestData(GenericAttributeType.STRING, null, null, false, 1, false));
        testdata.add(new TestData(GenericAttributeType.LONG, null, null, false, null, true));
        testdata.add(new TestData(GenericAttributeType.LONG, null, null, false, 1, true));
        testdata.add(new TestData(GenericAttributeType.LONG, null, null, false, 1l, true));
        testdata.add(new TestData(GenericAttributeType.LONG, null, null, false, 1.0d, null, null, 0l, false));
        testdata.add(new TestData(GenericAttributeType.LONG, null, null, false, 1.0d, null, null, 1l, true));
        testdata.add(new TestData(GenericAttributeType.LONG, null, null, false, null, 1.0d, null, 1l, true));
        testdata.add(new TestData(GenericAttributeType.LONG, null, null, false, null, 1.0d, null, 2l, false));
        testdata.add(new TestData(GenericAttributeType.LONG, null, null, false, 1.0, false));
        testdata.add(new TestData(GenericAttributeType.LONG, null, null, false, 1.0d, false));
        testdata.add(new TestData(GenericAttributeType.LONG, null, null, false, "foo", false));
        testdata.add(new TestData(GenericAttributeType.DOUBLE, null, null, false, null, true));
        testdata.add(new TestData(GenericAttributeType.DOUBLE, null, null, false, 1, true));
        testdata.add(new TestData(GenericAttributeType.DOUBLE, null, null, false, 1l, true));
        testdata.add(new TestData(GenericAttributeType.DOUBLE, null, null, false, 1.0, true));
        testdata.add(new TestData(GenericAttributeType.DOUBLE, null, null, false, 1.0d, true));
        testdata.add(new TestData(GenericAttributeType.DOUBLE, null, null, false, 1.0d, null, null, 0.99d, false));
        testdata.add(new TestData(GenericAttributeType.DOUBLE, null, null, false, 1.0d, null, null, 1.0d, true));
        testdata.add(new TestData(GenericAttributeType.DOUBLE, null, null, false, null, 1.0d, null, 1.0d, true));
        testdata.add(new TestData(GenericAttributeType.DOUBLE, null, null, false, null, 1.0d, null, 1.01d, false));
        testdata.add(new TestData(GenericAttributeType.DOUBLE, null, null, false, "foo", false));
        testdata.add(new TestData(GenericAttributeType.BOOLEAN, null, null, false, null, true));
        testdata.add(new TestData(GenericAttributeType.BOOLEAN, null, null, false, false, true));
        testdata.add(new TestData(GenericAttributeType.BOOLEAN, null, null, false, true, true));
        testdata.add(new TestData(GenericAttributeType.BOOLEAN, null, null, false, 1, false));
        testdata.add(new TestData(GenericAttributeType.BOOLEAN, null, null, false, "foof", false));
        testdata.add(new TestData(GenericAttributeType.DATE, null, null, false, null, true));
        testdata.add(new TestData(GenericAttributeType.DATE, null, null, false, "foo", true));
        testdata.add(new TestData(GenericAttributeType.DATE, null, null, false, 1, false));
        testdata.add(new TestData(GenericAttributeType.DATE, null, null, false, new Date(), false));
        testdata.add(new TestData(GenericAttributeType.DATE, null, null, false, LocalDateTime.now(), false));
        testdata.add(new TestData(GenericAttributeType.TIME, null, null, false, null, true));
        testdata.add(new TestData(GenericAttributeType.TIME, null, null, false, "foo", true));
        testdata.add(new TestData(GenericAttributeType.TIME, null, null, false, 1, false));
        testdata.add(new TestData(GenericAttributeType.TIME, null, null, false, new Date(), false));
        testdata.add(new TestData(GenericAttributeType.TIME, null, null, false, LocalDateTime.now(), false));
        testdata.add(new TestData(GenericAttributeType.DATETIME, null, null, false, null, true));
        testdata.add(new TestData(GenericAttributeType.DATETIME, null, null, false, "foo", true));
        testdata.add(new TestData(GenericAttributeType.DATETIME, null, null, false, 1, false));
        testdata.add(new TestData(GenericAttributeType.DATETIME, null, null, false, new Date(), false));
        testdata.add(new TestData(GenericAttributeType.DATETIME, null, null, false, LocalDateTime.now(), false));
        testdata.add(new TestData(GenericAttributeType.LIST, null, GenericAttributeType.STRING, false, null, true));
        testdata.add(new TestData(GenericAttributeType.LIST, null, GenericAttributeType.STRING, false, Arrays.asList(), true));
        testdata.add(new TestData(GenericAttributeType.LIST, null, GenericAttributeType.STRING, false, Arrays.asList("foo"), true));
        testdata.add(new TestData(GenericAttributeType.LIST, null, GenericAttributeType.STRING, false, Arrays.asList(1), false));
        testdata.add(new TestData(GenericAttributeType.LIST, null, GenericAttributeType.STRING, false, Arrays.asList(new GenericItem()), false));
        testdata.add(new TestData(GenericAttributeType.LIST, null, GenericAttributeType.STRUCTURE, false, null, true));
        testdata.add(new TestData(GenericAttributeType.LIST, null, GenericAttributeType.STRUCTURE, false, Arrays.asList(), true));
        testdata.add(new TestData(GenericAttributeType.LIST, null, GenericAttributeType.STRUCTURE, false, Arrays.asList("foo"), false));
        testdata.add(new TestData(GenericAttributeType.LIST, null, GenericAttributeType.STRUCTURE, false, Arrays.asList(1), false));
        testdata.add(new TestData(GenericAttributeType.LIST, null, GenericAttributeType.STRUCTURE, false, Arrays.asList(new GenericItem()), true));
        testdata.add(new TestData(GenericAttributeType.STRUCTURE, null, null, false, null, true));
        testdata.add(new TestData(GenericAttributeType.STRUCTURE, null, null, false, "foo", false));
        testdata.add(new TestData(GenericAttributeType.STRUCTURE, null, null, false, new GenericItem(), true));

        // mandatory
        testdata.add(new TestData(GenericAttributeType.STRING, null, null, true, null, false));
        testdata.add(new TestData(GenericAttributeType.STRING, null, null, true, "", false));
        testdata.add(new TestData(GenericAttributeType.STRING, null, null, true, " ", false));
        testdata.add(new TestData(GenericAttributeType.STRING, null, null, true, "foo", true));
        testdata.add(new TestData(GenericAttributeType.STRING, null, null, true, 3.0d, null, null, "fo", false));
        testdata.add(new TestData(GenericAttributeType.STRING, null, null, true, 3.0d, null, null, "foo", true));
        testdata.add(new TestData(GenericAttributeType.STRING, null, null, true, null, 3.0d, null, "foo", true));
        testdata.add(new TestData(GenericAttributeType.STRING, null, null, true, null, 3.0d, null, "foobar", false));
        testdata.add(new TestData(GenericAttributeType.STRING, null, null, true, null, null, "-.*-", "foobar", false));
        testdata.add(new TestData(GenericAttributeType.STRING, null, null, true, null, null, "-.*-", "-foobar", false));
        testdata.add(new TestData(GenericAttributeType.STRING, null, null, true, null, null, "-.*-", "-foobar-", true));
        testdata.add(new TestData(GenericAttributeType.STRING, null, null, true, 1, false));
        testdata.add(new TestData(GenericAttributeType.LONG, null, null, true, null, false));
        testdata.add(new TestData(GenericAttributeType.LONG, null, null, true, 1, true));
        testdata.add(new TestData(GenericAttributeType.LONG, null, null, true, 1l, true));
        testdata.add(new TestData(GenericAttributeType.LONG, null, null, true, 1.0d, null, null, 0l, false));
        testdata.add(new TestData(GenericAttributeType.LONG, null, null, true, 1.0d, null, null, 1l, true));
        testdata.add(new TestData(GenericAttributeType.LONG, null, null, true, null, 1.0d, null, 1l, true));
        testdata.add(new TestData(GenericAttributeType.LONG, null, null, true, null, 1.0d, null, 2l, false));
        testdata.add(new TestData(GenericAttributeType.LONG, null, null, true, 1.0, false));
        testdata.add(new TestData(GenericAttributeType.LONG, null, null, true, 1.0d, false));
        testdata.add(new TestData(GenericAttributeType.LONG, null, null, true, "foo", false));
        testdata.add(new TestData(GenericAttributeType.DOUBLE, null, null, true, null, false));
        testdata.add(new TestData(GenericAttributeType.DOUBLE, null, null, true, 1, true));
        testdata.add(new TestData(GenericAttributeType.DOUBLE, null, null, true, 1l, true));
        testdata.add(new TestData(GenericAttributeType.DOUBLE, null, null, true, 1.0, true));
        testdata.add(new TestData(GenericAttributeType.DOUBLE, null, null, true, 1.0d, true));
        testdata.add(new TestData(GenericAttributeType.DOUBLE, null, null, true, 1.0d, null, null, 0.99d, false));
        testdata.add(new TestData(GenericAttributeType.DOUBLE, null, null, true, 1.0d, null, null, 1.0d, true));
        testdata.add(new TestData(GenericAttributeType.DOUBLE, null, null, true, null, 1.0d, null, 1.0d, true));
        testdata.add(new TestData(GenericAttributeType.DOUBLE, null, null, true, null, 1.0d, null, 1.01d, false));
        testdata.add(new TestData(GenericAttributeType.DOUBLE, null, null, true, "foo", false));
        testdata.add(new TestData(GenericAttributeType.BOOLEAN, null, null, true, null, false));
        testdata.add(new TestData(GenericAttributeType.BOOLEAN, null, null, true, false, true));
        testdata.add(new TestData(GenericAttributeType.BOOLEAN, null, null, true, true, true));
        testdata.add(new TestData(GenericAttributeType.BOOLEAN, null, null, true, 1, false));
        testdata.add(new TestData(GenericAttributeType.BOOLEAN, null, null, true, "foof", false));
        testdata.add(new TestData(GenericAttributeType.DATE, null, null, true, null, false));
        testdata.add(new TestData(GenericAttributeType.DATE, null, null, true, "foo", true));
        testdata.add(new TestData(GenericAttributeType.DATE, null, null, true, 1, false));
        testdata.add(new TestData(GenericAttributeType.DATE, null, null, true, new Date(), false));
        testdata.add(new TestData(GenericAttributeType.DATE, null, null, true, LocalDateTime.now(), false));
        testdata.add(new TestData(GenericAttributeType.TIME, null, null, true, null, false));
        testdata.add(new TestData(GenericAttributeType.TIME, null, null, true, "foo", true));
        testdata.add(new TestData(GenericAttributeType.TIME, null, null, true, 1, false));
        testdata.add(new TestData(GenericAttributeType.TIME, null, null, true, new Date(), false));
        testdata.add(new TestData(GenericAttributeType.TIME, null, null, true, LocalDateTime.now(), false));
        testdata.add(new TestData(GenericAttributeType.DATETIME, null, null, true, null, false));
        testdata.add(new TestData(GenericAttributeType.DATETIME, null, null, true, "foo", true));
        testdata.add(new TestData(GenericAttributeType.DATETIME, null, null, true, 1, false));
        testdata.add(new TestData(GenericAttributeType.DATETIME, null, null, true, new Date(), false));
        testdata.add(new TestData(GenericAttributeType.DATETIME, null, null, true, LocalDateTime.now(), false));
        testdata.add(new TestData(GenericAttributeType.LIST, null, GenericAttributeType.STRING, true, null, false));
        testdata.add(new TestData(GenericAttributeType.LIST, null, GenericAttributeType.STRING, true, Arrays.asList(), false));
        testdata.add(new TestData(GenericAttributeType.LIST, null, GenericAttributeType.STRING, true, Arrays.asList("foo"), true));
        testdata.add(new TestData(GenericAttributeType.LIST, null, GenericAttributeType.STRING, true, Arrays.asList(1), false));
        testdata.add(new TestData(GenericAttributeType.LIST, null, GenericAttributeType.STRING, true, Arrays.asList(new GenericItem()), false));
        testdata.add(new TestData(GenericAttributeType.LIST, null, GenericAttributeType.STRUCTURE, true, null, false));
        testdata.add(new TestData(GenericAttributeType.LIST, null, GenericAttributeType.STRUCTURE, true, Arrays.asList(), false));
        testdata.add(new TestData(GenericAttributeType.LIST, null, GenericAttributeType.STRUCTURE, true, Arrays.asList("foo"), false));
        testdata.add(new TestData(GenericAttributeType.LIST, null, GenericAttributeType.STRUCTURE, true, Arrays.asList(1), false));
        testdata.add(new TestData(GenericAttributeType.LIST, null, GenericAttributeType.STRUCTURE, true, Arrays.asList(new GenericItem()), true));
        testdata.add(new TestData(GenericAttributeType.STRUCTURE, null, null, true, null, false));
        testdata.add(new TestData(GenericAttributeType.STRUCTURE, null, null, true, "foo", false));
        testdata.add(new TestData(GenericAttributeType.STRUCTURE, null, null, true, new GenericItem(), true));

        // done
        return testdata;
    }

    @Parameter
    public TestData testData;

    private ValidationService service = new DefaultValidationService(null);
    private GenericType type = new GenericType(0l, 0, "testType", "testGroup", null);
    private GenericItem item = new GenericItem(0l, type.getId(), null);

    @Test
    public void assertTestData() {
        untestedTypes.remove(testData.type);

        attribute(testData.type, testData.keyType, testData.valueType, testData.mandatory, testData.isStructure() ? new GenericStructure() : null, testData.min, testData.max,
                testData.pattern);
        value(testData.value);

        if (testData.valid) {
            TestUtils.expectValidItem(service, type, item);
        } else {
            TestUtils.expectInvalidItem(service, type, item);
        }
    }

    private <T, K, V> void attribute(GenericAttributeType type, GenericAttributeType keyType, GenericAttributeType valueType, boolean mandatory, GenericStructure structure,
            Double min, Double max, String pattern) {
        this.type.getAttributes().add(new GenericAttribute(0l, 0, "name", type, keyType, valueType, false, false, mandatory, structure, min, max, null, pattern));
    }

    private void value(Object value) {
        item.set("name", value);
    }
}
