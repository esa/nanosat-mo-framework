/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
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
package esa.mo.mc.impl.provider;

import java.lang.reflect.Method;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mc.structures.ThresholdFilter;
import org.ccsds.moims.mo.mc.structures.ThresholdType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Holds that an aggregation with a percentage filter reports a parameter only
 * when its value has changed by more than the threshold since the previous
 * report (requirement: Aggregation#9), measured against the size of the
 * previous value, so that negative values are filtered as positive ones are.
 * Any change from zero exceeds every percentage, so it is reported.
 */
public class AggregationPercentageFilterTest {

    private static final ThresholdFilter TEN_PERCENT
            = new ThresholdFilter(ThresholdType.PERCENTAGE, (Attribute) Attribute.javaType2Attribute(10.0), true);

    @Test
    public void aPositiveValueIsReportedOnlyBeyondTheThreshold() throws Exception {
        assertReported(100.0, 120.0);
        assertReported(100.0, 80.0);
        assertReported(100.0, 0.0);
        assertNotReported(100.0, 105.0);
        assertNotReported(100.0, 95.0);
    }

    @Test
    public void aNegativeValueIsReportedOnlyBeyondTheThreshold() throws Exception {
        assertReported(-20.0, -40.0);
        assertReported(-20.0, -10.0);
        assertReported(-20.0, 30.0);
        assertNotReported(-20.0, -21.0);
        assertNotReported(-20.0, -19.0);
    }

    @Test
    public void anyChangeFromZeroIsReported() throws Exception {
        assertReported(0.0, 0.001);
        assertReported(0.0, -5.0);
        assertNotReported(0.0, 0.0);
    }

    private static void assertReported(double previous, double current) throws Exception {
        Assert.assertTrue("A change from " + previous + " to " + current + " must be reported",
                triggered(previous, current));
    }

    private static void assertNotReported(double previous, double current) throws Exception {
        Assert.assertFalse("A change from " + previous + " to " + current + " must not be reported",
                triggered(previous, current));
    }

    private static boolean triggered(double previous, double current) throws Exception {
        Method method = AggregationManager.class.getDeclaredMethod("triggeredFilter",
                Attribute.class, Attribute.class, ThresholdFilter.class);
        method.setAccessible(true);
        AggregationManager manager = new AggregationManager(null, null);
        return (Boolean) method.invoke(manager, Attribute.javaType2Attribute(previous),
                Attribute.javaType2Attribute(current), TEN_PERCENT);
    }
}
