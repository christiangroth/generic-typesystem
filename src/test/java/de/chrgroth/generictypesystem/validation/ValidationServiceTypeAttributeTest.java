package de.chrgroth.generictypesystem.validation;

import java.util.ArrayList;
import java.util.Arrays;
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
import de.chrgroth.generictypesystem.model.GenericStructure;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.validation.impl.DefaultValidationService;

@RunWith(Parameterized.class)
public class ValidationServiceTypeAttributeTest {

    private static final String ATTRIBUTE_NAME = "dummy";
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
        List<GenericAttributeType> keyTypes;
        List<GenericAttributeType> valueTypes;
        boolean structured;
        boolean minAllowed;
        boolean maxAllowed;
        boolean stepAllowed;
        boolean patternAllowed;

        public TestData(GenericAttributeType type, List<GenericAttributeType> keyTypes, List<GenericAttributeType> valueTypes, boolean structured, boolean minAllowed,
                boolean maxAllowed, boolean stepAllowed, boolean patternAllowed) {
            this.type = type;
            this.keyTypes = keyTypes;
            this.valueTypes = valueTypes;
            this.structured = structured;
            this.minAllowed = minAllowed;
            this.maxAllowed = maxAllowed;
            this.stepAllowed = stepAllowed;
            this.patternAllowed = patternAllowed;
        }

        @Override
        public String toString() {
            return "TestData " + type;
        }
    }

    @Parameters(name = "{0}")
    public static Iterable<TestData> data() {
        List<TestData> testdata = new ArrayList<>();
        testdata.add(new TestData(GenericAttributeType.STRING, null, null, false, true, true, false, true));
        testdata.add(new TestData(GenericAttributeType.LONG, null, null, false, true, true, true, false));
        testdata.add(new TestData(GenericAttributeType.DOUBLE, null, null, false, true, true, true, false));
        testdata.add(new TestData(GenericAttributeType.BOOLEAN, null, null, false, false, false, false, false));
        testdata.add(new TestData(GenericAttributeType.DATE, null, null, false, false, false, false, false));
        testdata.add(new TestData(GenericAttributeType.TIME, null, null, false, false, false, true, false));
        testdata.add(new TestData(GenericAttributeType.DATETIME, null, null, false, false, false, true, false));
        testdata.add(new TestData(GenericAttributeType.LIST, null, Arrays.asList(GenericAttributeType.STRING, GenericAttributeType.LONG, GenericAttributeType.DOUBLE,
                GenericAttributeType.BOOLEAN, GenericAttributeType.DATE, GenericAttributeType.STRUCTURE), true, false, false, false, false));
        testdata.add(new TestData(GenericAttributeType.STRUCTURE, null, null, true, false, false, false, false));
        return testdata;
    }

    @Parameter
    public TestData testData;

    private ValidationService service = new DefaultValidationService(null);
    private GenericType type = new GenericType(0l, 0, "testType", "testGroup", null);

    @Test
    public void typeChecks() {

        // null guards
        List<GenericAttributeType> keyTypes = testData.keyTypes == null ? new ArrayList<>() : testData.keyTypes;
        List<GenericAttributeType> valueTypes = testData.valueTypes == null ? new ArrayList<>() : testData.valueTypes;

        // type only check
        expect(new GenericAttribute(0l, 0, ATTRIBUTE_NAME, testData.type, null, null, false, false, false, null),
                keyTypes.isEmpty() && valueTypes.isEmpty() && !testData.structured);

        // key and value type checks
        for (GenericAttributeType keyType : GenericAttributeType.values()) {
            for (GenericAttributeType valueType : GenericAttributeType.values()) {
                boolean needStructure = valueType == GenericAttributeType.STRUCTURE && testData.structured;

                expect(new GenericAttribute(0l, 0, ATTRIBUTE_NAME, testData.type, keyType, null, false, false, false, null),
                        keyTypes.contains(keyType) && valueTypes.isEmpty() && !needStructure);
                expect(new GenericAttribute(0l, 0, ATTRIBUTE_NAME, testData.type, null, valueType, false, false, false, null),
                        keyTypes.isEmpty() && valueTypes.contains(valueType) && !needStructure);
                expect(new GenericAttribute(0l, 0, ATTRIBUTE_NAME, testData.type, keyType, valueType, false, false, false, null),
                        keyTypes.contains(keyType) && valueTypes.contains(valueType) && !needStructure);

                if (valueType == GenericAttributeType.STRUCTURE) {
                    expect(new GenericAttribute(0l, 0, ATTRIBUTE_NAME, testData.type, keyType, null, false, false, false, new GenericStructure()),
                            keyTypes.contains(keyType) && valueTypes.isEmpty() && testData.structured);
                    expect(new GenericAttribute(0l, 0, ATTRIBUTE_NAME, testData.type, null, valueType, false, false, false, new GenericStructure()),
                            keyTypes.isEmpty() && valueTypes.contains(valueType) && testData.structured);
                    expect(new GenericAttribute(0l, 0, ATTRIBUTE_NAME, testData.type, keyType, valueType, false, false, false, new GenericStructure()),
                            keyTypes.contains(keyType) && valueTypes.contains(valueType) && testData.structured);
                }
            }
        }

        // remoev from global list of types to test
        untestedTypes.remove(testData.type);
    }

    @Test
    public void minMaxChecks() {

        expect(new GenericAttribute(0l, 0, ATTRIBUTE_NAME, testData.type, null, null, false, false, false, null, 0.0, null, null, null), testData.minAllowed);
        expect(new GenericAttribute(0l, 0, ATTRIBUTE_NAME, testData.type, null, null, false, false, false, null, null, 0.0, null, null), testData.maxAllowed);

        if (testData.minAllowed && testData.maxAllowed) {
            expect(new GenericAttribute(0l, 0, ATTRIBUTE_NAME, testData.type, null, null, false, false, false, null, 0.0, 10.0, null, null), true);
            expect(new GenericAttribute(0l, 0, ATTRIBUTE_NAME, testData.type, null, null, false, false, false, null, 10.0, 10.0, null, null), false);
            expect(new GenericAttribute(0l, 0, ATTRIBUTE_NAME, testData.type, null, null, false, false, false, null, 10.0, 0.0, null, null), false);
        }
    }

    @Test
    public void stepChecks() {
        expect(new GenericAttribute(0l, 0, ATTRIBUTE_NAME, testData.type, null, null, false, false, false, null, null, null, 1.0, null), testData.stepAllowed);
        expect(new GenericAttribute(0l, 0, ATTRIBUTE_NAME, testData.type, null, null, false, false, false, null, null, null, 0.1, null), testData.stepAllowed);
        expect(new GenericAttribute(0l, 0, ATTRIBUTE_NAME, testData.type, null, null, false, false, false, null, null, null, 0.0, null), false);
        expect(new GenericAttribute(0l, 0, ATTRIBUTE_NAME, testData.type, null, null, false, false, false, null, null, null, -1.0, null), false);
    }

    @Test
    public void patternCheck() {
        expect(new GenericAttribute(0l, 0, ATTRIBUTE_NAME, testData.type, null, null, false, false, false, null, null, null, null, "demo-pattern"), testData.patternAllowed);
    }

    // TODO test default values

    private void expect(GenericAttribute attribute, boolean expectValid) {
        type.getAttributes().add(attribute);
        if (expectValid) {
            TestUtils.expectValidType(service, type);
        } else {
            TestUtils.expectInvalidType(service, type);
        }
        type.getAttributes().clear();
    }
}
