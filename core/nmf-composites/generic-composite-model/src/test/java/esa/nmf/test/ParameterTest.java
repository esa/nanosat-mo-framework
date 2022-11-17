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
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.annotations.Parameter;
import java.io.IOException;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValue;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValueList;
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

    private static class automationAdapterTester extends MonitorAndControlNMFAdapter {

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

    private static class registrationTester extends MCRegistration {

        public registrationTester() {
            super(null, null, null, null, null);
        }

        @Override
        public LongList registerParameters(final IdentifierList names,
            final ParameterDefinitionDetailsList definitions) {
            LongList ids = new LongList(names.size());
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
        automationAdapterTester test = new automationAdapterTester();
        test.initialRegistrations(new registrationTester());

        ParameterRawValueList setList = new ParameterRawValueList();
        Object attribute = HelperAttributes.javaType2Attribute(42);
        setList.add(new ParameterRawValue(ID1, (Attribute) attribute));
        attribute = HelperAttributes.javaType2Attribute(0.8f);
        setList.add(new ParameterRawValue(ID2, (Attribute) attribute));
        attribute = HelperAttributes.javaType2Attribute(.99999);
        setList.add(new ParameterRawValue(ID3, (Attribute) attribute));
        attribute = HelperAttributes.javaType2Attribute("Hallo");
        setList.add(new ParameterRawValue(ID4, (Attribute) attribute));
        test.onSetValue(setList);

        Object v1 = HelperAttributes.attribute2JavaType(test.onGetValue(ID1));
        Object v2 = HelperAttributes.attribute2JavaType(test.onGetValue(ID2));
        Object v3 = HelperAttributes.attribute2JavaType(test.onGetValue(ID3));
        Object v4 = HelperAttributes.attribute2JavaType(test.onGetValue(ID4));
        Object v5 = HelperAttributes.attribute2JavaType(test.onGetValue(ID5));

        org.junit.Assert.assertTrue("'" + v1 + "' != '" + 42 + "'", v1.equals(42));
        org.junit.Assert.assertTrue("'" + v2 + "' != '" + 0.8f + "'", v2.equals(0.8f));
        org.junit.Assert.assertTrue("'" + v3 + "' != '" + .99999 + "'", v3.equals(.99999));
        org.junit.Assert.assertTrue("'" + v4 + "' != '" + "Hallo" + "'", v4.equals("Hallo"));
        org.junit.Assert.assertTrue("'" + v5 + "' != '" + "new" + "'", v5.equals("new"));

    }
}
