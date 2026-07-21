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
package esa.mo.nmf.filesystem;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import org.junit.Test;

/**
 * Tests for the {@link Mission} configuration object and the content it
 * produces for the {@code mission.properties} file.
 *
 * @author Cesar Coelho
 */
public class MissionTest {

    private static Mission mission(String name, String spacecraftName, Integer node,
            String scid, String orgAbbreviation, String orgName) throws Exception {
        Mission m = new Mission();
        set(m, "missionName", name);
        set(m, "spacecraftName", spacecraftName);
        set(m, "spacecraftNode", node);
        set(m, "spacecraftScid", scid);
        set(m, "organizationAbbreviation", orgAbbreviation);
        set(m, "organizationName", orgName);
        return m;
    }

    private static void set(Mission m, String field, Object value) throws Exception {
        Field f = Mission.class.getDeclaredField(field);
        f.setAccessible(true);
        f.set(m, value);
    }

    @Test
    public void fullFieldSetProducesAllLines() throws Exception {
        Mission m = mission("Novaradar", "Novaradar-X94", 94, "0x0140", "Novaradar", "Novaradar Ltd");
        String content = m.toPropertiesContent();

        assertTrue(content.contains("mission.name=Novaradar\n"));
        assertTrue(content.contains("spacecraft.name=Novaradar-X94\n"));
        assertTrue(content.contains("spacecraft.node=94\n"));
        assertTrue(content.contains("spacecraft.scid=0x0140\n"));
        assertTrue(content.contains("organization.abbreviation=Novaradar\n"));
        assertTrue(content.contains("organization.name=Novaradar Ltd\n"));
    }

    @Test
    public void optionalFieldsAreOmittedWhenAbsent() throws Exception {
        Mission m = mission("Phi-Sat-2", "Phi-Sat-2", 1, null, "ESA", null);
        String content = m.toPropertiesContent();

        assertFalse("scid line must be absent", content.contains("spacecraft.scid"));
        assertFalse("organization.name line must be absent", content.contains("organization.name"));
        assertTrue(content.contains("organization.abbreviation=ESA\n"));
    }

    @Test
    public void spacecraftNodeDefaultsToOne() throws Exception {
        Mission m = mission("OPS-SAT", "OPS-SAT", null, "0x032A", "ESA", "European Space Agency");
        String content = m.toPropertiesContent();

        assertTrue(content.contains("spacecraft.node=1\n"));
    }

    @Test
    public void checkRequiredFieldsPassesWhenPresent() throws Exception {
        Mission m = mission("Sentinel-1", "Sentinel-1B", 2, null, "ESA", "European Space Agency");
        m.checkRequiredFields(); // must not throw
    }

    @Test
    public void checkRequiredFieldsReportsAllMissing() throws Exception {
        Mission m = mission(null, "  ", 2, "0x0001", null, "Some Org");
        try {
            m.checkRequiredFields();
            fail("expected an IllegalArgumentException for the missing required fields");
        } catch (IllegalArgumentException ex) {
            String msg = ex.getMessage();
            assertTrue(msg.contains("missionName"));
            assertTrue(msg.contains("spacecraftName"));
            assertTrue(msg.contains("organizationAbbreviation"));
        }
    }
}
