package esa.nmf.test;

/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 */
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MCServicesProviderNMF;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.SimpleMonitorAndControlAdapter;
import esa.mo.nmf.annotations.Parameter;
import esa.mo.nmf.mcadapters.CompositeMCAdapter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mc.structures.ParameterDefinitionList;
import org.ccsds.moims.mo.mc.structures.ParameterRawValue;
import org.ccsds.moims.mo.mc.structures.ParameterRawValueList;
import org.junit.Test;

/**
 *
 * @author Kevin Otto
 */
public class ParameterTest {

    private final static long ID1 = 0;
    private final static long ID2 = 1;
    private final static long ID3 = 65;
    private final static long ID4 = 9555;
    private final static long ID5 = 8;

    private final static Identifier NAME1 = new Identifier("private");
    private final static Identifier NAME2 = new Identifier("float Parameter");
    private final static Identifier NAME3 = new Identifier("parameter Without Initial Value");
    private final static Identifier NAME4 = new Identifier("stringParameter");
    private final static Identifier NAME5 = new Identifier("Parameter with onGet");

    private static class AutomationAdapterTester extends MonitorAndControlNMFAdapter {

        @Parameter(name = "private")
        private int privateParameter = 0;

        @Parameter(name = "float Parameter")
        public float floatParameter = 0.5f;

        @Parameter(name = "parameter Without Initial Value")
        public double parameterWithoutInitialValue;

        @Parameter()
        public String stringParameter = "some String parameter without a name";

        @Parameter(name = "Parameter with onGet", onGetFunction = "getTest")
        public String onGetParameter = "some String";

        public void getTest() {
            onGetParameter = "new";
        }

        public int getPrivateParameter() {
            return privateParameter;
        }

        private void testAction() {

        }

        private void testActionWithParam(int someInt, double someDouble) {

        }

    }

    private static class RegistrationTester extends MCRegistration {

        public RegistrationTester() {
            super(new MCServicesProviderNMF());
        }

        @Override
        public LongList registerParameters(final ParameterDefinitionList definitions) {
            LongList ids = new LongList(definitions.size());
            ids.add(ID1);
            ids.add(ID2);
            ids.add(ID3);
            ids.add(ID4);
            ids.add(ID5);
            return ids;
        }

    }

    @Test
    public void test0() throws IOException {
        AutomationAdapterTester test = new AutomationAdapterTester();
        test.initialRegistrations(new RegistrationTester());

        IdentifierList names = new IdentifierList();
        ParameterRawValueList setList = new ParameterRawValueList();
        Object attribute = Attribute.javaType2Attribute(42);
        names.add(NAME1);
        setList.add(new ParameterRawValue(ID1, (Attribute) attribute));
        attribute = Attribute.javaType2Attribute(0.8f);
        names.add(NAME2);
        setList.add(new ParameterRawValue(ID2, (Attribute) attribute));
        attribute = Attribute.javaType2Attribute(.99999);
        names.add(NAME3);
        setList.add(new ParameterRawValue(ID3, (Attribute) attribute));
        attribute = Attribute.javaType2Attribute("Hallo");
        names.add(NAME4);
        setList.add(new ParameterRawValue(ID4, (Attribute) attribute));
        org.junit.Assert.assertTrue("Every value must be set", test.onSetValue(names, setList));

        Object v1 = test.onGetValue(NAME1).attribute2JavaType();
        Object v2 = test.onGetValue(NAME2).attribute2JavaType();
        Object v3 = test.onGetValue(NAME3).attribute2JavaType();
        Object v4 = test.onGetValue(NAME4).attribute2JavaType();
        Object v5 = test.onGetValue(NAME5).attribute2JavaType();

        org.junit.Assert.assertTrue("'" + v1 + "' != '" + 42 + "'", v1.equals(42));
        org.junit.Assert.assertTrue("'" + v2 + "' != '" + 0.8f + "'", v2.equals(0.8f));
        org.junit.Assert.assertTrue("'" + v3 + "' != '" + .99999 + "'", v3.equals(.99999));
        org.junit.Assert.assertTrue("'" + v4 + "' != '" + "Hallo" + "'", v4.equals("Hallo"));
        org.junit.Assert.assertTrue("'" + v5 + "' != '" + "new" + "'", v5.equals("new"));
    }

    /**
     * A value that cannot be set does not stop the values after it.
     */
    @Test
    public void annotationsSetTheValuesAfterOneThatCannotBeSet() throws IOException {
        AutomationAdapterTester test = new AutomationAdapterTester();
        test.initialRegistrations(new RegistrationTester());

        IdentifierList names = new IdentifierList();
        names.add(new Identifier("no such parameter"));
        names.add(NAME1);
        ParameterRawValueList values = new ParameterRawValueList();
        values.add(new ParameterRawValue(99L, (Attribute) Attribute.javaType2Attribute(1)));
        values.add(new ParameterRawValue(ID1, (Attribute) Attribute.javaType2Attribute(7)));

        org.junit.Assert.assertFalse("An unknown name must be reported", test.onSetValue(names, values));
        org.junit.Assert.assertEquals("The value after it must still be set", 7, test.getPrivateParameter());
    }

