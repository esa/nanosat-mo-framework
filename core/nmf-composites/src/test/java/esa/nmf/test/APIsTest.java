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
import esa.mo.nmf.NMFProvider;
import java.io.IOException;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.common.CommonHelper;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.platform.PlatformHelper;
import org.ccsds.moims.mo.softwaremanagement.SoftwareManagementHelper;
import org.junit.Test;

/**
 * Checks the auto-generated APIs
 */
public class APIsTest {

    @Test
    public void test0() throws IOException {
        // Check if the MO Area versions match the version of the Java code
        int version = NMFProvider.getMajorVersionNMF();

        org.junit.Assert.assertEquals(version, COMHelper.COM_AREA.getVersion().getValue());
        org.junit.Assert.assertEquals(version, CommonHelper.COMMON_AREA.getVersion().getValue());
        org.junit.Assert.assertEquals(version, MCHelper.MC_AREA.getVersion().getValue());
        org.junit.Assert.assertEquals(version, PlatformHelper.PLATFORM_AREA.getVersion().getValue());
        org.junit.Assert.assertEquals(version, SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA.getVersion().getValue());
    }
}
