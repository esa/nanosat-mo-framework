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
package esa.mo.nmf.accesscontrol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.ccsds.moims.mo.mal.AuthorisationFailException;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.NamedValueList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * NMF-2026-011: holds that the entries of the Supervisor's Directory belong to
 * their providers. Only an App may add or remove an entry, and only its own,
 * when access control is enforced; ground and the other Apps may not. The
 * entry that the Supervisor added for itself is refused to every caller in
 * every mode. Each refusal, and each call that audit lets through, is recorded
 * with the entry it names.
 */
public class DirectoryEntryOwnershipTest {

    private static final String SUPERVISOR = "maltcp://10.0.0.1:1024";
    private static final byte[] PROBE = NMFAccessControl.newCredential();
    private static final byte[] GROUND = NMFAccessControl.newCredential();

    private AccessControlAuthority authority;
    private final List<String> recorded = new ArrayList<>();

    @Before
    public void startTheAuthority() {
        authority = new AccessControlAuthority(SUPERVISOR + "/Directory");
        authority.setGroundCredential(GROUND);
        authority.registerApp("probe", PROBE, Collections.emptySet());
        authority.setRecorder((app, permission, operation, from, entry, refused) ->
                recorded.add(app + " " + permission + " " + operation + " " + entry + " refused=" + refused));
    }

    @After
    public void clearMode() {
        System.clearProperty(AccessControlMode.PROPERTY);
    }

    @Test
    public void anAppMayAddAndRemoveItsOwnEntry() throws Exception {
        mode("enforce");
        authority.checkDirectoryEntry(request(PROBE), "probe", "add", false);
        authority.checkDirectoryEntry(request(PROBE), "probe", "remove", false);
        Assert.assertTrue("Nothing is recorded for an App's own entry: " + recorded, recorded.isEmpty());
    }

    @Test
    public void enforceRefusesTheEntryOfAnotherApp() {
        mode("enforce");
        assertRefused("An App adding the entry of another App", request(PROBE), "other", "add", false);
        assertRefused("An App removing the entry of another App", request(PROBE), "other", "remove", false);
        Assert.assertEquals("[probe Supervisor.COM.Directory add other refused=true, "
                + "probe Supervisor.COM.Directory remove other refused=true]", recorded.toString());
    }

    @Test
    public void enforceRefusesGroundAndCallersWithoutCredentials() {
        mode("enforce");
        assertRefused("Ground adding the entry of an App", request(GROUND), "probe", "add", false);
        assertRefused("A caller without credentials removing the entry of an App", request(null), "probe",
                "remove", false);
        Assert.assertEquals(2, recorded.size());
    }

    @Test
    public void auditLetsTheEntryOfAnotherAppThroughButRecordsIt() throws Exception {
        mode("audit");
        authority.checkDirectoryEntry(request(PROBE), "other", "add", false);
        Assert.assertEquals("[probe Supervisor.COM.Directory add other refused=false]", recorded.toString());
    }

    @Test
    public void theSupervisorsOwnEntryIsRefusedInEveryMode() {
        for (String mode : new String[]{"enforce", "audit", "off"}) {
            mode(mode);
            assertRefused("In " + mode + " mode, an App adding the Supervisor's entry", request(PROBE),
                    "nanosat-mo-supervisor", "add", true);
            assertRefused("In " + mode + " mode, ground removing the Supervisor's entry", request(GROUND),
                    "nanosat-mo-supervisor", "remove", true);
        }
        Assert.assertEquals("Each refusal is recorded as refused: " + recorded, 6,
                recorded.stream().filter(r -> r.endsWith("nanosat-mo-supervisor refused=true")).count());
    }

    @Test
    public void offLetsTheEntryOfAnotherAppThrough() throws Exception {
        mode("off");
        authority.checkDirectoryEntry(request(PROBE), "other", "add", false);
        authority.checkDirectoryEntry(request(GROUND), "probe", "remove", false);
        Assert.assertTrue("Nothing is recorded when access control is off: " + recorded, recorded.isEmpty());
    }

    private void assertRefused(String what, MALMessageHeader header, String entry, String operation,
            boolean supervisorsOwn) {
        try {
            authority.checkDirectoryEntry(header, entry, operation, supervisorsOwn);
            Assert.fail(what + " must be refused");
        } catch (AuthorisationFailException expected) {
            // Refused
        }
    }

    private static void mode(String mode) {
        System.setProperty(AccessControlMode.PROPERTY, mode);
    }

    /** A request to the Supervisor's Directory, with the given credential or none. */
    private static MALMessageHeader request(byte[] credential) {
        return new MALMessageHeader(new Identifier("maltcp://10.0.0.2:1025/caller"),
                credential == null ? new Blob() : new Blob(credential.clone()),
                new Identifier(SUPERVISOR + "/nanosat-mo-supervisor-Directory"), Time.now(),
                InteractionType.REQUEST, new UOctet((short) 1), 1L, new UShort(2), new UShort(1),
                new UShort(2), new UOctet((short) 1), Boolean.FALSE, new NamedValueList());
    }
}