    /**
     * A simple adapter that records the values set on it.
     */
    private static class RecordingSimpleAdapter extends SimpleMonitorAndControlAdapter {

        private final Map<String, Serializable> set = new HashMap<>();

        @Override
        public boolean actionArrivedSimple(String name, Serializable[] values, Long executionId) {
            return false;
        }

        @Override
        public Serializable onGetValueSimple(String name) {
            return set.get(name);
        }

        @Override
        public boolean onSetValueSimple(String name, Serializable value) {
            set.put(name, value);
            return true;
        }
    }

    /**
     * The simple adapter sets every value of a setValue, not only the first.
     */
    @Test
    public void simpleAdapterSetsEveryValue() {
        RecordingSimpleAdapter adapter = new RecordingSimpleAdapter();

        IdentifierList names = new IdentifierList();
        names.add(new Identifier("a"));
        names.add(new Identifier("b"));
        ParameterRawValueList values = new ParameterRawValueList();
        values.add(new ParameterRawValue(1L, (Attribute) Attribute.javaType2Attribute(1)));
        values.add(new ParameterRawValue(2L, (Attribute) Attribute.javaType2Attribute(2)));

        org.junit.Assert.assertTrue("Every value must be set", adapter.onSetValue(names, values));
        org.junit.Assert.assertEquals(1, adapter.set.get("a"));
        org.junit.Assert.assertEquals(2, adapter.set.get("b"));
    }

    /**
     * The composite of the Supervisor passes a setValue to each of its
     * adapters, also after an adapter that has none of the parameters, so
     * that it reaches the adapter of the mission.
     */
    @Test
    public void compositeReachesEveryAdapter() {
        RecordingSimpleAdapter mission = new RecordingSimpleAdapter();
        CompositeMCAdapter composite = new CompositeMCAdapter(Arrays.asList(new AutomationAdapterTester(), mission));

        IdentifierList names = new IdentifierList();
        names.add(new Identifier("mission parameter"));
        ParameterRawValueList values = new ParameterRawValueList();
        values.add(new ParameterRawValue(1L, (Attribute) Attribute.javaType2Attribute(5)));

        org.junit.Assert.assertTrue("The value must be set", composite.onSetValue(names, values));
        org.junit.Assert.assertEquals("The value must reach the adapter of the mission",
                5, mission.set.get("mission parameter"));
    }

    /**
     * The composite reports a value that none of its adapters set, also when
     * another value of the same call was set.
     */
    @Test
    public void compositeReportsAValueThatNoAdapterSet() {
        CompositeMCAdapter composite = new CompositeMCAdapter(Arrays.asList(new AutomationAdapterTester()));
        composite.initialRegistrations(new RegistrationTester());

        IdentifierList names = new IdentifierList();
        names.add(NAME1);
        names.add(new Identifier("no such parameter"));
        ParameterRawValueList values = new ParameterRawValueList();
        values.add(new ParameterRawValue(ID1, (Attribute) Attribute.javaType2Attribute(3)));
        values.add(new ParameterRawValue(99L, (Attribute) Attribute.javaType2Attribute(4)));

        org.junit.Assert.assertFalse("A value that no adapter set must be reported",
                composite.onSetValue(names, values));
    }

    private static class NoValueAdapter extends MonitorAndControlNMFAdapter {

        @Parameter()
        public String noValue;
    }

    private static class NotMalTypeAdapter extends MonitorAndControlNMFAdapter {

        @Parameter()
        public Object notMalType = new Object();
    }

    private static class UnknownMalTypeAdapter extends MonitorAndControlNMFAdapter {

        @Parameter(malType = "Strnig")
        public String misspelled = "value";
    }

    /**
     * A parameter whose MAL type cannot be determined stops the registration
     * with an error that names the field, instead of a bare exception.
     */
    @Test
    public void undeterminableTypeNamesTheField() {
        assertRegistrationFails(new NoValueAdapter(), "noValue");
        assertRegistrationFails(new NotMalTypeAdapter(), "notMalType");
        assertRegistrationFails(new UnknownMalTypeAdapter(), "misspelled");
    }

    private static void assertRegistrationFails(MonitorAndControlNMFAdapter adapter, String fieldName) {
        try {
            adapter.initialRegistrations(new RegistrationTester());
            org.junit.Assert.fail("The registration of '" + fieldName + "' should have failed");
        } catch (IllegalStateException ex) {
            org.junit.Assert.assertTrue("The message does not name the field: " + ex.getMessage(),
                    ex.getMessage().contains("'" + fieldName + "'"));
        }
    }
}
